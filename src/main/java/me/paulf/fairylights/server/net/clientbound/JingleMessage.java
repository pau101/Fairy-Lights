package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.ClientMessageContext;
import me.paulf.fairylights.server.net.ConnectionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;

public final class JingleMessage extends ConnectionMessage {
    private int lightOffset;

    private JingleLibrary library;

    public Jingle jingle;

    public JingleMessage() {}

    public JingleMessage(final HangingLightsConnection connection, final int lightOffset, final JingleLibrary library, final Jingle jingle) {
        super(connection);
        this.lightOffset = lightOffset;
        this.library = library;
        this.jingle = jingle;
    }

    @Override
    public void encode(final PacketBuffer buf) {
        super.encode(buf);
        buf.writeVarInt(this.lightOffset);
        buf.writeResourceLocation(this.library.getName());
        buf.writeString(this.jingle.getId());
    }

    @Override
    public void decode(final PacketBuffer buf) {
        super.decode(buf);
        this.lightOffset = buf.readVarInt();
        this.library = JingleLibrary.fromName(buf.readResourceLocation());
        this.jingle = this.library.get(buf.readString());
    }

    public static class Handler implements BiConsumer<JingleMessage, ClientMessageContext> {
        @Override
        public void accept(final JingleMessage message, final ClientMessageContext context) {
            final Jingle jingle = message.jingle;
            if (jingle != null) {
                ConnectionMessage.<HangingLightsConnection>getConnection(message, c -> c instanceof HangingLightsConnection, Minecraft.getInstance().world).ifPresent(connection ->
                    connection.play(message.library, jingle, message.lightOffset));
            }
        }
    }
}
