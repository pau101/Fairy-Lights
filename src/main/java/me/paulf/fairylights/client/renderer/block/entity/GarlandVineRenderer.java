package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandVineConnection;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.stream.IntStream;

public class GarlandVineRenderer extends ConnectionRenderer<GarlandVineConnection> {
    private static final int RING_COUNT = 8;

    private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

    private final RingModel[] rings;

    protected GarlandVineRenderer() {
        super(39, 0, 1.0F);
        this.rings = IntStream.range(0, RING_COUNT)
            .mapToObj(i -> new RingModel(i * 8, 64))
            .toArray(RingModel[]::new);
    }

    @Override
    protected void render(final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(catenary, delta, matrix, source, packedLight, packedOverlay);
        catenary.visitPoints(0.25F, false, (index, x, y, z, yaw, pitch) -> {
            matrix.push();
            matrix.translate(x, y, z);
            matrix.rotate(Vector3f.YP.rotation(-yaw));
            matrix.rotate(Vector3f.ZP.rotation(pitch));
            final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
            final int uniquifier = 0;//connection.hashCode();
            final float rotZ = RAND.get(index + uniquifier) * 45;
            final float rotY = RAND.get(index + 8 + uniquifier) * 60 + 90;
            matrix.rotate(Vector3f.ZP.rotationDegrees(rotZ));
            matrix.rotate(Vector3f.YP.rotationDegrees(rotY));
//            final int ring = this.rings[index % RING_COUNT];
            final RingModel ring = new RingModel(0, 0);
            ring.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrix.pop();
        });
    }

    static class RingModel extends Model {
        final ModelRenderer root;

        RingModel(final int u, final int v) {
            super(RenderType::getEntityCutout);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.root = new ModelRenderer(this, 14, 91);
            final float size = 4.0F;
            this.root.addBox(-size / 2.0F, -size / 2.0F, -size / 2.0F, size, size, size);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
