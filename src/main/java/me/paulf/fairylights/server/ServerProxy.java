package me.paulf.fairylights.server;

import me.paulf.fairylights.*;
import me.paulf.fairylights.server.capability.*;
import me.paulf.fairylights.server.config.*;
import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.jingle.*;
import me.paulf.fairylights.server.net.clientbound.*;
import me.paulf.fairylights.server.net.serverbound.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.*;
import net.minecraftforge.fml.event.server.*;
import net.minecraftforge.fml.network.*;

import java.util.function.*;

public class ServerProxy {
    private int nextMessageId;

    public void initConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FLConfig.GENERAL_SPEC);
    }

    /*
     * |\   /|    __     __     __     __
     *  \|_|/    /  \   /  \   /  \   /  \
     *  /. .\   |%%%%| |@@@@| |####| |$$$$|
     * =\_Y_/=   \__/   \__/   \__/   \__/
     */
    public void initEggs() {
        FairyLights.christmasJingles = JingleLibrary.create("christmas");
        FairyLights.randomJingles = JingleLibrary.create("random");
        this.loadJingleLibraries();
    }

    private void loadJingleLibraries() {
        MinecraftForge.EVENT_BUS.<FMLServerAboutToStartEvent>addListener(e -> {
            final MinecraftServer server = e.getServer();
            server.getResourceManager().addReloadListener((IResourceManagerReloadListener) mgr -> JingleLibrary.loadAll(server));
        });
    }

    public void initHandlers() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        CapabilityHandler.register();
    }

    public void initNetwork() {
        final String version = "1";
        FairyLights.network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(FairyLights.ID, "net"))
            .networkProtocolVersion(() -> version)
            .clientAcceptedVersions(version::equals)
            .serverAcceptedVersions(version::equals)
            .simpleChannel();
        this.registerMessage(JingleMessage.class, JingleMessage::serialize, JingleMessage::deserialize, this.createJingleHandler());
        this.registerMessage(UpdateEntityFastenerMessage.class, UpdateEntityFastenerMessage::serialize, UpdateEntityFastenerMessage::deserialize, this.createUpdateFastenerEntityHandler());
        this.registerMessage(OpenEditLetteredConnectionScreenMessage.class, OpenEditLetteredConnectionScreenMessage::serialize, OpenEditLetteredConnectionScreenMessage::deserialize, this.createOpenEditLetteredConnectionGUIHandler());
        this.registerMessage(InteractionConnectionMessage.class, InteractionConnectionMessage::serialize, InteractionConnectionMessage::deserialize, this.createConnectionInteractionHandler());
        this.registerMessage(EditLetteredConnectionMessage.class, EditLetteredConnectionMessage::serialize, EditLetteredConnectionMessage::deserialize, this.createEditLetteredConnectionHandler());
    }

    private <T> BiConsumer<T, Supplier<NetworkEvent.Context>> noHandler() {
        return (msg, ctx) -> ctx.get().setPacketHandled(true);
    }

    protected BiConsumer<JingleMessage, Supplier<NetworkEvent.Context>> createJingleHandler() {
        return this.noHandler();
    }

    protected BiConsumer<UpdateEntityFastenerMessage, Supplier<NetworkEvent.Context>> createUpdateFastenerEntityHandler() {
        return this.noHandler();
    }

    protected BiConsumer<OpenEditLetteredConnectionScreenMessage, Supplier<NetworkEvent.Context>> createOpenEditLetteredConnectionGUIHandler() {
        return this.noHandler();
    }

    protected BiConsumer<InteractionConnectionMessage, Supplier<NetworkEvent.Context>> createConnectionInteractionHandler() {
        return new InteractionConnectionMessage.Handler();
    }

    protected BiConsumer<EditLetteredConnectionMessage, Supplier<NetworkEvent.Context>> createEditLetteredConnectionHandler() {
        return new EditLetteredConnectionMessage.Handler();
    }

    public void initRenders() {}

    public void initRendersLate() {}

    public static void sendToPlayersWatchingChunk(final Object message, final World world, final BlockPos pos) {
        FairyLights.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static void sendToPlayersWatchingEntity(final Object message, final World world, final Entity entity) {
        FairyLights.network.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public static BlockView buildBlockView() {
        final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.getView();
    }

    private <MSG> void registerMessage(final Class<MSG> clazz, final BiConsumer<MSG, PacketBuffer> encoder, final Function<PacketBuffer, MSG> decoder, final BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        FairyLights.network.messageBuilder(clazz, this.nextMessageId++)
            .encoder(encoder).decoder(decoder)
            .consumer(consumer)
            .add();
    }

    public void initIntegration() {
		/*if (Loader.isModLoaded(ValkyrienWarfareMod.MODID)) {
			final Class<?> vw;
			try {
				vw = Class.forName("ValkyrienWarfare");
			} catch (final ClassNotFoundException e) {
				throw new AssertionError(e);
			}
			MinecraftForge.EVENT_BUS.register(vw);
		}*/
    }
}
