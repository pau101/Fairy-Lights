package com.pau101.fairylights.server.net.clientbound;

import java.io.IOException;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.net.FLMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class MessageUpdateFastenerEntity extends FLMessage {
	private int entityId;

	private NBTTagCompound compound;

	public MessageUpdateFastenerEntity() {}

	public MessageUpdateFastenerEntity(Entity entity, NBTTagCompound compound) {
		this.entityId = entity.getEntityId();
		this.compound = compound;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeVarInt(entityId);
		buf.writeCompoundTag(compound);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		entityId = buf.readVarInt();
		compound = buf.readCompoundTag();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void process(MessageContext ctx) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.world != null) {
			Entity entity = mc.world.getEntityByID(entityId);
			if (entity != null && entity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) {
				entity.getCapability(CapabilityHandler.FASTENER_CAP, null).deserializeNBT(compound);
			}
		}
	}
}
