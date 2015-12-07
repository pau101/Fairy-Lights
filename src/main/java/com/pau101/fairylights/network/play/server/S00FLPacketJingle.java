package com.pau101.fairylights.network.play.server;

import java.util.Collection;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import com.pau101.fairylights.connection.ConnectionLogicFairyLights;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.network.FLPacket;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;

public class S00FLPacketJingle extends FLPacket {
	public int dimensionId;

	public BlockPos pos;

	public UUID uuid;

	public int lightOffset;

	public String jingle;

	public S00FLPacketJingle() {}

	public S00FLPacketJingle(TileEntityConnectionFastener lightsFastener, UUID uuid, int lightOffset, Jingle jingle) {
		dimensionId = lightsFastener.getWorld().provider.getDimensionId();
		pos = lightsFastener.getPos();
		this.uuid = uuid;
		this.lightOffset = lightOffset;
		this.jingle = jingle.toString();
	}

	@Override
	public void processPacket(INetHandler netHandler) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.theWorld != null && dimensionId == mc.theWorld.provider.getDimensionId()) {
			TileEntity tileEntity = mc.theWorld.getTileEntity(pos);
			if (tileEntity instanceof TileEntityConnectionFastener) {
				TileEntityConnectionFastener lightsFastener = (TileEntityConnectionFastener) tileEntity;
				Connection connection = lightsFastener.getConnection(uuid);
				if (connection != null && connection.getLogic() instanceof ConnectionLogicFairyLights) {
					((ConnectionLogicFairyLights) connection.getLogic()).play(Jingle.parse(jingle), lightOffset);
				}
			}
		}
	}

	@Override
	public void readPacketData(PacketBuffer buffer) {
		dimensionId = buffer.readInt();
		pos = buffer.readBlockPos();
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		lightOffset = buffer.readInt();
		jingle = buffer.readStringFromBuffer(Short.MAX_VALUE);
	}

	@Override
	public void writePacketData(PacketBuffer buffer) {
		buffer.writeInt(dimensionId);
		buffer.writeBlockPos(pos);
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		buffer.writeInt(lightOffset);
		buffer.writeString(jingle);
	}
}
