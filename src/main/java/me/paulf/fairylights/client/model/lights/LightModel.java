package me.paulf.fairylights.client.model.lights;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.AABBBuilder;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public abstract class LightModel extends Model {
	protected final AdvancedRendererModel colorableParts;

	// amutachromic [A - without] + [MUT - change] + [CHROM - color]
	protected final AdvancedRendererModel amutachromicParts;

	protected final AdvancedRendererModel amutachromicLitParts;

	public LightModel() {
		textureWidth = textureHeight = 128;
		colorableParts = new AdvancedRendererModel(this);
		colorableParts.setRotationOrder(RotationOrder.YXZ);
		amutachromicParts = new AdvancedRendererModel(this);
		amutachromicParts.setRotationOrder(RotationOrder.YXZ);
		amutachromicLitParts = new AdvancedRendererModel(this);
		amutachromicLitParts.setRotationOrder(RotationOrder.YXZ);
	}

	public boolean hasRandomRotation() {
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

	public AxisAlignedBB getBounds() {
		final MatrixStack matrix = new MatrixStack();
		final AABBBuilder builder = new AABBBuilder();
		buildBounds(matrix, amutachromicParts, 0.0625F, builder);
		buildBounds(matrix, colorableParts, 0.0625F, builder);
		return builder.build();
	}

	private void buildBounds(final MatrixStack matrix, final RendererModel bone, final float scale, final AABBBuilder builder) {
		matrix.push();
		matrix.translate(bone.rotationPointX * scale, bone.rotationPointY * scale, bone.rotationPointZ * scale);
		if (bone.rotateAngleZ != 0.0F) {
			matrix.rotate(bone.rotateAngleZ, 0.0F, 0.0F, 1.0F);
		}
		if (bone.rotateAngleY != 0.0F) {
			matrix.rotate(bone.rotateAngleY, 0.0F, 1.0F, 0.0F);
		}
		if (bone.rotateAngleX != 0.0F) {
			matrix.rotate(bone.rotateAngleX, 1.0F, 0.0F, 0.0F);
		}
		for (final ModelBox box : bone.cubeList) {
			final float x1 = box.posX1 * scale;
			final float y1 = box.posY1 * scale;
			final float z1 = box.posZ1 * scale;
			final float x2 = box.posX2 * scale;
			final float y2 = box.posY2 * scale;
			final float z2 = box.posZ2 * scale;
			for (Vec3d v : new Vec3d[] {
				new Vec3d(x1, y1, z1),
				new Vec3d(x2, y1, z1),
				new Vec3d(x1, y1, z2),
				new Vec3d(x2, y1, z2),
				new Vec3d(x1, y2, z1),
				new Vec3d(x2, y2, z1),
				new Vec3d(x1, y2, z2),
				new Vec3d(x2, y2, z2)
			}) {
				builder.include(matrix.transform(v));
			}
		}
		if (bone.childModels != null) {
			for (final RendererModel child : bone.childModels) {
				buildBounds(matrix, child, scale, builder);
			}
		}
		matrix.pop();
	}

	public void prepare(int index) {
		if (hasRandomRotation()) {
			float randomOffset = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4;
			colorableParts.secondaryRotateAngleY = randomOffset;
			amutachromicParts.secondaryRotateAngleY = randomOffset;
			amutachromicLitParts.secondaryRotateAngleY = randomOffset;
		}
	}

	public void render(World world, Light light, float scale, Vec3d color, int moonlight, int sunlight, float brightness, int index, float delta) {
		float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, b, moonlight);
		GlStateManager.enableLighting();
		amutachromicLitParts.render(scale);
		float[] hsb = new float[3];
		Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
		/*/ trippin balls
		hsb[0] = (float) ((hsb[0] + System.currentTimeMillis() / 2000D) % 1);
		hsb[1] = hsb[2] = 1;//*/
		int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * 0.75F + (brightness * 0.75F + 0.25F) * 0.25F);
		float cr = (colorRGB >> 16 & 0xFF) / 255F, cg = (colorRGB >> 8 & 0xFF) / 255F, cb = (colorRGB & 0xFF) / 255F;
		GlStateManager.color3f(cr, cg, cb);
		colorableParts.render(scale);
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
		float c = b / 255;
		if (c < 0.5F) {
			c = 0.5F;
		}
		GlStateManager.color3f(c, c, c);
		amutachromicParts.render(scale);
		GlStateManager.disableLighting();
		Minecraft.getInstance().gameRenderer.disableLightmap();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.color4f(cr, cg, cb, brightness * 0.15F + 0.1F);
		colorableParts.isGlowing = true;
		colorableParts.render(scale);
		colorableParts.isGlowing = false;
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		Minecraft.getInstance().gameRenderer.enableLightmap();
	}
}
