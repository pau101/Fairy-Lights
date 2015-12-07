package com.pau101.fairylights.proxy;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.block.BlockConnectionFastenerFence;
import com.pau101.fairylights.config.Configurator;
import com.pau101.fairylights.creativetabs.CreativeTabsFairyLights;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.item.ItemConnectionFairyLights;
import com.pau101.fairylights.item.ItemConnectionGarland;
import com.pau101.fairylights.item.ItemConnectionTinsel;
import com.pau101.fairylights.item.ItemLegacySupportFairyLightsFastener;
import com.pau101.fairylights.item.ItemLight;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.item.crafting.RecipeDyeColorNBT;
import com.pau101.fairylights.item.crafting.RecipeFairyLights;
import com.pau101.fairylights.network.FLNetworkManager;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.util.CalendarEvent;
import com.pau101.fairylights.world.TickHandler;

public class CommonProxy {
	public ItemStack getConnectionFastenerPickBlock(MovingObjectPosition target, World world, BlockPos pos, BlockConnectionFastener block) {
		return null;
	}

	public void initGUI() {
		FairyLights.fairyLightsTab = new CreativeTabsFairyLights();
	}

	public void initBlocks() {
		FairyLights.connectionFastener = (BlockConnectionFastener) registerFairyLights(new BlockConnectionFastener(), "fairy_lights_fastener");
		FairyLights.fences = Arrays.asList(new Block[] { Blocks.oak_fence, Blocks.nether_brick_fence, Blocks.spruce_fence, Blocks.birch_fence, Blocks.jungle_fence, Blocks.dark_oak_fence, Blocks.acacia_fence });
		FairyLights.fastenerFences = new BlockConnectionFastenerFence[FairyLights.fences.size()];
		FairyLights.fastenerFenceToNormalFenceMap = new HashMap<BlockConnectionFastenerFence, BlockFence>();
		FairyLights.normalFenceToFastenerFenceMap = new HashMap<BlockFence, BlockConnectionFastenerFence>();
		for (int i = 0; i < FairyLights.fences.size(); i++) {
			BlockFence fence = (BlockFence) FairyLights.fences.get(i);
			FairyLights.fastenerFences[i] = (BlockConnectionFastenerFence) registerFairyLights(new BlockConnectionFastenerFence(fence), "fairy_lights_" + fence.getUnlocalizedName().substring(5));
			FairyLights.fastenerFenceToNormalFenceMap.put(FairyLights.fastenerFences[i], fence);
			FairyLights.normalFenceToFastenerFenceMap.put(fence, FairyLights.fastenerFences[i]);
		}
	}

	public void initItems() {
		FairyLights.light = registerItem(new ItemLight(), "light");
		FairyLights.fairyLights = registerItem(new ItemConnectionFairyLights(), "fairy_lights");
		FairyLights.garland = registerItem(new ItemConnectionGarland(), "garland");
		FairyLights.tinsel = registerItem(new ItemConnectionTinsel(), "tinsel");
	}

	public void initCrafting() {
		GameRegistry.addRecipe(new RecipeFairyLights());
		for (LightVariant variant : LightVariant.values()) {
			GameRegistry.addRecipe(new RecipeDyeColorNBT(variant.getCraftingResult(), variant.getCraftingRecipe()));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FairyLights.garland, 2), "I-I", 'I', "ingotIron", '-', Item.getItemFromBlock(Blocks.vine)));
		GameRegistry.addRecipe(new RecipeDyeColorNBT(new ItemStack(FairyLights.tinsel, 2), new Object[] { " P ", "I-I", " D ", 'P', Items.paper, 'I', "ingotIron", '-', Items.string, 'D', "dye" }));
		RecipeSorter.register(FairyLights.MODID + ":fairy_lights", RecipeFairyLights.class, Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register(FairyLights.MODID + ":dye_color_nbt", RecipeDyeColorNBT.class, Category.SHAPED, "after:minecraft:shaped");
	}

	/**
	 * <pre>
	 * |\   /|    __     __
	 *  \|_|/    /  \   /  \
	 *  /. .\   |%%%%| |%%%%|
	 * =\_Y_/=   \__/   \__/
	 * </pre>
	 */
	public void initEggs() {
		Calendar now = Calendar.getInstance();
		GregorianCalendar upcomingChristmas = new GregorianCalendar(now.get(Calendar.YEAR), 11, 24);
		if (now.after(upcomingChristmas)) {
			upcomingChristmas.add(Calendar.YEAR, 1);
		}
		FairyLights.christmas = new CalendarEvent(upcomingChristmas);
		FairyLights.christmas.setLengthDays(3);
		Jingle.initJingles(Configurator.jingles);
	}

	public void initEntities() {
		GameRegistry.registerTileEntity(TileEntityConnectionFastener.class, "fairyLightsFastener");
	}

	public void initHandlers() {
        MinecraftForge.EVENT_BUS.register(new TickHandler());
	}

	public void initNetwork() {
		FairyLights.networkManager = new FLNetworkManager(FairyLights.MODID);
	}

	public void initRenders() {}

	private Block registerFairyLights(Block block, String name) {
		GameRegistry.registerBlock(block, ItemLegacySupportFairyLightsFastener.class, name);
		return block.setUnlocalizedName(name);
	}

	private <T extends Item> T registerItem(T item, String name) {
		GameRegistry.registerItem(item, name);
		return (T) item.setUnlocalizedName(name);
	}
}
