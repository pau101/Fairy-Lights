package com.pau101.fairylights.server;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.config.Configurator;
import com.pau101.fairylights.server.creativetabs.CreativeTabsFairyLights;
import com.pau101.fairylights.server.fastener.BlockView;
import com.pau101.fairylights.server.fastener.CreateBlockViewEvent;
import com.pau101.fairylights.server.fastener.RegularBlockView;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.net.FLMessage;
import com.pau101.fairylights.server.net.clientbound.MessageJingle;
import com.pau101.fairylights.server.net.clientbound.MessageOpenEditLetteredConnectionGUI;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import com.pau101.fairylights.server.net.serverbound.MessageConnectionInteraction;
import com.pau101.fairylights.server.net.serverbound.MessageEditLetteredConnection;
import com.pau101.fairylights.util.CalendarEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import valkyrienwarfare.ValkyrienWarfareMod;

import javax.annotation.Nullable;
import java.time.Month;

public class ServerProxy implements IMessageHandler<FLMessage, IMessage> {
	private int nextMessageId;

	public void initConfig(FMLPreInitializationEvent event) {
		Configurator.initConfig(event);
	}

	public void initGUI() {
		FairyLights.fairyLightsTab = new CreativeTabsFairyLights();
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
		FairyLights.network = NetworkRegistry.INSTANCE.newSimpleChannel(FairyLights.ID);
		registerMessage(MessageJingle.class, Side.CLIENT);
		registerMessage(MessageUpdateFastenerEntity.class, Side.CLIENT);
		registerMessage(MessageOpenEditLetteredConnectionGUI.class, Side.CLIENT);
		registerMessage(MessageConnectionInteraction.class, Side.SERVER);
		registerMessage(MessageEditLetteredConnection.class, Side.SERVER);
	}

	public void initRenders() {}

	public void initRendersLate() {}

	@Nullable
	@Override
	public IMessage onMessage(FLMessage message, MessageContext ctx) {
		IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
		thread.addScheduledTask(() -> message.process(ctx));
		return null;
	}

	public static void sendToPlayersWatchingChunk(FLMessage message, World world, BlockPos pos) {
		PlayerChunkMap map = ((WorldServer) world).getPlayerChunkMap();
		PlayerChunkMapEntry e = map.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
		if (e != null) {	
			e.sendPacket(FairyLights.network.getPacketFrom(message));
		}
	}

	public static void sendToPlayersWatchingEntity(FLMessage message, World world, Entity entity) {
		for (EntityPlayer player : ((WorldServer) world).getEntityTracker().getTrackingPlayers(entity)) {
			FairyLights.network.sendTo(message, (EntityPlayerMP) player);
		}
		if (entity instanceof EntityPlayerMP) {
			FairyLights.network.sendTo(message, (EntityPlayerMP) entity);
		}
	}

	public static BlockView buildBlockView() {
		final CreateBlockViewEvent evt = new CreateBlockViewEvent(new RegularBlockView());
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.getView();
	}

	private void registerMessage(Class<? extends FLMessage> clazz, Side toSide) {
		FairyLights.network.registerMessage(this, clazz, nextMessageId++, toSide);
	}

	public void initIntegration() {
		if (Loader.isModLoaded(ValkyrienWarfareMod.MODID)) {
			final Class<?> vw;
			try {
				vw = Class.forName("com.pau101.fairylights.server.integration.valkyrienwarfare.ValkyrienWarfare");
			} catch (final ClassNotFoundException e) {
				throw new AssertionError(e);
			}
			MinecraftForge.EVENT_BUS.register(vw);
		}
	}
}
