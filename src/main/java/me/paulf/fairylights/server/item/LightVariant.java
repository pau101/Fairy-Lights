package me.paulf.fairylights.server.item;

import me.paulf.fairylights.util.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.*;

import java.util.function.*;

public enum LightVariant {
    FAIRY("fairy_light", () -> FLItems.FAIRY_LIGHT, true, 5, 5,
        Placement.ONWARD
    ),
    PAPER("paper_lantern", () -> FLItems.PAPER_LANTERN, false, 9, 16.5F,
        Placement.UPRIGHT
    ),
    ORB("orb_lantern", () -> FLItems.ORB_LANTERN, false, 10, 11.5F,
        Placement.UPRIGHT
    ),
    FLOWER("flower_light", () -> FLItems.FLOWER_LIGHT, true, 10, 6,
        Placement.OUTWARD
    ),
    ORNATE("ornate_lantern", () -> FLItems.ORNATE_LANTERN, false, 24, 8, 12,
        Placement.UPRIGHT
    ),
    OIL("oil_lantern", () -> FLItems.OIL_LANTERN, false, 32, 8, 13.5F,
        Placement.UPRIGHT
    ),
    JACK_O_LANTERN("jack_o_lantern", () -> FLItems.JACK_O_LANTERN, true, 7, 9,
        Placement.UPRIGHT
    ),
    SKULL("skull_light", () -> FLItems.SKULL_LIGHT, true, 6, 9,
        Placement.UPRIGHT
    ),
    GHOST("ghost_light", () -> FLItems.GHOST_LIGHT, true, 6, 8,
        Placement.UPRIGHT
    ),
    SPIDER("spider_light", () -> FLItems.SPIDER_LIGHT, true, 12, 14,
        Placement.UPRIGHT
    ),
    WITCH("witch_light", () -> FLItems.WITCH_LIGHT, true, 8, 10,
        Placement.UPRIGHT
    ),
    SNOWFLAKE("snowflake_light", () -> FLItems.SNOWFLAKE_LIGHT, true, 8, 12.5F,
        Placement.UPRIGHT
    ),
    ICICLE("icicle_lights", () -> FLItems.ICICLE_LIGHTS, false, 0.625F, 7, 20,
        Placement.UPRIGHT
    ),
    METEOR("meteor_light", () -> FLItems.METEOR_LIGHT, false, 1.5F, 3, 28.5F,
        0.02F, 100,
        Placement.UPRIGHT
    );

    private final String name;

    private final Supplier<RegistryObject<? extends Item>> item;

    private final boolean parallelsCord;

    private final float spacing;

    private final float width;

    private final float height;

    private final float twinkleChance;

    private final int tickCycle;

    private final boolean alwaysTwinkle;

    private final Placement placement;

    LightVariant(final String name, final Supplier<RegistryObject<? extends Item>> item, final boolean parallelsCord, final float width, final float height, final Placement orientable) {
        this(name, item, parallelsCord, 1.0F, width, height, orientable);
    }

    LightVariant(final String name, final Supplier<RegistryObject<? extends Item>> item, final boolean parallelsCord, final float spacing, final float width, final float height, final Placement orientable) {
        this(name, item, parallelsCord, spacing, width, height, 0.05F, 40, false, orientable);
    }

    LightVariant(final String name, final Supplier<RegistryObject<? extends Item>> item, final boolean parallelsCord, final float spacing, final float width, final float height, final float twinkleChance, final int tickCycle, final Placement orientable) {
        this(name, item, parallelsCord, spacing, width, height, twinkleChance, tickCycle, true, orientable);
    }

    LightVariant(final String name, final Supplier<RegistryObject<? extends Item>> item, final boolean parallelsCord, final float spacing, final float width, final float height, final float twinkleChance, final int tickCycle, final boolean alwaysTwinkle, final Placement orientable) {
        this.name = name;
        this.item = item;
        this.parallelsCord = parallelsCord;
        this.spacing = spacing;
        this.width = width / 16;
        this.height = height / 16;
        this.twinkleChance = twinkleChance;
        this.tickCycle = tickCycle;
        this.alwaysTwinkle = alwaysTwinkle;
        this.placement = orientable;
    }

    public String getName() {
        return this.name;
    }

    public Item getItem() {
        return this.item.get().orElseThrow(IllegalStateException::new);
    }

    public boolean parallelsCord() {
        return this.parallelsCord;
    }

    public float getSpacing() {
        return this.spacing;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getTwinkleChance() {
        return this.twinkleChance;
    }

    public int getTickCycle() {
        return this.tickCycle;
    }

    public boolean alwaysDoTwinkleLogic() {
        return this.alwaysTwinkle;
    }

    public Placement getPlacement() {
        return this.placement;
    }

    public static LightVariant getLightVariant(final int index) {
        return Utils.getEnumValue(LightVariant.class, index);
    }

    public enum Placement {
        UPRIGHT,
        OUTWARD,
        ONWARD
    }
}
