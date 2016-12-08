package com.pau101.fairylights.client.renderer;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.util.WorldEventListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public final class FenceFastenerRendererDispatcher extends TileEntitySpecialRenderer<FenceFastenerRepresentative> implements WorldEventListener {
	private FenceFastenerRendererDispatcher() {}

	public static final FenceFastenerRendererDispatcher INSTANCE = new FenceFastenerRendererDispatcher();

	private Set<EntityFenceFastener> fences = Collections.EMPTY_SET;

	public FenceFastenerRendererDispatcher init(World world) {
		fences = Collections.newSetFromMap(new WeakHashMap<>());
		return this;
	}

	@Override
	public void onEntityAdded(Entity entity) {
		if (entity instanceof EntityFenceFastener) {
			fences.add((EntityFenceFastener) entity);
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		if (entity instanceof EntityFenceFastener) {
			fences.remove(entity);
		}
	}

	@Override
	public void renderTileEntityAt(FenceFastenerRepresentative rep, double px, double py, double pz, float delta, int destroy) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity view = mc.getRenderViewEntity();
		RenderManager renderMgr = mc.getRenderManager();
		double x = view.lastTickPosX + (view.posX - view.lastTickPosX) * delta;
		double y = view.lastTickPosY + (view.posY - view.lastTickPosY) * delta;
		double z = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * delta;
		ICamera camera = new Frustum();
		camera.setPosition(x, y, z);
		for (EntityFenceFastener fence : fences) {
			if (fence.isInRangeToRender3d(x, y, z) && (camera.isBoundingBoxInFrustum(fence.getRenderBoundingBox()))) {
				renderMgr.renderEntityStatic(fence, delta, false);
			}
		}
	}
}
