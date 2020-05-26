package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.matrix.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.server.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/mjolnir.png");

    //private final FastenerModel model;

    public FenceFastenerRenderer(final EntityRendererManager mgr) {
        super(mgr);
        //this.model = new FastenerModel();
    }

    @Override
    protected int getBlockLight(final FenceFastenerEntity entity, final float delta) {
        return entity.world.getLightFor(LightType.BLOCK, new BlockPos(entity));
    }

    @Override
    public void render(final FenceFastenerEntity p_225623_1_, final float p_225623_2_, final float p_225623_3_, final MatrixStack p_225623_4_, final IRenderTypeBuffer p_225623_5_, final int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    /*@Override
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
    }*/

    @Override
    public ResourceLocation getEntityTexture(final FenceFastenerEntity p_110775_1_) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    /*static class FastenerModel extends Model {
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
    }*/
}
