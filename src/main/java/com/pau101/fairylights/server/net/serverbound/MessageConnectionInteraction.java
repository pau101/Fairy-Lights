package com.pau101.fairylights.server.net.serverbound;

import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.net.MessageConnection;
import com.pau101.fairylights.util.Utils;

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
		hit = intersection.getResult().hitVec;
		featureType = intersection.getFeatureType();
		featureId = intersection.getFeature().getId();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeByte(type.ordinal());
		buf.writeDouble(hit.xCoord);
		buf.writeDouble(hit.yCoord);
		buf.writeDouble(hit.zCoord);
		buf.writeVarIntToBuffer(featureType.getId());
		buf.writeVarIntToBuffer(featureId);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		type = Utils.getEnumValue(PlayerAction.class, buf.readUnsignedByte());
		hit = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		featureType = FeatureType.fromId(buf.readVarIntFromBuffer());
		featureId = buf.readVarIntFromBuffer();
	}

	@Override
	protected boolean isInstanceOfType(Class<? extends Connection> connection) {
		return true;
	}

	@Override
	protected World getWorld(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.worldObj;
	}

	@Override
	protected void process(MessageContext ctx, Connection connection) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		if (player.getDistanceSq(connection.getFastener().getPos()) < RANGE && player.getDistanceSq(hit.xCoord, hit.yCoord, hit.zCoord) < REACH) {
			if (type == PlayerAction.ATTACK) {
				connection.disconnect(player, hit);
			} else {
				interact(player, connection, hit);
			}
		}
	}

	private void interact(EntityPlayer player, Connection connection, Vec3d hit) {
		for (EnumHand hand : EnumHand.values()) {
			ItemStack stack = player.getHeldItem(hand);
			ItemStack oldStack = stack == null ? null : stack.copy();
			if (connection.interact(player, hit, featureType, featureId, stack, hand)) {
				updateItem(player, oldStack, stack, hand);
				break;
			}
		}
	}

	private void updateItem(EntityPlayer player, ItemStack oldStack, @Nullable ItemStack stack, EnumHand hand) {
		if (stack != null && stack == player.getHeldItem(hand)) {
			if (stack.stackSize <= 0 && !player.capabilities.isCreativeMode) {
				ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
				player.setHeldItem(hand, null);
			} else if (stack.stackSize < oldStack.stackSize && player.capabilities.isCreativeMode) {
				stack.stackSize = oldStack.stackSize;
			}
		}
	}
}
