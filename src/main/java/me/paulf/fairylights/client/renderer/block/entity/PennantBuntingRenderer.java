package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.pennant.Pennant;
import me.paulf.fairylights.server.fastener.connection.type.pennant.PennantBuntingConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;

import java.util.Random;

public class PennantBuntingRenderer extends ConnectionRenderer<PennantBuntingConnection> {
    public static final ResourceLocation MODEL = new ResourceLocation(FairyLights.ID, "entity/pennant");

    public PennantBuntingRenderer() {
        super(0, 17, 1.0F, 0.125F);
    }

    @Override
    protected void render(final PennantBuntingConnection conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final IBakedModel model = Minecraft.getInstance().getModelManager().getModel(MODEL);
        final Pennant[] currLights = conn.getFeatures();
        final Pennant[] prevLights = conn.getPrevFeatures();
        if (currLights != null && prevLights != null) {
            final FontRenderer font = Minecraft.getInstance().fontRenderer;
            final IVertexBuilder buf = source.getBuffer(Atlases.getCutoutBlockType());
            final int count = Math.min(currLights.length, prevLights.length);;
            if (count == 0) {
                return;
            }
            StyledString text = conn.getText();
            if (text.length() > count) {
                text = text.substring(0, count);
            }
            final int offset = (count - text.length()) / 2;
            for (int i = 0; i < count; i++) {
                final Pennant prevLight = prevLights[i];
                final Pennant currLight = currLights[i];
                final int color = currLight.getColor();
                final float r = ((color >> 16) & 0xFF) / 255.0F;
                final float g = ((color >> 8) & 0xFF) / 255.0F;
                final float b = (color & 0xFF) / 255.0F;
                final Vec3d pos = Mth.lerp(prevLight.getPoint(), currLight.getPoint(), delta);
                matrix.push();
                matrix.translate(pos.x, pos.y, pos.z);
                matrix.rotate(Vector3f.YP.rotation(-currLight.getYaw(delta)));
                matrix.rotate(Vector3f.ZP.rotation(currLight.getPitch(delta)));
                matrix.rotate(Vector3f.XP.rotation(currLight.getRoll(delta)));
                matrix.push();
                //noinspection deprecation (refusing to use handlePerspective due to IForgeTransformationMatrix#push superfluous undocumented MatrixStack#push)
                model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.HEAD).apply(false, matrix);
                for (final BakedQuad quad : model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE)) {
                    buf.addQuad(matrix.getLast(), quad, r, g, b, packedLight, packedOverlay);
                }
                matrix.pop();
                if (i >= offset && i < offset + text.length()) {
                    this.drawLetter(matrix, source, packedLight, font, text, i - offset, 1);
                    this.drawLetter(matrix, source, packedLight, font, text, text.length() - 1 - (i - offset), -1);
                }
                matrix.pop();
            }
        }
    }

    private void drawLetter(final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final FontRenderer font, final StyledString text, final int index, final int side) {
        final Style style = text.styleAt(index);
        final StringBuilder bob = new StringBuilder();
        if (style.isObfuscated()) bob.append(TextFormatting.OBFUSCATED);
        if (style.isBold()) bob.append(TextFormatting.BOLD);
        if (style.isStrikethrough()) bob.append(TextFormatting.STRIKETHROUGH);
        if (style.isUnderline()) bob.append(TextFormatting.UNDERLINE);
        if (style.isItalic()) bob.append(TextFormatting.ITALIC);
        bob.append(text.charAt(index));
        final String chr = bob.toString();
        matrix.push();
        matrix.translate(0.0F, -0.25F, 0.04F * side);
        final Vector3f v = new Vector3f(0.0F, 0.0F, 1.0F);
        v.transform(matrix.getLast().getNormal());
        final float brightness = LightUtil.diffuseLight(v.getX(), v.getY(), v.getZ());
        final int styleColor = MoreObjects.firstNonNull(style.getColor().getColor(), 0xFFFFFF);
        final int r = (int) ((styleColor >> 16 & 0xFF) * brightness);
        final int g = (int) ((styleColor >> 8 & 0xFF) * brightness);
        final int b = (int) ((styleColor & 0xFF) * brightness);
        final float s = 0.03075F;
        matrix.scale(s * side, -s, s);
        final float w = font.getStringWidth(chr);
        final int argb = 0xFF000000 | r << 16 | g << 8 | b;
        font.renderString(chr, -(w - 1.0F) / 2.0F, -4.0F, argb, false, matrix.getLast().getMatrix(), source, false, 0, packedLight);
        matrix.pop();
    }
}
