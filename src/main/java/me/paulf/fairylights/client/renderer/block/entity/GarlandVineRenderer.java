package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.server.connection.GarlandVineConnection;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.Mth;
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

import java.util.function.Function;

public class GarlandVineRenderer extends ConnectionRenderer<GarlandVineConnection> {
    private static final int RING_COUNT = 7;

    private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

    private final RingsModel rings;

    protected GarlandVineRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        super(baker, FLModelLayers.VINE_WIRE);
        this.rings = new RingsModel(baker.apply(FLModelLayers.GARLAND_RINGS));
    }

    @Override
    protected void render(final GarlandVineConnection conn, final Curve catenary, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final int hash = conn.getUUID().hashCode();
        final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
        catenary.visitPoints(0.25F, false, (index, x, y, z, yaw, pitch) -> {
            matrix.pushPose();
            matrix.translate(x, y, z);
            matrix.mulPose(Vector3f.YP.rotation(-yaw));
            matrix.mulPose(Vector3f.ZP.rotation(pitch));
            matrix.mulPose(Vector3f.ZP.rotationDegrees(RAND.get(index + hash) * 45.0F));
            matrix.mulPose(Vector3f.YP.rotationDegrees(RAND.get(index + 8 + hash) * 60.F + 90.0F));
            this.rings.setWhich(index % RING_COUNT);
            this.rings.renderToBuffer(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrix.popPose();
        });
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(39, 0, 1);
    }

    public static class RingsModel extends Model {
        final ModelPart[] roots;
        int which;

        RingsModel(final ModelPart root) {
            super(RenderType::entityCutout);
            ModelPart[] roots = new ModelPart[RING_COUNT];
            for (int i = 0; i < RING_COUNT; i++) {
                roots[i] = root.getChild(Integer.toString(i));
            }
            this.roots = roots;
        }

        public static LayerDefinition createLayer() {
            final float size = 4.0F;
            CubeListBuilder root = CubeListBuilder.create()
                .texOffs(14, 91)
                .addBox(-size / 2.0F, -size / 2.0F, -size / 2.0F, size, size, size);
            PartPose crossPose = PartPose.rotation(0.0F, 0.0F, Mth.HALF_PI);
            MeshDefinition mesh = new MeshDefinition();
            for (int i = 0; i < RING_COUNT; i++) {
                mesh.getRoot().addOrReplaceChild(Integer.toString(i), root, PartPose.ZERO)
                    .addOrReplaceChild("cross_" + i, CubeListBuilder.create()
                        .texOffs(i * 8, 64)
                        .addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F)
                        .addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F), crossPose);
            }
            return LayerDefinition.create(mesh, 128, 128);
        }

        public void setWhich(int which) {
            this.which = which;
        }

        @Override
        public void renderToBuffer(final PoseStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.roots[this.which].render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
