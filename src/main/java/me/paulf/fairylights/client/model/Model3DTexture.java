package me.paulf.fairylights.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;

public final class Model3DTexture extends ModelBox {
    public int width;

    public int height;

    public int textureOffsetX;

    public int textureOffsetY;

    public Model3DTexture(final RendererModel model, final int textureOffsetX, final int textureOffsetY, final float posX, final float posY, final float posZ, final int width, final int height) {
        super(model, 0, 0, posX, posY, posZ, width, height, 1, 0);
        this.width = width;
        this.height = height;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
    }

    public Model3DTexture(final RendererModel model, final int textureOffsetX, final int textureOffsetY, final int width, final int height) {
        this(model, textureOffsetX, textureOffsetY, 0, 0, 0, width, height);
    }

    @Override
    public void render(final BufferBuilder buf, final float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(this.posX1 * scale, this.posY1 * scale, this.posZ1 * scale);
        FastenerRenderer.render3DTexture(this.width, this.height, this.textureOffsetX, this.textureOffsetY);
        GlStateManager.popMatrix();
    }
}
