package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class FLItems {
    private FLItems() {}

    public static final DeferredRegister<Item> REG = new DeferredRegister<>(ForgeRegistries.ITEMS, FairyLights.ID);

    public static final RegistryObject<ConnectionItem> HANGING_LIGHTS = REG.register("hanging_lights", () -> new HangingLightsConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> PENNANT_BUNTING = REG.register("pennant_bunting", () -> new PennantBuntingConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> TINSEL = REG.register("tinsel", () -> new TinselConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> LETTER_BUNTING = REG.register("letter_bunting", () -> new LetterBuntingConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> GARLAND = REG.register("garland", () -> new GarlandConnectionItem(defaultProperties()));

    public static final RegistryObject<LightItem> FAIRY_LIGHT = REG.register("fairy_light", FLItems.createColorLight(FLBlocks.FAIRY_LIGHT));

    public static final RegistryObject<LightItem> PAPER_LANTERN = REG.register("paper_lantern", FLItems.createColorLight(FLBlocks.PAPER_LANTERN));

    public static final RegistryObject<LightItem> ORB_LANTERN = REG.register("orb_lantern", FLItems.createColorLight(FLBlocks.ORB_LANTERN));

    public static final RegistryObject<LightItem> FLOWER_LIGHT = REG.register("flower_light", FLItems.createColorLight(FLBlocks.FLOWER_LIGHT));

    public static final RegistryObject<LightItem> CANDLE_LANTERN_LIGHT = REG.register("candle_lantern_light", FLItems.createColorLight(FLBlocks.CANDLE_LANTERN_LIGHT));

    public static final RegistryObject<LightItem> OIL_LANTERN_LIGHT = REG.register("oil_lantern_light", FLItems.createColorLight(FLBlocks.OIL_LANTERN_LIGHT));

    public static final RegistryObject<LightItem> JACK_O_LANTERN = REG.register("jack_o_lantern", FLItems.createColorLight(FLBlocks.JACK_O_LANTERN));

    public static final RegistryObject<LightItem> SKULL_LIGHT = REG.register("skull_light", FLItems.createColorLight(FLBlocks.SKULL_LIGHT));

    public static final RegistryObject<LightItem> GHOST_LIGHT = REG.register("ghost_light", FLItems.createColorLight(FLBlocks.GHOST_LIGHT));

    public static final RegistryObject<LightItem> SPIDER_LIGHT = REG.register("spider_light", FLItems.createColorLight(FLBlocks.SPIDER_LIGHT));

    public static final RegistryObject<LightItem> WITCH_LIGHT = REG.register("witch_light", FLItems.createColorLight(FLBlocks.WITCH_LIGHT));

    public static final RegistryObject<LightItem> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLItems.createColorLight(FLBlocks.SNOWFLAKE_LIGHT));

    public static final RegistryObject<LightItem> ICICLE_LIGHTS = REG.register("icicle_lights", FLItems.createColorLight(FLBlocks.ICICLE_LIGHTS));

    public static final RegistryObject<LightItem> METEOR_LIGHT = REG.register("meteor_light", FLItems.createColorLight(FLBlocks.METEOR_LIGHT));

    public static final RegistryObject<LightItem> OIL_LANTERN = REG.register("oil_lantern", FLItems.createLight(FLBlocks.OIL_LANTERN, LightItem::new));

    public static final RegistryObject<LightItem> CANDLE_LANTERN = REG.register("candle_lantern", FLItems.createLight(FLBlocks.CANDLE_LANTERN, LightItem::new));

    public static final RegistryObject<Item> TRIANGLE_PENNANT = REG.register("triangle_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistryObject<Item> SPEARHEAD_PENNANT = REG.register("spearhead_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistryObject<Item> SWALLOWTAIL_PENNANT = REG.register("swallowtail_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistryObject<Item> SQUARE_PENNANT = REG.register("square_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistryObject<Item> LADDER = REG.register("ladder", () -> new LadderItem(defaultProperties()));

    private static Item.Properties defaultProperties() {
        return new Item.Properties().group(FairyLights.ITEM_GROUP);
    }

    private static Supplier<LightItem> createLight(final RegistryObject<LightBlock> block, final BiFunction<LightBlock, Item.Properties, LightItem> factory) {
        return () -> factory.apply(block.get(), defaultProperties().maxStackSize(16));
    }

    private static Supplier<LightItem> createColorLight(final RegistryObject<LightBlock> block) {
        return createLight(block, ColorLightItem::new);
    }

    public static Stream<LightItem> lights() {
        return REG.getEntries().stream()
            .flatMap(RegistryObject::stream)
            .filter(LightItem.class::isInstance)
            .map(LightItem.class::cast);
    }
}
