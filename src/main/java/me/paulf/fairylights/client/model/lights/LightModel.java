package me.paulf.fairylights.client.model.lights;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.AABBBuilder;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public abstract class LightModel extends Model {
    protected final AdvancedRendererModel colorableParts;

    // amutachromic [A - without] + [MUT - change] + [CHROM - color]
    protected final AdvancedRendererModel amutachromicParts;

    protected final AdvancedRendererModel amutachromicLitParts;

    public LightModel() {
        this.textureWidth = this.textureHeight = 128;
        this.colorableParts = new AdvancedRendererModel(this);
        this.colorableParts.setRotationOrder(RotationOrder.YXZ);
        this.amutachromicParts = new AdvancedRendererModel(this);
        this.amutachromicParts.setRotationOrder(RotationOrder.YXZ);
        this.amutachromicLitParts = new AdvancedRendererModel(this);
        this.amutachromicLitParts.setRotationOrder(RotationOrder.YXZ);
    }

    public boolean hasRandomRotation() {
        return false;
    }

    public void setRotationAngles(final double x, final double y, final double z) {
        this.colorableParts.rotateAngleX = (float) x;
        this.colorableParts.rotateAngleY = (float) y;
        this.colorableParts.rotateAngleZ = (float) z;
        this.amutachromicParts.rotateAngleX = (float) x;
        this.amutachromicParts.rotateAngleY = (float) y;
        this.amutachromicParts.rotateAngleZ = (float) z;
        this.amutachromicLitParts.rotateAngleX = (float) x;
        this.amutachromicLitParts.rotateAngleY = (float) y;
        this.amutachromicLitParts.rotateAngleZ = (float) z;
    }

    public void setOffsets(final double x, final double y, final double z) {
        this.colorableParts.offsetX = (float) x;
        this.colorableParts.offsetY = (float) y;
        this.colorableParts.offsetZ = (float) z;
        this.amutachromicParts.offsetX = (float) x;
        this.amutachromicParts.offsetY = (float) y;
        this.amutachromicParts.offsetZ = (float) z;
        this.amutachromicLitParts.offsetX = (float) x;
        this.amutachromicLitParts.offsetY = (float) y;
        this.amutachromicLitParts.offsetZ = (float) z;
    }

    public void setAfts(final float x, final float y, final float z) {
        this.colorableParts.aftMoveX = x;
        this.colorableParts.aftMoveY = y;
        this.colorableParts.aftMoveZ = z;
        this.amutachromicParts.aftMoveX = x;
        this.amutachromicParts.aftMoveY = y;
        this.amutachromicParts.aftMoveZ = z;
        this.amutachromicLitParts.aftMoveX = x;
        this.amutachromicLitParts.aftMoveY = y;
        this.amutachromicLitParts.aftMoveZ = z;
    }

    public void setScale(final float scale) {
        this.setScales(scale, scale, scale);
    }

    public void setScales(final float x, final float y, final float z) {
        this.colorableParts.scaleX = x;
        this.colorableParts.scaleY = y;
        this.colorableParts.scaleZ = z;
        this.amutachromicParts.scaleX = x;
        this.amutachromicParts.scaleY = y;
        this.amutachromicParts.scaleZ = z;
        this.amutachromicLitParts.scaleX = x;
        this.amutachromicLitParts.scaleY = y;
        this.amutachromicLitParts.scaleZ = z;
    }

    public AxisAlignedBB getBounds() {
        final MatrixStack matrix = new MatrixStack();
        final AABBBuilder builder = new AABBBuilder();
        this.buildBounds(matrix, this.amutachromicParts, 0.0625F, builder);
        this.buildBounds(matrix, this.colorableParts, 0.0625F, builder);
        return builder.build();
    }

    private void buildBounds(final MatrixStack matrix, final RendererModel bone, final float scale, final AABBBuilder builder) {
        matrix.push();
        matrix.translate(bone.rotationPointX * scale, bone.rotationPointY * scale, bone.rotationPointZ * scale);
        if (bone.rotateAngleZ != 0.0F) {
            matrix.rotate(bone.rotateAngleZ, 0.0F, 0.0F, 1.0F);
        }
        if (bone.rotateAngleY != 0.0F) {
            matrix.rotate(bone.rotateAngleY, 0.0F, 1.0F, 0.0F);
        }
        if (bone.rotateAngleX != 0.0F) {
            matrix.rotate(bone.rotateAngleX, 1.0F, 0.0F, 0.0F);
        }
        for (final ModelBox box : bone.cubeList) {
            final float x1 = box.posX1 * scale;
            final float y1 = box.posY1 * scale;
            final float z1 = box.posZ1 * scale;
            final float x2 = box.posX2 * scale;
            final float y2 = box.posY2 * scale;
            final float z2 = box.posZ2 * scale;
            for (final Vec3d v : new Vec3d[]{
                new Vec3d(x1, y1, z1),
                new Vec3d(x2, y1, z1),
                new Vec3d(x1, y1, z2),
                new Vec3d(x2, y1, z2),
                new Vec3d(x1, y2, z1),
                new Vec3d(x2, y2, z1),
                new Vec3d(x1, y2, z2),
                new Vec3d(x2, y2, z2)
            }) {
                builder.include(matrix.transform(v));
            }
        }
        if (bone.childModels != null) {
            for (final RendererModel child : bone.childModels) {
                this.buildBounds(matrix, child, scale, builder);
            }
        }
        matrix.pop();
    }

    public void prepare(final int index) {
        if (this.hasRandomRotation()) {
            final float randomOffset = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4;
            this.colorableParts.secondaryRotateAngleY = randomOffset;
            this.amutachromicParts.secondaryRotateAngleY = randomOffset;
            this.amutachromicLitParts.secondaryRotateAngleY = randomOffset;
        }
    }

    public void render(final World world, final Light light, final float scale, final Vec3d color, final int moonlight, final int sunlight, final float brightness, final int index, final float delta) {
        final float b = Math.max(Math.max(brightness, world.getSunBrightness(1) * 0.95F + 0.05F) * 240, sunlight);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, b, moonlight);
        GlStateManager.enableLighting();
        this.amutachromicLitParts.render(scale);
        final float[] hsb = new float[3];
        Color.RGBtoHSB((int) (color.x * 255 + 0.5F), (int) (color.y * 255 + 0.5F), (int) (color.z * 255 + 0.5F), hsb);
		/*/ trippin balls
		hsb[0] = (float) ((hsb[0] + System.currentTimeMillis() / 2000D) % 1);
		hsb[1] = hsb[2] = 1;//*/
        final int colorRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * 0.75F + (brightness * 0.75F + 0.25F) * 0.25F);
        final float cr = (colorRGB >> 16 & 0xFF) / 255F;
        final float cg = (colorRGB >> 8 & 0xFF) / 255F;
        final float cb = (colorRGB & 0xFF) / 255F;
        GlStateManager.color3f(cr, cg, cb);
        this.colorableParts.render(scale);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, sunlight, moonlight);
        float c = b / 255;
        if (c < 0.5F) {
            c = 0.5F;
        }
        GlStateManager.color3f(c, c, c);
        this.amutachromicParts.render(scale);
        GlStateManager.disableLighting();
        Minecraft.getInstance().gameRenderer.disableLightmap();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.color4f(cr, cg, cb, brightness * 0.15F + 0.1F);
        this.colorableParts.isGlowing = true;
        this.colorableParts.render(scale);
        this.colorableParts.isGlowing = false;
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        Minecraft.getInstance().gameRenderer.enableLightmap();
    }
}
