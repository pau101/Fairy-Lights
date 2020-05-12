package me.paulf.fairylights.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
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

    protected void addLight(final int u, final int v, final float x, final float y, final float z, final float width, final float height, final float depth) {
        this.litTint.setTextureOffset(u, v);
        this.litTint.addCuboid(x, y, z, width, height, depth);
        this.litTintGlow.setTextureOffset(u, v);
        this.litTintGlow.addCuboid(x, y, z, width, height, depth, 0.7F);
    }

    public void animate(final Light light, final float delta) {
        final float yaw = light.getYaw(delta);
        final float pitch = light.getPitch(delta);
        final float roll = light.getRoll(delta);
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
        this.litTintGlow.render(matrix, builder, emissiveLight, overlay, lr, lg, lb, this.brightness * 0.15F + 0.1F);
    }
}
