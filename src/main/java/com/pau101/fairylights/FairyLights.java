package com.pau101.fairylights;

import net.minecraft.creativetab.CreativeTabs;

import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.block.BlockConnectionFastenerFence;
import com.pau101.fairylights.config.Configurator;
import com.pau101.fairylights.item.ItemConnection;
import com.pau101.fairylights.item.ItemLight;
import com.pau101.fairylights.network.FLNetworkManager;
import com.pau101.fairylights.proxy.CommonProxy;
import com.pau101.fairylights.util.CalendarEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = FairyLights.MODID, name = FairyLights.NAME, version = FairyLights.VERSION)
public class FairyLights {
	public static final String MODID = "fairylights";

	public static final String NAME = "Fairy Lights";

	public static final String VERSION = "1.4.0";

	public static final int MAX_LENGTH = 20;

	@Instance
	public static FairyLights instance;

	@SidedProxy(clientSide = "com.pau101.fairylights.proxy.ClientProxy", serverSide = "com.pau101.fairylights.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static FLNetworkManager networkManager;

	public static BlockConnectionFastener connectionFastener;

	public static BlockConnectionFastenerFence fastenerFence;

	public static ItemLight light;

	public static ItemConnection fairyLights;

	public static ItemConnection garland;

	public static ItemConnection tinsel;

	public static CreativeTabs fairyLightsTab;

	public static CalendarEvent christmas;

	public static boolean isShadersModInstalled = false;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		Configurator.initConfig(event);
		FMLCommonHandler.instance().bus().register(Configurator.class);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initGUI();
		proxy.initBlocks();
		proxy.initItems();
		proxy.initCrafting();
		proxy.initEntities();
		proxy.initRenders();
		proxy.initHandlers();
		proxy.initNetwork();
		proxy.initEggs();
	}

	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		try {
			Class.forName("shadersmod.client.Shaders");
			isShadersModInstalled = true;
		} catch (ClassNotFoundException e) {}
	}
}
