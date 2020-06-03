package me.paulf.fairylights.server.item;

public enum StandardLightVariant implements LightVariant {
    FAIRY("fairy_light", true, 5, 5, LightVariant.Placement.ONWARD),
    PAPER("paper_lantern", false, 9, 16.5F, LightVariant.Placement.UPRIGHT),
    ORB("orb_lantern", false, 10, 11.5F, LightVariant.Placement.UPRIGHT),
    FLOWER("flower_light", true, 10, 6, LightVariant.Placement.OUTWARD),
    ORNATE("ornate_lantern", false, 24, 8, 11, LightVariant.Placement.UPRIGHT),
    OIL("oil_lantern", false, 32, 8, 13, LightVariant.Placement.UPRIGHT),
    JACK_O_LANTERN("jack_o_lantern", true, 7, 9, LightVariant.Placement.UPRIGHT),
    SKULL("skull_light", true, 6, 9, LightVariant.Placement.UPRIGHT),
    GHOST("ghost_light", true, 6, 8, LightVariant.Placement.UPRIGHT),
    SPIDER("spider_light", true, 12, 14, LightVariant.Placement.UPRIGHT),
    WITCH("witch_light", true, 8, 10, LightVariant.Placement.UPRIGHT),
    SNOWFLAKE("snowflake_light", true, 10.0F, 15.0F, LightVariant.Placement.UPRIGHT),
    ICICLE("icicle_lights", false, 0.625F, 7, 20, LightVariant.Placement.UPRIGHT),
    METEOR("meteor_light", false, 1.5F, 3, 28.5F, 0.02F, 100, LightVariant.Placement.UPRIGHT);

    private final String name;

    private final boolean parallelsCord;

    private final float spacing;

    private final float width;

    private final float height;

    private final float twinkleChance;

    private final int tickCycle;

    private final boolean alwaysTwinkle;

    private final LightVariant.Placement placement;

    StandardLightVariant(final String name, final boolean parallelsCord, final float width, final float height, final LightVariant.Placement orientable) {
        this(name, parallelsCord, 1.0F, width, height, orientable);
    }

    StandardLightVariant(final String name, final boolean parallelsCord, final float spacing, final float width, final float height, final LightVariant.Placement orientable) {
        this(name, parallelsCord, spacing, width, height, 0.05F, 40, false, orientable);
    }

    StandardLightVariant(final String name, final boolean parallelsCord, final float spacing, final float width, final float height, final float twinkleChance, final int tickCycle, final LightVariant.Placement orientable) {
        this(name, parallelsCord, spacing, width, height, twinkleChance, tickCycle, true, orientable);
    }

    StandardLightVariant(final String name, final boolean parallelsCord, final float spacing, final float width, final float height, final float twinkleChance, final int tickCycle, final boolean alwaysTwinkle, final LightVariant.Placement orientable) {
        this.name = name;
        this.parallelsCord = parallelsCord;
        this.spacing = spacing;
        this.width = width / 16;
        this.height = height / 16;
        this.twinkleChance = twinkleChance;
        this.tickCycle = tickCycle;
        this.alwaysTwinkle = alwaysTwinkle;
        this.placement = orientable;
    }

    @Override
    public String getName() {
        return this.name;
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
    public float getTwinkleChance() {
        return this.twinkleChance;
    }

    @Override
    public int getTickCycle() {
        return this.tickCycle;
    }

    @Override
    public boolean alwaysDoTwinkleLogic() {
        return this.alwaysTwinkle;
    }

    @Override
    public LightVariant.Placement getPlacement() {
        return this.placement;
    }
}
