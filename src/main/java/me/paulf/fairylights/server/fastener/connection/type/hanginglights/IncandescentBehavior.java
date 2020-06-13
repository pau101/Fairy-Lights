package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class IncandescentBehavior implements BrightLightBehavior {
    private float prevBrightness = 1.0F;

    private float brightness = 1.0F;

    private boolean powered = true;

    @Override
    public float getBrightness(final float delta) {
        return MathHelper.lerp(delta, this.prevBrightness, this.brightness);
    }

    @Override
    public void power(final boolean powered) {
        this.powered = powered;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
        this.prevBrightness = this.brightness;
        if (this.powered) {
            this.brighten(1.0F, 0.2F);
        } else {
            this.brighten(0.0F, 0.1F);
        }
        if (this.brightness > 0.85F && world.rand.nextFloat() < 0.25F) {
            this.brightness -= world.rand.nextFloat() * 0.05F;
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
