package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public final class FLItems {
    private FLItems() {}

    public static final DeferredRegister<Item> REG = new DeferredRegister<>(ForgeRegistries.ITEMS, FairyLights.ID);

    public static final RegistryObject<LightItem> FAIRY_LIGHT = REG.register("fairy_light", FLItems.createLight(FLBlocks.FAIRY_LIGHT));

    public static final RegistryObject<LightItem> PAPER_LANTERN = REG.register("paper_lantern", FLItems.createLight(FLBlocks.PAPER_LANTERN));

    public static final RegistryObject<LightItem> ORB_LANTERN = REG.register("orb_lantern", FLItems.createLight(FLBlocks.ORB_LANTERN));

    public static final RegistryObject<LightItem> FLOWER_LIGHT = REG.register("flower_light", FLItems.createLight(FLBlocks.FLOWER_LIGHT));

    public static final RegistryObject<LightItem> ORNATE_LANTERN = REG.register("ornate_lantern", FLItems.createLight(FLBlocks.ORNATE_LANTERN));

    public static final RegistryObject<LightItem> OIL_LANTERN = REG.register("oil_lantern", FLItems.createLight(FLBlocks.OIL_LANTERN));

    public static final RegistryObject<LightItem> JACK_O_LANTERN = REG.register("jack_o_lantern", FLItems.createLight(FLBlocks.JACK_O_LANTERN));

    public static final RegistryObject<LightItem> SKULL_LIGHT = REG.register("skull_light", FLItems.createLight(FLBlocks.SKULL_LIGHT));

    public static final RegistryObject<LightItem> GHOST_LIGHT = REG.register("ghost_light", FLItems.createLight(FLBlocks.GHOST_LIGHT));

    public static final RegistryObject<LightItem> SPIDER_LIGHT = REG.register("spider_light", FLItems.createLight(FLBlocks.SPIDER_LIGHT));

    public static final RegistryObject<LightItem> WITCH_LIGHT = REG.register("witch_light", FLItems.createLight(FLBlocks.WITCH_LIGHT));

    public static final RegistryObject<LightItem> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLItems.createLight(FLBlocks.SNOWFLAKE_LIGHT));

    public static final RegistryObject<LightItem> ICICLE_LIGHTS = REG.register("icicle_lights", FLItems.createLight(FLBlocks.ICICLE_LIGHTS));

    public static final RegistryObject<LightItem> METEOR_LIGHT = REG.register("meteor_light", FLItems.createLight(FLBlocks.METEOR_LIGHT));

    public static final RegistryObject<ConnectionItem> HANGING_LIGHTS = REG.register("hanging_lights", () -> new HangingLightsConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> GARLAND = REG.register("garland", () -> new GarlandConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> TINSEL = REG.register("tinsel", () -> new TinselConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> PENNANT_BUNTING = REG.register("pennant_bunting", () -> new PennantBuntingConnectionItem(defaultProperties()));

    public static final RegistryObject<ConnectionItem> LETTER_BUNTING = REG.register("letter_bunting", () -> new LetterBuntingConnectionItem(defaultProperties()));

    public static final RegistryObject<Item> PENNANT = REG.register("pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistryObject<Item> LADDER = REG.register("ladder", () -> new LadderItem(defaultProperties()));

    private static Item.Properties defaultProperties() {
        return new Item.Properties().group(FairyLights.fairyLightsTab);
    }

    private static Supplier<LightItem> createLight(final RegistryObject<LightBlock> block) {
        LogManager.getLogger().info("waldo {}", block);
        return () -> new LightItem(block.orElseThrow(IllegalStateException::new), defaultProperties().maxStackSize(16));
    }
}
