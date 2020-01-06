package me.paulf.fairylights.server.net.serverbound;

import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class InteractionConnectionMessage extends ConnectionMessage<Connection> {
    private static final float RANGE = (Connection.MAX_LENGTH + 1) * (Connection.MAX_LENGTH + 1);

    private static final float REACH = 6 * 6;

    private PlayerAction type;

    private Vec3d hit;

    private FeatureType featureType;

    private int featureId;

    public InteractionConnectionMessage() {}

    public InteractionConnectionMessage(final Connection connection, final PlayerAction type, final Intersection intersection) {
        super(connection);
        this.type = type;
        this.hit = intersection.getResult();
        this.featureType = intersection.getFeatureType();
        this.featureId = intersection.getFeature().getId();
    }

    public static void serialize(final InteractionConnectionMessage message, final PacketBuffer buf) {
        ConnectionMessage.serialize(message, buf);
        buf.writeByte(message.type.ordinal());
        buf.writeDouble(message.hit.x);
        buf.writeDouble(message.hit.y);
        buf.writeDouble(message.hit.z);
        buf.writeVarInt(message.featureType.getId());
        buf.writeVarInt(message.featureId);
    }

    public static InteractionConnectionMessage deserialize(final PacketBuffer buf) {
        final InteractionConnectionMessage message = new InteractionConnectionMessage();
        ConnectionMessage.deserialize(message, buf);
        message.type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
        message.hit = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        message.featureType = FeatureType.fromId(buf.readVarInt());
        message.featureId = buf.readVarInt();
        return message;
    }

    public static final class Handler implements BiConsumer<InteractionConnectionMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final InteractionConnectionMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            this.handle(message, context);
            context.setPacketHandled(true);
        }

        private void handle(final InteractionConnectionMessage message, final NetworkEvent.Context context) {
            final PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }
            final Connection connection = getConnection(message, c -> true, player.world);
            if (connection == null) {
                return;
            }
            if (player.getPositionVec().squareDistanceTo(new Vec3d(connection.getFastener().getPos())) < RANGE && player.getDistanceSq(message.hit.x, message.hit.y, message.hit.z) < REACH && connection.isModifiable(player)) {
                if (message.type == PlayerAction.ATTACK) {
                    connection.disconnect(player, message.hit);
                } else {
                    this.interact(message, player, connection, message.hit);
                }
            }
        }

        private void interact(final InteractionConnectionMessage message, final PlayerEntity player, final Connection connection, final Vec3d hit) {
            for (final Hand hand : Hand.values()) {
                final ItemStack stack = player.getHeldItem(hand);
                final ItemStack oldStack = stack.copy();
                if (connection.interact(player, hit, message.featureType, message.featureId, stack, hand)) {
                    this.updateItem(player, oldStack, stack, hand);
                    break;
                }
            }
        }

        private void updateItem(final PlayerEntity player, final ItemStack oldStack, final ItemStack stack, final Hand hand) {
            if (stack.getCount() <= 0 && !player.abilities.isCreativeMode) {
                ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
                player.setHeldItem(hand, ItemStack.EMPTY);
            } else if (stack.getCount() < oldStack.getCount() && player.abilities.isCreativeMode) {
                stack.setCount(oldStack.getCount());
            }
        }
    }
}
