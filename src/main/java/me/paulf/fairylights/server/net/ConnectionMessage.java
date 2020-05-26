package me.paulf.fairylights.server.net;

import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.accessor.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import net.minecraft.network.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;

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

    @Nullable
    public static <C extends Connection> C getConnection(final ConnectionMessage<C> message, final Predicate<? super Connection> typePredicate, final World world) {
        message.accessor.update(world, message.pos);
        if (message.accessor.isLoaded(world)) {
            final Fastener<?> fastener = message.accessor.get(world);
            final Connection c = fastener.getConnections().get(message.uuid);
            if (typePredicate.test(c)) {
                //noinspection unchecked
                return (C) c;
            }
        }
        return null;
    }
}
