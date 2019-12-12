package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.MessageConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

	public static void serialize(MessageJingle message, PacketBuffer buf) {
		MessageConnection.serialize(message, buf);
		buf.writeVarInt(message.lightOffset);
		buf.writeByte(message.library.getId());
		buf.writeString(message.jingle.getId());
	}

	public static MessageJingle deserialize(PacketBuffer buf) {
		MessageJingle message = new MessageJingle();
		MessageConnection.deserialize(message, buf);
		message.lightOffset = buf.readVarInt();
		message.library = JingleLibrary.fromId(buf.readUnsignedByte());
		message.jingle = message.library.get(buf.readString());
		return message;
	}

	public static class Handler implements BiConsumer<MessageJingle, Supplier<NetworkEvent.Context>> {
		@Override
		public void accept(MessageJingle message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			Connection connection = MessageConnection.getConnection(message, c -> true, Minecraft.getInstance().world);
			if (message.jingle != null && connection instanceof ConnectionHangingLights) {
				((ConnectionHangingLights) connection).play(message.library, message.jingle, message.lightOffset);
			}
			context.setPacketHandled(true);
		}
	}
}
