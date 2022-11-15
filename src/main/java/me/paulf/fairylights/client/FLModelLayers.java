package me.paulf.fairylights.client;

import me.paulf.fairylights.FairyLights;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class FLModelLayers {

    public static final ModelLayerLocation BOW = main("bow");
    public static final ModelLayerLocation GARLAND_RINGS = main("garland_rings");
    public static final ModelLayerLocation TINSEL_STRIP = main("tinsel_strip");
    public static final ModelLayerLocation FAIRY_LIGHT = main("fairy_light");
    public static final ModelLayerLocation PAPER_LANTERN = main("paper_lantern");
    public static final ModelLayerLocation ORB_LANTERN = main("orb_lantern");
    public static final ModelLayerLocation FLOWER_LIGHT = main("flower_light");
    public static final ModelLayerLocation CANDLE_LANTERN_LIGHT = main("color_candle_lantern");
    public static final ModelLayerLocation OIL_LANTERN_LIGHT = main("color_oil_lantern_light");
    public static final ModelLayerLocation JACK_O_LANTERN = main("jack_o_lantern");
    public static final ModelLayerLocation SKULL_LIGHT = main("skull_light");
    public static final ModelLayerLocation GHOST_LIGHT = main("ghost_light");
    public static final ModelLayerLocation SPIDER_LIGHT = main("spider_light");
    public static final ModelLayerLocation WITCH_LIGHT = main("witch_light");
    public static final ModelLayerLocation SNOWFLAKE_LIGHT = main("snowflake_light");
    public static final ModelLayerLocation HEART_LIGHT = main("heart_light");
    public static final ModelLayerLocation MOON_LIGHT = main("moon_light");
    public static final ModelLayerLocation STAR_LIGHT = main("star_light");
    public static final ModelLayerLocation ICICLE_LIGHTS_1 = main("icicle_lights_1");
    public static final ModelLayerLocation ICICLE_LIGHTS_2 = main("icicle_lights_2");
    public static final ModelLayerLocation ICICLE_LIGHTS_3 = main("icicle_lights_3");
    public static final ModelLayerLocation ICICLE_LIGHTS_4 = main("icicle_lights_4");
    public static final ModelLayerLocation METEOR_LIGHT = main("meteor_light");
    public static final ModelLayerLocation OIL_LANTERN = main("oil_lantern");
    public static final ModelLayerLocation CANDLE_LANTERN = main("candle_lantern");
    public static final ModelLayerLocation INCANDESCENT_LIGHT = main("incandescent_light");
    public static final ModelLayerLocation LETTER_WIRE = main("letter_wire");
    public static final ModelLayerLocation PENNANT_WIRE = main("pennant_wire");
    public static final ModelLayerLocation TINSEL_WIRE = main("tinsel_wire");
    public static final ModelLayerLocation VINE_WIRE = main("vine_wire");
    public static final ModelLayerLocation LIGHTS_WIRE = main("lights_wire");

    private static ModelLayerLocation main(String name) {
        return layer(name, "main");
    }

    private static ModelLayerLocation layer(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(FairyLights.ID, name), layer);
    }
}
