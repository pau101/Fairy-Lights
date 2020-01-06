package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/fastener.png");

    private final FastenerModel model;

    public FenceFastenerRenderer(final EntityRendererManager mgr) {
        super(mgr);
        this.model = new FastenerModel();
    }

    @Override
    public void doRender(final FenceFastenerEntity fastener, final double x, final double y, final double z, final float yaw, final float delta) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translated(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(fastener));
        }
        this.bindTexture(TEXTURE);
        this.model.render(0.0625F);
        this.bindEntityTexture(fastener);
        FastenerRenderer.render(fastener.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new), delta);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.doRender(fastener, x, y, z, yaw, delta);
    }

    @Override
    protected ResourceLocation getEntityTexture(final FenceFastenerEntity entity) {
        return FastenerRenderer.TEXTURE;
    }

    static class FastenerModel extends Model {
        RendererModel root;

        FastenerModel() {
            this.textureWidth = 32;
            this.textureHeight = 32;
            this.root = new RendererModel(this);
            this.root.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
        }

        void render(final float scale) {
            this.root.render(scale);
        }
    }
}
