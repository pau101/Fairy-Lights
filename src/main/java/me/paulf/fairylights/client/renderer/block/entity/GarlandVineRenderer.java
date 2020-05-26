package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import me.paulf.fairylights.client.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.type.garland.*;
import me.paulf.fairylights.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;

import java.util.stream.*;

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
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
        catenary.visitPoints(0.25F, false, (index, x, y, z, yaw, pitch) -> {
            matrix.push();
            matrix.translate(x, y, z);
            matrix.rotate(Vector3f.YP.rotation(-yaw));
            matrix.rotate(Vector3f.ZP.rotation(pitch));
            matrix.rotate(Vector3f.ZP.rotationDegrees(RAND.get(index + hash) * 45.0F));
            matrix.rotate(Vector3f.YP.rotationDegrees(RAND.get(index + 8 + hash) * 60.F + 90.0F));
            this.rings[index % RING_COUNT].render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
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
            final ModelRenderer cross = new ModelRenderer(this, u, v);
            cross.rotateAngleZ = Mth.HALF_PI;
            cross.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F);
            cross.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F);
            this.root.addChild(cross);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
