package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import me.paulf.fairylights.client.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.data.*;

import java.util.*;

public abstract class ConnectionRenderer<C extends Connection> {
    private final WireModel model;

    protected ConnectionRenderer(final int wireU, final int wireV, final float wireSize) {
        this.model = new WireModel(wireU, wireV, wireSize);
    }

    public void render(final C conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        final Catenary currCat = conn.getCatenary();
        final Catenary prevCat = conn.getPrevCatenary();
        if (currCat != null && prevCat != null) {
            final Catenary cat = prevCat.lerp(currCat, delta);
            final Catenary.SegmentIterator it = cat.iterator();
            final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
            final int color = this.getWireColor(conn);
            final float r = ((color >> 16) & 0xFF) / 255.0F;
            final float g = ((color >> 8) & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;
            while (it.next()) {
                this.model.root.rotationPointX = it.getX(0.0F) * 16.0F;
                this.model.root.rotationPointY = it.getY(0.0F) * 16.0F;
                this.model.root.rotationPointZ = it.getZ(0.0F) * 16.0F;
                this.model.root.rotateAngleY = Mth.PI / 2.0F - it.getYaw();
                this.model.root.rotateAngleX = -it.getPitch();
                this.model.root.rotateAngleZ = 0.0F;
                this.model.length = it.getLength() * 16.0F;
                this.model.render(matrix, buf, packedLight, packedOverlay, r, g, b, 1.0F);
                this.renderSegment(conn, it, delta, matrix, source, packedLight, packedOverlay);
            }
            this.render(conn, cat, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    protected int getWireColor(final C conn) {
        return 0xFFFFFF;
    }

    protected void render(final C conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {}

    protected void renderSegment(final C connection, final Catenary.SegmentView it, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {}

    protected final void renderBakedModel(final ResourceLocation path, final MatrixStack matrix, final IVertexBuilder buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        this.renderBakedModel(Minecraft.getInstance().getModelManager().getModel(path), matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    // (refusing to use handlePerspective due to IForgeTransformationMatrix#push superfluous undocumented MatrixStack#push)
    protected final void renderBakedModel(final IBakedModel model, final MatrixStack matrix, final IVertexBuilder buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.FIXED).apply(false, matrix);
        for (final Direction side : Direction.values()) {
            for (final BakedQuad quad : model.getQuads(null, side, new Random(42L), EmptyModelData.INSTANCE)) {
                buf.addQuad(matrix.getLast(), quad, r, g, b, packedLight, packedOverlay);
            }
        }
        for (final BakedQuad quad : model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE)) {
            buf.addQuad(matrix.getLast(), quad, r, g, b, packedLight, packedOverlay);
        }
    }

    private static class WireModel extends Model {
        final ModelRenderer root;
        float length;

        WireModel(final int u, final int v, final float size) {
            super(RenderType::getEntityCutout);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.root = new ModelRenderer(this, u, v) {
                @Override
                public void translateRotate(final MatrixStack stack) {
                    super.translateRotate(stack);
                    // Don't scale normal matrix
                    stack.getLast().getMatrix().mul(Matrix4f.makeScale(1.0F, 1.0F, WireModel.this.length));
                }
            };
            this.root.addBox(-size * 0.5F, -size * 0.5F, 0.0F, size, size, 1.0F);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.root.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
