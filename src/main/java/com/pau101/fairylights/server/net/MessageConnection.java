package com.pau101.fairylights.server.net;

import java.io.IOException;
import java.util.UUID;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerType;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.connection.type.Connection;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageConnection<C extends Connection> extends FLMessage {
	public BlockPos pos;

	public FastenerAccessor accessor;

	public UUID uuid;

	public MessageConnection() {}

	public MessageConnection(C connection) {
		Fastener<?> fastener = connection.getFastener();
		pos = fastener.getPos();
		accessor = fastener.createAccessor();
		uuid = connection.getUUID();
	}

	protected abstract boolean isInstanceOfType(Class<? extends Connection> connection);

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeNBTTagCompoundToBuffer(FastenerType.serialize(accessor));
		buf.writeUuid(uuid);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		pos = buf.readBlockPos();
		accessor = FastenerType.deserialize(buf.readNBTTagCompoundFromBuffer());
		uuid = buf.readUuid();
	}

	@Override
	public final void process(MessageContext ctx) {
		World world = getWorld(ctx);
		accessor.update(world, pos);
		if (accessor.isLoaded(world)) {
			Fastener<?> fastener = accessor.get(world);
			Connection connection = fastener.getConnections().get(uuid);
			if (connection != null && isInstanceOfType(connection.getClass())) {
				process(ctx, (C) connection);
			}
		}
	}

	protected abstract World getWorld(MessageContext ctx);

	protected abstract void process(MessageContext ctx, C connection);
}
