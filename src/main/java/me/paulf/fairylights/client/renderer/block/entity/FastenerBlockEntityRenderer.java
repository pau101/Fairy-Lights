package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.Vec3d;

public final class FastenerBlockEntityRenderer extends TileEntityRenderer<FastenerBlockEntity> {
    private final BlockView view;

    public FastenerBlockEntityRenderer(final BlockView view) {
        this.view = view;
    }

    @Override
    public boolean isGlobalRenderer(final FastenerBlockEntity fastener) {
        return true;
    }

    @Override
    public void render(final FastenerBlockEntity fastener, final double x, final double y, final double z, final float delta, final int destroyStage) {
        fastener.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> {
            this.bindTexture(FastenerRenderer.TEXTURE);
            GlStateManager.pushMatrix();
            final Vec3d offset = fastener.getOffset();
            GlStateManager.translated(x + offset.x, y + offset.y, z + offset.z);
            this.view.unrotate(this.getWorld(), f.getPos(), FastenerBlockEntityRenderer.GlMatrix.INSTANCE, delta);
            FastenerRenderer.render(f, delta);
            GlStateManager.popMatrix();
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
