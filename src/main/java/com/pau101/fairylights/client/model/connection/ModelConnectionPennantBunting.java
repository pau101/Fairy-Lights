package com.pau101.fairylights.client.model.connection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.model.RotationOrder;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.type.pennant.ConnectionPennantBunting;
import com.pau101.fairylights.server.fastener.connection.type.pennant.Pennant;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.styledstring.StyledString;

public class ModelConnectionPennantBunting extends ModelConnection<ConnectionPennantBunting> {
	private AdvancedModelRenderer cordModel;

	private AdvancedModelRenderer[] pennantModels = { createPennant(63), createPennant(72), createPennant(81) };

	public ModelConnectionPennantBunting() {
		cordModel = new AdvancedModelRenderer(this, 0, 17);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
		cordModel.scaleX = 1.5F;
		cordModel.scaleY = 1.5F;
	}

	private AdvancedModelRenderer createPennant(int u) {
		AdvancedModelRenderer pennant = new AdvancedModelRenderer(this, u, 16);
		pennant.add3DTexture(-4.5F, -10, 0.5F, 9, 10);
		pennant.setRotationOrder(RotationOrder.YXZ);
		pennant.secondaryRotateAngleY = Mth.HALF_PI;
		return pennant;
	}

	@Override
	public boolean hasTexturedRender() {
		return true;
	}

	@Override
	public void render(Fastener<?> fastener, ConnectionPennantBunting bunting, World world, int skylight, int moonlight, float delta) {
		super.render(fastener, bunting, world, skylight, moonlight, delta);
		Pennant[] pennants = bunting.getFeatures();
		Pennant[] prevPennants = bunting.getPrevFeatures();
		GlStateManager.disableCull();
		for (int i = 0, count = Math.min(pennants.length, prevPennants.length); i < count; i++) {
			AdvancedModelRenderer model = preparePennantModel(pennants, prevPennants, i, delta);
			int rgb = pennants[i].getColor();
			GlStateManager.color(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, (rgb & 0xFF) / 255F);
			model.render(0.0625F);
		}
		GlStateManager.enableCull();
		GlStateManager.color(1, 1, 1);
	}

	@Override
	public void renderTexturePass(Fastener<?> fastener, ConnectionPennantBunting bunting, World world, int skylight, int moonlight, float delta) {
		Pennant[] pennants = bunting.getFeatures();
		Pennant[] prevPennants = bunting.getPrevFeatures();
		StyledString text = bunting.getText();
		int pennantCount = Math.min(pennants.length, prevPennants.length);
		int offset;
		if (text.length() > pennantCount) {
			int over = text.length() - pennantCount;
			int lower = over / 2;
			text = text.substring(lower, text.length() - over + lower);
			offset = 0;
		} else {
			offset = pennantCount / 2 - text.length() / 2;
		}
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		for (int i = 0; i < text.length(); i++) {
			int pennantIndex = i + offset;
			StyledString chrA = text.substring(i, i + 1);
			StyledString chrB = text.substring(text.length() - i - 1, text.length() - i);
			String charAStr = chrA.toString();
			String charBStr = chrB.toString();
			AdvancedModelRenderer model = preparePennantModel(pennants, prevPennants, pennantIndex, delta);
			GlStateManager.pushMatrix();
			model.postRender(0.0625F);
			float s = 0.03075F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -0.25F, -0.04F);
			GlStateManager.scale(-s, -s, s);
			GlStateManager.translate(-font.getStringWidth(charAStr) / 2F + 0.5F, -4, 0);
			GlStateManager.glNormal3f(0, 0, 1);
			font.drawString(charAStr, 0, 0, 0xFFFFFFFF);
			GlStateManager.popMatrix();
			GlStateManager.translate(0, -0.25F, 0.04F);
			GlStateManager.scale(s, -s, s);
			GlStateManager.translate(-font.getStringWidth(charBStr) / 2F + 0.5F, -4, 0);
			GlStateManager.glNormal3f(0, 0, -1);
			font.drawString(charBStr, 0, 0, 0xFFFFFFFF);
			GlStateManager.popMatrix();
		}
	}

	private AdvancedModelRenderer preparePennantModel(Pennant[] pennants, Pennant[] prevPennants, int index, float delta) {
		Pennant pennant = pennants[index];
		Vec3d point = Mth.lerp(prevPennants[index].getPoint(), pennant.getPoint(), delta);
		Vec3d rotation = Mth.lerpAngles(prevPennants[index].getRotation(), pennant.getRotation(), delta);
		AdvancedModelRenderer model = pennantModels[index % pennantModels.length];
		model.setRotationPoint(point.xCoord, point.yCoord, point.zCoord);
		model.setRotationAngles(rotation.yCoord, rotation.xCoord, rotation.zCoord);
		return model;
	}

	@Override
	protected void renderSegment(ConnectionPennantBunting connection, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
	}
}
