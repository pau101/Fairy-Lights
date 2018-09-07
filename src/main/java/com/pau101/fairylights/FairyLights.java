package com.pau101.fairylights;

import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.util.CalendarEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
	modid = FairyLights.ID,
	name = FairyLights.NAME,
	version = FairyLights.VERSION,
	dependencies = "required-after:forge@[14.23.4.2705,)",
	guiFactory = "com.pau101.fairylights.client.gui.FairyLightsGuiFactory"
)
public final class FairyLights {
	public static final String ID = "fairylights";

	public static final String NAME = "Fairy Lights";

	public static final String VERSION = "2.1.3";

	private static final class Holder {
		private static final FairyLights INSTANCE = new FairyLights();
	}

	@SidedProxy(
		clientSide = "com.pau101.fairylights.client.ClientProxy",
		serverSide = "com.pau101.fairylights.server.ServerProxy"
	)
	public static ServerProxy proxy;

	public static SimpleNetworkWrapper network;

	public static CreativeTabs fairyLightsTab;

	public static CalendarEvent christmas;

	public static JingleLibrary christmasJingles;

	public static JingleLibrary randomJingles;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		proxy.initConfig(event);
		proxy.initGUI();
		proxy.initRenders();
		proxy.initNetwork();
		proxy.initEggs();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initHandlers();
		proxy.initRendersLate();
	}

	@InstanceFactory
	public static FairyLights instance() {
		return Holder.INSTANCE;
	}
}
