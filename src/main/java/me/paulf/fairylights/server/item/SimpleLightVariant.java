package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.DefaultBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.MeteorLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.StandardLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.TwinkleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Function;

public class SimpleLightVariant<T extends LightBehavior> implements LightVariant<T> {
    public static final LightVariant<StandardLightBehavior> FAIRY = new SimpleLightVariant<>(true, 1.0F, 5, 5, SimpleLightVariant::standardBehavior, Placement.ONWARD);
    public static final LightVariant<StandardLightBehavior> PAPER = new SimpleLightVariant<>(false, 1.0F, 9, 16.5F, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> ORB = new SimpleLightVariant<>(false, 1.0F, 10, 11.5F, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> FLOWER = new SimpleLightVariant<>(true, 1.0F, 10, 6, SimpleLightVariant::standardBehavior, Placement.OUTWARD);
    public static final LightVariant<StandardLightBehavior> ORNATE = new SimpleLightVariant<>(false, 24, 8, 11, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> OIL = new SimpleLightVariant<>(false, 32, 8, 13, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> JACK_O_LANTERN = new SimpleLightVariant<>(true, 1.0F, 7, 9, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> SKULL = new SimpleLightVariant<>(true, 1.0F, 6, 9, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> GHOST = new SimpleLightVariant<>(true, 1.0F, 6, 8, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> SPIDER = new SimpleLightVariant<>(true, 1.0F, 12, 14, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> WITCH = new SimpleLightVariant<>(true, 1.0F, 8, 10, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> SNOWFLAKE = new SimpleLightVariant<>(true, 1.0F, 10.0F, 15.0F, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<StandardLightBehavior> ICICLE = new SimpleLightVariant<>(false, 0.625F, 7, 20, SimpleLightVariant::standardBehavior, Placement.UPRIGHT);
    public static final LightVariant<MeteorLightBehavior> METEOR = new SimpleLightVariant<>(false, 1.5F, 3, 28.5F, stack -> {
        final int rgb = LightItem.getColorValue(LightItem.getLightColor(stack));
        final float red = (rgb >> 16 & 0xFF) / 255.0F;
        final float green = (rgb >> 8 & 0xFF) / 255.0F;
        final float blue = (rgb & 0xFF) / 255.0F;
        return new MeteorLightBehavior(red, green, blue);
    }, LightVariant.Placement.UPRIGHT);

    private final boolean parallelsCord;

    private final float spacing;

    private final float width;

    private final float height;

    private final Function<ItemStack, T> behaviorFactory;

    private final LightVariant.Placement placement;

    SimpleLightVariant(final boolean parallelsCord, final float spacing, final float width, final float height, final Function<ItemStack, T> behaviorFactory, final Placement orientable) {
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
    public T createBehavior(final ItemStack stack) {
        return this.behaviorFactory.apply(stack);
    }

    @Override
    public LightVariant.Placement getPlacement() {
        return this.placement;
    }

    private static StandardLightBehavior standardBehavior(final ItemStack stack) {
        final CompoundNBT tag = stack.getTag();
        final int rgb = LightItem.getColorValue(LightItem.getLightColor(stack));
        final float red = (rgb >> 16 & 0xFF) / 255.0F;
        final float green = (rgb >> 8 & 0xFF) / 255.0F;
        final float blue = (rgb & 0xFF) / 255.0F;
        if (tag != null && tag.getBoolean("twinkle")) {
            return new TwinkleBehavior(red, green, blue, 0.05F, 40);
        }
        return new DefaultBehavior(red, green, blue);
    }
}
