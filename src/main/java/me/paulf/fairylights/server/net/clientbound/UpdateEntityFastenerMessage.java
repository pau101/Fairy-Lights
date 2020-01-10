package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class UpdateEntityFastenerMessage {
    private int entityId;

    private CompoundNBT compound;

    public UpdateEntityFastenerMessage() {}

    public UpdateEntityFastenerMessage(final Entity entity, final CompoundNBT compound) {
        this.entityId = entity.getEntityId();
        this.compound = compound;
    }

    public static void serialize(final UpdateEntityFastenerMessage message, final PacketBuffer buf) {
        buf.writeVarInt(message.entityId);
        buf.writeCompoundTag(message.compound);
    }

    public static UpdateEntityFastenerMessage deserialize(final PacketBuffer buf) {
        final UpdateEntityFastenerMessage message = new UpdateEntityFastenerMessage();
        message.entityId = buf.readVarInt();
        message.compound = buf.readCompoundTag();
        return message;
    }

    public static final class Handler implements BiConsumer<UpdateEntityFastenerMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final UpdateEntityFastenerMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                final Minecraft mc = Minecraft.getInstance();
                if (mc.world != null) {
                    final Entity entity = mc.world.getEntityByID(message.entityId);
                    if (entity != null) {
                        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
