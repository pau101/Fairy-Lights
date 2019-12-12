package com.pau101.fairylights.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.model.ModelLadder;
import com.pau101.fairylights.server.entity.EntityLadder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class RenderLadder extends LivingRenderer<EntityLadder, ModelLadder> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/ladder.png");

	public RenderLadder(EntityRendererManager mgr) {
		super(mgr, new ModelLadder(), 0);
	}

	@Override
	public void doRender(EntityLadder ladder, double x, double y, double z, float yaw, float delta) {
		super.doRender(ladder, x, y, z, yaw, delta);
		if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox() && !ladder.isInvisible() && !Minecraft.getInstance().isReducedDebug()) {
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.lineWidth(2);
			GlStateManager.disableTexture();
			GlStateManager.depthMask(false);
			for (AxisAlignedBB s : ladder.getCollisionSurfaces()) {
				WorldRenderer.drawSelectionBoundingBox(s.grow(0.002).offset(-ladder.posX + x, -ladder.posY + y, -ladder.posZ + z), 1, 1, 1, 1);
			}
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLadder ladder) {
		return TEXTURE;
	}

	@Override
	protected boolean canRenderName(EntityLadder ladder) {
		return ladder.getAlwaysRenderNameTagForRender();
	}
}
