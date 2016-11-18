package com.pau101.fairylights.client.model.connection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.ClientProxy;
import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.renderer.FastenerRenderer;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.type.letter.ConnectionLetterBunting;
import com.pau101.fairylights.server.fastener.connection.type.letter.Letter;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.styledstring.StyledString;

public class ModelConnectionLetterBunting extends ModelConnection<ConnectionLetterBunting> {
	private static final ResourceLocation LETTERS = new ResourceLocation(FairyLights.ID, "textures/entity/letters.png");

	private AdvancedModelRenderer cordModel;

	public ModelConnectionLetterBunting() {
		cordModel = new AdvancedModelRenderer(this, 0, 17);
		cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
		cordModel.scaleX = 0.9F;
		cordModel.scaleY = 0.9F;
	}

	@Override
	public boolean hasTexturedRender() {
		return true;
	}

	@Override
	public void renderTexturePass(Fastener<?> fastener, ConnectionLetterBunting letterBunting, World world, int skylight, int moonlight, float delta) {
		Letter[] letters = letterBunting.getLetters();
		Letter[] prevLetters = letterBunting.getPrevLetters();
		GlStateManager.disableCull();
		GlStateManager.enableNormalize();
		Minecraft.getMinecraft().getTextureManager().bindTexture(LETTERS);
		for (int i = 0, count = Math.min(letters.length, prevLetters.length); i < count; i++) {
			Letter letter = letters[i];
			if (Character.isWhitespace(letter.getLetter())) {
				continue;
			}
			Vec3d point = Mth.lerp(prevLetters[i].getPoint(), letter.getPoint(), delta);
			Vec3d rotation = letter.getRotation(delta);
			GlStateManager.pushMatrix();
			GlStateManager.translate(point.xCoord / 16, point.yCoord / 16, point.zCoord / 16);
			GlStateManager.rotate((float) rotation.xCoord * Mth.RAD_TO_DEG, 0, 1, 0);
			GlStateManager.rotate((float) rotation.yCoord * Mth.RAD_TO_DEG, 1, 0, 0);
			GlStateManager.rotate((float) rotation.zCoord * Mth.RAD_TO_DEG, 0, 0, 1);
			int rgb = StyledString.getColor(ClientProxy.recoloredFont, letterBunting.getText().colorAt(i));
			GlStateManager.color((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F);
			GlStateManager.rotate(-90, 0, 1, 0);
			GlStateManager.scale(1, 1, 1.2F);
			GlStateManager.translate(-letter.getWidth() / 2F, -letter.getSymbolHeight() / 16F + 0.5F / 16F, 0.0625F / 2);
			FastenerRenderer.render3DTexture(letter.getSymbolWidth(), letter.getSymbolHeight(), letter.getU(), letter.getV(), 64, 64);
			GlStateManager.popMatrix();
		}
		GlStateManager.enableCull();
		GlStateManager.disableNormalize();
		GlStateManager.color(1, 1, 1);
	}

	@Override
	protected void renderSegment(ConnectionLetterBunting connection, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
	}
}
