package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.server.connection.PennantBuntingConnection;
import me.paulf.fairylights.server.feature.Pennant;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

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

    public PennantBuntingRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        super(baker, FLModelLayers.PENNANT_WIRE, 0.25F);
    }

    @Override
    protected void render(final PennantBuntingConnection conn, final Curve catenary, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Pennant[] currLights = conn.getFeatures();
        if (currLights != null) {
            final Font font = Minecraft.getInstance().font;
            final VertexConsumer buf = source.getBuffer(Sheets.cutoutBlockSheet());
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
                final BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.models.getOrDefault(currPennant.getItem(), TRIANGLE_MODEL));
                final Vec3 pos = currPennant.getPoint(delta);
                matrix.pushPose();
                matrix.translate(pos.x, pos.y, pos.z);
                matrix.mulPose(Vector3f.YP.rotation(-currPennant.getYaw(delta)));
                matrix.mulPose(Vector3f.ZP.rotation(currPennant.getPitch(delta)));
                matrix.mulPose(Vector3f.XP.rotation(currPennant.getRoll(delta)));
                matrix.pushPose();
                FastenerRenderer.renderBakedModel(model, matrix, buf, r, g, b, packedLight, packedOverlay);
                matrix.popPose();
                if (i >= offset && i < offset + text.length()) {
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, i - offset, 1, delta);
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, text.length() - 1 - (i - offset), -1, delta);
                }
                matrix.popPose();
            }
        }
    }

    private void drawLetter(final PoseStack matrix, final MultiBufferSource source, final Pennant pennant, final int packedLight, final Font font, final StyledString text, final int index, final int side, final float delta) {
        final Style style = text.styleAt(index);
        final StringBuilder bob = new StringBuilder();
        if (style.isObfuscated()) bob.append(ChatFormatting.OBFUSCATED);
        if (style.isBold()) bob.append(ChatFormatting.BOLD);
        if (style.isStrikethrough()) bob.append(ChatFormatting.STRIKETHROUGH);
        if (style.isUnderline()) bob.append(ChatFormatting.UNDERLINE);
        if (style.isItalic()) bob.append(ChatFormatting.ITALIC);
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
        final float brightness = Mth.diffuseLight(v.x(), v.y(), v.z());
        final int styleColor = MoreObjects.firstNonNull(style.getColor().getColor(), 0xFFFFFF);
        final int r = (int) ((styleColor >> 16 & 0xFF) * brightness);
        final int g = (int) ((styleColor >> 8 & 0xFF) * brightness);
        final int b = (int) ((styleColor & 0xFF) * brightness);
        final int argb = 0xFF000000 | r << 16 | g << 8 | b;
        matrix.pushPose();
        matrix.translate(0.0F, -0.25F, 0.04F * side);
        final float s = 0.03075F;
        matrix.scale(s * side, -s, s);
        final float w = font.width(chr);
        font.drawInBatch(chr, -(w - 1.0F) / 2.0F, -4.0F, argb, false, matrix.last().pose(), source, false, 0, packedLight);
        matrix.popPose();
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(0, 17, 1);
    }
}
