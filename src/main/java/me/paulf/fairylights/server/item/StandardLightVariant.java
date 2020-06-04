package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.ConstantBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.TwinkleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Function;

public enum StandardLightVariant implements LightVariant {
    FAIRY(true, 5, 5, LightVariant.Placement.ONWARD),
    PAPER(false, 9, 16.5F, LightVariant.Placement.UPRIGHT),
    ORB(false, 10, 11.5F, LightVariant.Placement.UPRIGHT),
    FLOWER(true, 10, 6, LightVariant.Placement.OUTWARD),
    ORNATE(false, 24, 8, 11, LightVariant.Placement.UPRIGHT),
    OIL(false, 32, 8, 13, LightVariant.Placement.UPRIGHT),
    JACK_O_LANTERN(true, 7, 9, LightVariant.Placement.UPRIGHT),
    SKULL(true, 6, 9, LightVariant.Placement.UPRIGHT),
    GHOST(true, 6, 8, LightVariant.Placement.UPRIGHT),
    SPIDER(true, 12, 14, LightVariant.Placement.UPRIGHT),
    WITCH(true, 8, 10, LightVariant.Placement.UPRIGHT),
    SNOWFLAKE(true, 10.0F, 15.0F, LightVariant.Placement.UPRIGHT),
    ICICLE(false, 0.625F, 7, 20, LightVariant.Placement.UPRIGHT),
    METEOR(false, 1.5F, 3, 28.5F, stack -> new TwinkleBehavior(0.02F, 100), LightVariant.Placement.UPRIGHT);

    private final boolean parallelsCord;

    private final float spacing;

    private final float width;

    private final float height;

    private final Function<ItemStack, LightBehavior> behaviorFactory;

    private final LightVariant.Placement placement;

    StandardLightVariant(final boolean parallelsCord, final float width, final float height, final Placement orientable) {
        this(parallelsCord, 1.0F, width, height, orientable);
    }

    StandardLightVariant(final boolean parallelsCord, final float spacing, final float width, final float height, final Placement orientable) {
        this(parallelsCord, spacing, width, height, StandardLightVariant::standardBehavior, orientable);
    }

    StandardLightVariant(final boolean parallelsCord, final float spacing, final float width, final float height, final Function<ItemStack, LightBehavior> behaviorFactory, final Placement orientable) {
        this.parallelsCord = parallelsCord;
        this.spacing = spacing;
        this.width = width / 16;
        this.height = height / 16;
        this.behaviorFactory = behaviorFactory;
        this.placement = orientable;
    }

    @Override
    public boolean parallelsCord() {
        return this.parallelsCord;
    }

    @Override
    public float getSpacing() {
        return this.spacing;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public LightBehavior createBehavior(final ItemStack stack) {
        return this.behaviorFactory.apply(stack);
    }

    @Override
    public LightVariant.Placement getPlacement() {
        return this.placement;
    }

    private static LightBehavior standardBehavior(final ItemStack stack) {
        final CompoundNBT tag = stack.getTag();
        if (tag != null && tag.getBoolean("twinkle")) {
            return new TwinkleBehavior(0.05F, 40);
        }
        return ConstantBehavior.on();
    }
}
