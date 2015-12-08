package com.pau101.fairylights.client.model.connection;

import java.util.Random;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.renderer.ConnectionRenderer;
import com.pau101.fairylights.connection.ConnectionLogicGarland;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.tileentity.connection.ConnectionPlayer;
import com.pau101.fairylights.util.MathUtils;

public class ModelConnectionGarland extends ModelConnection<ConnectionLogicGarland> {
	private static final int RING_COUNT = 8;

	private static final float RINGS_PER_METER = 4;

	private static final float[] RANDOM_VALUES = new float[RING_COUNT * 4];

	private AdvancedModelRenderer cordModel;

	static {
		// #1449202134 Here's a little cookie challenge, find the significance of 8,411!
		Random random = new Random(8411);
		for (int i = 0; i < RANDOM_VALUES.length; i++) {
			RANDOM_VALUES[i] = random.nextFloat() * 2 - 1;
		}
	}

	private int ringId = -1;

	private int uniquifier;

	public ModelConnectionGarland() {
		cordModel = new AdvancedModelRenderer(this, 39, 0);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
	}

	private void generateGarlandRings() {
		ringId = GLAllocation.generateDisplayLists(RING_COUNT);
		for (int i = 0; i < RING_COUNT; i++) {
			GL11.glNewList(ringId + i, GL11.GL_COMPILE);
			ConnectionRenderer.render3DTexture(8, 8, i * 8, 64);
			GL11.glEndList();
		}
	}

	@Override
	public void renderCord(ConnectionLogicGarland connectionLogic, World world, int sunlight, int moonlight, float delta) {
		Connection connection = connectionLogic.getConnection();
		TileEntityConnectionFastener fastener = connection.getFastener();
		uniquifier = (fastener.yCoord + fastener.zCoord * 31) * 31 + fastener.xCoord;
		if (connection instanceof ConnectionPlayer) {
			uniquifier = uniquifier * 31 + ((ConnectionPlayer) connection).getPlayerUUID().hashCode();
		} else {
			uniquifier = uniquifier * 31 + connection.getTo().hashCode();
		}
		super.renderCord(connectionLogic, world, sunlight, moonlight, delta);
	}

	@Override
	protected void renderSegment(ConnectionLogicGarland garland, int index, float angleX, float angleY, float length, float x, float y, float z, float delta) {
		if (ringId == -1) {
			generateGarlandRings();
		}
		cordModel.rotateAngleX = angleX;
		cordModel.rotateAngleY = angleY;
		cordModel.scaleZ = length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
		GL11.glPushMatrix();
		GL11.glTranslatef(x / 16, y / 16, z / 16);
		GL11.glRotatef(angleY * MathUtils.RAD_TO_DEG, 0, 1, 0);
		GL11.glRotatef(angleX * MathUtils.RAD_TO_DEG, 1, 0, 0);
		int rings = MathHelper.ceiling_float_int(length * RINGS_PER_METER / 16) + 1;
		for (int i = 0; i < rings; i++) {
			float t = i / (float) rings * length / 16;
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, t);
			float rotZ = rand(index + i + uniquifier) * 45;
			float rotY = rand(index + i + 8 + uniquifier) * 60 + 90;
			GL11.glPushMatrix();
			GL11.glRotatef(rotZ, 0, 0, 1);
			GL11.glRotatef(rotY, 0, 1, 0);
			GL11.glTranslatef(-4 / 16F, -4 / 16F, -0.5F / 16);
			int ring = ringId + index % RING_COUNT;
			GL11.glCallList(ring);
			GL11.glPopMatrix();
			GL11.glRotatef(rotZ + 90, 0, 0, 1);
			GL11.glRotatef(rotY, 0, 1, 0);
			GL11.glScalef(7 / 8F, 7 / 8F, 7 / 8F);
			GL11.glTranslatef(-4 / 16F, -4 / 16F, -0.5F / 16);
			GL11.glCallList(ring);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

	private static float rand(int index) {
		return RANDOM_VALUES[MathUtils.modi(index, RANDOM_VALUES.length)];
	}
}
