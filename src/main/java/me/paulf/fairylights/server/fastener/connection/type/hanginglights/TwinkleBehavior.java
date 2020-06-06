package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TwinkleBehavior extends FixedColorBehavior implements StandardLightBehavior {
    private static final CubicBezier EASE_IN_OUT = new CubicBezier(0.4F, 0, 0.6F, 1);

    private final TwinkleLogic logic;

    private boolean powered = true;

    public TwinkleBehavior(final float red, final float green, final float blue, final float chance, final int duration) {
        super(red, green, blue);
        this.logic = new TwinkleLogic(chance, duration);
    }

    @Override
    public float getBrightness(final float delta) {
        final float x = this.logic.get(delta);
        return x < 0.25F ? EASE_IN_OUT.eval(x / 0.25F) : 1.0F - EASE_IN_OUT.eval(Mth.transform(x, 0.25F, 1.0F, 0.0F, 1.0F));
    }

    @Override
    public void power(final boolean powered) {
        this.powered = powered;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
        this.logic.tick(world.rand, this.powered);
    }
}
