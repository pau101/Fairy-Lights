package me.paulf.fairylights.server.net;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class ConnectionMessage<C extends Connection> {
    public BlockPos pos;

    public FastenerAccessor accessor;

    public UUID uuid;

    public ConnectionMessage() {}

    public ConnectionMessage(final C connection) {
        final Fastener<?> fastener = connection.getFastener();
        this.pos = fastener.getPos();
        this.accessor = fastener.createAccessor();
        this.uuid = connection.getUUID();
    }

    public static void serialize(final ConnectionMessage message, final PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeCompoundTag(FastenerType.serialize(message.accessor));
        buf.writeUniqueId(message.uuid);
    }

    public static void deserialize(final ConnectionMessage message, final PacketBuffer buf) {
        message.pos = buf.readBlockPos();
        message.accessor = FastenerType.deserialize(buf.readCompoundTag());
        message.uuid = buf.readUniqueId();
    }

    public static <C extends Connection> Optional<C> getConnection(final ConnectionMessage<C> message, final Predicate<? super Connection> typePredicate, final World world) {
        message.accessor.update(world, message.pos);
        return message.accessor.get(world, false).map(Optional::of).orElse(Optional.empty()).flatMap(f -> {
            final Connection c = f.getConnections().get(message.uuid);
            if (typePredicate.test(c)) {
                //noinspection unchecked
                return Optional.of((C) c);
            }
            return Optional.empty();
        });
    }
}
