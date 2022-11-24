package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.FLMth;
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

public abstract class ConnectionRenderer<C extends Connection> {
    private final WireModel model;
    private final float wireInflate;

    protected ConnectionRenderer(final Function<ModelLayerLocation, ModelPart> baker, final ModelLayerLocation wireModelLocation) {
        this(baker, wireModelLocation, 0.0F);
    }

    protected ConnectionRenderer(final Function<ModelLayerLocation, ModelPart> baker, final ModelLayerLocation wireModelLocation, final float wireInflate) {
        this.model = new WireModel(baker.apply(wireModelLocation));
        this.wireInflate = wireInflate;
    }

    public void render(final C conn, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        final Curve currCat = conn.getCatenary();
        final Curve prevCat = conn.getPrevCatenary();
        if (currCat != null && prevCat != null) {
            final Curve cat = prevCat.lerp(currCat, delta);
            final Curve.SegmentIterator it = cat.iterator();
            final VertexConsumer buf = ClientProxy.SOLID_TEXTURE.buffer(source, RenderType::entityCutout);
            final int color = this.getWireColor(conn);
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            while (it.next()) {
                matrix.pushPose();
                matrix.translate(it.getX(0.0F), it.getY(0.0F),  it.getZ(0.0F));
                matrix.mulPose(Vector3f.YP.rotation(FLMth.PI / 2.0F - it.getYaw()));
                matrix.mulPose(Vector3f.XP.rotation(-it.getPitch()));
                matrix.scale(1.0F + this.wireInflate, 1.0F, it.getLength() * 16.0F);
                this.model.renderToBuffer(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
                matrix.popPose();
                this.renderSegment(conn, it, delta, matrix, packedLight, source, packedOverlay);
            }
            this.render(conn, cat, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    protected int getWireColor(final C conn) {
        return 0xFFFFFF;
    }

    protected void render(final C conn, final Curve catenary, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {}

    protected void renderSegment(final C connection, final Catenary.SegmentView it, final float delta, final PoseStack matrix, final int packedLight, final MultiBufferSource source, final int packedOverlay) {}

    public static class WireModel extends Model {
        final ModelPart root;

        WireModel(final ModelPart root) {
            super(RenderType::entityCutout);
            this.root = root;
        }

        @Override
        public void renderToBuffer(final PoseStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }

        public static LayerDefinition createLayer(final int u, final int v, final int size) {
            MeshDefinition mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create()
                .texOffs(u, v)
                .addBox(-size * 0.5F, -size * 0.5F, 0.0F, size, size, 1.0F), PartPose.ZERO);
            return LayerDefinition.create(mesh, 128, 128);
        }
    }
}
