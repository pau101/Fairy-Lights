package me.paulf.fairylights.server;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.config.Configurator;
import me.paulf.fairylights.server.fastener.BlockView;
import me.paulf.fairylights.server.fastener.CreateBlockViewEvent;
import me.paulf.fairylights.server.fastener.RegularBlockView;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.clientbound.MessageJingle;
import me.paulf.fairylights.server.net.clientbound.MessageOpenEditLetteredConnectionGUI;
import me.paulf.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import me.paulf.fairylights.server.net.serverbound.MessageConnectionInteraction;
import me.paulf.fairylights.server.net.serverbound.MessageEditLetteredConnection;
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
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configurator.GENERAL_SPEC);
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
		registerMessage(MessageJingle.class, MessageJingle::serialize, MessageJingle::deserialize, createJingleHandler());
		registerMessage(MessageUpdateFastenerEntity.class, MessageUpdateFastenerEntity::serialize, MessageUpdateFastenerEntity::deserialize, createUpdateFastenerEntityHandler());
		registerMessage(MessageOpenEditLetteredConnectionGUI.class, MessageOpenEditLetteredConnectionGUI::serialize, MessageOpenEditLetteredConnectionGUI::deserialize, createOpenEditLetteredConnectionGUIHandler());
		registerMessage(MessageConnectionInteraction.class, MessageConnectionInteraction::serialize, MessageConnectionInteraction::deserialize, createConnectionInteractionHandler());
		registerMessage(MessageEditLetteredConnection.class, MessageEditLetteredConnection::serialize, MessageEditLetteredConnection::deserialize, createEditLetteredConnectionHandler());
	}

	private <T> BiConsumer<T, Supplier<NetworkEvent.Context>> noHandler() {
		return (msg, ctx) -> ctx.get().setPacketHandled(true);
	}

	protected BiConsumer<MessageJingle, Supplier<NetworkEvent.Context>> createJingleHandler() {
		return noHandler();
	}

	protected BiConsumer<MessageUpdateFastenerEntity, Supplier<NetworkEvent.Context>> createUpdateFastenerEntityHandler() {
		return noHandler();
	}

	protected BiConsumer<MessageOpenEditLetteredConnectionGUI, Supplier<NetworkEvent.Context>> createOpenEditLetteredConnectionGUIHandler() {
		return noHandler();
	}

	protected BiConsumer<MessageConnectionInteraction, Supplier<NetworkEvent.Context>> createConnectionInteractionHandler() {
		return new MessageConnectionInteraction.Handler();
	}

	protected BiConsumer<MessageEditLetteredConnection, Supplier<NetworkEvent.Context>> createEditLetteredConnectionHandler() {
		return new MessageEditLetteredConnection.Handler();
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
