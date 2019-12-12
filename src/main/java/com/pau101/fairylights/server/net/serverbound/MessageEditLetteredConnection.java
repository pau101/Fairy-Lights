package com.pau101.fairylights.server.net.serverbound;

import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.MessageConnection;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageEditLetteredConnection<C extends Connection & Lettered> extends MessageConnection<C> {
	private StyledString text;

	public MessageEditLetteredConnection() {}

	public MessageEditLetteredConnection(C connection, StyledString text) {
		super(connection);
		this.text = text;
	}

	public static void serialize(MessageEditLetteredConnection<?> message, PacketBuffer buf) {
		MessageConnection.serialize(message, buf);
		buf.writeCompoundTag(StyledString.serialize(message.text));
	}

	public static <C extends Connection & Lettered> MessageEditLetteredConnection<C> deserialize(PacketBuffer buf) {
		MessageEditLetteredConnection<C> message = new MessageEditLetteredConnection<>();
		MessageConnection.deserialize(message, buf);
		message.text = StyledString.deserialize(buf.readCompoundTag());
		return message;
	}

	public static final class Handler implements BiConsumer<MessageEditLetteredConnection, Supplier<NetworkEvent.Context>> {
		@Override
		public void accept(final MessageEditLetteredConnection message, final Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			accept((MessageEditLetteredConnection<?>) message, context);
			context.setPacketHandled(true);
		}

		private <C extends Connection & Lettered> void accept(final MessageEditLetteredConnection<C> message, NetworkEvent.Context context) {
			ServerPlayerEntity player = context.getSender();
			if (player != null) {
				C connection = MessageConnection.getConnection(message, c -> c instanceof Lettered, player.world);
				if (connection != null && connection.isModifiable(player) && connection.isSuppportedText(message.text)) {
					connection.setText(message.text);
				}
			}
		}
	}
}
