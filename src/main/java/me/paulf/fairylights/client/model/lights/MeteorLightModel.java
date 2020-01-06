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
    private final AdvancedRendererModel[] lights;

    private final AdvancedRendererModel connector;

    private final AdvancedRendererModel cap;

    private final AdvancedRendererModel rodDepthMask;

    public MeteorLightModel() {
        this.connector = new AdvancedRendererModel(this, 77, 0);
        this.connector.addBox(-1, -0.5F, -1, 2, 2, 2, 0);
        this.amutachromicParts.addChild(this.connector);
        this.cap = new AdvancedRendererModel(this, 77, 0);
        this.cap.addBox(-1, -25.45F - 0.05F, -1, 2, 1, 2, 0);
        this.amutachromicParts.addChild(this.cap);
        final int lightCount = 12;
        this.lights = new AdvancedRendererModel[lightCount];
        final float rodScale = 0.8F;
        for (int i = 0; i < lightCount; i++) {
            final AdvancedRendererModel light = new AdvancedRendererModel(this, 37, 72);
            light.addMeteorLightBox(-1, -i * 2 - 2.5F - 0.05F, -1, 2, 2, 2, i == 0 ? 0 : i == lightCount - 1 ? 1 : 2);
            light.isMeteorLightGlow = true;
            this.lights[i] = light;
            light.scaleX = light.scaleZ = rodScale;
            this.colorableParts.addChild(light);
        }
        this.rodDepthMask = new AdvancedRendererModel(this);
        this.rodDepthMask.addBox(-1, 0, -1, 2, 24, 2, 0.45F);
        this.rodDepthMask.rotateAngleX = Mth.PI;
        this.rodDepthMask.scaleX = this.rodDepthMask.scaleZ = rodScale;
        this.amutachromicParts.addChild(this.rodDepthMask);
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }

    @Override
    public void render(final World world, final Light light, final float scale, final Vec3d color, final int moonlight, final int sunlight, final float normalBrightness, final int index, final float delta) {
        if (this.hasRandomRotation()) {
            final float randomOffset = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4;
            this.colorableParts.secondaryRotateAngleY = randomOffset;
            this.amutachromicParts.secondaryRotateAngleY = randomOffset;
            this.amutachromicLitParts.secondaryRotateAngleY = randomOffset;
        }
        final float stage = light.getTwinkleTimePercent(delta) * 3 - 1;
        this.rodDepthMask.isHidden = true;
        for (int i = 0; i < this.lights.length; i++) {
            final float t = i / (float) this.lights.length;
            float brightness = t - stage > 0 ? 1 - Math.abs(t - stage) * 4 : 1 - Math.abs(t - stage);
            if (brightness < 0) {
                brightness = 0;
            }
            if (brightness > 1) {
                brightness = 1;
            }
            final float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, b, moonlight);
            GlStateManager.enableLighting();
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].isHidden = i != n;
            }
            this.amutachromicLitParts.render(scale);
            final float[] hsb = new float[3];
            Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
            hsb[2] = brightness * 0.75F + 0.25F;
            final int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            final float cr = (colorRGB >> 16 & 0xFF) / 255F;
            final float cg = (colorRGB >> 8 & 0xFF) / 255F;
            final float cb = (colorRGB & 0xFF) / 255F;
            GlStateManager.color3f(cr, cg, cb);
            this.colorableParts.render(scale);
            if (i == 0 || i == this.lights.length - 1) {
                GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
                float c = b / 255;
                if (c < 0.5F) {
                    c = 0.5F;
                }
                GlStateManager.color3f(c, c, c);
                this.connector.isHidden = i != 0;
                this.cap.isHidden = i == 0;
                this.amutachromicParts.render(scale);
                GlStateManager.disableLighting();
            }
        }
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
        GlStateManager.disableLighting();
        Minecraft.getInstance().gameRenderer.disableLightmap();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        this.colorableParts.isGlowing = true;
        GlStateManager.depthMask(false);
        for (int i = 0; i < this.lights.length; i++) {
            final float t = i / (float) this.lights.length;
            float brightness = t - stage > 0 ? 1 - Math.abs(t - stage) * 4 : 1 - Math.abs(t - stage) * 2;
            if (brightness < 0) {
                brightness = 0;
            }
            if (brightness > 1) {
                brightness = 1;
            }
            final float[] hsb = new float[3];
            Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
            if (hsb[1] > 0) {
                hsb[1] = brightness;
                hsb[2] = 1;
            }
            final int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            final float cr = (colorRGB >> 16 & 0xFF) / 255F;
            final float cg = (colorRGB >> 8 & 0xFF) / 255F;
            final float cb = (colorRGB & 0xFF) / 255F;
            GlStateManager.color4f(cr, cg, cb, brightness * 0.15F + 0.1F);
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].isHidden = i != n;
            }
            this.colorableParts.render(scale);
        }
        GlStateManager.depthMask(true);
        this.colorableParts.isGlowing = false;
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        Minecraft.getInstance().gameRenderer.enableLightmap();
        GlStateManager.disableAlphaTest();
        GlStateManager.colorMask(false, false, false, false);
        this.rodDepthMask.isHidden = false;
        this.connector.isHidden = true;
        this.cap.isHidden = true;
        this.amutachromicParts.render(scale);
        this.connector.isHidden = false;
        this.cap.isHidden = false;
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableAlphaTest();
    }
}
