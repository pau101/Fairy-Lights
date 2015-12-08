package com.pau101.fairylights.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashBiMap;
import com.pau101.fairylights.network.play.server.S00FLPacketJingle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.CustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class FLNetworkManager {
	private static final Logger logger = LogManager.getLogger();

	public static HashBiMap<Integer, Class<? extends FLPacket>> packetMap = HashBiMap.<Integer, Class<? extends FLPacket>> create();
	static {
		packetMap.put(Integer.valueOf(0), S00FLPacketJingle.class);
	}

	private String channelName;

	private FMLEventChannel channel;

	public FLNetworkManager(String channelName) {
		this.channelName = channelName;
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);
		channel.register(this);
	}

	private FMLProxyPacket getInProxy(FLPacket modPacket) {
		int id = FLNetworkManager.packetMap.inverse().get(modPacket.getClass());
		PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
		packetBuffer.writeVarIntToBuffer(id);
		modPacket.writePacketData(packetBuffer);
		FMLProxyPacket packet = new FMLProxyPacket(packetBuffer, channelName);
		return packet;
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent e) throws IOException {
		processPacket(e);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent e) throws IOException {
		processPacket(e);
	}

	private void processPacket(CustomPacketEvent e) throws IOException {
		FMLProxyPacket proxyPacket = e.packet;
		if (channelName.equals(proxyPacket.channel())) {
			ByteBuf byteBuf = proxyPacket.payload();
			int readableBytes = byteBuf.readableBytes();

			if (readableBytes != 0) {
				PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
				int id = packetBuffer.readVarIntFromBuffer();
				FLPacket packet = FLPacket.generatePacket(packetMap, id);

				if (packet == null) {
					logger.warn("Bad Packet id " + id);
				} else {
					packet.readPacketData(packetBuffer);
					packet.processPacket(e.handler);
				}
			}
		}
	}

	public void sendPacketToClient(FLPacket modPacket, EntityPlayerMP player) {
		channel.sendTo(getInProxy(modPacket), player);
	}

	public void sendPacketToClientsWatchingChunk(int x, int z, World world, FLPacket packet, Entity... exceptions) {
		int chunkX = x >> 4, chunkZ = z >> 4;
		Iterator<EntityPlayerMP> players = MinecraftServer.getServer().getEntityWorld().playerEntities.iterator();

		while (players.hasNext()) {
			EntityPlayerMP player = players.next();

			boolean found = false;
			if (exceptions != null) {
				for (Entity e : exceptions) {
					if (e == player) {
						found = true;
					}
				}
			}

			if (!found && player.worldObj == world && player.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(player, chunkX, chunkZ)) {
				sendPacketToClient(packet, player);
			}
		}
	}

	public void sendPacketToServer(FLPacket modPacket) {
		channel.sendToServer(getInProxy(modPacket));
	}
}
