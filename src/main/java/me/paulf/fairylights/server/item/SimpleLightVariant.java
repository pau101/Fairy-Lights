package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.DefaultBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.MeteorLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.OilLanternBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.StandardLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.TwinkleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.function.Function;

public class SimpleLightVariant<T extends LightBehavior> implements LightVariant<T> {
    public static final LightVariant<StandardLightBehavior> FAIRY = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.094D, -0.094D, -0.094D, 0.094D, 0.094D, 0.094D), SimpleLightVariant::standardBehavior, true);
    public static final LightVariant<StandardLightBehavior> PAPER = new SimpleLightVariant<>(false, 1.0F, new AxisAlignedBB(-0.250D, -0.906D, -0.250D, 0.250D, 0.091D, 0.250D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> ORB = new SimpleLightVariant<>(false, 1.0F, new AxisAlignedBB(-0.219D, -0.469D, -0.219D, 0.219D, 0.091D, 0.219D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> FLOWER = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.409D, -0.158D, -0.409D, 0.369D, 0.125D, 0.369D), SimpleLightVariant::standardBehavior, true);
    public static final LightVariant<StandardLightBehavior> ORNATE = new SimpleLightVariant<>(false, 1.5F, new AxisAlignedBB(-0.198D, -0.531D, -0.198D, 0.198D, 0.091D, 0.198D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> OIL = new SimpleLightVariant<>(false, 1.5F, new AxisAlignedBB(-0.219D, -0.656D, -0.188D, 0.219D, 0.091D, 0.188D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> JACK_O_LANTERN = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.188D, -0.375D, -0.203D, 0.188D, 0.122D, 0.188D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> SKULL = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.156D, -0.360D, -0.172D, 0.156D, 0.122D, 0.156D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> GHOST = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.201D, -0.314D, -0.201D, 0.201D, 0.125D, 0.201D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> SPIDER = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.515D, -0.774D, -0.156D, 0.515D, 0.122D, 0.156D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> WITCH = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.250D, -0.375D, -0.250D, 0.250D, 0.130D, 0.250D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> SNOWFLAKE = new SimpleLightVariant<>(true, 1.0F, new AxisAlignedBB(-0.458D, -1.007D, -0.059D, 0.458D, 0.072D, 0.059D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<StandardLightBehavior> ICICLE = new SimpleLightVariant<>(false, 0.625F, new AxisAlignedBB(-0.205D, -1.020D, -0.206D, 0.207D, 0.091D, 0.200D), SimpleLightVariant::standardBehavior);
    public static final LightVariant<MeteorLightBehavior> METEOR = new SimpleLightVariant<>(false, 1.5F, new AxisAlignedBB(-0.063D, -1.588D, -0.063D, 0.063D, 0.091D, 0.063D), stack -> {
        final int rgb = ColorLightItem.getColorValue(ColorLightItem.getLightColor(stack));
        final float red = (rgb >> 16 & 0xFF) / 255.0F;
        final float green = (rgb >> 8 & 0xFF) / 255.0F;
        final float blue = (rgb & 0xFF) / 255.0F;
        return new MeteorLightBehavior(red, green, blue);
    });
    public static final LightVariant<OilLanternBehavior> TORCH_LANTERN = new SimpleLightVariant<>(false, 1.5F, new AxisAlignedBB(-0.219D, -0.656D, -0.188D, 0.219D, 0.091D, 0.188D), stack -> new OilLanternBehavior());

    private final boolean parallelsCord;

    private final float spacing;

    private final AxisAlignedBB bounds;
    private final Function<ItemStack, T> behaviorFactory;

    private final boolean orientable;

    SimpleLightVariant(final boolean parallelsCord, final float spacing, final AxisAlignedBB bounds, final Function<ItemStack, T> behaviorFactory) {
        this(parallelsCord, spacing, bounds, behaviorFactory, false);
    }

    SimpleLightVariant(final boolean parallelsCord, final float spacing, final AxisAlignedBB bounds, final Function<ItemStack, T> behaviorFactory, final boolean orientable) {
        this.parallelsCord = parallelsCord;
        this.spacing = spacing;
        this.bounds = bounds;
        this.behaviorFactory = behaviorFactory;
        this.orientable = orientable;
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
    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    @Override
    public T createBehavior(final ItemStack stack) {
        return this.behaviorFactory.apply(stack);
    }

    @Override
    public boolean isOrientable() {
        return this.orientable;
    }

    private static StandardLightBehavior standardBehavior(final ItemStack stack) {
        final CompoundNBT tag = stack.getTag();
        final int rgb = ColorLightItem.getColorValue(ColorLightItem.getLightColor(stack));
        final float red = (rgb >> 16 & 0xFF) / 255.0F;
        final float green = (rgb >> 8 & 0xFF) / 255.0F;
        final float blue = (rgb & 0xFF) / 255.0F;
        if (tag != null && tag.getBoolean("twinkle")) {
            return new TwinkleBehavior(red, green, blue, 0.05F, 40);
        }
        return new DefaultBehavior(red, green, blue);
    }
}
