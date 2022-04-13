package me.paulf.fairylights.server.net;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.checkerframework.checker.units.qual.C;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public abstract class ConnectionMessage implements Message {
    public BlockPos pos;

    public FastenerAccessor accessor;

    public UUID uuid;

    public ConnectionMessage() {}

    public ConnectionMessage(final Connection connection) {
        final Fastener<?> fastener = connection.getFastener();
        this.pos = fastener.getPos();
        this.accessor = fastener.createAccessor();
        this.uuid = connection.getUUID();
    }

    @Override
    public void encode(final FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(FastenerType.serialize(this.accessor));
        buf.writeUUID(this.uuid);
    }

    @Override
    public void decode(final FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.accessor = FastenerType.deserialize(Objects.requireNonNull(buf.readNbt(), "tag"));
        this.uuid = buf.readUUID();
    }

    @SuppressWarnings("unchecked")
    public static <C extends Connection> Optional<C> getConnection(final ConnectionMessage message, final Predicate<? super Connection> typePredicate, final Level world) {
        return message.accessor.get(world, false).map(Optional::of).orElse(Optional.empty()).flatMap(f -> (Optional<C>) f.get(message.uuid).filter(typePredicate));
    }
}
