package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.matrix.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.client.model.*;
import me.paulf.fairylights.server.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;

public class LadderRenderer extends LivingRenderer<LadderEntity, LadderModel> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/ladder.png");

    public LadderRenderer(final EntityRendererManager mgr) {
        super(mgr, new LadderModel(), 0);
    }

    @Override
    public void render(final LadderEntity p_225623_1_, final float p_225623_2_, final float p_225623_3_, final MatrixStack p_225623_4_, final IRenderTypeBuffer p_225623_5_, final int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
        /*if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox() && !ladder.isInvisible() && !Minecraft.getInstance().isReducedDebug()) {
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2);
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            for (final AxisAlignedBB s : ladder.getCollisionSurfaces()) {
                WorldRenderer.drawSelectionBoundingBox(s.grow(0.002).offset(-ladder.posX + x, -ladder.posY + y, -ladder.posZ + z), 1, 1, 1, 1);
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }*/
    }

    @Override
    public ResourceLocation getEntityTexture(final LadderEntity ladder) {
        return TEXTURE;
    }

    @Override
    protected boolean canRenderName(final LadderEntity ladder) {
        return ladder.getAlwaysRenderNameTagForRender();
    }
}
