package com.pau101.fairylights.client.model.connection;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.Segment;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.util.Mth;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class ModelConnection<T extends Connection> extends ModelBase {
	public ModelConnection() {
		textureWidth = textureHeight = 128;
	}

	public boolean hasTexturedRender() {
		return false;
	}

	@Nullable
	public ResourceLocation getAlternateTexture() {
		return null;
	}

	public void render(Fastener<?> fastener, T connection, World world, int skylight, int moonlight, float delta) {
		renderCord(connection, world, skylight, moonlight, delta);
	}

	public void renderTexturePass(Fastener<?> fastener, T connnection, World world, int skylight, int moonlight, float delta) {}

	public void renderCord(T connection, World world, int sunlight, int moonlight, float delta) {
		Catenary prevCatenary = connection.getPrevCatenary();
		Vec3d to = connection.getDestination().get(world).getConnectionPoint();
		int toBlockBrightness = world.getCombinedLight(new BlockPos(to), 0);
		int toSunlight = toBlockBrightness % 65536;
		int toMoonlight = toBlockBrightness / 65536;
		Segment[] segments = connection.getCatenary().getSegments();
		Segment[] segmentsOld = prevCatenary.getSegments();
		GlStateManager.color(1, 1, 1);
		GlStateManager.disableRescaleNormal();
		final float sdelta;
		if (segments.length >= segmentsOld.length) {
			Segment[] t = segments;
			segments = segmentsOld;
			segmentsOld = t;
			sdelta = 1 - delta;
		} else {
			sdelta = delta;
		}
		for (int i = 0; i < segments.length; i++) {
			float v = i / (float) segments.length;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight * (1 - v) + toSunlight * v, moonlight * (1 - v) + toMoonlight * v);
			Segment segment = segments[i];
			Segment old = segmentsOld[i];
			Vec3d rotation;
			double length;
			Vec3d vertex = Mth.lerp(old.getStart(), segment.getStart(), sdelta);
			if (segmentsOld.length > segment.getLength() && i == segments.length - 1) {
				final Segment s = new Segment(vertex);
				s.connectTo(Mth.lerp(segmentsOld[segmentsOld.length - 1].getEnd(), segment.getEnd(), sdelta));
				rotation = s.getRotation();
				length = s.getLength();
			} else {
				rotation = Mth.lerpAngles(old.getRotation(), segment.getRotation(), sdelta);
				length = old.getLength() * (1.0F - sdelta) + segment.getLength() * sdelta;
			}
			renderSegment(connection, i, rotation.y, rotation.x, length, vertex.x, vertex.y, vertex.z, delta);
		}
		GlStateManager.enableRescaleNormal();
	}

	protected abstract void renderSegment(T connection, int index, double angleX, double angleY, double length, double x, double y, double z, float delta);

	@Override
	public final ModelRenderer getRandomModelBox(Random rand) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final TextureOffset getTextureOffset(String partName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void render(Entity entity, float speed, float swing, float entityAge, float yaw, float pitch, float scale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float delta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setModelAttributes(ModelBase model) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setRotationAngles(float speed, float swing, float entityAge, float yaw, float pitch, float scale, Entity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final void setTextureOffset(String partName, int x, int y) {
		throw new UnsupportedOperationException();
	}
}
