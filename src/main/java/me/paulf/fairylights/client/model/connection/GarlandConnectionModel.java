package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.renderer.FastenerRenderer;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandVineConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public final class GarlandConnectionModel extends ConnectionModel<GarlandVineConnection> {
    private static final int RING_COUNT = 8;

    private static final float RINGS_PER_METER = 4;

    private static final RandomArray RAND = new RandomArray(8411, RING_COUNT * 4);

    private final AdvancedRendererModel cordModel;

    private int ringId = -1;

    private int uniquifier;

    public GarlandConnectionModel() {
        this.cordModel = new AdvancedRendererModel(this, 39, 0);
        this.cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
    }

    private void generateGarlandRings() {
        this.ringId = GLAllocation.generateDisplayLists(RING_COUNT);
        for (int i = 0; i < RING_COUNT; i++) {
            GlStateManager.newList(this.ringId + i, GL11.GL_COMPILE);
            FastenerRenderer.render3DTexture(8, 8, i * 8, 64);
            GlStateManager.endList();
        }
    }

    @Override
    public void renderCord(final GarlandVineConnection connection, final World world, final int sunlight, final int moonlight, final float delta) {
        this.uniquifier = connection.hashCode();
        super.renderCord(connection, world, sunlight, moonlight, delta);
    }

    @Override
    protected void renderSegment(final GarlandVineConnection garland, final int index, final double angleX, final double angleY, final double length, final double x, final double y, final double z, final float delta) {
        if (this.ringId == -1) {
            this.generateGarlandRings();
        }
        this.cordModel.rotateAngleX = (float) angleX;
        this.cordModel.rotateAngleY = (float) angleY;
        this.cordModel.scaleZ = (float) length;
        this.cordModel.setRotationPoint(x, y, z);
        this.cordModel.render(0.0625F);
        GlStateManager.pushMatrix();
        GlStateManager.translated(x / 16, y / 16, z / 16);
        GlStateManager.rotatef((float) angleY * Mth.RAD_TO_DEG, 0, 1, 0);
        GlStateManager.rotatef((float) angleX * Mth.RAD_TO_DEG, 1, 0, 0);
        final int rings = MathHelper.ceil(length * RINGS_PER_METER / 16) + 1;
        for (int i = 0; i < rings; i++) {
            final double t = i / (float) rings * length / 16;
            GlStateManager.pushMatrix();
            GlStateManager.translated(0, 0, t);
            final float rotZ = RAND.get(index + i + this.uniquifier) * 45;
            final float rotY = RAND.get(index + i + 8 + this.uniquifier) * 60 + 90;
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(rotZ, 0, 0, 1);
            GlStateManager.rotatef(rotY, 0, 1, 0);
            GlStateManager.translated(-4 / 16F, -4 / 16F, -0.5F / 16);
            final int ring = this.ringId + index % RING_COUNT;
            GlStateManager.callList(ring);
            GlStateManager.popMatrix();
            GlStateManager.rotatef(rotZ + 90, 0, 0, 1);
            GlStateManager.rotatef(rotY, 0, 1, 0);
            GlStateManager.scalef(7 / 8F, 7 / 8F, 7 / 8F);
            GlStateManager.translatef(-4 / 16F, -4 / 16F, -0.5F / 16);
            GlStateManager.callList(ring);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
