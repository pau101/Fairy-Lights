package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.Segment;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class ModelConnection<T extends Connection> extends Model {
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
		GlStateManager.color3f(1, 1, 1);
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
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight * (1 - v) + toSunlight * v, moonlight * (1 - v) + toMoonlight * v);
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
}
