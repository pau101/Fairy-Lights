package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandTinselConnection;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class GarlandTinselRenderer extends ConnectionRenderer<GarlandTinselConnection> {
    private static final RandomArray RAND = new RandomArray(9171, 128);

    private final StripModel strip;

    public GarlandTinselRenderer() {
        super(62, 0, 1.0F);
        this.strip = new StripModel();
    }

    @Override
    protected int getWireColor(final GarlandTinselConnection conn) {
        return conn.getColor();
    }

    @Override
    protected void renderSegment(final GarlandTinselConnection connection, final Catenary.SegmentView it, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.renderSegment(connection, it, delta, matrix, source, packedLight, packedOverlay);
        final int color = connection.getColor();
        final float r = ((color >> 16) & 0xFF) / 255.0F;
        final float g = ((color >> 8) & 0xFF) / 255.0F;
        final float b = (color & 0xFF) / 255.0F;
        matrix.push();
        matrix.translate(it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F));
        matrix.rotate(Vector3f.YP.rotation(-it.getYaw()));
        matrix.rotate(Vector3f.ZP.rotation(it.getPitch()));
        final float length = it.getLength();
        final int rings = MathHelper.ceil(length * 64);
        final int hash = connection.getUUID().hashCode();
        final int index = it.getIndex();
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
        for (int i = 0; i < rings; i++) {
            final double t = i / (float) rings * length;
            matrix.push();
            matrix.translate(t, 0.0F, 0.0F);
            final float rotX = RAND.get(31 * (index + 31 * i) + hash) * 22;
            final float rotY = RAND.get(31 * (index + 3 + 31 * i) + hash) * 180;
            final float rotZ = RAND.get(31 * (index + 7 + 31 * i) + hash) * 180;
            matrix.rotate(Vector3f.XP.rotationDegrees(rotZ));
            matrix.rotate(Vector3f.YP.rotationDegrees(rotY));
            matrix.rotate(Vector3f.ZP.rotationDegrees(rotX));
            matrix.scale(1.0F, RAND.get(i * 63) * 0.1F + 1.0F, 1.0F);
            this.strip.render(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
            matrix.pop();
        }
        matrix.pop();
    }

    private static class StripModel extends Model {
        final ModelRenderer root;

        StripModel() {
            super(RenderType::getEntityCutout);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.root = new ModelRenderer(this, 62, 0) {
                @Override
                public void translateRotate(final MatrixStack stack) {
                    super.translateRotate(stack);
                    stack.scale(1.0F, 1.0F, 0.5F);
                }
            };
            this.root.addBox(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 0.0F);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
