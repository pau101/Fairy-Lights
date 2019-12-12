package com.pau101.fairylights;

import com.pau101.fairylights.client.ClientProxy;
import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.block.FLBlocks;
import com.pau101.fairylights.server.block.entity.FLBlockEntities;
import com.pau101.fairylights.server.creativetabs.CreativeTabsFairyLights;
import com.pau101.fairylights.server.entity.FLEntities;
import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.item.crafting.Recipes;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.CalendarEvent;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(FairyLights.ID)
public final class FairyLights {
	public static final String ID = "fairylights";

	public static ServerProxy proxy;

	public static SimpleChannel network;

	public static ItemGroup fairyLightsTab = new CreativeTabsFairyLights();

	public static CalendarEvent christmas;

	public static JingleLibrary christmasJingles;

	public static JingleLibrary randomJingles;

	public FairyLights() {
		proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		FLSounds.REG.register(bus);
		FLBlocks.REG.register(bus);
		FLEntities.REG.register(bus);
		FLItems.REG.register(bus);
		FLBlockEntities.REG.register(bus);
		Recipes.REG.register(bus);
		bus.<FMLCommonSetupEvent>addListener(this::init);
		bus.<ModelRegistryEvent>addListener(this::init);
	}

	public void init(FMLCommonSetupEvent event) {
		proxy.initConfig();
		proxy.initIntegration();
		proxy.initGUI();
		proxy.initRenders();
		proxy.initNetwork();
		proxy.initEggs();
		proxy.initHandlers();
	}

	public void init(ModelRegistryEvent event) {
		proxy.initRendersLate();
	}
}
