package com.pau101.fairylights.client.model;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.renderer.block.FastenerStateMapper;
import com.pau101.fairylights.server.block.FLBlocks;
import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.item.LightVariant;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = FairyLights.ID)
public final class FLModels {
	private FLModels() {}

	@SubscribeEvent
	public static void register(ModelRegistryEvent event) {
		LightVariant[] lightVariants = LightVariant.values();
		for (int var = 0; var < lightVariants.length; var++) {
			LightVariant variant = lightVariants[var];
			ModelResourceLocation model = new ModelResourceLocation(FairyLights.ID + ":light_" + variant.getName(), "inventory");
			for (int color = 0, meta = var * ItemLight.COLOR_COUNT; color < ItemLight.COLOR_COUNT; meta++, color++) {
				setModel(FLItems.LIGHT, meta, model);
			}
		}
		setModel(FLItems.HANGING_LIGHTS, "hanging_lights");
		setModel(FLItems.GARLAND, "garland");
		setModel(FLItems.TINSEL, "tinsel");
		setModel(FLItems.PENNANT_BUNTING, "pennant_bunting");
		setModel(FLItems.LETTER_BUNTING, "letter_bunting");
		for (int meta = 0; meta < ItemLight.COLOR_COUNT; meta++) {
			setModel(FLItems.PENNANT, meta, "pennant");
		}
		setModel(FLItems.LADDER, "ladder");
		ModelLoader.setCustomStateMapper(FLBlocks.FASTENER, new FastenerStateMapper());
	}

	private static void setModel(Item item, String id) {
		setModel(item, 0, id);
	}

	private static void setModel(Item item, int meta, String id) {
		setModel(item, meta, new ModelResourceLocation(FairyLights.ID + ':' + id, "inventory"));
	}

	private static void setModel(Item item, int meta, ModelResourceLocation model) {
		ModelLoader.setCustomModelResourceLocation(item, meta, model);
	}
}
