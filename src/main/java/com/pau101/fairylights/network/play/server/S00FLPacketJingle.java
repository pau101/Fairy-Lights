package com.pau101.fairylights.network.play.server;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.network.FLPacket;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.tileentity.connection.Connection;

public class S00FLPacketJingle extends FLPacket {
	public int dimensionId;

	public int xCoord;

	public int yCoord;

	public int zCoord;

	public UUID uuid;

	public int lightOffset;

	public String jingle;

	public S00FLPacketJingle() {}

	public S00FLPacketJingle(TileEntityFairyLightsFastener lightsFastener, UUID uuid, int lightOffset, Jingle jingle) {
		dimensionId = lightsFastener.getWorldObj().provider.dimensionId;
		xCoord = lightsFastener.xCoord;
		yCoord = lightsFastener.yCoord;
		zCoord = lightsFastener.zCoord;
		this.uuid = uuid;
		this.lightOffset = lightOffset;
		this.jingle = jingle.toString();
	}

	@Override
	public void processPacket(INetHandler netHandler) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.theWorld != null && dimensionId == mc.theWorld.provider.dimensionId) {
			TileEntity tileEntity = mc.theWorld.getTileEntity(xCoord, yCoord, zCoord);
			if (tileEntity instanceof TileEntityFairyLightsFastener) {
				TileEntityFairyLightsFastener lightsFastener = (TileEntityFairyLightsFastener) tileEntity;
				Connection connection = lightsFastener.getConnection(uuid);
				if (connection != null) {
					connection.play(Jingle.parse(jingle), lightOffset);
				}
			}
		}
	}

	@Override
	public void readPacketData(PacketBuffer buffer) {
		dimensionId = buffer.readInt();
		xCoord = buffer.readInt();
		yCoord = buffer.readInt();
		zCoord = buffer.readInt();
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		lightOffset = buffer.readInt();
		try {
			jingle = buffer.readStringFromBuffer(Short.MAX_VALUE);
		} catch (IOException e) {
			jingle = Jingle.BACKUP_JINGLE;
		}
	}

	@Override
	public void writePacketData(PacketBuffer buffer) {
		buffer.writeInt(dimensionId);
		buffer.writeInt(xCoord);
		buffer.writeInt(yCoord);
		buffer.writeInt(zCoord);
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		buffer.writeInt(lightOffset);
		try {
			buffer.writeStringToBuffer(jingle);
		} catch (IOException e) {
			try {
				buffer.writeStringToBuffer(Jingle.BACKUP_JINGLE);
			} catch (IOException e1) {
				// some tampering must have gone on...
			}
		}
	}
}
