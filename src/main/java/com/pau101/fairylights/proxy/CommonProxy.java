package com.pau101.fairylights.proxy;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockFairyLightsFastener;
import com.pau101.fairylights.block.BlockFairyLightsFence;
import com.pau101.fairylights.config.Configurator;
import com.pau101.fairylights.creativetabs.CreativeTabsFairyLights;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.item.ItemFairyLights;
import com.pau101.fairylights.item.ItemLight;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.item.crafting.RecipeFairyLights;
import com.pau101.fairylights.item.crafting.RecipeLight;
import com.pau101.fairylights.network.FLNetworkManager;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.util.calendarevents.CalendarEvent;
import com.pau101.fairylights.world.TickHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public float getCatenaryOffset(EntityPlayer player) {
		return 0;
	}

	public ItemStack getFairyLightsFastenerPickBlock(MovingObjectPosition target, World world, int x, int y, int z, BlockFairyLightsFastener block) {
		return null;
	}

	public void initGUI() {
		FairyLights.fairyLights = new CreativeTabsFairyLights();
	}

	public void initBlocks() {
		FairyLights.fairyLightsFastener = (BlockFairyLightsFastener) registerFairyLight(new BlockFairyLightsFastener(), "fairy_lights_fastener");
		FairyLights.fairyLightsFence = (BlockFairyLightsFence) registerFairyLight(new BlockFairyLightsFence(), "fairy_lights_fence");
	}

	public void initItems() {
		FairyLights.light = registerItem(new ItemLight(), "light");
	}

	public void initCrafting() {
		GameRegistry.addRecipe(new RecipeFairyLights());
		for (LightVariant variant : LightVariant.values()) {
			GameRegistry.addRecipe(new RecipeLight(variant));
		}
		RecipeSorter.register(FairyLights.MODID + ":fairy_lights_fastener", RecipeFairyLights.class, Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register(FairyLights.MODID + ":light", RecipeLight.class, Category.SHAPED, "after:minecraft:shaped");
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
		GameRegistry.registerTileEntity(TileEntityFairyLightsFastener.class, "fairyLightsFastener");
	}

	public void initHandlers() {
		FMLCommonHandler.instance().bus().register(new TickHandler());
	}

	public void initNetwork() {
		FairyLights.networkManager = new FLNetworkManager(FairyLights.MODID);
	}

	public void initRenders() {}

	private Block registerFairyLight(Block block, String name) {
		GameRegistry.registerBlock(block, ItemFairyLights.class, name);
		return block.setBlockName(name);
	}

	private <T extends Item> T registerItem(T item, String name) {
		GameRegistry.registerItem(item, name);
		return (T) item.setUnlocalizedName(name);
	}
}
