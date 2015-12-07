package com.pau101.fairylights.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import com.google.common.collect.BiMap;

public abstract class FLPacket {
	public static FLPacket generatePacket(BiMap<Integer, Class<? extends FLPacket>> packetMap, int id) {
		try {
			Class<? extends FLPacket> packetClass = packetMap.get(Integer.valueOf(id));
			return packetClass == null ? null : packetClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract void processPacket(INetHandler netHandler);

	public abstract void readPacketData(PacketBuffer buffer);

	public abstract void writePacketData(PacketBuffer buffer);
}
