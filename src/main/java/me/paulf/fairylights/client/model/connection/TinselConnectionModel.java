package me.paulf.fairylights.client.model.connection;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandTinselConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.RandomArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class TinselConnectionModel extends ConnectionModel<GarlandTinselConnection> {
    private static final RandomArray RAND = new RandomArray(9171, 32);

    private final AdvancedRendererModel cordModel;

    private final AdvancedRendererModel stripModel;

    private int uniquifier;

    public TinselConnectionModel() {
        this.cordModel = new AdvancedRendererModel(this, 62, 0);
        this.cordModel.addBox(-0.5F, -0.5F, 0, 1, 1, 1);
        this.stripModel = new AdvancedRendererModel(this, 62, 0);
        this.stripModel.addBox(-0.5F, 0, -0.5F, 1, 3, 1);
        this.stripModel.scaleZ = 0.5F;
    }

    @Override
    public void renderCord(final GarlandTinselConnection connection, final World world, final int sunlight, final int moonlight, final float delta) {
        this.uniquifier = connection.hashCode();
        super.renderCord(connection, world, sunlight, moonlight, delta);
    }

    @Override
    protected void renderSegment(final GarlandTinselConnection tinsel, final int index, final double angleX, final double angleY, final double length, final double x, final double y, final double z, final float delta) {
        final int color = tinsel.getColor();
        final float colorRed = ((color >> 16) & 0xFF) / 255F;
        final float colorGreen = ((color >> 8) & 0xFF) / 255F;
        final float colorBlue = ((color) & 0xFF) / 255F;
        GlStateManager.color3f(colorRed, colorGreen, colorBlue);
        this.cordModel.rotateAngleX = (float) angleX;
        this.cordModel.rotateAngleY = (float) angleY;
        this.cordModel.scaleZ = (float) length;
        this.cordModel.setRotationPoint(x, y, z);
        this.cordModel.render(0.0625F);
        GlStateManager.pushMatrix();
        GlStateManager.translated(x / 16, y / 16, z / 16);
        GlStateManager.rotatef((float) angleY * Mth.RAD_TO_DEG, 0, 1, 0);
        GlStateManager.rotatef((float) angleX * Mth.RAD_TO_DEG, 1, 0, 0);
        final int rings = MathHelper.ceil(length * 4);
        for (int i = 0; i < rings; i++) {
            final double t = i / (float) rings * length / 16;
            GlStateManager.pushMatrix();
            GlStateManager.translated(0, 0, t);
            final float rotX = RAND.get(index + i + this.uniquifier) * 22;
            final float rotY = RAND.get(index * 3 + i + this.uniquifier) * 180;
            final float rotZ = RAND.get(index * 7 + i + this.uniquifier) * 180;
            GlStateManager.rotatef(rotZ, 0, 0, 1);
            GlStateManager.rotatef(rotY, 0, 1, 0);
            GlStateManager.rotatef(rotX, 1, 0, 0);
            this.stripModel.render(0.0625F);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        GlStateManager.color3f(1, 1, 1);
    }
}
