package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.server.connection.GarlandTinselConnection;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

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
        matrix.func_227860_a_();
        matrix.func_227861_a_(it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F));
        matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-it.getYaw()));
        matrix.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(it.getPitch()));
        final float length = it.getLength();
        final int rings = MathHelper.func_76123_f(length * 64);
        final int hash = connection.getUUID().hashCode();
        final int index = it.getIndex();
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.func_229311_a_(source, RenderType::func_228638_b_);
        for (int i = 0; i < rings; i++) {
            final double t = i / (float) rings * length;
            matrix.func_227860_a_();
            matrix.func_227861_a_(t, 0.0F, 0.0F);
            final float rotX = RAND.get(31 * (index + 31 * i) + hash) * 22;
            final float rotY = RAND.get(31 * (index + 3 + 31 * i) + hash) * 180;
            final float rotZ = RAND.get(31 * (index + 7 + 31 * i) + hash) * 180;
            matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(rotZ));
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(rotY));
            matrix.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(rotX));
            matrix.func_227862_a_(1.0F, RAND.get(i * 63) * 0.1F + 1.0F, 1.0F);
            this.strip.func_225598_a_(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
            matrix.func_227865_b_();
        }
        matrix.func_227865_b_();
    }

    private static class StripModel extends Model {
        final ModelRenderer root;

        StripModel() {
            super(RenderType::func_228638_b_);
            this.field_78090_t = 128;
            this.field_78089_u = 128;
            this.root = new ModelRenderer(this, 62, 0) {
                @Override
                public void func_228307_a_(final MatrixStack stack) {
                    super.func_228307_a_(stack);
                    stack.func_227866_c_().func_227870_a_().func_226595_a_(Matrix4f.func_226593_a_(1.0F, 1.0F, 0.5F));
                }
            };
            this.root.func_228300_a_(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 0.0F);
        }

        @Override
        public void func_225598_a_(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
