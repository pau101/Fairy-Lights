package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.model.light.GhostLightModel;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.Vec3d;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final WireModel model = new WireModel();

    private final LightModel light = new GhostLightModel();

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light[] currLights = conn.getFeatures();
        final Light[] prevLights = conn.getPrevFeatures();
        if (currLights != null && prevLights != null) {
            final IVertexBuilder bufSolid = ClientProxy.SOLID_TEXTURE.getVertexConsumer(source, this.model::getLayer);
            final IVertexBuilder bufTranslucent = ClientProxy.TRANSLUCENT_TEXTURE.getVertexConsumer(source, this.light::getLayer);
            final int count = Math.min(currLights.length, prevLights.length);
            for (int i = 0; i < count; i++) {
                final Light prevLight = prevLights[i];
                final Light currLight = currLights[i];
                final Vec3d pos = Mth.lerp(prevLight.getPoint(), currLight.getPoint(), delta);
                this.light.animate(currLight, delta);
                matrix.push();
                matrix.translate(pos.x, pos.y, pos.z);
                matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(-currLight.getYaw(delta)));
                matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(currLight.getPitch(delta)));
                matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(currLight.getRoll(delta)));
                matrix.translate(0.0D, -0.125D, 0.0D);
                this.light.render(matrix, bufSolid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                this.light.renderTranslucent(matrix, bufTranslucent, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrix.pop();
            }
        }
    }

    @Override
    protected void render(final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        final Catenary.SegmentIterator it = catenary.iterator();
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getVertexConsumer(source, this.model::getLayer);
        while (it.next()) {
            this.model.root.rotationPointX = it.getX(0.0F) * 16.0F;
            this.model.root.rotationPointY = it.getY(0.0F) * 16.0F;
            this.model.root.rotationPointZ = it.getZ(0.0F) * 16.0F;
            this.model.root.rotateAngleY = Mth.PI / 2.0F - it.getYaw();
            this.model.root.rotateAngleX = -it.getPitch();
            this.model.root.rotateAngleZ = 0.0F;
            this.model.length = it.getLength() * 16.0F;
            this.model.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static class WireModel extends Model {
        final ModelRenderer root;
        float length;

        public WireModel() {
            super(RenderType::getEntityCutout);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.root = new ModelRenderer(this, 0, 0) {
                @Override
                public void rotate(final MatrixStack stack) {
                    super.rotate(stack);
                    stack.scale(1.0F, 1.0F, WireModel.this.length);
                }
            };
            this.root.addCuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 1.0F);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
