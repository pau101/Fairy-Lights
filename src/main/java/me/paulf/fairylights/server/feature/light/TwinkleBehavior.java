package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.FLMth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TwinkleBehavior implements BrightnessLightBehavior {
    private static final CubicBezier EASE_IN_OUT = new CubicBezier(0.4F, 0, 0.6F, 1);

    private final TwinkleLogic logic;

    private boolean powered = true;

    public TwinkleBehavior(final float chance, final int duration) {
        this.logic = new TwinkleLogic(chance, duration);
    }

    @Override
    public float getBrightness(final float delta) {
        if (this.powered) {
            final float x = this.logic.get(delta);
            return x < 0.25F ? 1.0F - EASE_IN_OUT.eval(x / 0.25F) : EASE_IN_OUT.eval(FLMth.transform(x, 0.25F, 1.0F, 0.0F, 1.0F));
        }
        return 0.0F;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.powered = powered;
    }

    @Override
    public void tick(final Level world, final Vec3 origin, final Light<?> light) {
        this.logic.tick(world.random, this.powered);
    }

    public static boolean exists(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean("twinkle");
    }
}
