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

	public UpdateEntityFastenerMessage(Entity entity, CompoundNBT compound) {
		this.entityId = entity.getEntityId();
		this.compound = compound;
	}

	public static void serialize(UpdateEntityFastenerMessage message, PacketBuffer buf) {
		buf.writeVarInt(message.entityId);
		buf.writeCompoundTag(message.compound);
	}

	public static UpdateEntityFastenerMessage deserialize(PacketBuffer buf) {
		UpdateEntityFastenerMessage message = new UpdateEntityFastenerMessage();
		message.entityId = buf.readVarInt();
		message.compound = buf.readCompoundTag();
		return message;
	}

	public static final class Handler implements BiConsumer<UpdateEntityFastenerMessage, Supplier<NetworkEvent.Context>> {
		@Override
		public void accept(UpdateEntityFastenerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			Minecraft mc = Minecraft.getInstance();
			if (mc.world != null) {
				Entity entity = mc.world.getEntityByID(message.entityId);
				if (entity != null) {
					entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.deserializeNBT(message.compound));
				}
			}
			context.setPacketHandled(true);
		}
	}
}
