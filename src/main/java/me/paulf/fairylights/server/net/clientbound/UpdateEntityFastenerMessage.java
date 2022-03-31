package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.net.ClientMessageContext;
import me.paulf.fairylights.server.net.Message;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;

public final class UpdateEntityFastenerMessage implements Message {
    private int entityId;

    private CompoundNBT compound;

    public UpdateEntityFastenerMessage() {}

    public UpdateEntityFastenerMessage(final Entity entity, final CompoundNBT compound) {
        this.entityId = entity.func_145782_y();
        this.compound = compound;
    }

    @Override
    public void encode(final PacketBuffer buf) {
        buf.func_150787_b(this.entityId);
        buf.func_150786_a(this.compound);
    }

    @Override
    public void decode(final PacketBuffer buf) {
        this.entityId = buf.func_150792_a();
        this.compound = buf.func_150793_b();
    }

    public static final class Handler implements BiConsumer<UpdateEntityFastenerMessage, ClientMessageContext> {
        @Override
        public void accept(final UpdateEntityFastenerMessage message, final ClientMessageContext context) {
            final Entity entity = context.getWorld().func_73045_a(message.entityId);
            if (entity != null) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
            }
        }
    }
}
