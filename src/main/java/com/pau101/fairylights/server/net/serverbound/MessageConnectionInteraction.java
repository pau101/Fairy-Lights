package com.pau101.fairylights.server.net.serverbound;

import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.net.MessageConnection;
import com.pau101.fairylights.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class MessageConnectionInteraction extends MessageConnection<Connection> {
	private static final float RANGE = (Connection.MAX_LENGTH + 1) * (Connection.MAX_LENGTH + 1);

	private static final float REACH = 6 * 6;

	private PlayerAction type;

	private Vec3d hit;

	private FeatureType featureType;

	private int featureId;

	public MessageConnectionInteraction() {}

	public MessageConnectionInteraction(Connection connection, PlayerAction type, Intersection intersection) {
		super(connection);
		this.type = type;
		hit = intersection.getResult();
		featureType = intersection.getFeatureType();
		featureId = intersection.getFeature().getId();
	}

	public static void serialize(MessageConnectionInteraction message, PacketBuffer buf) {
		MessageConnection.serialize(message, buf);
		buf.writeByte(message.type.ordinal());
		buf.writeDouble(message.hit.x);
		buf.writeDouble(message.hit.y);
		buf.writeDouble(message.hit.z);
		buf.writeVarInt(message.featureType.getId());
		buf.writeVarInt(message.featureId);
	}

	public static MessageConnectionInteraction deserialize(PacketBuffer buf) {
		MessageConnectionInteraction message = new MessageConnectionInteraction();
		MessageConnection.deserialize(message, buf);
		message.type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
		message.hit = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		message.featureType = FeatureType.fromId(buf.readVarInt());
		message.featureId = buf.readVarInt();
		return message;
	}

	public static final class Handler implements BiConsumer<MessageConnectionInteraction, Supplier<NetworkEvent.Context>> {
		@Override
		public void accept(final MessageConnectionInteraction message, final Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			handle(message, context);
			context.setPacketHandled(true);
		}

		private void handle(MessageConnectionInteraction message, NetworkEvent.Context context) {
			PlayerEntity player = context.getSender();
			if (player == null) {
				return;
			}
			Connection connection = getConnection(message, c -> true, player.world);
			if (connection == null) {
				return;
			}
			if (player.getPositionVec().squareDistanceTo(new Vec3d(connection.getFastener().getPos())) < RANGE && player.getDistanceSq(message.hit.x, message.hit.y, message.hit.z) < REACH && connection.isModifiable(player)) {
				if (message.type == PlayerAction.ATTACK) {
					connection.disconnect(player, message.hit);
				} else {
					interact(message, player, connection, message.hit);
				}
			}
		}

		private void interact(MessageConnectionInteraction message, PlayerEntity player, Connection connection, Vec3d hit) {
			for (Hand hand : Hand.values()) {
				ItemStack stack = player.getHeldItem(hand);
				ItemStack oldStack = stack.copy();
				if (connection.interact(player, hit, message.featureType, message.featureId, stack, hand)) {
					updateItem(player, oldStack, stack, hand);
					break;
				}
			}
		}

		private void updateItem(PlayerEntity player, ItemStack oldStack, ItemStack stack, Hand hand) {
			if (stack.getCount() <= 0 && !player.abilities.isCreativeMode) {
				ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
				player.setHeldItem(hand, ItemStack.EMPTY);
			} else if (stack.getCount() < oldStack.getCount() && player.abilities.isCreativeMode) {
				stack.setCount(oldStack.getCount());
			}
		}
	}
}
