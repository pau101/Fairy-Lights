package com.pau101.fairylights.client.model.connection;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.connection.ConnectionLogicTinsel;
import com.pau101.fairylights.util.MathUtils;

public class ModelConnectionTinsel extends ModelConnection<ConnectionLogicTinsel> {
	private AdvancedModelRenderer cordModel;

	private AdvancedModelRenderer stripModel;

	private float step;

	public ModelConnectionTinsel() {
		cordModel = new AdvancedModelRenderer(this, 62, 0);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
		stripModel = new AdvancedModelRenderer(this, 62, 0);
		stripModel.addBox(-0.5F, 0, -0.5F, 1, 3, 1);
		stripModel.scaleZ = 0.5F;
	}

	@Override
	public void renderCord(ConnectionLogicTinsel connectionLogic, World world, int sunlight, int moonlight, float delta) {
		step = 0;
		super.renderCord(connectionLogic, world, sunlight, moonlight, delta);
	}

	@Override
	protected void renderSegment(ConnectionLogicTinsel tinsel, int index, float angleX, float angleY, float length, float x, float y, float z, float delta) {
		int color = tinsel.getColor();
		float colorRed = ((color >> 16) & 0xFF) / 255F;
		float colorGreen = ((color >> 8) & 0xFF) / 255F;
		float colorBlue = ((color) & 0xFF) / 255F;
		GL11.glColor3f(colorRed, colorGreen, colorBlue);
		cordModel.rotateAngleX = angleX;
		cordModel.rotateAngleY = angleY;
		cordModel.scaleZ = length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
		GL11.glPushMatrix();
		GL11.glTranslatef(x / 16, y / 16, z / 16);
		GL11.glRotatef(angleY * MathUtils.RAD_TO_DEG, 0, 1, 0);
		GL11.glRotatef(angleX * MathUtils.RAD_TO_DEG, 1, 0, 0);
		int rings = MathHelper.ceiling_float_int(length * 4);
		for (int i = 0; i < rings; i++) {
			float t = i / (float) rings * length / 16;
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, t);
			float pos = (step + i / (float) rings * length);
			float rotZ = pos * 361;
			float rotY = pos * 90;
			float rotX = (MathHelper.sin(step + i / (float) rings * length) * 2 - 1) * 20;
			GL11.glRotatef(rotZ, 0, 0, 1);
			GL11.glRotatef(rotY, 0, 1, 0);
			GL11.glRotatef(rotX, 1, 0, 0);
			stripModel.render(0.0625F);
			GL11.glPopMatrix();
		}
		step += length;
		GL11.glPopMatrix();
		GL11.glColor3f(1, 1, 1);
	}
}
