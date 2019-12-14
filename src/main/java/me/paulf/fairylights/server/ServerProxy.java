package me.paulf.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.config.FLConfig;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.server.fastener.CreateBlockViewEvent;
import me.paulf.fairylights.server.fastener.RegularBlockView;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.paulf.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.paulf.fairylights.util.CalendarEvent;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;

import java.time.Month;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerProxy {
	private int nextMessageId;

	public void initConfig() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FLConfig.GENERAL_SPEC);
	}

	public void initGUI() {
	}

	/*
	 * |\   /|    __     __     __     __
	 *  \|_|/    /  \   /  \   /  \   /  \
	 *  /. .\   |%%%%| |@@@@| |####| |$$$$|
	 * =\_Y_/=   \__/   \__/   \__/   \__/
	 */
	public void initEggs() {
		FairyLights.christmas = new CalendarEvent(Month.DECEMBER, 24, 26);
		FairyLights.christmasJingles = JingleLibrary.create("christmas");
		FairyLights.randomJingles = JingleLibrary.create("random");
		loadJingleLibraries();
	}

	protected void loadJingleLibraries() {
		JingleLibrary.loadAll();
	}

	public void initHandlers() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        CapabilityHandler.register();
	}

	public void initNetwork() {
		String version = "1";
		FairyLights.network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(FairyLights.ID, "net"))
			.networkProtocolVersion(() -> version)
			.clientAcceptedVersions(version::equals)
			.serverAcceptedVersions(version::equals)
			.simpleChannel();
		registerMessage(JingleMessage.class, JingleMessage::serialize, JingleMessage::deserialize, createJingleHandler());
		registerMessage(UpdateEntityFastenerMessage.class, UpdateEntityFastenerMessage::serialize, UpdateEntityFastenerMessage::deserialize, createUpdateFastenerEntityHandler());
		registerMessage(OpenEditLetteredConnectionScreenMessage.class, OpenEditLetteredConnectionScreenMessage::serialize, OpenEditLetteredConnectionScreenMessage::deserialize, createOpenEditLetteredConnectionGUIHandler());
		registerMessage(InteractionConnectionMessage.class, InteractionConnectionMessage::serialize, InteractionConnectionMessage::deserialize, createConnectionInteractionHandler());
		registerMessage(EditLetteredConnectionMessage.class, EditLetteredConnectionMessage::serialize, EditLetteredConnectionMessage::deserialize, createEditLetteredConnectionHandler());
	}

	private <T> BiConsumer<T, Supplier<NetworkEvent.Context>> noHandler() {
		return (msg, ctx) -> ctx.get().setPacketHandled(true);
	}

	protected BiConsumer<JingleMessage, Supplier<NetworkEvent.Context>> createJingleHandler() {
		return noHandler();
	}

	protected BiConsumer<UpdateEntityFastenerMessage, Supplier<NetworkEvent.Context>> createUpdateFastenerEntityHandler() {
		return noHandler();
	}

	protected BiConsumer<OpenEditLetteredConnectionScreenMessage, Supplier<NetworkEvent.Context>> createOpenEditLetteredConnectionGUIHandler() {
		return noHandler();
	}

	protected BiConsumer<InteractionConnectionMessage, Supplier<NetworkEvent.Context>> createConnectionInteractionHandler() {
		return new InteractionConnectionMessage.Handler();
	}

	protected BiConsumer<EditLetteredConnectionMessage, Supplier<NetworkEvent.Context>> createEditLetteredConnectionHandler() {
		return new EditLetteredConnectionMessage.Handler();
	}

	public void initRenders() {}

	public void initRendersLate() {}

	public static void sendToPlayersWatchingChunk(Object message, World world, BlockPos pos) {
		FairyLights.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
	}

	public static void sendToPlayersWatchingEntity(Object message, World world, Entity entity) {
		FairyLights.network.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
	}

	public static BlockView buildBlockView() {
		final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.getView();
	}

	private <MSG> void registerMessage(Class<MSG> clazz, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
		FairyLights.network.messageBuilder(clazz, nextMessageId++)
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
