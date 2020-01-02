package me.paulf.fairylights.client.model.lights;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public final class MeteorLightModel extends LightModel {
	private AdvancedRendererModel[] lights;

	private AdvancedRendererModel connector;

	private AdvancedRendererModel cap;

	private AdvancedRendererModel rodDepthMask;

	public MeteorLightModel() {
		connector = new AdvancedRendererModel(this, 77, 0);
		connector.addBox(-1, -0.5F, -1, 2, 2, 2, 0);
		amutachromicParts.addChild(connector);
		cap = new AdvancedRendererModel(this, 77, 0);
		cap.addBox(-1, -25.45F - 0.05F, -1, 2, 1, 2, 0);
		amutachromicParts.addChild(cap);
		int lightCount = 12;
		lights = new AdvancedRendererModel[lightCount];
		float rodScale = 0.8F;
		for (int i = 0; i < lightCount; i++) {
			AdvancedRendererModel light = new AdvancedRendererModel(this, 37, 72);
			light.addMeteorLightBox(-1, -i * 2 - 2.5F - 0.05F, -1, 2, 2, 2, i == 0 ? 0 : i == lightCount - 1 ? 1 : 2);
			light.isMeteorLightGlow = true;
			lights[i] = light;
			light.scaleX = light.scaleZ = rodScale;
			colorableParts.addChild(light);
		}
		rodDepthMask = new AdvancedRendererModel(this);
		rodDepthMask.addBox(-1, 0, -1, 2, 24, 2, 0.45F);
		rodDepthMask.rotateAngleX = Mth.PI;
		rodDepthMask.scaleX = rodDepthMask.scaleZ = rodScale;
		amutachromicParts.addChild(rodDepthMask);
	}

	@Override
	public boolean hasRandomRotation() {
		return true;
	}

	@Override
	public void render(World world, Light light, float scale, Vec3d color, int moonlight, int sunlight, float normalBrightness, int index, float delta) {
		if (hasRandomRotation()) {
			float randomOffset = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4;
			colorableParts.secondaryRotateAngleY = randomOffset;
			amutachromicParts.secondaryRotateAngleY = randomOffset;
			amutachromicLitParts.secondaryRotateAngleY = randomOffset;
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
			float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, b, moonlight);
			GlStateManager.enableLighting();
			for (int n = 0; n < lights.length; n++) {
				lights[n].isHidden = i != n;
			}
			amutachromicLitParts.render(scale);
			float[] hsb = new float[3];
			Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
			hsb[2] = brightness * 0.75F + 0.25F;
			int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
			GlStateManager.color3f(cr, cg, cb);
			colorableParts.render(scale);
			if (i == 0 || i == lights.length - 1) {
				GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
				float c = b / 255;
				if (c < 0.5F) {
					c = 0.5F;
				}
				GlStateManager.color3f(c, c, c);
				connector.isHidden = i != 0;
				cap.isHidden = i == 0;
				amutachromicParts.render(scale);
				GlStateManager.disableLighting();
			}
		}
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
		GlStateManager.disableLighting();
		Minecraft.getInstance().gameRenderer.disableLightmap();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		colorableParts.isGlowing = true;
		GlStateManager.depthMask(false);
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
			if (hsb[1] > 0) {
				hsb[1] = brightness;
				hsb[2] = 1;
			}
			int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
			GlStateManager.color4f(cr, cg, cb, brightness * 0.15F + 0.1F);
			for (int n = 0; n < lights.length; n++) {
				lights[n].isHidden = i != n;
			}
			colorableParts.render(scale);
		}
		GlStateManager.depthMask(true);
		colorableParts.isGlowing = false;
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		Minecraft.getInstance().gameRenderer.enableLightmap();
		GlStateManager.disableAlphaTest();
		GlStateManager.colorMask(false, false, false, false);
		rodDepthMask.isHidden = false;
		connector.isHidden = true;
		cap.isHidden = true;
		amutachromicParts.render(scale);
		connector.isHidden = false;
		cap.isHidden = false;
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableAlphaTest();
	}
}
