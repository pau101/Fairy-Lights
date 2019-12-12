package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.model.AdvancedModelRenderer;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.letter.ConnectionLetterBunting;
import me.paulf.fairylights.server.fastener.connection.type.letter.Letter;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
		Minecraft.getInstance().getTextureManager().bindTexture(LETTERS);
		for (int i = 0, count = Math.min(letters.length, prevLetters.length); i < count; i++) {
			Letter letter = letters[i];
			if (Character.isWhitespace(letter.getLetter())) {
				continue;
			}
			Vec3d point = Mth.lerp(prevLetters[i].getPoint(), letter.getPoint(), delta);
			Vec3d rotation = letter.getRotation(delta);
			GlStateManager.pushMatrix();
			GlStateManager.translated(point.x / 16, point.y / 16, point.z / 16);
			GlStateManager.rotatef((float) rotation.x * Mth.RAD_TO_DEG, 0, 1, 0);
			GlStateManager.rotatef((float) rotation.y * Mth.RAD_TO_DEG, 1, 0, 0);
			GlStateManager.rotatef((float) rotation.z * Mth.RAD_TO_DEG, 0, 0, 1);
			int rgb = StyledString.getColor(letterBunting.getText().colorAt(i));
			GlStateManager.color3f((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F);
			GlStateManager.rotatef(-90, 0, 1, 0);
			GlStateManager.scalef(1, 1, 1.2F);
			GlStateManager.translated(-letter.getWidth() / 2F, -letter.getSymbolHeight() / 16F + 0.5F / 16F, 0.0625F / 2);
			FastenerRenderer.render3DTexture(letter.getSymbolWidth(), letter.getSymbolHeight(), letter.getU(), letter.getV(), 64, 64);
			GlStateManager.popMatrix();
		}
		GlStateManager.enableCull();
		GlStateManager.disableNormalize();
		GlStateManager.color3f(1, 1, 1);
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
