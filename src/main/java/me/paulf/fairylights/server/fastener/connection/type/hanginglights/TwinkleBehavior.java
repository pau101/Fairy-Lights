package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class TwinkleBehavior implements LightBehavior {
    private static final CubicBezier EASE_IN_OUT = new CubicBezier(0.4F, 0, 0.6F, 1);

    private final float chance;

    private final int duration;

    private final Curve curve;

    private int time = -1;

    private int prevTime = -1;

    public TwinkleBehavior(final float chance, final int duration) {
        this(chance, duration, TwinkleBehavior::brightnessFunc);
    }

    public TwinkleBehavior(final float chance, final int duration, final Curve curve) {
        this.chance = chance;
        this.duration = duration;
        this.curve = curve;
    }

    @Override
    public float get(final float delta) {
        return this.time == -1 ? 0.0F : this.curve.apply(MathHelper.lerp(delta, this.prevTime, this.time) / this.duration);
    }

    @Override
    public void tick(final Random rng) {
        this.prevTime = this.time;
        if (this.time != -1 || rng.nextFloat() < this.chance) this.time++;
        if (this.time >= this.duration) this.time = -1;
    }

    @Override
    public void inherit(final LightBehavior parent) {
        if (parent instanceof TwinkleBehavior) {
            this.time = ((TwinkleBehavior) parent).time;
        }
    }

    private static float brightnessFunc(final float x) {
        return x < 0.25F ? EASE_IN_OUT.eval(x / 0.25F) : 1.0F - EASE_IN_OUT.eval(Mth.transform(x, 0.25F, 1.0F, 0.0F, 1.0F));
    }

    public interface Curve {
        float apply(final float x);
    }
}
