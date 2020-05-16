package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class LightBlockEntityRenderer extends TileEntityRenderer<LightBlockEntity> {
    public LightBlockEntityRenderer(final TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    static class FastenerModel extends Model {
        final ModelRenderer model;

        FastenerModel() {
            super(RenderType::getEntityCutoutNoCull);
            this.textureWidth = 32;
            this.textureHeight = 32;
            this.model = new ModelRenderer(this, 0, 12);
            this.model.addBox(-1.0F, -1.0F, 0.05F, 2, 2, 8, -0.05F);
        }

        @Override
        public void render(final MatrixStack p_225598_1_, final IVertexBuilder p_225598_2_, final int p_225598_3_, final int p_225598_4_, final float p_225598_5_, final float p_225598_6_, final float p_225598_7_, final float p_225598_8_) {
            this.model.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        }
    }

    FastenerModel fastener = new FastenerModel();

    @Override
    public void render(final LightBlockEntity entity, final float p_225616_2_, final MatrixStack stack, final IRenderTypeBuffer p_225616_4_, final int p_225616_5_, final int p_225616_6_) {
        /*stack.push();

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

        stack.translate(0.5D, 0.5D, 0.5D);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotation));
        if (variant.getPlacement() == LightVariant.Placement.UPRIGHT) {
            if (face == AttachFace.CEILING) {
                GlStateManager.translated(0.0D, 0.25D, 0.0D);
                GlStateManager.pushMatrix();
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                this.bindTexture(FenceFastenerRenderer.TEXTURE);
                this.fastener.render();
                GlStateManager.popMatrix();
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


        model.render(entity.getWorld(), light, 0.0625F, light.getLight(), moonlight, skylight, light.getBrightness(delta), 0, delta);

        stack.pop();*/
    }
}
