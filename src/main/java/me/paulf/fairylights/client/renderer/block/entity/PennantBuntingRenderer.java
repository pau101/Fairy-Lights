package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.pennant.Pennant;
import me.paulf.fairylights.server.fastener.connection.type.pennant.PennantBuntingConnection;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class PennantBuntingRenderer extends ConnectionRenderer<PennantBuntingConnection> {
    private static final ResourceLocation TRIANGLE_MODEL = new ResourceLocation(FairyLights.ID, "entity/triangle_pennant");

    private static final ResourceLocation SPEARHEAD_MODEL = new ResourceLocation(FairyLights.ID, "entity/spearhead_pennant");

    private static final ResourceLocation SWALLOWTAIL_MODEl = new ResourceLocation(FairyLights.ID, "entity/swallowtail_pennant");

    private static final ResourceLocation SQUARE_MODEL = new ResourceLocation(FairyLights.ID, "entity/square_pennant");

    public static final ImmutableSet<ResourceLocation> MODELS = ImmutableSet.of(TRIANGLE_MODEL, SPEARHEAD_MODEL, SWALLOWTAIL_MODEl, SQUARE_MODEL);

    private final ImmutableMap<Item, ResourceLocation> models = ImmutableMap.of(
        FLItems.TRIANGLE_PENNANT.get(), TRIANGLE_MODEL,
        FLItems.SPEARHEAD_PENNANT.get(), SPEARHEAD_MODEL,
        FLItems.SWALLOWTAIL_PENNANT.get(), SWALLOWTAIL_MODEl,
        FLItems.SQUARE_PENNANT.get(), SQUARE_MODEL
    );

    public PennantBuntingRenderer() {
        super(0, 17, 1.25F);
    }

    @Override
    protected void render(final PennantBuntingConnection conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Pennant[] currLights = conn.getFeatures();
        if (currLights != null) {
            final FontRenderer font = Minecraft.getInstance().fontRenderer;
            final IVertexBuilder buf = source.getBuffer(Atlases.getCutoutBlockType());
            final int count = currLights.length;
            if (count == 0) {
                return;
            }
            StyledString text = conn.getText();
            if (text.length() > count) {
                text = text.substring(0, count);
            }
            final int offset = (count - text.length()) / 2;
            for (int i = 0; i < count; i++) {
                final Pennant currPennant = currLights[i];
                final int color = currPennant.getColor();
                final float r = ((color >> 16) & 0xFF) / 255.0F;
                final float g = ((color >> 8) & 0xFF) / 255.0F;
                final float b = (color & 0xFF) / 255.0F;
                final IBakedModel model = Minecraft.getInstance().getModelManager().getModel(models.getOrDefault(currPennant.getItem(), TRIANGLE_MODEL));
                final Vec3d pos = currPennant.getPoint(delta);
                matrix.push();
                matrix.translate(pos.x, pos.y, pos.z);
                matrix.rotate(Vector3f.YP.rotation(-currPennant.getYaw(delta)));
                matrix.rotate(Vector3f.ZP.rotation(currPennant.getPitch(delta)));
                matrix.rotate(Vector3f.XP.rotation(currPennant.getRoll(delta)));
                matrix.push();
                FastenerRenderer.renderBakedModel(model, matrix, buf, r, g, b, packedLight, packedOverlay);
                matrix.pop();
                if (i >= offset && i < offset + text.length()) {
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, i - offset, 1, delta);
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, text.length() - 1 - (i - offset), -1, delta);
                }
                matrix.pop();
            }
        }
    }

    private void drawLetter(final MatrixStack matrix, final IRenderTypeBuffer source, final Pennant pennant, final int packedLight, final FontRenderer font, final StyledString text, final int index, final int side, final float delta) {
        final Style style = text.styleAt(index);
        final StringBuilder bob = new StringBuilder();
        if (style.isObfuscated()) bob.append(TextFormatting.OBFUSCATED);
        if (style.isBold()) bob.append(TextFormatting.BOLD);
        if (style.isStrikethrough()) bob.append(TextFormatting.STRIKETHROUGH);
        if (style.isUnderline()) bob.append(TextFormatting.UNDERLINE);
        if (style.isItalic()) bob.append(TextFormatting.ITALIC);
        bob.append(text.charAt(index));
        final String chr = bob.toString();
        final Matrix3f m = new Matrix3f();
        m.setIdentity();
        m.mul(Vector3f.YP.rotation(pennant.getYaw(delta)));
        m.mul(Vector3f.ZP.rotation(pennant.getPitch(delta)));
        m.mul(Vector3f.XP.rotation(pennant.getRoll(delta)));
        final Vector3f v = new Vector3f(0.0F, 0.0F, side);
        v.transform(m);
        // TODO: correct entity diffuse
        final float brightness = LightUtil.diffuseLight(v.getX(), v.getY(), v.getZ());
        final int styleColor = MoreObjects.firstNonNull(style.getColor().getColor(), 0xFFFFFF);
        final int r = (int) ((styleColor >> 16 & 0xFF) * brightness);
        final int g = (int) ((styleColor >> 8 & 0xFF) * brightness);
        final int b = (int) ((styleColor & 0xFF) * brightness);
        final int argb = 0xFF000000 | r << 16 | g << 8 | b;
        matrix.push();
        matrix.translate(0.0F, -0.25F, 0.04F * side);
        final float s = 0.03075F;
        matrix.scale(s * side, -s, s);
        final float w = font.getStringWidth(chr);
        font.renderString(chr, -(w - 1.0F) / 2.0F, -4.0F, argb, false, matrix.getLast().getMatrix(), source, false, 0, packedLight);
        matrix.pop();
    }

}
