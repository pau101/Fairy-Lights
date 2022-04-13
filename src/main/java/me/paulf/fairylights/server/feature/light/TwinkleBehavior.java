package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.FLMath;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
            return x < 0.25F ? 1.0F - EASE_IN_OUT.eval(x / 0.25F) : EASE_IN_OUT.eval(FLMath.transform(x, 0.25F, 1.0F, 0.0F, 1.0F));
        }
        return 0.0F;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.powered = powered;
    }

    @Override
    public void tick(final World world, final Vector3d origin, final Light<?> light) {
        this.logic.tick(world.field_73012_v, this.powered);
    }

    public static boolean exists(final ItemStack stack) {
        final CompoundNBT tag = stack.func_77978_p();
        return tag != null && tag.func_74767_n("twinkle");
    }
}
