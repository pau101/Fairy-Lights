package me.paulf.fairylights.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class AdvancedRendererModel extends RendererModel {
    protected int textureOffsetX;

    protected int textureOffsetY;

    public boolean compiled;

    public int displayList = -1;

    protected Model modelBase;

    public float scaleX;

    public float scaleY;

    public float scaleZ;

    public float aftMoveX;

    public float aftMoveY;

    public float aftMoveZ;

    protected RotationOrder rotationOrder;

    public float secondaryRotateAngleX;

    public float secondaryRotateAngleY;

    public float secondaryRotateAngleZ;

    protected RotationOrder secondaryRotationOrder;

    public boolean isGlowing;

    public boolean shouldntGlow;

    public boolean isMeteorLightGlow;

    public float glowExpandAmount = 0.7F;

    public AdvancedRendererModel(final Model modelBase) {
        this(modelBase, null);
    }

    public AdvancedRendererModel(final Model modelBase, final int textureOffsetX, final int textureOffsetY) {
        this(modelBase);
        this.setTextureOffset(textureOffsetX, textureOffsetY);
    }

    public AdvancedRendererModel(final Model modelBase, final String name) {
        super(modelBase, name);
        this.modelBase = modelBase;
        this.setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
        this.scaleX = this.scaleY = this.scaleZ = 1;
        this.rotationOrder = RotationOrder.ZYX;
        this.secondaryRotationOrder = RotationOrder.ZYX;
    }

    @Override
    public AdvancedRendererModel addBox(final float posX, final float posY, final float posZ, final int width, final int height, final int depth) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height, depth, 0));
        return this;
    }

    @Override
    public void addBox(final float posX, final float posY, final float posZ, final int width, final int height, final int depth, final float scale) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height, depth, scale));
    }

    @Override
    public RendererModel addBox(final float offX, final float offY, final float offZ, final int width, final int height, final int depth, final boolean mirrored) {
        return super.addBox(offX, offY, offZ, width, height, depth, mirrored);
    }

    @Override
    public RendererModel func_217178_a(String name, final float posX, final float posY, final float posZ, final int width, final int height, final int depth, final float expand, final int u, final int v) {
        name = this.boxName + "." + name;
        this.setTextureOffset(u, v);
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height, depth, expand).setBoxName(name));
        return this;
    }

    public AdvancedRendererModel add3DTexture(final float posX, final float posY, final float posZ, final int width, final int height) {
        this.cubeList.add(new Model3DTexture(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height));
        return this;
    }

    public void addMeteorLightBox(final float posX, final float posY, final float posZ, final int width, final int height, final int depth, final int type) {
        this.cubeList.add(new MeteorLightBoxModel(this, this.textureOffsetX, this.textureOffsetY, posX, posY, posZ, width, height, depth, type));
    }

    protected void compileDisplayList(final float scale) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.newList(this.displayList, GL11.GL_COMPILE);
        final BufferBuilder buf = Tessellator.getInstance().getBuffer();
        for (final ModelBox box : this.cubeList) {
            box.render(buf, scale);
        }
        GlStateManager.endList();
        this.compiled = true;
    }

    @Override
    public void postRender(final float scale) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            this.rotationOrder.rotate(this.rotateAngleX * Mth.RAD_TO_DEG, this.rotateAngleY * Mth.RAD_TO_DEG, this.rotateAngleZ * Mth.RAD_TO_DEG);
            this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
            GlStateManager.translatef(this.aftMoveX, this.aftMoveY, this.aftMoveZ);
            GlStateManager.scalef(this.scaleX, this.scaleY, this.scaleZ);
        }
    }

    private void baseRender(final float scale) {
        if (this.isGlowing && this.shouldntGlow) {
            return;
        }
        if (this.isGlowing) {
            final List<ModelBox> boxModels = this.cubeList;
            final BufferBuilder buf = Tessellator.getInstance().getBuffer();
            for (final ModelBox box : boxModels) {
                final int bri = 1;
                final float meteorExpandY = 0;
                for (int i = 0; i < bri; i++) {
                    final float width = box.posX2 - box.posX1;
                    final float height = box.posY2 - box.posY1;
                    final float depth = box.posZ2 - box.posZ1;
                    final float localExpand = this.glowExpandAmount * (i + 1);
                    final float localMeteorExpandY = meteorExpandY * (i + 1);
                    final float newWidth = width + 2 * localExpand;
                    final float newHeight = height + 2 * (this.isMeteorLightGlow ? localMeteorExpandY : localExpand);
                    final float newDepth = depth + 2 * localExpand;
                    final float scaleX = newWidth / width;
                    final float scaleY = newHeight / height;
                    final float scaleZ = newDepth / depth;
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((box.posX1 - this.glowExpandAmount - scaleX * box.posX1) / 16, (box.posY1 - (this.isMeteorLightGlow ? meteorExpandY : this.glowExpandAmount) - scaleY * box.posY1) / 16, (box.posZ1 - this.glowExpandAmount - scaleZ * box.posZ1) / 16 * (box instanceof Model3DTexture ? -1 : 1));
                    GlStateManager.scalef(scaleX, scaleY, scaleZ);
                    box.render(buf, scale);
                    GlStateManager.popMatrix();
                }
            }
        } else {
            GlStateManager.callList(this.displayList);
        }

        if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); i++) {
                final RendererModel modelRenderer = this.childModels.get(i);
                if (modelRenderer instanceof AdvancedRendererModel) {
                    ((AdvancedRendererModel) modelRenderer).isGlowing = this.isGlowing;
                }
                modelRenderer.render(scale);
            }
        }
    }

    @Override
    public void render(final float scale) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }
            GlStateManager.translatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0 && this.rotateAngleY == 0 && this.rotateAngleZ == 0) {
                if (this.rotationPointX == 0 && this.rotationPointY == 0 && this.rotationPointZ == 0) {
                    if (this.scaleX == 1 && this.scaleY == 1 && this.scaleZ == 1) {
                        GlStateManager.pushMatrix();
                        this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
                        GlStateManager.translatef(this.aftMoveX, this.aftMoveY, this.aftMoveZ);
                        this.baseRender(scale);
                        GlStateManager.popMatrix();
                    } else {
                        GlStateManager.pushMatrix();
                        this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
                        GlStateManager.translatef(this.aftMoveX, this.aftMoveY, this.aftMoveZ);
                        GlStateManager.scalef(this.scaleX, this.scaleY, this.scaleZ);
                        this.baseRender(scale);
                        GlStateManager.popMatrix();
                    }
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                    this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
                    GlStateManager.translatef(this.aftMoveX, this.aftMoveY, this.aftMoveZ);
                    GlStateManager.scalef(this.scaleX, this.scaleY, this.scaleZ);
                    this.baseRender(scale);
                    GlStateManager.popMatrix();
                }
            } else {
                GlStateManager.pushMatrix();
                GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                this.rotationOrder.rotate(this.rotateAngleX * Mth.RAD_TO_DEG, this.rotateAngleY * Mth.RAD_TO_DEG, this.rotateAngleZ * Mth.RAD_TO_DEG);
                this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
                GlStateManager.translatef(this.aftMoveX, this.aftMoveY, this.aftMoveZ);
                GlStateManager.scalef(this.scaleX, this.scaleY, this.scaleZ);
                this.baseRender(scale);
                GlStateManager.popMatrix();
            }
            GlStateManager.translatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }
    }

    @Override
    public void renderWithRotation(final float scale) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(scale);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            this.rotationOrder.rotate(this.rotateAngleX * Mth.RAD_TO_DEG, this.rotateAngleY * Mth.RAD_TO_DEG, this.rotateAngleZ * Mth.RAD_TO_DEG);
            this.secondaryRotationOrder.rotate(this.secondaryRotateAngleX * Mth.RAD_TO_DEG, this.secondaryRotateAngleY * Mth.RAD_TO_DEG, this.secondaryRotateAngleZ * Mth.RAD_TO_DEG);
            GlStateManager.scalef(this.scaleX, this.scaleY, this.scaleZ);
            this.baseRender(scale);
            GlStateManager.popMatrix();
        }
    }

    public void setRotationPoint(final double rotationPointX, final double rotationPointY, final double rotationPointZ) {
        this.setRotationPoint((float) rotationPointX, (float) rotationPointY, (float) rotationPointZ);
    }

    public void setRotationAngles(final double rotateAngleX, final double rotateAngleY, final double rotateAngleZ) {
        this.rotateAngleX = (float) rotateAngleX;
        this.rotateAngleY = (float) rotateAngleY;
        this.rotateAngleZ = (float) rotateAngleZ;
    }

    @Override
    public AdvancedRendererModel setTextureOffset(final int textureOffsetX, final int textureOffsetY) {
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        return this;
    }

    @Override
    public AdvancedRendererModel setTextureSize(final int textureWidth, final int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    public void setRotationOrder(final RotationOrder rotationOrder) {
        this.rotationOrder = rotationOrder;
    }

    public void setSecondaryRotationOrder(final RotationOrder secondaryRotationOrder) {
        this.secondaryRotationOrder = secondaryRotationOrder;
    }
}
