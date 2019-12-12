package com.pau101.fairylights.server.net.clientbound;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.MessageConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageOpenEditLetteredConnectionGUI<C extends Connection & Lettered> extends MessageConnection<C> {
	public MessageOpenEditLetteredConnectionGUI() {}

	public MessageOpenEditLetteredConnectionGUI(C connection) {
		super(connection);
	}


	public static <C extends Connection & Lettered> MessageOpenEditLetteredConnectionGUI<C> deserialize(PacketBuffer buf) {
		MessageOpenEditLetteredConnectionGUI<C> message = new MessageOpenEditLetteredConnectionGUI<>();
		MessageConnection.deserialize(message, buf);
		return message;
	}

	public static final class Handler implements BiConsumer<MessageOpenEditLetteredConnectionGUI, Supplier<NetworkEvent.Context>> {
		@Override
		public void accept(final MessageOpenEditLetteredConnectionGUI message, final Supplier<NetworkEvent.Context> contextSupplier) {
			accept((MessageOpenEditLetteredConnectionGUI<?>) message);
			contextSupplier.get().setPacketHandled(true);
		}

		private <C extends Connection & Lettered> void accept(final MessageOpenEditLetteredConnectionGUI<C> message) {
			C connection = MessageConnection.getConnection(message, c -> c instanceof Lettered, Minecraft.getInstance().world);
			if (connection != null) {
				Minecraft.getInstance().displayGuiScreen(new GuiEditLetteredConnection<>(connection));
			}
		}
	}
}
