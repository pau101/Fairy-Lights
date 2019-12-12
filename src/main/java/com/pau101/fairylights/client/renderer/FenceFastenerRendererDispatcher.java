package com.pau101.fairylights.client.renderer;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;

public final class FenceFastenerRendererDispatcher extends TileEntityRenderer<FenceFastenerRepresentative> {
	private FenceFastenerRendererDispatcher() {}

	public static final FenceFastenerRendererDispatcher INSTANCE = new FenceFastenerRendererDispatcher();

	@Override
	public void render(FenceFastenerRepresentative rep, double px, double py, double pz, float delta, int destroy) {
		Minecraft mc = Minecraft.getInstance();
		Entity view = mc.getRenderViewEntity();
		EntityRendererManager renderMgr = mc.getRenderManager();
		double x = view.lastTickPosX + (view.posX - view.lastTickPosX) * delta;
		double y = view.lastTickPosY + (view.posY - view.lastTickPosY) * delta;
		double z = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * delta;
		ICamera camera = new Frustum();
		camera.setPosition(x, y, z);
		for (Entity e : mc.world.getAllEntities()) {
			if (e instanceof EntityFenceFastener) {
				EntityFenceFastener fence = (EntityFenceFastener) e;
				if (fence.isInRangeToRender3d(x, y, z) && camera.isBoundingBoxInFrustum(fence.getRenderBoundingBox())) {
					renderMgr.renderEntityStatic(fence, delta, false);
				}	
			}
		}
	}
}
