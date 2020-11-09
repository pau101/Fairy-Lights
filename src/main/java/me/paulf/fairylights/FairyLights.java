package me.paulf.fairylights;

import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.creativetabs.FairyLightsItemGroup;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.net.NetBuilder;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.paulf.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.CalendarEvent;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.time.Month;

@Mod(FairyLights.ID)
public final class FairyLights {
    public static final String ID = "fairylights";

    @SuppressWarnings("Convert2MethodRef")
    public static final SimpleChannel NETWORK = new NetBuilder(new ResourceLocation(ID, "net"))
        .version(1).optionalServer().requiredClient()
        .clientbound(JingleMessage::new).consumer(() -> new JingleMessage.Handler())
        .clientbound(UpdateEntityFastenerMessage::new).consumer(() -> new UpdateEntityFastenerMessage.Handler())
        .clientbound(OpenEditLetteredConnectionScreenMessage::new).consumer(() -> new OpenEditLetteredConnectionScreenMessage.Handler())
        .serverbound(InteractionConnectionMessage::new).consumer(() -> new InteractionConnectionMessage.Handler())
        .serverbound(EditLetteredConnectionMessage::new).consumer(() -> new EditLetteredConnectionMessage.Handler())
        .build();

    public static final ItemGroup ITEM_GROUP = new FairyLightsItemGroup();

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);

    public static final CalendarEvent HALLOWEEN = new CalendarEvent(Month.OCTOBER, 31, 31);

    @SuppressWarnings("unchecked")
    public static final IForgeRegistry<ConnectionType<?>> CONNECTION_TYPES = new RegistryBuilder<ConnectionType<?>>()
        .setType((Class<ConnectionType<?>>) (Class<?>) ConnectionType.class)
        .setName(new ResourceLocation(ID, "connection_type"))
        .disableSaving()
        .create();

    public static final IForgeRegistry<StringType> STRING_TYPES = new RegistryBuilder<StringType>()
        .setType(StringType.class)
        .setName(new ResourceLocation(ID, "string_type"))
        .setDefaultKey(new ResourceLocation(ID, "black_string"))
        .disableSaving()
        .create();

    public FairyLights() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FLSounds.REG.register(bus);
        FLBlocks.REG.register(bus);
        FLEntities.REG.register(bus);
        FLItems.REG.register(bus);
        FLBlockEntities.REG.register(bus);
        FLCraftingRecipes.REG.register(bus);
        ConnectionTypes.REG.register(bus);
        StringTypes.REG.register(bus);
        final ServerProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        proxy.init(bus);
    }

    public static boolean ingredientMatches(final boolean equalsExact, final ItemStack ingredient, final ItemStack stack) {
        return equalsExact || RegistryObjects.namespaceEquals(ingredient.getItem(), FairyLights.ID) && RegistryObjects.namespaceEquals(stack.getItem(), FairyLights.ID);
    }
}
