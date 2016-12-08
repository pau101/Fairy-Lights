package com.pau101.fairylights.client.renderer.entity;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.model.ModelLadder;
import com.pau101.fairylights.server.entity.EntityLadder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class RenderLadder extends RenderLivingBase<EntityLadder> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/ladder.png");

	public RenderLadder(RenderManager mgr) {
		super(mgr, new ModelLadder(), 0);
	}

	@Override
	public void doRender(EntityLadder ladder, double x, double y, double z, float yaw, float delta) {
		super.doRender(ladder, x, y, z, yaw, delta);
		if (Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox() && !ladder.isInvisible() && !Minecraft.getMinecraft().func_189648_am()) {
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			GlStateManager.glLineWidth(2);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			for (AxisAlignedBB s : ladder.getCollisionSurfaces()) {
				RenderGlobal.func_189697_a(s.expandXyz(0.002).offset(-ladder.posX + x, -ladder.posY + y, -ladder.posZ + z), 1, 1, 1, 1);
			}
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
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
		return ladder.getAlwaysRenderNameTag();
	}
}
