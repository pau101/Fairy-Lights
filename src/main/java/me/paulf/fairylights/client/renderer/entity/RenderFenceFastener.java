package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.EntityFenceFastener;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

public final class RenderFenceFastener extends EntityRenderer<EntityFenceFastener> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/fastener.png");

	private FastenerModel model;

	public RenderFenceFastener(EntityRendererManager mgr) {
		super(mgr);
		model = new FastenerModel();
	}

	@Override
	public void doRender(EntityFenceFastener fastener, double x, double y, double z, float yaw, float delta) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translated(x, y, z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(getTeamColor(fastener));
		}
		bindTexture(TEXTURE);
		model.render(0.0625F);
		bindEntityTexture(fastener);
		FastenerRenderer.render(fastener.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new), delta);
		if (renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}
		GlStateManager.popMatrix();
		super.doRender(fastener, x, y, z, yaw, delta);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFenceFastener entity) {
		return FastenerRenderer.TEXTURE;
	}

	static class FastenerModel extends Model {
		RendererModel root;

		FastenerModel() {
			textureWidth = 32;
			textureHeight = 32;
			root = new RendererModel(this);
			root.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		}

		void render(float scale) {
			root.render(scale);
		}
	}
}
