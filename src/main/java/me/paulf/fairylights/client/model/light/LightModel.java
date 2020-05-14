package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class LightModel extends Model {
    protected final ModelRenderer lit;

    protected final ModelRenderer litTint;

    protected final ModelRenderer litTintGlow;

    protected final ModelRenderer unlit;

    private int light;

    private Vec3d color;

    private float brightness;

    public LightModel() {
        super(RenderType::getEntityTranslucent);
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.lit = new ModelRenderer(this);
        this.litTint = new ModelRenderer(this);
        this.litTintGlow = new ModelRenderer(this);
        this.unlit = new ModelRenderer(this);
    }

    protected BulbBuilder createBulb() {
        return new BulbBuilder(this.litTint, this.litTintGlow);
    }

    public void animate(final Light light, final float delta) {
        this.brightness = light.getBrightness(delta);
        this.color = light.getLight();
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.unlit.render(matrix, builder, light, overlay, r, g, b, a);
        final int emissiveLight = Math.max((int) (this.brightness * 15.0F * 16.0F), light & 255) | light & (255 << 16);
        this.lit.render(matrix, builder, emissiveLight, overlay, r, g, b, a);
        final float lr = r * (float) this.color.x;
        final float lg = g * (float) this.color.y;
        final float lb = b * (float) this.color.z;
        this.litTint.render(matrix, builder, emissiveLight, overlay, lr, lg, lb, a);
    }

    public void renderTranslucent(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        final int emissiveLight = Math.max((int) (this.brightness * 15.0F * 16.0F), light & 255) | light & (255 << 16);
        final float lr = r * (float) this.color.x;
        final float lg = g * (float) this.color.y;
        final float lb = b * (float) this.color.z;
        this.litTintGlow.render(matrix, builder, emissiveLight, overlay, lr, lg, lb, this.brightness * 0.15F + 0.1F);
    }

    // http://bediyap.com/programming/convert-quaternion-to-euler-rotations/
    protected static float[] toEuler(final Quaternion q) {
        final float r11 = 2.0F * (q.getX() * q.getY() + q.getW() * q.getZ());
        final float r12 = q.getW() * q.getW() + q.getX() * q.getX() - q.getY() * q.getY() - q.getZ() * q.getZ();
        final float r21 = -2.0F * (q.getX() * q.getZ() - q.getW() * q.getY());
        final float r31 = 2.0F * (q.getY() * q.getZ() + q.getW() * q.getX());
        final float r32 = q.getW() * q.getW() - q.getX() * q.getX() - q.getY() * q.getY() + q.getZ() * q.getZ();
        return new float[] {
            (float) MathHelper.atan2(r31, r32),
            (float) Math.asin(r21),
            (float) MathHelper.atan2(r11, r12)
        };
    }

    class BulbBuilder {
        ModelRenderer base;
        ModelRenderer glow;

        public BulbBuilder(final ModelRenderer base, final ModelRenderer glow) {
            this.base = base;
            this.glow = glow;
        }

        public void setUV(final int u, final int v) {
            this.base.setTextureOffset(u, v);
            this.glow.setTextureOffset(u, v);
        }

        void addCuboid(final float x, final float y, final float z, final float width, final float height, final float depth) {
            this.addCuboid(x, y, z, width, height, depth, 0.0F);
        }

        void addCuboid(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand) {
            this.addCuboid(x, y, z, width, height, depth, expand, 0.7F);
        }

        void addCuboid(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand, final float glow) {
            this.base.addCuboid(x, y, z, width, height, depth, expand);
            this.glow.addCuboid(x, y, z, width, height, depth, expand + glow);
        }

        BulbBuilder createChild(final int u, final int v) {
            final ModelRenderer base = new ModelRenderer(LightModel.this, u, v);
            final ModelRenderer glow = new ModelRenderer(LightModel.this, u, v);
            this.base.addChild(base);
            this.glow.addChild(glow);
            return new BulbBuilder(base, glow);
        }

        public void setPosition(final float x, final float y, final float z) {
            this.base.setRotationPoint(x, y, z);
            this.glow.setRotationPoint(x, y, z);
        }

        public void setAngles(final float x, final float y, final float z) {
            this.base.rotateAngleX = x;
            this.base.rotateAngleY = y;
            this.base.rotateAngleZ = z;
            this.glow.rotateAngleX = x;
            this.glow.rotateAngleY = y;
            this.glow.rotateAngleZ = z;
        }
    }
}
