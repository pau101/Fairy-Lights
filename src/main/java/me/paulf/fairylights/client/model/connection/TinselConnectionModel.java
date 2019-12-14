package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandTinselConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class TinselConnectionModel extends ConnectionModel<GarlandTinselConnection> {
	private static final RandomArray RAND = new RandomArray(9171, 32);

	private AdvancedRendererModel cordModel;

	private AdvancedRendererModel stripModel;

	private int uniquifier;

	public TinselConnectionModel() {
		cordModel = new AdvancedRendererModel(this, 62, 0);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
		stripModel = new AdvancedRendererModel(this, 62, 0);
		stripModel.addBox(-0.5F, 0, -0.5F, 1, 3, 1);
		stripModel.scaleZ = 0.5F;
	}

	@Override
	public void renderCord(GarlandTinselConnection connection, World world, int sunlight, int moonlight, float delta) {
		uniquifier = connection.hashCode();
		super.renderCord(connection, world, sunlight, moonlight, delta);
	}

	@Override
	protected void renderSegment(GarlandTinselConnection tinsel, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		int color = tinsel.getColor();
		float colorRed = ((color >> 16) & 0xFF) / 255F;
		float colorGreen = ((color >> 8) & 0xFF) / 255F;
		float colorBlue = ((color) & 0xFF) / 255F;
		GlStateManager.color3f(colorRed, colorGreen, colorBlue);
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
		GlStateManager.pushMatrix();
		GlStateManager.translated(x / 16, y / 16, z / 16);
		GlStateManager.rotatef((float) angleY * Mth.RAD_TO_DEG, 0, 1, 0);
		GlStateManager.rotatef((float) angleX * Mth.RAD_TO_DEG, 1, 0, 0);
		int rings = MathHelper.ceil(length * 4);
		for (int i = 0; i < rings; i++) {
			double t = i / (float) rings * length / 16;
			GlStateManager.pushMatrix();
			GlStateManager.translated(0, 0, t);
			float rotX = RAND.get(index + i + uniquifier) * 22;
			float rotY = RAND.get(index * 3 + i + uniquifier) * 180;
			float rotZ = RAND.get(index * 7 + i + uniquifier) * 180;
			GlStateManager.rotatef(rotZ, 0, 0, 1);
			GlStateManager.rotatef(rotY, 0, 1, 0);
			GlStateManager.rotatef(rotX, 1, 0, 0);
			stripModel.render(0.0625F);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
		GlStateManager.color3f(1, 1, 1);
	}
}
