package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.model.LadderModel;
import me.paulf.fairylights.server.entity.LadderEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class LadderRenderer extends LivingRenderer<LadderEntity, LadderModel> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/ladder.png");

	public LadderRenderer(EntityRendererManager mgr) {
		super(mgr, new LadderModel(), 0);
	}

	@Override
	public void doRender(LadderEntity ladder, double x, double y, double z, float yaw, float delta) {
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
	protected ResourceLocation getEntityTexture(LadderEntity ladder) {
		return TEXTURE;
	}

	@Override
	protected boolean canRenderName(LadderEntity ladder) {
		return ladder.getAlwaysRenderNameTagForRender();
	}
}
