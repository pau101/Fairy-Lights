package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.util.AABBBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;

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
        super(RenderType::func_228644_e_);
        this.field_78090_t = 128;
        this.field_78089_u = 128;
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
            this.func_225598_a_(matrix, builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.renderTranslucent(matrix, builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.bounds = builder.build();
        }
        return this.bounds;
    }

    public double getFloorOffset() {
        if (Double.isNaN(this.floorOffset)) {
            final AABBVertexBuilder builder = new AABBVertexBuilder();
            this.func_225598_a_(new MatrixStack(), builder, 0, 0, 1.0F, 1.0F, 1.0F, 1.0F);
            this.floorOffset = builder.build().field_72338_b-this.getBounds().field_72338_b;
        }
        return this.floorOffset;
    }

    public void animate(final Light<?> light, final T behavior, final float delta) {
        this.powered = light.isPowered();
    }

    @Override
    public void func_225598_a_(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.unlit.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
        final int emissiveLight = this.getLight(light);
        this.lit.func_228309_a_(matrix, builder, emissiveLight, overlay, r, g, b, a);
        this.litTint.func_228309_a_(matrix, builder, emissiveLight, overlay, r * this.red, g * this.green, b * this.blue, a);
    }

    public void renderTranslucent(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        final float v = this.brightness;
        this.litTintGlow.func_228309_a_(matrix, builder, this.getLight(light), overlay, r * this.red * v + (1.0F - v), g * this.green * v + (1.0F - v), b * this.blue * v + (1.0F - v), v * 0.15F + 0.2F);
    }

    protected int getLight(final int packedLight) {
        return (int) Math.max((this.brightness * 15.0F * 16.0F), this.powered ? 0 : packedLight & 255) | packedLight & (255 << 16);
    }

    // http://bediyap.com/programming/convert-quaternion-to-euler-rotations/
    protected static float[] toEuler(final Quaternion q) {
        final float r11 = 2.0F * (q.func_195889_a() * q.func_195891_b() + q.func_195894_d() * q.func_195893_c());
        final float r12 = q.func_195894_d() * q.func_195894_d() + q.func_195889_a() * q.func_195889_a() - q.func_195891_b() * q.func_195891_b() - q.func_195893_c() * q.func_195893_c();
        final float r21 = -2.0F * (q.func_195889_a() * q.func_195893_c() - q.func_195894_d() * q.func_195891_b());
        final float r31 = 2.0F * (q.func_195891_b() * q.func_195893_c() + q.func_195894_d() * q.func_195889_a());
        final float r32 = q.func_195894_d() * q.func_195894_d() - q.func_195889_a() * q.func_195889_a() - q.func_195891_b() * q.func_195891_b() + q.func_195893_c() * q.func_195893_c();
        return new float[]{
            (float) MathHelper.func_181159_b(r31, r32),
            (float) Math.asin(r21),
            (float) MathHelper.func_181159_b(r11, r12)
        };
    }

    static class AABBVertexBuilder implements IVertexBuilder {
        final AABBBuilder builder = new AABBBuilder();

        @Override
        public IVertexBuilder func_225582_a_(final double x, final double y, final double z) {
            this.builder.include(x, y, z);
            return this;
        }

        @Override
        public IVertexBuilder func_225586_a_(final int red, final int green, final int blue, final int alpha) {
            return this;
        }

        @Override
        public IVertexBuilder func_225583_a_(final float u, final float v) {
            return this;
        }

        @Override
        public IVertexBuilder func_225585_a_(final int u, final int v) {
            return this;
        }

        @Override
        public IVertexBuilder func_225587_b_(final int u, final int v) {
            return this;
        }

        @Override
        public IVertexBuilder func_225584_a_(final float x, final float y, final float z) {
            return this;
        }

        @Override
        public void func_181675_d() {
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
            this.base.func_78784_a(u, v);
            this.glow.func_78784_a(u, v);
            return this;
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth) {
            this.addBox(x, y, z, width, height, depth, 0.0F);
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand) {
            this.addBox(x, y, z, width, height, depth, expand, 0.7F);
        }

        void addBox(final float x, final float y, final float z, final float width, final float height, final float depth, final float expand, final float glow) {
            this.base.func_228301_a_(x, y, z, width, height, depth, expand);
            this.glow.func_228301_a_(x, y, z, width, height, depth, expand + glow);
        }

        BulbBuilder createChild(final int u, final int v) {
            return this.createChild(u, v, ModelRenderer::new);
        }

        BulbBuilder createChild(final int u, final int v, final ModelRendererFactory factory) {
            final ModelRenderer base = factory.create(this.model, u, v);
            final ModelRenderer glow = factory.create(this.model, u, v);
            this.base.func_78792_a(base);
            this.glow.func_78792_a(glow);
            return new BulbBuilder(this.model, base, glow);
        }

        public void setPosition(final float x, final float y, final float z) {
            this.base.func_78793_a(x, y, z);
            this.glow.func_78793_a(x, y, z);
        }

        public void setAngles(final float x, final float y, final float z) {
            this.base.field_78795_f = x;
            this.base.field_78796_g = y;
            this.base.field_78808_h = z;
            this.glow.field_78795_f = x;
            this.glow.field_78796_g = y;
            this.glow.field_78808_h = z;
        }

        public void setVisible(final boolean value) {
            this.base.field_78806_j = value;
            this.glow.field_78806_j = value;
        }

        public void renderTint(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.base.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
        }

        public void renderGlow(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
            this.glow.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
        }
    }

    interface ModelRendererFactory {
        ModelRenderer create(final Model model, final int u, final int v);
    }
}
