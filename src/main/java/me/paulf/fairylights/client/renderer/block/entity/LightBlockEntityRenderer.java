package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.lights.FairyLightModel;
import me.paulf.fairylights.client.model.lights.FlowerLightModel;
import me.paulf.fairylights.client.model.lights.GhostLightModel;
import me.paulf.fairylights.client.model.lights.IcicleLightsModel;
import me.paulf.fairylights.client.model.lights.JackOLanternLightModel;
import me.paulf.fairylights.client.model.lights.LightModel;
import me.paulf.fairylights.client.model.lights.MeteorLightModel;
import me.paulf.fairylights.client.model.lights.OilLanternModel;
import me.paulf.fairylights.client.model.lights.OrbLanternModel;
import me.paulf.fairylights.client.model.lights.OrnateLanternModel;
import me.paulf.fairylights.client.model.lights.PaperLanternModel;
import me.paulf.fairylights.client.model.lights.SkullLightModel;
import me.paulf.fairylights.client.model.lights.SnowflakeLightModel;
import me.paulf.fairylights.client.model.lights.SpiderLightModel;
import me.paulf.fairylights.client.model.lights.WitchLightModel;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.math.AxisAlignedBB;

public class LightBlockEntityRenderer extends TileEntityRenderer<LightBlockEntity> {
    private final LightModel[] lightModels = new LightModel[]{
        new FairyLightModel(),
        new PaperLanternModel(),
        new OrbLanternModel(),
        new FlowerLightModel(),
        new OrnateLanternModel(),
        new OilLanternModel(),
        new JackOLanternLightModel(),
        new SkullLightModel(),
        new GhostLightModel(),
        new SpiderLightModel(),
        new WitchLightModel(),
        new SnowflakeLightModel(),
        new IcicleLightsModel(),
        new MeteorLightModel()
    };

    static class FastenerModel extends Model {
        final RendererModel model;

        FastenerModel() {
            this.textureWidth = 32;
            this.textureHeight = 32;
            this.model = new RendererModel(this, 0, 12);
            this.model.addBox(-1.0F, -1.0F, 0.05F, 2, 2, 8, -0.05F);
        }

        void render() {
            this.model.render(0.0625F);
        }
    }

    FastenerModel fastener = new FastenerModel();

    @Override
    public void render(final LightBlockEntity entity, final double x, final double y, final double z, final float delta, final int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.translatef((float) x, (float) y, (float) z);
        final BlockState state = entity.getBlockState();
        final AttachFace face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.HORIZONTAL_FACING).getHorizontalAngle();
        final LightVariant variant = ((LightBlock) state.getBlock()).getVariant();
        final Light light = entity.getLight();
        final LightModel model = this.lightModels[variant.ordinal()];
        model.setOffsets(0, 0, 0);
        model.setRotationAngles(0, 0, 0);
        final AxisAlignedBB box = model.getBounds();
        final double h = -box.minY;
        final int blockBrightness = entity.getWorld().getCombinedLight(entity.getPos(), 0);
        final int skylight = blockBrightness % 0x10000;
        final int moonlight = blockBrightness / 0x10000;

        GlStateManager.translated(0.5D, 0.5D, 0.5D);
        GlStateManager.rotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);
        if (variant.getPlacement() == LightVariant.Placement.UPRIGHT) {
            if (face == AttachFace.CEILING) {
                GlStateManager.translated(0.0D, 0.25D, 0.0D);
            } else if (face == AttachFace.WALL) {
                GlStateManager.translated(0.0D, 0.15D, 0.125D);
                this.bindTexture(FenceFastenerRenderer.TEXTURE);
                this.fastener.render();
            } else {
                GlStateManager.translated(0.0D, h - 0.5D, 0.0D);
            }
        } else {
            if (face == AttachFace.CEILING) {
                if (variant.getPlacement() == LightVariant.Placement.ONWARD) {
                    GlStateManager.rotatef(-180.0F, 1.0F, 0.0F, 0.0F);
                }
            } else if (face == AttachFace.WALL) {
                GlStateManager.rotatef(variant.getPlacement() == LightVariant.Placement.OUTWARD ? 90.0F : -90.0F, 1.0F, 0.0F, 0.0F);
            } else {
                if (variant.getPlacement() == LightVariant.Placement.OUTWARD) {
                    GlStateManager.rotatef(-180.0F, 1.0F, 0.0F, 0.0F);
                }
            }
            GlStateManager.translated(0.0D, variant.getPlacement() == LightVariant.Placement.OUTWARD ? 0.5D : h - 0.5D, 0.0D);
        }

        this.bindTexture(FastenerRenderer.TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        model.render(entity.getWorld(), light, 0.0625F, light.getLight(), moonlight, skylight, light.getBrightness(delta), 0, delta);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
