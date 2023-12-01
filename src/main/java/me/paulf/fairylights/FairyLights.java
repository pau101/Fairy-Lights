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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.time.Month;
import java.util.function.Supplier;

@Mod(FairyLights.ID)
public final class FairyLights {
    public static final String ID = "fairylights";

    public static final ResourceLocation STRING_TYPE = new ResourceLocation(ID, "string_type");

    public static final ResourceLocation CONNECTION_TYPE = new ResourceLocation(ID, "connection_type");

    @SuppressWarnings("Convert2MethodRef")
    public static final SimpleChannel NETWORK = new NetBuilder(new ResourceLocation(ID, "net"))
        .version(1).optionalServer().requiredClient()
        .clientbound(JingleMessage::new).consumer(() -> new JingleMessage.Handler())
        .clientbound(UpdateEntityFastenerMessage::new).consumer(() -> new UpdateEntityFastenerMessage.Handler())
        .clientbound(OpenEditLetteredConnectionScreenMessage::new).consumer(() -> new OpenEditLetteredConnectionScreenMessage.Handler())
        .serverbound(InteractionConnectionMessage::new).consumer(() -> new InteractionConnectionMessage.Handler())
        .serverbound(EditLetteredConnectionMessage::new).consumer(() -> new EditLetteredConnectionMessage.Handler())
        .build();

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);

    public static final CalendarEvent HALLOWEEN = new CalendarEvent(Month.OCTOBER, 31, 31);

    public static Supplier<IForgeRegistry<ConnectionType<?>>> CONNECTION_TYPES;

    public static Supplier<IForgeRegistry<StringType>> STRING_TYPES;

    public FairyLights() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((NewRegistryEvent event) -> {
            CONNECTION_TYPES = event.create(new RegistryBuilder<ConnectionType<?>>()
                .setName(CONNECTION_TYPE)
                .disableSaving());
            STRING_TYPES = event.create(new RegistryBuilder<StringType>()
                .setName(STRING_TYPE)
                .setDefaultKey(new ResourceLocation(ID, "black_string"))
                .disableSaving());
        });
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
        FairyLightsItemGroup.TAB_REG.register(FMLJavaModLoadingContext.get().getModEventBus());

    }


}
