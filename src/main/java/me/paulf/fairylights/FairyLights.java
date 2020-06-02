package me.paulf.fairylights;

import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.creativetabs.FairyLightsItemGroup;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.CalendarEvent;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.time.Month;

@Mod(FairyLights.ID)
public final class FairyLights {
    public static final String ID = "fairylights";

    public ServerProxy proxy;

    public static SimpleChannel network;

    public static final ItemGroup ITEM_GROUP = new FairyLightsItemGroup();

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);

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
//        new DataGatherer().register(bus);
    }

    public void init(final FMLCommonSetupEvent event) {
        this.proxy.initIntegration();
        this.proxy.initRenders();
        this.proxy.initNetwork();
        this.proxy.initEggs();
        this.proxy.initHandlers();
    }

    public void init(final ModelRegistryEvent event) {
        this.proxy.initRendersLate();
    }

    public static boolean ingredientMatches(final boolean equalsExact, final ItemStack ingredient, final ItemStack stack) {
        return equalsExact || ingredient.getItem().isIn(FLCraftingRecipes.LIGHTS) && stack.getItem().isIn(FLCraftingRecipes.LIGHTS);
    }
}
