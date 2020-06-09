package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.Vec3d;

public final class FastenerBlockEntityRenderer extends TileEntityRenderer<FastenerBlockEntity> {
    private final BlockView view;

    public FastenerBlockEntityRenderer(final TileEntityRendererDispatcher dispatcher, final BlockView view) {
        super(dispatcher);
        this.view = view;
    }

    private final FastenerRenderer renderer = new FastenerRenderer();

    @Override
    public boolean isGlobalRenderer(final FastenerBlockEntity fastener) {
        return true;
    }

    @Override
    public void render(final FastenerBlockEntity fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer bufferSource, final int packedLight, final int packedOverlay) {
        fastener.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> {
            //this.bindTexture(FastenerRenderer.TEXTURE);
            matrix.push();
            final Vec3d offset = fastener.getOffset();
            matrix.translate(offset.x, offset.y, offset.z);
            //this.view.unrotate(this.getWorld(), f.getPos(), FastenerBlockEntityRenderer.GlMatrix.INSTANCE, delta);
            this.renderer.render(f, delta, matrix, bufferSource, packedLight, packedOverlay);
            matrix.pop();
        });
    }

    static class GlMatrix implements Matrix {
        static final FastenerBlockEntityRenderer.GlMatrix INSTANCE = new FastenerBlockEntityRenderer.GlMatrix();

        @Override
        public void translate(final float x, final float y, final float z) {
            RenderSystem.translatef(x, y, z);
        }

        @Override
        public void rotate(final float angle, final float x, final float y, final float z) {
            RenderSystem.rotatef(angle, x, y, z);
        }
    }
}
