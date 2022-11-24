package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.util.FLMth;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ColorChangingBehavior implements ColorLightBehavior {
    private final float[] red;

    private final float[] green;

    private final float[] blue;

    private final float rate;

    private boolean powered;

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
        final float p = this.powered ? FLMth.mod(Util.getMillis() * (20.0F / 1000.0F) * this.rate, values.length) : 0.0F;
        final int i = (int) p;
        return Mth.lerp(p - i, values[i % values.length], values[(i + 1) % values.length]);
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.powered = powered;
    }

    @Override
    public void tick(final Level world, final Vec3 origin, final Light<?> light) {
    }

    public static ColorLightBehavior create(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null) {
            return new FixedColorBehavior(1.0F, 1.0F, 1.0F);
        }
        final ListTag list = tag.getList("colors", Tag.TAG_INT);
        final float[] red = new float[list.size()];
        final float[] green = new float[list.size()];
        final float[] blue = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            final int color = list.getInt(i);
            red[i] = (color >> 16 & 0xFF) / 255.0F;
            green[i] = (color >> 8 & 0xFF) / 255.0F;
            blue[i] = (color & 0xFF) / 255.0F;
        }
        return new ColorChangingBehavior(red, green, blue, list.size() / 960.0F);
    }

    public static int animate(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0xFFFFFF;
        }
        final ListTag list = tag.getList("colors", Tag.TAG_INT);
        if (list.isEmpty()) {
            return 0xFFFFFF;
        }
        if (list.size() == 1) {
            return list.getInt(0);
        }
        final float p = FLMth.mod(Util.getMillis() * (20.0F / 1000.0F) * (list.size() / 960.0F), list.size());
        final int i = (int) p;
        final int c0 = list.getInt(i % list.size());
        final float r0 = (c0 >> 16 & 0xFF) / 255.0F;
        final float g0 = (c0 >> 8 & 0xFF) / 255.0F;
        final float b0 = (c0 & 0xFF) / 255.0F;
        final int c1 = list.getInt((i + 1) % list.size());
        final float r1 = (c1 >> 16 & 0xFF) / 255.0F;
        final float g1 = (c1 >> 8 & 0xFF) / 255.0F;
        final float b1 = (c1 & 0xFF) / 255.0F;
        return (int) (Mth.lerp(p - i, r0, r1) * 255.0F) << 16 |
            (int) (Mth.lerp(p - i, g0, g1) * 255.0F) << 8 |
            (int) (Mth.lerp(p - i, b0, b1) * 255.0F);
    }

    public static boolean exists(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("colors", Tag.TAG_LIST);
    }
}
