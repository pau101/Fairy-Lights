package com.pau101.fairylights;

import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.util.CalendarEvent;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
	modid = FairyLights.ID,
	name = FairyLights.NAME,
	version = FairyLights.VERSION,
	dependencies = "required-after:Forge@[12.18.3.2254,);",
	guiFactory = "com.pau101.fairylights.client.gui.FairyLightsGuiFactory"
)
public final class FairyLights {
	public static final String ID = "fairylights";

	public static final String NAME = "Fairy Lights";

	public static final String VERSION = "2.0.7";

	@Instance(ID)
	public static FairyLights instance;

	@SidedProxy(
		clientSide = "com.pau101.fairylights.client.ClientProxy",
		serverSide = "com.pau101.fairylights.server.ServerProxy"
	)
	public static ServerProxy proxy;

	public static SimpleNetworkWrapper network;

	public static BlockFastener fastener;

	public static ItemLight light;

	public static ItemConnection hangingLights;

	public static ItemConnection garland;

	public static ItemConnection tinsel;

	public static ItemConnection pennantBunting;

	public static ItemConnection letterBunting;

	public static Item pennant;

	public static Item ladder;

	public static CreativeTabs fairyLightsTab;

	public static CalendarEvent christmas;

	public static JingleLibrary christmasJingles;

	public static JingleLibrary randomJingles; 

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		proxy.initConfig(event);
		proxy.initSounds();
		proxy.initGUI();
		proxy.initBlocks();
		proxy.initItems();
		proxy.initEntities();
		proxy.initRenders();
		proxy.initNetwork();
		proxy.initEggs();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initHandlers();
		proxy.initCrafting();
		proxy.initRendersLate();
	}
}
