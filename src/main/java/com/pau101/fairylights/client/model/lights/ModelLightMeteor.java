package com.pau101.fairylights.client.model.lights;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.connection.Light;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ModelLightMeteor extends ModelLight {
	private AdvancedModelRenderer[] lights;

	private AdvancedModelRenderer connector;

	private AdvancedModelRenderer cap;

	private AdvancedModelRenderer rodDepthMask;

	public ModelLightMeteor() {
		connector = new AdvancedModelRenderer(this, 77, 0);
		connector.addBox(-1, -0.5F, -1, 2, 2, 2, 0);
		amutachromicParts.addChild(connector);
		cap = new AdvancedModelRenderer(this, 77, 0);
		cap.addBox(-1, -25.45F + 0.05F, -1, 2, 1, 2, 0);
		amutachromicParts.addChild(cap);
		int lightCount = 12;
		lights = new AdvancedModelRenderer[lightCount];
		float rodScale = 0.8F;
		for (int i = 0; i < lightCount; i++) {
			AdvancedModelRenderer light = new AdvancedModelRenderer(this, 37, 72);
			light.addMeteorLightBox(-1, -i * 2 - 2.5F + 0.05F, -1, 2, 2, 2, i == 0 ? 0 : i == lightCount - 1 ? 1 : 2);
			light.isMeteorLightGlow = true;
			lights[i] = light;
			light.scaleX = light.scaleZ = rodScale;
			colorableParts.addChild(light);
		}
		rodDepthMask = new AdvancedModelRenderer(this);
		rodDepthMask.addBox(-1, 0, -1, 2, 24, 2, 0.45F);
		rodDepthMask.rotateAngleX = MathUtils.PI;
		rodDepthMask.scaleX = rodDepthMask.scaleZ = rodScale;
		amutachromicParts.addChild(rodDepthMask);
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}

	@Override
	public boolean shouldParallelCord() {
		return false;
	}

	public void render(World world, Light light, float scale, Vector3f color, int moonlight, int sunlight, float normalBrightness, int index, float delta) {
		if (hasRandomRotatation()) {
			float randomOffset = MathUtils.modf(MathUtils.hash(index) * MathUtils.DEG_TO_RAD, MathUtils.TAU) + MathUtils.PI / 4;
			colorableParts.secondaryRotateAngleY = randomOffset;
			amutachromicParts.secondaryRotateAngleY = randomOffset;
		}
		float stage = light.getTwinkleTimePercent(delta) * 3 - 1;
		rodDepthMask.isHidden = true;
		for (int i = 0; i < lights.length; i++) {
			float t = i / (float) lights.length;
			float brightness = t - stage > 0 ? 1 - Math.abs(t - stage) * 4 : 1 - Math.abs(t - stage);
			if (brightness < 0) {
				brightness = 0;
			}
			if (brightness > 1) {
				brightness = 1;
			}
			float[] hsb = new float[3];
			Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
			hsb[2] = brightness * 0.75F + 0.25F;
			int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
			GL11.glColor3f(cr, cg, cb);
			float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, b, moonlight);
			GL11.glEnable(GL11.GL_LIGHTING);
			for (int n = 0; n < lights.length; n++) {
				lights[n].isHidden = i != n;
			}
			colorableParts.render(scale);
			if (i == 0 || i == lights.length - 1) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
				float c = b / 255;
				if (c < 0.5F) {
					c = 0.5F;
				}
				GL11.glColor3f(c, c, c);
				connector.isHidden = i != 0;
				cap.isHidden = i == 0;
				amutachromicParts.render(scale);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
		}
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
		GL11.glDisable(GL11.GL_LIGHTING);
		Minecraft.getMinecraft().entityRenderer.disableLightmap(delta);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		colorableParts.isGlowing = true;
		GL11.glDepthMask(false);
		for (int i = 0; i < lights.length; i++) {
			float t = i / (float) lights.length;
			float brightness = t - stage > 0 ? 1 - Math.abs(t - stage) * 4 : 1 - Math.abs(t - stage) * 2;
			if (brightness < 0) {
				brightness = 0;
			}
			if (brightness > 1) {
				brightness = 1;
			}
			float[] hsb = new float[3];
			Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
			hsb[2] = brightness * 0.75F + 0.25F;
			int maxBrightness = 1;
			float expand = 1.3F;
			Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
			if (hsb[1] > 0) {
				hsb[1] = brightness;
				hsb[2] = 1;
			}
			int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
			cr = (colorRGB >> 16 & 0xFF) / 255F;
			cg = (colorRGB >> 8 & 0xFF) / 255F;
			cb = (colorRGB & 0xff) / 255F;
			GL11.glColor4f(cr, cg, cb, brightness * 0.15F + 0.1F);
			for (int n = 0; n < lights.length; n++) {
				lights[n].isHidden = i != n;
			}
			colorableParts.render(scale);
		}
		GL11.glDepthMask(true);
		colorableParts.isGlowing = false;
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		Minecraft.getMinecraft().entityRenderer.enableLightmap(delta);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glColorMask(false, false, false, false);
		rodDepthMask.isHidden = false;
		connector.isHidden = true;
		cap.isHidden = true;
		amutachromicParts.render(scale);
		connector.isHidden = false;
		cap.isHidden = false;
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}
}
