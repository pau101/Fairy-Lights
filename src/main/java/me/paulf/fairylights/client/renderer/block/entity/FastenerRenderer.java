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
import net.minecraft.util.math.MathHelper;

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
                Catenary currCat = conn.getCatenary();
                Catenary prevCat = conn.getPrevCatenary();
                if (currCat != null && prevCat != null) {
                    final float dt;
                    if (currCat.getCount() > prevCat.getCount()) {
                        final Catenary temp = currCat;
                        currCat = prevCat;
                        prevCat = temp;
                        dt = 1.0F - delta;
                    } else {
                        dt = delta;
                    }
                    final Catenary.SegmentIterator currSegs = currCat.iterator();
                    final Catenary.SegmentIterator prevSegs = prevCat.iterator();
                    final IVertexBuilder buf = source.getBuffer(this.model.getLayer(TEXTURE));
                    while (currSegs.next()) {
                        if (!prevSegs.next()) {
                            throw new IllegalStateException();
                        }
                        final float x = MathHelper.lerp(dt, prevSegs.getX(0.0F), currSegs.getX(0.0F));
                        final float y = MathHelper.lerp(dt, prevSegs.getY(0.0F), currSegs.getY(0.0F));
                        final float z = MathHelper.lerp(dt, prevSegs.getZ(0.0F), currSegs.getZ(0.0F));
                        final float len;
                        if (prevSegs.hasNext() && !currSegs.hasNext()) {
                            final float dx = MathHelper.lerp(dt, prevCat.getX(), currSegs.getX(1.0F)) - x;
                            final float dy = MathHelper.lerp(dt, prevCat.getY(), currSegs.getY(1.0F)) - y;
                            final float dz = MathHelper.lerp(dt, prevCat.getZ(), currSegs.getZ(1.0F)) - z;
                            len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                            // TODO: angle
                        } else {
                            len = MathHelper.lerp(dt, prevSegs.getLength(), currSegs.getLength());
                        }
                        this.model.root.rotationPointX = x * 16.0F;
                        this.model.root.rotationPointY = y * 16.0F;
                        this.model.root.rotationPointZ = z * 16.0F;
                        this.model.root.rotateAngleY = Mth.PI / 2.0F - Mth.lerpAngle(prevSegs.getYaw(), currSegs.getYaw(), dt);
                        this.model.root.rotateAngleX = -Mth.lerpAngle(prevSegs.getPitch(), currSegs.getPitch(), dt);
                        this.model.root.rotateAngleZ = 0.0F;
                        this.model.length = len * 16.0F;
                        this.model.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}
