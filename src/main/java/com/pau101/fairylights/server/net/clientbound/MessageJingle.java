package com.pau101.fairylights.server.net.clientbound;

import java.io.IOException;

import javax.annotation.Nullable;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.server.jingle.Jingle;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.net.MessageConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class MessageJingle extends MessageConnection<Connection> {
	private int lightOffset;

	private JingleLibrary library;

	@Nullable
	public Jingle jingle;

	public MessageJingle() {}

	public MessageJingle(Connection connection, int lightOffset, JingleLibrary library, Jingle jingle) {
		super(connection);
		this.lightOffset = lightOffset;
		this.library = library;
		this.jingle = jingle;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeVarIntToBuffer(lightOffset);
		buf.writeByte(library.getId());
		buf.writeString(jingle.getId());
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		lightOffset = buf.readVarIntFromBuffer();
		library = JingleLibrary.fromId(buf.readUnsignedByte());
		jingle = library.get(buf.readStringFromBuffer(64));
	}

	@Override
	protected boolean isInstanceOfType(Class<? extends Connection> connection) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected World getWorld(MessageContext ctx) {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	protected void process(MessageContext ctx, Connection connection) {
		if (jingle != null && connection.getType() == ConnectionType.HANGING_LIGHTS) {
			((ConnectionHangingLights) connection).play(library, jingle, lightOffset);
		}
	}
}
