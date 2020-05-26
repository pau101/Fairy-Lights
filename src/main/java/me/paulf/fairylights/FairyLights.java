package me.paulf.fairylights;

import me.paulf.fairylights.client.*;
import me.paulf.fairylights.server.*;
import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.block.entity.*;
import me.paulf.fairylights.server.creativetabs.*;
import me.paulf.fairylights.server.entity.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.server.item.crafting.*;
import me.paulf.fairylights.server.jingle.*;
import me.paulf.fairylights.server.sound.*;
import me.paulf.fairylights.util.*;
import net.minecraft.item.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.*;
import net.minecraftforge.fml.network.simple.*;

import java.time.*;

@Mod(FairyLights.ID)
public final class FairyLights {
    public static final String ID = "fairylights";

    public ServerProxy proxy;

    public static SimpleChannel network;

    public static ItemGroup fairyLightsTab = new FairyLightsItemGroup();

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);

    public static JingleLibrary christmasJingles;

    public static JingleLibrary randomJingles;

    public FairyLights() {
        this.proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FLSounds.REG.register(bus);
        FLBlocks.REG.register(bus);
        FLEntities.REG.register(bus);
        FLItems.REG.register(bus);
        FLBlockEntities.REG.register(bus);
        FLCraftingRecipes.REG.register(bus);
        bus.<FMLCommonSetupEvent>addListener(this::init);
        bus.<ModelRegistryEvent>addListener(this::init);
    }

    public void init(final FMLCommonSetupEvent event) {
        this.proxy.initConfig();
        this.proxy.initIntegration();
        this.proxy.initRenders();
        this.proxy.initNetwork();
        this.proxy.initEggs();
        this.proxy.initHandlers();
    }

    public void init(final ModelRegistryEvent event) {
        this.proxy.initRendersLate();
    }
}
