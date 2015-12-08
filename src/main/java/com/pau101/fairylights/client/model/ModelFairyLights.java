package com.pau101.fairylights.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.pau101.fairylights.block.BlockFairyLightsFastener;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.Light;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.Segment;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ModelFairyLights extends ModelBase {
	private static final ModelLight[] LIGHT_MODELS = new ModelLight[] {
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
		new ModelLightWeedwoodLantern()
	};

	private ModelRenderer fastenerModel;

	private AdvancedModelRenderer cordModel;

	public ModelFairyLights() {
		textureWidth = 128;
		textureHeight = 64;
		fastenerModel = new ModelRenderer(this, 111, 25);
		fastenerModel.addBox(-2, -8, -2, 4, 4, 4);
		fastenerModel.setRotationPoint(8, 8, 8);
		cordModel = new AdvancedModelRenderer(this, 0, 0);
		cordModel.addBox(-1, -1, 0, 2, 2, 1);
	}

	private Vector3f adjustSaturation(Vector3f color, float amount) {
		float r = color.x, g = color.y, b = color.z;
		float p = MathHelper.sqrt_float(r * r * 0.299F + g * g * 0.587F + b * b * 0.114F);
		r = MathHelper.clamp_float(p + (r - p) * amount, 0, 1);
		g = MathHelper.clamp_float(p + (g - p) * amount, 0, 1);
		b = MathHelper.clamp_float(p + (b - p) * amount, 0, 1);
		return new Vector3f(r, g, b);
	}

	private void renderCord(World world, Connection connection, int sunlight, int moonlight, float partialRenderTicks) {
		Point3f to = connection.getTo();
		int toBlockBrightness = world.getLightBrightnessForSkyBlocks(MathHelper.floor_float(to.x), MathHelper.floor_float(to.y), MathHelper.floor_float(to.z), 0);
		int toSunlight = toBlockBrightness % 65536;
		int toMoonlight = toBlockBrightness / 65536;
		Segment[] segments = connection.getCatenary().getSegments();
		Catenary prevCatenary = connection.getPrevCatenary();
		Segment[] segmentsOld = null;
		if (prevCatenary != null) {
			segmentsOld = prevCatenary.getSegments();
		}
		GL11.glColor3f(0.2F, 0.2F, 0.2F);
		for (int i = 0; i < segments.length; i++) {
			float v = i / (float) segments.length;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight * (1 - v) + toSunlight * v, moonlight * (1 - v) + toMoonlight * v);
			Segment segment = segments[i];
			Vector3f rotation = segment.getRotation();
			if (segmentsOld != null && i < segmentsOld.length) {
				rotation.interpolate(segmentsOld[i].getRotation(), 1 - partialRenderTicks, true);
			}
			cordModel.rotateAngleX = rotation.y;
			cordModel.rotateAngleY = rotation.x;
			float length = segment.getLength();
			if (segmentsOld != null && i < segmentsOld.length) {
				length = length * partialRenderTicks + segmentsOld[i].getLength() * (1 - partialRenderTicks);
			}
			cordModel.scaleZ = length;
			Point3f vertex = segment.getVertex();
			if (segmentsOld != null && i < segmentsOld.length) {
				vertex.interpolate(segmentsOld[i].getVertex(), 1 - partialRenderTicks);
			}
			cordModel.setRotationPoint(vertex.x, vertex.y, vertex.z);
			cordModel.render(0.0625F);
		}
	}

	private List<Connection> removeUnnecessaryConnections(Collection<Connection> connections) {
		List<Connection> visibleConnections = new ArrayList<Connection>();
		Iterator<Connection> connectionIterator = connections.iterator();
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			if (!connection.isOrigin()) {
				continue;
			}
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			if (connection.getTo() == null) {
				continue;
			}
			Segment[] vertices = catenary.getSegments();
			if (vertices == null) {
				continue;
			}
			visibleConnections.add(connection);
		}
		return visibleConnections;
	}

	public void renderConnections(TileEntityFairyLightsFastener fairyLightsFastener, float partialRenderTicks) {
		World world = fairyLightsFastener.getWorldObj();
		if (world == null || !(world.getBlock(fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord) instanceof BlockFairyLightsFastener)) {
			return;
		}
		List<Connection> connections = removeUnnecessaryConnections(fairyLightsFastener.getConnections());
		GL11.glPushMatrix();
		Point3f offset = ((BlockFairyLightsFastener) fairyLightsFastener.getBlockType()).getOffsetForData(fairyLightsFastener.getWorldObj().getBlockMetadata(fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord), 0.125F);
		GL11.glTranslatef(offset.x, offset.y, offset.z);
		int blockBrightness = fairyLightsFastener.getWorldObj().getLightBrightnessForSkyBlocks(fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord, 0);
		int skylight = blockBrightness % 65536;
		int moonlight = blockBrightness / 65536;
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		int fastenerRotation = fairyLightsFastener.getBlockMetadata();
		for (Connection connection : connections) {
			renderCord(world, connection, skylight, moonlight, partialRenderTicks);
			Light[] lightPoints = connection.getLightPoints();
			Light[] lightPointsOld = connection.getPrevLightPoints();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			for (int i = 0; i < lightPoints.length; i++) {
				Light light = lightPoints[i];
				Point3f point = light.getPoint();
				Vector3f color = light.getLight();
				Vector3f rotation = light.getRotation();
				float brightness = 1;
				if (lightPointsOld != null && i < lightPointsOld.length) {
					point.interpolate(lightPointsOld[i].getPoint(), 1 - partialRenderTicks);
					rotation.interpolate(lightPointsOld[i].getRotation(), 1 - partialRenderTicks, true);
					brightness = light.getBrightness(partialRenderTicks);
				}
				ModelLight lightModel = LIGHT_MODELS[light.getVariant().ordinal()];
				lightModel.setOffsets(point.x / 16, point.y / 16, point.z / 16);
				float rotationOffset = 0;
				boolean vert = Math.abs(rotation.y) == MathUtils.PI / 2;
				if (vert) {
					switch (fastenerRotation) {
						case 2:
							rotationOffset = -MathUtils.PI;
							break;
						case 3:
							rotationOffset = -MathUtils.PI / 2;
							break;
						case 4:
							rotationOffset = MathUtils.PI / 2;
					}
				}
				lightModel.setAfts(0, -2.2F / 16, 0);
				lightModel.setRotationAngles(lightModel.shouldParallelCord() ? rotation.y : vert ? 0.3F : 0, rotation.x + rotationOffset, rotation.z);
				lightModel.setScale(1);
				lightModel.render(world, 0.0625F, color, moonlight, skylight, brightness, i, partialRenderTicks);
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor3f(1, 1, 1);
		GL11.glPopMatrix();
	}

	public void renderFastener(int data) {
		fastenerModel.rotateAngleZ = (data & 3) * (float) Math.PI / 2;
		fastenerModel.rotateAngleX = (data >> 2 & 3) * (float) Math.PI / 2;
		fastenerModel.render(0.0625f);
	}
}
