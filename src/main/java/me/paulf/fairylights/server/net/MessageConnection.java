package me.paulf.fairylights.server.net;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class MessageConnection<C extends Connection> {
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

	public static void serialize(MessageConnection message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeCompoundTag(FastenerType.serialize(message.accessor));
		buf.writeUniqueId(message.uuid);
	}

	public static void deserialize(MessageConnection message, PacketBuffer buf) {
		message.pos = buf.readBlockPos();
		message.accessor = FastenerType.deserialize(buf.readCompoundTag());
		message.uuid = buf.readUniqueId();
	}

	@Nullable
	public static <C extends Connection> C getConnection(MessageConnection<C> message, Predicate<? super Connection> typePredicate, World world) {
		message.accessor.update(world, message.pos);
		if (message.accessor.isLoaded(world)) {
			Fastener<?> fastener = message.accessor.get(world);
			Connection c = fastener.getConnections().get(message.uuid);
			if (typePredicate.test(c)) {
				//noinspection unchecked
				return (C) c;
			}
		}
		return null;
	}
}
