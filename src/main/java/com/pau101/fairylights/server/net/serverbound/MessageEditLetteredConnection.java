package com.pau101.fairylights.server.net.serverbound;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.MessageConnection;
import com.pau101.fairylights.util.styledstring.StyledString;

public class MessageEditLetteredConnection<C extends Connection & Lettered> extends MessageConnection<C> {
	private StyledString text;

	public MessageEditLetteredConnection() {}

	public MessageEditLetteredConnection(C connection, StyledString text) {
		super(connection);
		this.text = text;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeNBTTagCompoundToBuffer(StyledString.serialize(text));
	}

	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		text = StyledString.deserialize(buf.readNBTTagCompoundFromBuffer());
	}

	@Override
	protected boolean isInstanceOfType(Class<? extends Connection> connection) {
		return Lettered.class.isAssignableFrom(connection);
	}

	@Override
	protected World getWorld(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.worldObj;
	}

	@Override
	protected void process(MessageContext ctx, C connection) {
		if (connection.isSuppportedText(text)) {
			connection.setText(text);
		}
	}
}
