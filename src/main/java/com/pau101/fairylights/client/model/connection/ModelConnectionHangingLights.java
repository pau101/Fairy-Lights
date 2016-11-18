package com.pau101.fairylights.client.model.connection;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.model.lights.ModelLight;
import com.pau101.fairylights.client.model.lights.ModelLightFairy;
import com.pau101.fairylights.client.model.lights.ModelLightFlower;
import com.pau101.fairylights.client.model.lights.ModelLightGhost;
import com.pau101.fairylights.client.model.lights.ModelLightIcicle;
import com.pau101.fairylights.client.model.lights.ModelLightJackOLantern;
import com.pau101.fairylights.client.model.lights.ModelLightLuxoBall;
import com.pau101.fairylights.client.model.lights.ModelLightMeteor;
import com.pau101.fairylights.client.model.lights.ModelLightOil;
import com.pau101.fairylights.client.model.lights.ModelLightOrb;
import com.pau101.fairylights.client.model.lights.ModelLightOrnate;
import com.pau101.fairylights.client.model.lights.ModelLightPaper;
import com.pau101.fairylights.client.model.lights.ModelLightSkull;
import com.pau101.fairylights.client.model.lights.ModelLightSnowflake;
import com.pau101.fairylights.client.model.lights.ModelLightSpider;
import com.pau101.fairylights.client.model.lights.ModelLightWitch;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.Light;
import com.pau101.fairylights.util.Mth;

public final class ModelConnectionHangingLights extends ModelConnection<ConnectionHangingLights> {
	private AdvancedModelRenderer cordModel;

	private ModelLight[] lightModels = new ModelLight[] {
		new ModelLightFairy(),
		new ModelLightPaper(),
		new ModelLightOrb(),
		new ModelLightFlower(),
		new ModelLightOrnate(),
		new ModelLightOil(),
		new ModelLightLuxoBall(),
		new ModelLightJackOLantern(),
		new ModelLightSkull(),
		new ModelLightGhost(),
		new ModelLightSpider(),
		new ModelLightWitch(),
		new ModelLightSnowflake(),
		new ModelLightIcicle(),
		new ModelLightMeteor()
	};

	public ModelConnectionHangingLights() {
		cordModel = new AdvancedModelRenderer(this, 0, 0).addBox(-1, -1, 0, 2, 2, 1);
	}

	@Override
	public void render(Fastener<?> fastener, ConnectionHangingLights hangingLights, World world, int skylight, int moonlight, float delta) {
		super.render(fastener, hangingLights, world, skylight, moonlight, delta);
		Light[] lights = hangingLights.getFeatures();
		Light[] prevLights = hangingLights.getPrevFeatures();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		for (int i = 0, count = Math.min(lights.length, prevLights.length); i < count; i++) {
			Light light = lights[i];
			Vec3d color = light.getLight();
			Vec3d point = Mth.lerp(prevLights[i].getPoint(), light.getPoint(), delta);
			Vec3d rotation = light.getRotation(delta);
			float brightness = light.getBrightness(delta);
			ModelLight model = lightModels[light.getVariant().ordinal()];
			model.setOffsets(point.xCoord / 16, point.yCoord / 16, point.zCoord / 16);
			boolean vert = Math.abs(Math.abs(rotation.yCoord) - Mth.PI / 2) < 1e-6F;
			model.setAfts(0, -2.2F / 16, 0);
			model.setRotationAngles(light.getVariant().parallelsCord() ? rotation.yCoord : vert ? 0.3F : 0, rotation.xCoord, rotation.zCoord);
			model.setScale(1);
			model.render(world, light, 0.0625F, color, moonlight, skylight, brightness, i, delta);
		}
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
	}

	@Override
	protected void renderSegment(ConnectionHangingLights fairylights, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
	}
}
