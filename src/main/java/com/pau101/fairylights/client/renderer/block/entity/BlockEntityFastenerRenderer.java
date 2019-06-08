package com.pau101.fairylights.client.renderer.block.entity;

import com.pau101.fairylights.client.renderer.FastenerRenderer;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.BlockView;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.util.matrix.Matrix;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;

public final class BlockEntityFastenerRenderer extends TileEntitySpecialRenderer<BlockEntityFastener> {
	private final BlockView view;

	public BlockEntityFastenerRenderer(final BlockView view) {
		this.view = view;
	}

	@Override
	public boolean isGlobalRenderer(BlockEntityFastener fastener) {
		return true;
	}

	@Override
	public void render(BlockEntityFastener fastener, double x, double y, double z, float delta, int destroyStage, float alpha) {
		bindTexture(FastenerRenderer.TEXTURE);
		GlStateManager.pushMatrix();
		final Fastener<?> f = fastener.getCapability(CapabilityHandler.FASTENER_CAP, null);
		final Vec3d offset = fastener.getOffset();
		GlStateManager.translate(x + offset.x, y + offset.y, z + offset.z);
		this.view.unrotate(this.getWorld(), f.getPos(), BlockEntityFastenerRenderer.GlMatrix.INSTANCE, delta);
		FastenerRenderer.render(f, delta);
		GlStateManager.popMatrix();
	}

	static class GlMatrix implements Matrix {
		static final BlockEntityFastenerRenderer.GlMatrix INSTANCE = new BlockEntityFastenerRenderer.GlMatrix();

		@Override
		public void translate(final float x, final float y, final float z) {
			GlStateManager.translate(x, y, z);
		}

		@Override
		public void rotate(final float angle, final float x, final float y, final float z) {
			GlStateManager.rotate(angle, x, y, z);
		}
	}
}
