package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.server.connection.GarlandTinselConnection;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class GarlandTinselRenderer extends ConnectionRenderer<GarlandTinselConnection> {
    private static final RandomArray RAND = new RandomArray(9171, 128);

    private final StripModel strip;

    public GarlandTinselRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        super(baker, FLModelLayers.TINSEL_WIRE);
        this.strip = new StripModel(baker.apply(FLModelLayers.TINSEL_STRIP));
    }

    @Override
    protected int getWireColor(final GarlandTinselConnection conn) {
        return conn.getColor();
    }

    @Override
    protected void renderSegment(final GarlandTinselConnection connection, final Catenary.SegmentView it, final float delta, final PoseStack matrix, final int packedLight, final MultiBufferSource source, final int packedOverlay) {
        super.renderSegment(connection, it, delta, matrix, packedLight, source, packedOverlay);
        final int color = connection.getColor();
        final float r = ((color >> 16) & 0xFF) / 255.0F;
        final float g = ((color >> 8) & 0xFF) / 255.0F;
        final float b = (color & 0xFF) / 255.0F;
        matrix.pushPose();
        matrix.translate(it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F));
        matrix.mulPose(Axis.YP.rotation(-it.getYaw()));
        matrix.mulPose(Axis.ZP.rotation(it.getPitch()));
        final float length = it.getLength();
        final int rings = Mth.ceil(length * 64);
        final int hash = connection.getUUID().hashCode();
        final int index = it.getIndex();
        final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
        for (int i = 0; i < rings; i++) {
            final double t = i / (float) rings * length;
            matrix.pushPose();
            matrix.translate(t, 0.0F, 0.0F);
            final float rotX = RAND.get(31 * (index + 31 * i) + hash) * 22;
            final float rotY = RAND.get(31 * (index + 3 + 31 * i) + hash) * 180;
            final float rotZ = RAND.get(31 * (index + 7 + 31 * i) + hash) * 180;
            matrix.mulPose(Axis.XP.rotationDegrees(rotZ));
            matrix.mulPose(Axis.YP.rotationDegrees(rotY));
            matrix.mulPose(Axis.ZP.rotationDegrees(rotX));
            matrix.scale(1.0F, RAND.get(i * 63) * 0.1F + 1.0F, 0.5F);
            this.strip.renderToBuffer(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
            matrix.popPose();
        }
        
        matrix.popPose();
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(62, 0, 1);
    }

    public static class StripModel extends Model {
        final ModelPart root;

        StripModel(final ModelPart root) {
            super(RenderType::entityCutout);
            this.root = root;
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create()
                .texOffs(62, 0)
                .addBox(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 0.0F), PartPose.ZERO);
            return LayerDefinition.create(mesh, 128, 128);
        }

        @Override
        public void renderToBuffer(final PoseStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
