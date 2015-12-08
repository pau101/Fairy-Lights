package com.pau101.fairylights.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.model.ModelFairyLights;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;

public class TileEntityFairyLightsFastenerRenderer extends TileEntitySpecialRenderer {
	private ModelFairyLights fairyLights;

	private ResourceLocation texture;

	public TileEntityFairyLightsFastenerRenderer() {
		fairyLights = new ModelFairyLights();
		texture = new ResourceLocation(FairyLights.MODID, "textures/entity/fairy_lights.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float delta) {
		renderTileEntityAt((TileEntityFairyLightsFastener) tileEntity, x, y, z, delta);
	}

	private void renderTileEntityAt(TileEntityFairyLightsFastener fairyLightsFastener, double x, double y, double z, float delta) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		bindTexture(texture);
		if (fairyLightsFastener.getBlockType() != FairyLights.fairyLightsFence) {
			fairyLights.renderFastener(fairyLightsFastener.getBlockMetadata());
		}
		fairyLights.renderConnections(fairyLightsFastener, delta);
		GL11.glPopMatrix();
	}
}
