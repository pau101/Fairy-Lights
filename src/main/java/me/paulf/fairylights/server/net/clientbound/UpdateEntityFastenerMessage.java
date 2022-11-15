package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.net.ClientMessageContext;
import me.paulf.fairylights.server.net.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

public final class UpdateEntityFastenerMessage implements Message {
    private int entityId;

    private CompoundTag compound;

    public UpdateEntityFastenerMessage() {}

    public UpdateEntityFastenerMessage(final Entity entity, final CompoundTag compound) {
        this.entityId = entity.getId();
        this.compound = compound;
    }

    @Override
    public void encode(final FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeNbt(this.compound);
    }

    @Override
    public void decode(final FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.compound = buf.readNbt();
    }

    public static final class Handler implements BiConsumer<UpdateEntityFastenerMessage, ClientMessageContext> {
        @Override
        public void accept(final UpdateEntityFastenerMessage message, final ClientMessageContext context) {
            final Entity entity = context.getWorld().getEntity(message.entityId);
            if (entity != null) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
            }
        }
    }
}
