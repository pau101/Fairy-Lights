package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.client.model.light.BowModel;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.GarlandTinselConnection;
import me.paulf.fairylights.server.connection.GarlandVineConnection;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.connection.LetterBuntingConnection;
import me.paulf.fairylights.server.connection.PennantBuntingConnection;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;
import java.util.function.Function;

public class FastenerRenderer {
    private final HangingLightsRenderer hangingLights;
    private final GarlandVineRenderer garland;
    private final GarlandTinselRenderer tinsel;
    private final PennantBuntingRenderer pennants;
    private final LetterBuntingRenderer letters;
    private final BowModel bow;

    public FastenerRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        this.hangingLights = new HangingLightsRenderer(baker);
        this.garland = new GarlandVineRenderer(baker);
        this.tinsel = new GarlandTinselRenderer(baker);
        this.pennants = new PennantBuntingRenderer(baker);
        this.letters = new LetterBuntingRenderer(baker);
        this.bow = new BowModel(baker.apply(FLModelLayers.BOW));
    }

    public void render(final Fastener<?> fastener, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        boolean renderBow = true;
        for (final Connection conn : fastener.getAllConnections()) {
            if (conn.getFastener() == fastener) {
                this.renderConnection(delta, matrix, source, packedLight, packedOverlay, conn);
            }
            if (renderBow && conn instanceof GarlandVineConnection && fastener.getFacing().getAxis() != Direction.Axis.Y) {
                final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
                matrix.pushPose();
                matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F - fastener.getFacing().toYRot()));
                this.bow.renderToBuffer(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrix.popPose();
                renderBow = false;
            }
        }
    }

    private void renderConnection(final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay, final Connection conn) {
        if (conn instanceof HangingLightsConnection) {
            this.hangingLights.render((HangingLightsConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof GarlandVineConnection) {
            this.garland.render((GarlandVineConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof GarlandTinselConnection) {
            this.tinsel.render((GarlandTinselConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof PennantBuntingConnection) {
            this.pennants.render((PennantBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        } else if (conn instanceof LetterBuntingConnection) {
            this.letters.render((LetterBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    public static void renderBakedModel(final ResourceLocation path, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(Minecraft.getInstance().getModelManager().getModel(path), matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    public static void renderBakedModel(final BakedModel model, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(model, ItemTransforms.TransformType.FIXED, matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    // (refusing to use handlePerspective due to IForgeTransformationMatrix#push superfluous undocumented MatrixStack#push)
    public static void renderBakedModel(final BakedModel model, final ItemTransforms.TransformType type, final PoseStack matrix, final VertexConsumer buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        model.getTransforms().getTransform(type).apply(false, matrix);
        for (final Direction side : Direction.values()) {
            for (final BakedQuad quad : model.getQuads(null, side, new Random(42L), EmptyModelData.INSTANCE)) {
                buf.putBulkData(matrix.last(), quad, r, g, b, packedLight, packedOverlay);
            }
        }
        for (final BakedQuad quad : model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE)) {
            buf.putBulkData(matrix.last(), quad, r, g, b, packedLight, packedOverlay);
        }
    }
}
