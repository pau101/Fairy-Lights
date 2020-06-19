package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.util.AABBBuilder;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public abstract class LightModel<T extends LightBehavior> extends Model {
    protected final ModelRenderer lit;

    protected final ModelRenderer litTint;

    protected final ModelRenderer litTintGlow;

    protected final ModelRenderer unlit;

    protected float brightness = 1.0F;

    protected float red = 1.0F;

    protected float green = 1.0F;

    protected float blue = 1.0F;

    @Nullable
    private AxisAlignedBB bounds;

    private double floorOffset = Double.NaN;

    private boolean powered;

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
        return new BulbBuilder(this, this.litTint, this.litTintGlow);
    }

    public AxisAlignedBB getBounds() {
        if (this.bounds == null) {
            final MatrixStack matrix = new MatrixStack();
            final AABBVertexBuilder builder = new AABBVertexBuilder();
            this.render(matrix, builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.renderTranslucent(matrix, builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.bounds = builder.build();
        }
        return this.bounds;
    }

    public double getFloorOffset() {
        if (Double.isNaN(this.floorOffset)) {
            final AABBVertexBuilder builder = new AABBVertexBuilder();
            this.render(new MatrixStack(), builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.floorOffset = builder.build().minY-this.getBounds().minY;
        }
        return this.floorOffset;
    }

    public void animate(final Light<?> light, final T behavior, final float delta) {
        this.powered = light.isPowered();
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.unlit.render(matrix, builder, light, overlay, r, g, b, a);
        final int emissiveLight = this.getLight(light);
        this.lit.render(matrix, builder, emissiveLight, overlay, r, g, b, a);
        this.litTint.render(matrix, builder, emissiveLight, overlay, r * this.red, g * this.green, b * this.blue, a);
    }

    public void renderTranslucent(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        final float v = this.brightness;
        this.litTintGlow.render(matrix, builder, this.getLight(light), overlay, r * this.red * v + (1.0F - v), g * this.green * v + (1.0F - v), b * this.blue * v + (1.0F - v), v * 0.15F + 0.1F);
    }

    protected int getLight(final int packedLight) {
        return (int) Math.max((this.brightness * 15.0F * 16.0F), this.powered ? 0 : packedLight & 255) | packedLight & (255 << 16);
    }

    // http://bediyap.com/programming/convert-quaternion-to-euler-rotations/
    protected static float[] toEuler(final Quaternion q) {
        final float r11 = 2.0F * (q.getX() * q.getY() + q.getW() * q.getZ());
        final float r12 = q.getW() * q.getW() + q.getX() * q.getX() - q.getY() * q.getY() - q.getZ() * q.getZ();
        final float r21 = -2.0F * (q.getX() * q.getZ() - q.getW() * q.getY());
        final float r31 = 2.0F * (q.getY() * q.getZ() + q.getW() * q.getX());
        final float r32 = q.getW() * q.getW() - q.getX() * q.getX() - q.getY() * q.getY() + q.getZ() * q.getZ();
        return new float[]{
            (float) MathHelper.atan2(r31, r32),
            (float) Math.asin(r21),
            (float) MathHelper.atan2(r11, r12)
        };
    }

    static class AABBVertexBuilder implements IVertexBuilder {
        final AABBBuilder builder = new AABBBuilder();

        @Override
        public IVertexBuilder pos(final double x, final double y, final double z) {
            this.builder.include(x, y, z);
            return this;
        }

        @Override
        public IVertexBuilder color(final int red, final int green, final int blue, final int alpha) {
            return this;
        }

        @Override
        public IVertexBuilder tex(final float u, final float v) {
            return this;
        }

        @Override
        public IVertexBuilder overlay(final int u, final int v) {
            return this;
        }

        @Override
        public IVertexBuilder lightmap(final int u, final int v) {
            return this;
        }

        @Override
        public IVertexBuilder normal(final float x, final float y, final float z) {
            return this;
        }

        @Override
        public void endVertex() {
        }

        AxisAlignedBB build() {
            return this.builder.build();
        }
    }

    static class BulbBuilder {
        final LightModel<?> model;
        ModelRenderer base;
        ModelRenderer glow;

        public BulbBuilder(final LightModel<?> model, final ModelRenderer base, final ModelRenderer glow) {
            this.model = model;
            this.base = base;
            this.glow = glow;
        }

        public BulbBuilder setUV(final int u, final int v) {
            this.base.setTextureOffset(u, v);
            this.glow.setTextureOffset(u, v);
            return this;
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth) {
            this.addBox(x, y, z, width, height, depth, 0.0F);
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand) {
            this.addBox(x, y, z, width, height, depth, expand, 0.7F);
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand, final float glow) {
            this.base.addBox(x, y, z, width, height, depth, expand);
            this.glow.addBox(x, y, z, width, height, depth, expand + glow);
        }

        BulbBuilder createChild(final int u, final int v) {
            return this.createChild(u, v, ModelRenderer::new);
        }

        BulbBuilder createChild(final int u, final int v, final ModelRendererFactory factory) {
            final ModelRenderer base = factory.create(this.model, u, v);
            final ModelRenderer glow = factory.create(this.model, u, v);
            this.base.addChild(base);
            this.glow.addChild(glow);
            return new BulbBuilder(this.model, base, glow);
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

        public void setVisible(final boolean value) {
            this.base.showModel = value;
            this.glow.showModel = value;
        }

        public void renderTint(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.base.render(matrix, builder, light, overlay, r, g, b, a);
        }

        public void renderGlow(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.glow.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }

    interface ModelRendererFactory {
        ModelRenderer create(final Model model, final int u, final int v);
    }
}
