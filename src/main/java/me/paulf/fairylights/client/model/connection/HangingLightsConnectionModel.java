package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.lights.LightModel;
import me.paulf.fairylights.client.model.lights.FairyLightModel;
import me.paulf.fairylights.client.model.lights.FlowerLightModel;
import me.paulf.fairylights.client.model.lights.GhostLightModel;
import me.paulf.fairylights.client.model.lights.IcicleLightsModel;
import me.paulf.fairylights.client.model.lights.JackOLanternLightModel;
import me.paulf.fairylights.client.model.lights.MeteorLightModel;
import me.paulf.fairylights.client.model.lights.OilLanternModel;
import me.paulf.fairylights.client.model.lights.OrbLanternModel;
import me.paulf.fairylights.client.model.lights.OrnateLanternModel;
import me.paulf.fairylights.client.model.lights.PaperLanternModel;
import me.paulf.fairylights.client.model.lights.SkullLightModel;
import me.paulf.fairylights.client.model.lights.SnowflakeLightModel;
import me.paulf.fairylights.client.model.lights.SpiderLightModel;
import me.paulf.fairylights.client.model.lights.WitchLightModel;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class HangingLightsConnectionModel extends ConnectionModel<HangingLightsConnection> {
	private AdvancedRendererModel cordModel;

	private LightModel[] lightModels = new LightModel[] {
		new FairyLightModel(),
		new PaperLanternModel(),
		new OrbLanternModel(),
		new FlowerLightModel(),
		new OrnateLanternModel(),
		new OilLanternModel(),
		new JackOLanternLightModel(),
		new SkullLightModel(),
		new GhostLightModel(),
		new SpiderLightModel(),
		new WitchLightModel(),
		new SnowflakeLightModel(),
		new IcicleLightsModel(),
		new MeteorLightModel()
	};

	public HangingLightsConnectionModel() {
		cordModel = new AdvancedRendererModel(this, 0, 0).addBox(-1, -1, 0, 2, 2, 1);
	}

	@Override
	public void render(Fastener<?> fastener, HangingLightsConnection hangingLights, World world, int skylight, int moonlight, float delta) {
		super.render(fastener, hangingLights, world, skylight, moonlight, delta);
		Light[] lights = hangingLights.getFeatures();
		Light[] prevLights = hangingLights.getPrevFeatures();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		for (int i = 0, count = Math.min(lights.length, prevLights.length); i < count; i++) {
			Light light = lights[i];
			Vec3d color = light.getLight();
			Vec3d point = Mth.lerp(prevLights[i].getPoint(), light.getPoint(), delta);
			Vec3d rotation = light.getRotation(delta);
			float brightness = light.getBrightness(delta);
			LightModel model = lightModels[light.getVariant().ordinal()];
			model.setOffsets(point.x / 16, point.y / 16, point.z / 16);
			boolean vert = Math.abs(Math.abs(rotation.y) - Mth.PI / 2) < 1e-6F;
			model.setAfts(0, -2.2F / 16, 0);
			model.setRotationAngles(light.getVariant().parallelsCord() ? rotation.y : vert ? 0.3F : 0, rotation.x, rotation.z);
			model.setScale(1);
			model.render(world, light, 0.0625F, color, moonlight, skylight, brightness, i, delta);
		}
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
	}

	@Override
	protected void renderSegment(HangingLightsConnection fairylights, int index, double angleX, double angleY, double length, double x, double y, double z, float delta) {
		cordModel.rotateAngleX = (float) angleX;
		cordModel.rotateAngleY = (float) angleY;
		cordModel.scaleZ = (float) length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
	}
}
