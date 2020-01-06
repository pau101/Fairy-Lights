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

public abstract class ConnectionModel<T extends Connection> extends Model {
    public ConnectionModel() {
        this.textureWidth = this.textureHeight = 128;
    }

    public boolean hasTexturedRender() {
        return false;
    }

    @Nullable
    public ResourceLocation getAlternateTexture() {
        return null;
    }

    public void render(final Fastener<?> fastener, final T connection, final World world, final int skylight, final int moonlight, final float delta) {
        this.renderCord(connection, world, skylight, moonlight, delta);
    }

    public void renderTexturePass(final Fastener<?> fastener, final T connnection, final World world, final int skylight, final int moonlight, final float delta) {}

    public void renderCord(final T connection, final World world, final int sunlight, final int moonlight, final float delta) {
        final Catenary prevCatenary = connection.getPrevCatenary();
        final Vec3d to = connection.getDestination().get(world).getConnectionPoint();
        final int toBlockBrightness = world.getCombinedLight(new BlockPos(to), 0);
        final int toSunlight = toBlockBrightness % 65536;
        final int toMoonlight = toBlockBrightness / 65536;
        Segment[] segments = connection.getCatenary().getSegments();
        Segment[] segmentsOld = prevCatenary.getSegments();
        GlStateManager.color3f(1, 1, 1);
        GlStateManager.disableRescaleNormal();
        final float sdelta;
        if (segments.length >= segmentsOld.length) {
            final Segment[] t = segments;
            segments = segmentsOld;
            segmentsOld = t;
            sdelta = 1 - delta;
        } else {
            sdelta = delta;
        }
        for (int i = 0; i < segments.length; i++) {
            final float v = i / (float) segments.length;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight * (1 - v) + toSunlight * v, moonlight * (1 - v) + toMoonlight * v);
            final Segment segment = segments[i];
            final Segment old = segmentsOld[i];
            final Vec3d rotation;
            final double length;
            final Vec3d vertex = Mth.lerp(old.getStart(), segment.getStart(), sdelta);
            if (segmentsOld.length > segment.getLength() && i == segments.length - 1) {
                final Segment s = new Segment(vertex);
                s.connectTo(Mth.lerp(segmentsOld[segmentsOld.length - 1].getEnd(), segment.getEnd(), sdelta));
                rotation = s.getRotation();
                length = s.getLength();
            } else {
                rotation = Mth.lerpAngles(old.getRotation(), segment.getRotation(), sdelta);
                length = old.getLength() * (1.0F - sdelta) + segment.getLength() * sdelta;
            }
            this.renderSegment(connection, i, rotation.y, rotation.x, length, vertex.x, vertex.y, vertex.z, delta);
        }
        GlStateManager.enableRescaleNormal();
    }

    protected abstract void renderSegment(T connection, int index, double angleX, double angleY, double length, double x, double y, double z, float delta);
}
