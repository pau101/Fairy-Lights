package com.pau101.fairylights.client.model.lights;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.model.RotationOrder;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.Light;
import com.pau101.fairylights.util.Mth;

public abstract class ModelLight extends ModelBase {
	protected final AdvancedModelRenderer colorableParts;

	// amutachromic [A - without] + [MUT - change] + [CHROM - color]
	protected final AdvancedModelRenderer amutachromicParts;

	protected final AdvancedModelRenderer amutachromicLitParts;

	public ModelLight() {
		textureWidth = textureHeight = 128;
		colorableParts = new AdvancedModelRenderer(this);
		colorableParts.setRotationOrder(RotationOrder.YXZ);
		amutachromicParts = new AdvancedModelRenderer(this);
		amutachromicParts.setRotationOrder(RotationOrder.YXZ);
		amutachromicLitParts = new AdvancedModelRenderer(this);
		amutachromicLitParts.setRotationOrder(RotationOrder.YXZ);
	}

	public boolean hasRandomRotatation() {
		return false;
	}

	public void setRotationAngles(double x, double y, double z) {
		colorableParts.rotateAngleX = (float) x;
		colorableParts.rotateAngleY = (float) y;
		colorableParts.rotateAngleZ = (float) z;
		amutachromicParts.rotateAngleX = (float) x;
		amutachromicParts.rotateAngleY = (float) y;
		amutachromicParts.rotateAngleZ = (float) z;
		amutachromicLitParts.rotateAngleX = (float) x;
		amutachromicLitParts.rotateAngleY = (float) y;
		amutachromicLitParts.rotateAngleZ = (float) z;
	}

	public void setOffsets(double x, double y, double z) {
		colorableParts.offsetX = (float) x;
		colorableParts.offsetY = (float) y;
		colorableParts.offsetZ = (float) z;
		amutachromicParts.offsetX = (float) x;
		amutachromicParts.offsetY = (float) y;
		amutachromicParts.offsetZ = (float) z;
		amutachromicLitParts.offsetX = (float) x;
		amutachromicLitParts.offsetY = (float) y;
		amutachromicLitParts.offsetZ = (float) z;
	}

	public void setAfts(float x, float y, float z) {
		colorableParts.aftMoveX = x;
		colorableParts.aftMoveY = y;
		colorableParts.aftMoveZ = z;
		amutachromicParts.aftMoveX = x;
		amutachromicParts.aftMoveY = y;
		amutachromicParts.aftMoveZ = z;
		amutachromicLitParts.aftMoveX = x;
		amutachromicLitParts.aftMoveY = y;
		amutachromicLitParts.aftMoveZ = z;
	}

	public void setScale(float scale) {
		setScales(scale, scale, scale);
	}

	public void setScales(float x, float y, float z) {
		colorableParts.scaleX = x;
		colorableParts.scaleY = y;
		colorableParts.scaleZ = z;
		amutachromicParts.scaleX = x;
		amutachromicParts.scaleY = y;
		amutachromicParts.scaleZ = z;
		amutachromicLitParts.scaleX = x;
		amutachromicLitParts.scaleY = y;
		amutachromicLitParts.scaleZ = z;
	}

	public void render(World world, Light light, float scale, Vec3d color, int moonlight, int sunlight, float brightness, int index, float delta) {
		if (hasRandomRotatation()) {
			float randomOffset = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4;
			colorableParts.secondaryRotateAngleY = randomOffset;
			amutachromicParts.secondaryRotateAngleY = randomOffset;
			amutachromicLitParts.secondaryRotateAngleY = randomOffset;
		}
		float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, b, moonlight);
		GlStateManager.enableLighting();
		amutachromicLitParts.render(scale);
		float[] hsb = new float[3];
		Color.RGBtoHSB((int) (color.xCoord * 255 + 0.5F), (int) (color.yCoord * 255 + 0.5F), (int) (color.zCoord * 255 + 0.5F), hsb);
		/*/ trippin balls
		hsb[0] = (float) ((hsb[0] + System.currentTimeMillis() / 2000D) % 1);
		hsb[1] = hsb[2] = 1;//*/
		int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * 0.75F + (brightness * 0.75F + 0.25F) * 0.25F);
		float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
		GlStateManager.color(cr, cg, cb);
		colorableParts.render(scale);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
		float c = b / 255;
		if (c < 0.5F) {
			c = 0.5F;
		}
		GlStateManager.color(c, c, c);
		amutachromicParts.render(scale);
		GlStateManager.disableLighting();
		int maxBrightness = 1;
		float expand = 1.3F;
		if (hsb[1] > 0) {
			hsb[1] = brightness;
			hsb[2] = 1;
		}
		colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
		cr = (colorRGB >> 16 & 0xFF) / 255F;
		cg = (colorRGB >> 8 & 0xFF) / 255F;
		cb = (colorRGB & 0xff) / 255F;
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.color(cr, cg, cb, brightness * 0.15F + 0.1F);
		colorableParts.isGlowing = true;
		colorableParts.render(scale);
		colorableParts.isGlowing = false;
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}
}
