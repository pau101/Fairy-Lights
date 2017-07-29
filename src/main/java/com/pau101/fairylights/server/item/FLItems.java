package com.pau101.fairylights.server.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.Utils;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(FairyLights.ID)
@EventBusSubscriber(modid = FairyLights.ID)
public final class FLItems {
	private FLItems() {}

	public static final ItemLight LIGHT = Utils.nil();

	public static final ItemConnection HANGING_LIGHTS = Utils.nil();

	public static final ItemConnection GARLAND = Utils.nil();

	public static final ItemConnection TINSEL = Utils.nil();

	public static final ItemConnection PENNANT_BUNTING = Utils.nil();

	public static final ItemConnection LETTER_BUNTING = Utils.nil();

	public static final Item PENNANT = Utils.nil();

	public static final Item LADDER = Utils.nil();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
			new ItemLight(),
			new ItemConnectionHangingLights(),
			new ItemConnectionGarland(),
			new ItemConnectionTinsel(),
			new ItemConnectionPennantBunting(),
			new ItemConnectionLetterBunting(),
			new ItemPennant(),
			new ItemLadder()
		);
	}
}
