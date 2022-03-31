package me.paulf.fairylights.server.net.serverbound;

import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.connection.PlayerAction;
import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.server.net.ServerMessageContext;
import me.paulf.fairylights.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.BiConsumer;

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
    public void encode(final PacketBuffer buf) {
        super.encode(buf);
        buf.writeByte(this.type.ordinal());
        buf.writeDouble(this.hit.field_72450_a);
        buf.writeDouble(this.hit.field_72448_b);
        buf.writeDouble(this.hit.field_72449_c);
        buf.func_150787_b(this.featureType.getId());
        buf.func_150787_b(this.featureId);
    }

    @Override
    public void decode(final PacketBuffer buf) {
        super.decode(buf);
        this.type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
        this.hit = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.featureType = FeatureType.fromId(buf.func_150792_a());
        this.featureId = buf.func_150792_a();
    }

    public static final class Handler implements BiConsumer<InteractionConnectionMessage, ServerMessageContext> {
        @Override
        public void accept(final InteractionConnectionMessage message, final ServerMessageContext context) {
            final ServerPlayerEntity player = context.getPlayer();
            getConnection(message, c -> true, player.field_70170_p).ifPresent(connection -> {
                if (connection.isModifiable(player) &&
                    player.func_213303_ch().func_72436_e(Vector3d.func_237491_b_(connection.getFastener().getPos())) < RANGE &&
                    player.func_70092_e(message.hit.field_72450_a, message.hit.field_72448_b, message.hit.field_72449_c) < REACH
                ) {
                    if (message.type == PlayerAction.ATTACK) {
                        connection.disconnect(player, message.hit);
                    } else {
                        this.interact(message, player, connection, message.hit);
                    }
                }
            });
        }

        private void interact(final InteractionConnectionMessage message, final PlayerEntity player, final Connection connection, final Vector3d hit) {
            for (final Hand hand : Hand.values()) {
                final ItemStack stack = player.func_184586_b(hand);
                final ItemStack oldStack = stack.func_77946_l();
                if (connection.interact(player, hit, message.featureType, message.featureId, stack, hand)) {
                    this.updateItem(player, oldStack, stack, hand);
                    break;
                }
            }
        }

        private void updateItem(final PlayerEntity player, final ItemStack oldStack, final ItemStack stack, final Hand hand) {
            if (stack.func_190916_E() <= 0 && !player.field_71075_bZ.field_75098_d) {
                ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
                player.func_184611_a(hand, ItemStack.field_190927_a);
            } else if (stack.func_190916_E() < oldStack.func_190916_E() && player.field_71075_bZ.field_75098_d) {
                stack.func_190920_e(oldStack.func_190916_E());
            }
        }
    }
}
