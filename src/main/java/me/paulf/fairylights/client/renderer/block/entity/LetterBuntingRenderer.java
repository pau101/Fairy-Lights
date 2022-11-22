package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.server.connection.LetterBuntingConnection;
import me.paulf.fairylights.server.feature.Letter;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
import java.util.function.Function;

public class LetterBuntingRenderer extends ConnectionRenderer<LetterBuntingConnection> {
    public static final Int2ObjectMap<ResourceLocation> MODELS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ&!?".chars()
        .collect(
            Int2ObjectOpenHashMap::new,
            (map, cp) -> map.put(cp, new ResourceLocation(FairyLights.ID, "entity/letter/" + Character.getName(cp).toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "_"))),
            Int2ObjectOpenHashMap::putAll
        );

    public LetterBuntingRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        super(baker, FLModelLayers.LETTER_WIRE);
    }

    @Override
    protected void render(final LetterBuntingConnection conn, final Curve catenary, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final Letter[] letters = conn.getLetters();
        if (letters == null) {
            return;
        }
        final int count = letters.length;
        if (count == 0) {
            return;
        }
        final VertexConsumer buf = source.getBuffer(Sheets.cutoutBlockSheet());
        for (final Letter letter : letters) {
            final ResourceLocation path = MODELS.get(letter.getLetter());
            if (path == null) {
                continue;
            }
            final int color = StyledString.getColor(letter.getStyle().getColor());
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            final Vec3 pos = letter.getPoint(delta);
            matrix.pushPose();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.mulPose(Vector3f.YP.rotation(-letter.getYaw(delta)));
            matrix.mulPose(Vector3f.ZP.rotation(letter.getPitch(delta)));
            matrix.mulPose(Vector3f.XP.rotation(letter.getRoll(delta)));
            matrix.translate(-0.5F, -1.0F - 0.5F / 16.0F, -0.5F);
            FastenerRenderer.renderBakedModel(path, matrix, buf, r, g, b, packedLight, packedOverlay);
            matrix.popPose();
        }
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(0, 17, 1);
    }
}
