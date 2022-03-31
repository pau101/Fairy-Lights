package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.server.feature.Pennant;
import me.paulf.fairylights.server.connection.PennantBuntingConnection;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
            final FontRenderer font = Minecraft.func_71410_x().field_71466_p;
            final IVertexBuilder buf = source.getBuffer(Atlases.func_228783_h_());
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
                final IBakedModel model = Minecraft.func_71410_x().func_209506_al().getModel(this.models.getOrDefault(currPennant.getItem(), TRIANGLE_MODEL));
                final Vector3d pos = currPennant.getPoint(delta);
                matrix.func_227860_a_();
                matrix.func_227861_a_(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
                matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-currPennant.getYaw(delta)));
                matrix.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(currPennant.getPitch(delta)));
                matrix.func_227863_a_(Vector3f.field_229179_b_.func_229193_c_(currPennant.getRoll(delta)));
                matrix.func_227860_a_();
                FastenerRenderer.renderBakedModel(model, matrix, buf, r, g, b, packedLight, packedOverlay);
                matrix.func_227865_b_();
                if (i >= offset && i < offset + text.length()) {
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, i - offset, 1, delta);
                    this.drawLetter(matrix, source, currPennant, packedLight, font, text, text.length() - 1 - (i - offset), -1, delta);
                }
                matrix.func_227865_b_();
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
        m.func_226119_c_();
        m.func_226115_a_(Vector3f.field_229181_d_.func_229193_c_(pennant.getYaw(delta)));
        m.func_226115_a_(Vector3f.field_229183_f_.func_229193_c_(pennant.getPitch(delta)));
        m.func_226115_a_(Vector3f.field_229179_b_.func_229193_c_(pennant.getRoll(delta)));
        final Vector3f v = new Vector3f(0.0F, 0.0F, side);
        v.func_229188_a_(m);
        // TODO: correct entity diffuse
        final float brightness = LightUtil.diffuseLight(v.func_195899_a(), v.func_195900_b(), v.func_195902_c());
        final int styleColor = MoreObjects.firstNonNull(style.getColor().func_211163_e(), 0xFFFFFF);
        final int r = (int) ((styleColor >> 16 & 0xFF) * brightness);
        final int g = (int) ((styleColor >> 8 & 0xFF) * brightness);
        final int b = (int) ((styleColor & 0xFF) * brightness);
        final int argb = 0xFF000000 | r << 16 | g << 8 | b;
        matrix.func_227860_a_();
        matrix.func_227861_a_(0.0F, -0.25F, 0.04F * side);
        final float s = 0.03075F;
        matrix.func_227862_a_(s * side, -s, s);
        final float w = font.func_78256_a(chr);
        font.func_228079_a_(chr, -(w - 1.0F) / 2.0F, -4.0F, argb, false, matrix.func_227866_c_().func_227870_a_(), source, false, 0, packedLight);
        matrix.func_227865_b_();
    }

}
