package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLItems {
	private FLItems() {}

	public static final DeferredRegister<Item> REG = new DeferredRegister<>(ForgeRegistries.ITEMS, FairyLights.ID);

	public static final RegistryObject<ItemLight> FAIRY_LIGHT = REG.register("fairy_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> PAPER_LANTERN = REG.register("paper_lantern", FLItems::createLight);

	public static final RegistryObject<ItemLight> ORB_LANTERN = REG.register("orb_lantern", FLItems::createLight);

	public static final RegistryObject<ItemLight> FLOWER_LIGHT = REG.register("flower_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> ORNATE_LANTERN = REG.register("ornate_lantern", FLItems::createLight);

	public static final RegistryObject<ItemLight> OIL_LANTERN = REG.register("oil_lantern", FLItems::createLight);

	public static final RegistryObject<ItemLight> JACK_O_LANTERN = REG.register("jack_o_lantern", FLItems::createLight);

	public static final RegistryObject<ItemLight> SKULL_LIGHT = REG.register("skull_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> GHOST_LIGHT = REG.register("ghost_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> SPIDER_LIGHT = REG.register("spider_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> WITCH_LIGHT = REG.register("witch_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLItems::createLight);

	public static final RegistryObject<ItemLight> ICICLE_LIGHTS = REG.register("icicle_lights", FLItems::createLight);

	public static final RegistryObject<ItemLight> METEOR_LIGHT = REG.register("meteor_light", FLItems::createLight);

	public static final RegistryObject<ItemConnection> HANGING_LIGHTS = REG.register("hanging_lights", () -> new ItemConnectionHangingLights(defaultProperties()));

	public static final RegistryObject<ItemConnection> GARLAND = REG.register("garland", () -> new ItemConnectionGarland(defaultProperties()));

	public static final RegistryObject<ItemConnection> TINSEL = REG.register("tinsel", () -> new ItemConnectionTinsel(defaultProperties()));

	public static final RegistryObject<ItemConnection> PENNANT_BUNTING = REG.register("pennant_bunting", () -> new ItemConnectionPennantBunting(defaultProperties()));

	public static final RegistryObject<ItemConnection> LETTER_BUNTING = REG.register("letter_bunting", () -> new ItemConnectionLetterBunting(defaultProperties()));

	public static final RegistryObject<Item> PENNANT = REG.register("pennant", () -> new ItemPennant(defaultProperties()));

	public static final RegistryObject<Item> LADDER = REG.register("ladder", () -> new ItemLadder(defaultProperties()));

	private static Item.Properties defaultProperties() {
		return new Item.Properties().group(FairyLights.fairyLightsTab);
	}

	private static ItemLight createLight() {
		return new ItemLight(defaultProperties().maxStackSize(16));
	}
}
