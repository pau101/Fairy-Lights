package com.pau101.fairylights.client.renderer.entity;

import com.pau101.fairylights.client.renderer.FastenerRenderer;
import com.pau101.fairylights.client.renderer.block.FastenerStateMapper;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.entity.EntityFenceFastener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public final class RenderFenceFastener extends Render<EntityFenceFastener> {
	public RenderFenceFastener(RenderManager mgr) {
		super(mgr);
	}

	@Override
	public boolean shouldRender(EntityFenceFastener livingEntity, ICamera camera, double x, double y, double z) {
		return false;
	}

	@Override
	public void doRender(EntityFenceFastener fastener, double x, double y, double z, float yaw, float delta) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(fastener));
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5, -0.5, 0.5);
		renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(FastenerStateMapper.FENCE_FASTENER_STATE, fastener.getBrightness());
		GlStateManager.popMatrix();
		bindEntityTexture(fastener);
		FastenerRenderer.render(fastener.getCapability(CapabilityHandler.FASTENER_CAP, null), delta);
		if (renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		GlStateManager.popMatrix();
		super.doRender(fastener, x, y, z, yaw, delta);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFenceFastener entity) {
		return FastenerRenderer.TEXTURE;
	}
}
