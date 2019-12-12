package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.block.entity.BlockEntityFastener;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.Vec3d;

public final class BlockEntityFastenerRenderer extends TileEntityRenderer<BlockEntityFastener> {
	private final BlockView view;

	public BlockEntityFastenerRenderer(final BlockView view) {
		this.view = view;
	}

	@Override
	public boolean isGlobalRenderer(BlockEntityFastener fastener) {
		return true;
	}

	@Override
	public void render(BlockEntityFastener fastener, double x, double y, double z, float delta, int destroyStage) {
		bindTexture(FastenerRenderer.TEXTURE);
		GlStateManager.pushMatrix();
		// FIXME
		final Fastener<?> f = fastener.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
		final Vec3d offset = fastener.getOffset();
		GlStateManager.translated(x + offset.x, y + offset.y, z + offset.z);
		this.view.unrotate(this.getWorld(), f.getPos(), BlockEntityFastenerRenderer.GlMatrix.INSTANCE, delta);
		FastenerRenderer.render(f, delta);
		GlStateManager.popMatrix();
	}

	static class GlMatrix implements Matrix {
		static final BlockEntityFastenerRenderer.GlMatrix INSTANCE = new BlockEntityFastenerRenderer.GlMatrix();

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
