package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.*;
import me.paulf.fairylights.server.block.entity.*;
import me.paulf.fairylights.server.capability.*;
import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.util.matrix.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.util.math.*;

public final class FastenerBlockEntityRenderer extends TileEntityRenderer<FastenerBlockEntity> {
    private final BlockView view;

    public FastenerBlockEntityRenderer(final TileEntityRendererDispatcher dispatcher, final BlockView view) {
        super(dispatcher);
        this.view = view;
    }

    private FastenerRenderer renderer = new FastenerRenderer();

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
            GlStateManager.translatef(x, y, z);
        }

        @Override
        public void rotate(final float angle, final float x, final float y, final float z) {
            GlStateManager.rotatef(angle, x, y, z);
        }
    }
}
