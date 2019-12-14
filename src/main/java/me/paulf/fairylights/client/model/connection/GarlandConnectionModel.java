package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandVineConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public final class GarlandConnectionModel extends ConnectionModel<GarlandVineConnection> {
	private static final int RING_COUNT = 8;

	private static final float RINGS_PER_METER = 4;

	private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

	private AdvancedRendererModel cordModel;

	private int ringId = -1;

	private int uniquifier;

	public GarlandConnectionModel() {
		cordModel = new AdvancedRendererModel(this, 39, 0);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
	}

	private void generateGarlandRings() {
		ringId = GLAllocation.generateDisplayLists(RING_COUNT);
		for (int i = 0; i < RING_COUNT; i++) {
			GlStateManager.newList(ringId + i, GL11.GL_COMPILE);
			FastenerRenderer.render3DTexture(8, 8, i * 8, 64);
			GlStateManager.endList();
		}
	}

	@Override
	public void renderCord(GarlandVineConnection connection, World world, int sunlight, int moonlight, float delta) {
		uniquifier = connection.hashCode();
		super.renderCord(connection, world, sunlight, moonlight, delta);
	}

	@Override
	protected void renderSegment(GarlandVineConnection garland, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		if (ringId == -1) {
			generateGarlandRings();
		}
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
		GlStateManager.pushMatrix();
		GlStateManager.translated(x / 16, y / 16, z / 16);
		GlStateManager.rotatef((float) angleY * Mth.RAD_TO_DEG, 0, 1, 0);
		GlStateManager.rotatef((float) angleX * Mth.RAD_TO_DEG, 1, 0, 0);
		int rings = MathHelper.ceil(length * RINGS_PER_METER / 16) + 1;
		for (int i = 0; i < rings; i++) {
			double t = i / (float) rings * length / 16;
			GlStateManager.pushMatrix();
			GlStateManager.translated(0, 0, t);
			float rotZ = RAND.get(index + i + uniquifier) * 45;
			float rotY = RAND.get(index + i + 8 + uniquifier) * 60 + 90;
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(rotZ, 0, 0, 1);
			GlStateManager.rotatef(rotY, 0, 1, 0);
			GlStateManager.translated(-4 / 16F, -4 / 16F, -0.5F / 16);
			int ring = ringId + index % RING_COUNT;
			GlStateManager.callList(ring);
			GlStateManager.popMatrix();
			GlStateManager.rotatef(rotZ + 90, 0, 0, 1);
			GlStateManager.rotatef(rotY, 0, 1, 0);
			GlStateManager.scalef(7 / 8F, 7 / 8F, 7 / 8F);
			GlStateManager.translatef(-4 / 16F, -4 / 16F, -0.5F / 16);
			GlStateManager.callList(ring);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
}
