package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ColorChangingBehavior implements ColorLightBehavior {
    private final float[] red;

    private final float[] green;

    private final float[] blue;

    private final float rate;

    private boolean powered;

    private float offset;

    private float position;

    private float prevPosition;

    public ColorChangingBehavior(final float[] red, final float[] green, final float[] blue, final float rate) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.rate = rate;
    }

    @Override
    public float getRed(final float delta) {
        return this.get(this.red, delta);
    }

    @Override
    public float getGreen(final float delta) {
        return this.get(this.green, delta);
    }

    @Override
    public float getBlue(final float delta) {
        return this.get(this.blue, delta);
    }

    private float get(final float[] values, final float delta) {
        final float p = Mth.mod(Mth.lerpMod(this.prevPosition, this.position, delta, 1.0F), 1.0F) * values.length;
        final int i = (int) p;
        return MathHelper.lerp(p - i, values[i], values[(i + 1) % values.length]);
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.powered = powered;
        this.offset = light.getId() * this.rate * 3;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
        this.prevPosition = this.position;
        if (this.powered) {
            this.position = Mth.mod(this.position + this.rate, 1.0F);
        }
    }
}
