package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.server.connection.GarlandVineConnection;
import me.paulf.fairylights.util.FLMath;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

import java.util.stream.IntStream;

public class GarlandVineRenderer extends ConnectionRenderer<GarlandVineConnection> {
    private static final int RING_COUNT = 7;

    private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

    private final RingModel[] rings;

    protected GarlandVineRenderer() {
        super(39, 0, 1.0F);
        this.rings = IntStream.range(0, RING_COUNT)
            .mapToObj(i -> new RingModel(i * 8, 64))
            .toArray(RingModel[]::new);
    }

    @Override
    protected void render(final GarlandVineConnection conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, catenary, delta, matrix, source, packedLight, packedOverlay);
        final int hash = conn.getUUID().hashCode();
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.func_229311_a_(source, RenderType::func_228638_b_);
        catenary.visitPoints(0.25F, false, (index, x, y, z, yaw, pitch) -> {
            matrix.func_227860_a_();
            matrix.func_227861_a_(x, y, z);
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-yaw));
            matrix.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(pitch));
            matrix.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(RAND.get(index + hash) * 45.0F));
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(RAND.get(index + 8 + hash) * 60.F + 90.0F));
            this.rings[index % RING_COUNT].func_225598_a_(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrix.func_227865_b_();
        });
    }

    static class RingModel extends Model {
        final ModelRenderer root;

        RingModel(final int u, final int v) {
            super(RenderType::func_228638_b_);
            this.field_78090_t = 128;
            this.field_78089_u = 128;
            this.root = new ModelRenderer(this, 14, 91);
            final float size = 4.0F;
            this.root.func_228300_a_(-size / 2.0F, -size / 2.0F, -size / 2.0F, size, size, size);
            final ModelRenderer cross = new ModelRenderer(this, u, v);
            cross.field_78808_h = FLMath.HALF_PI;
            cross.func_228300_a_(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F);
            cross.func_228300_a_(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F);
            this.root.func_78792_a(cross);
        }

        @Override
        public void func_225598_a_(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
