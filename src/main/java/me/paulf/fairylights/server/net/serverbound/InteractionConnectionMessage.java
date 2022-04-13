package me.paulf.fairylights.server.net.serverbound;

import java.util.function.BiConsumer;

import com.mojang.math.Vector3d;

import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.PlayerAction;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.server.net.ServerMessageContext;
import me.paulf.fairylights.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

public final class InteractionConnectionMessage extends ConnectionMessage {
    private static final float RANGE = (Connection.MAX_LENGTH + 1) * (Connection.MAX_LENGTH + 1);

    private static final float REACH = 6 * 6;

    private PlayerAction type;

    private Vector3d hit;

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

    @Override
    public void encode(final FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeByte(this.type.ordinal());
        buf.writeDouble(this.hit.x);
        buf.writeDouble(this.hit.y);
        buf.writeDouble(this.hit.z);
        buf.writeVarInt(this.featureType.getId());
        buf.writeVarInt(this.featureId);
    }

    @Override
    public void decode(final FriendlyByteBuf buf) {
        super.decode(buf);
        this.type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
        this.hit = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.featureType = FeatureType.fromId(buf.readVarInt());
        this.featureId = buf.readVarInt();
    }

    public static final class Handler implements BiConsumer<InteractionConnectionMessage, ServerMessageContext> {
        @Override
        public void accept(final InteractionConnectionMessage message, final ServerMessageContext context) {
            final ServerPlayer player = context.getPlayer();
            getConnection(message, c -> true, player.level).ifPresent(connection -> {
                if (connection.isModifiable(player) &&
                    player.position().squareDistanceTo(Vector3d.copy(connection.getFastener().getPos())) < RANGE &&
                    player.func_70092_e(message.hit.x, message.hit.y, message.hit.z) < REACH
                ) {
                    if (message.type == PlayerAction.ATTACK) {
                        connection.disconnect(player, message.hit);
                    } else {
                        this.interact(message, player, connection, message.hit);
                    }
                }
            });
        }

        private void interact(final InteractionConnectionMessage message, final Player player, final Connection connection, final Vector3d hit) {
            for (final InteractionHand hand : InteractionHand.values()) {
                final ItemStack stack = player.getItemInHand(hand);
                final ItemStack oldStack = stack.copy();
                if (connection.interact(player, hit, message.featureType, message.featureId, stack, hand)) {
                    this.updateItem(player, oldStack, stack, hand);
                    break;
                }
            }
        }

        private void updateItem(final Player player, final ItemStack oldStack, final ItemStack stack, final InteractionHand hand) {
            if (stack.getCount() <= 0 && !player.isCreative()) {
                ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
                player.setItemInHand(hand, ItemStack.EMPTY);
            } else if (stack.getCount() < oldStack.getCount() && player.isCreative()) {
                stack.setCount(oldStack.getCount());
            }
        }
    }
}
