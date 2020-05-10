package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class FastenerRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/connections.png");

    class WireModel extends Model {
        final ModelRenderer root;
        float length;

        public WireModel() {
            super(RenderType::getEntityTranslucent);
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

    final WireModel model = new WireModel();

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        for (final Connection conn : fastener.getConnections().values()) {
            if (conn.isOrigin()) {
                final Catenary currCat = conn.getCatenary();
                final Catenary prevCat = conn.getPrevCatenary();
                if (currCat != null && prevCat != null) {
                    final Catenary cat = prevCat.lerp(currCat, delta);
                    final Catenary.SegmentIterator it = cat.iterator();
                    final IVertexBuilder buf = source.getBuffer(this.model.getLayer(TEXTURE));
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
            }
        }
    }
}
