package me.paulf.fairylights.server.feature.light;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class IncandescentBehavior implements BrightnessLightBehavior {
    private float prevBrightness = 1.0F;

    private float brightness = 1.0F;

    private boolean powered = true;

    @Override
    public float getBrightness(final float delta) {
        return MathHelper.func_219799_g(delta, this.prevBrightness, this.brightness);
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.powered = powered;
        if (now) this.prevBrightness = this.brightness = this.powered ? 1.0F : 0.0F;
    }

    @Override
    public void tick(final World world, final Vector3d origin, final Light<?> light) {
        this.prevBrightness = this.brightness;
        if (this.powered) {
            this.brighten(1.0F, 0.2F);
        } else {
            this.brighten(0.0F, 0.1F);
        }
        if (this.brightness > 0.85F && world.field_73012_v.nextFloat() < 0.25F) {
            this.brightness -= world.field_73012_v.nextFloat() * 0.05F;
        }
    }

    private void brighten(final float target, final float rate) {
        if (this.brightness != target) {
            this.brightness += (target - this.brightness) * rate;
            if (Math.abs(target - this.brightness) < 1e-2F) {
                this.brightness = target;
            }
        }
    }
}
