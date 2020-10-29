package me.paulf.fairylights.server.net.clientbound;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.net.ClientMessageContext;
import me.paulf.fairylights.server.net.ConnectionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.function.BiConsumer;

public final class JingleMessage extends ConnectionMessage {
    private int lightOffset;

    public Jingle jingle;

    public JingleMessage() {}

    public JingleMessage(final HangingLightsConnection connection, final int lightOffset, final Jingle jingle) {
        super(connection);
        this.lightOffset = lightOffset;
        this.jingle = jingle;
    }

    @Override
    public void encode(final PacketBuffer buf) {
        super.encode(buf);
        buf.writeVarInt(this.lightOffset);
        this.jingle.write(buf);
    }

    @Override
    public void decode(final PacketBuffer buf) {
        super.decode(buf);
        this.lightOffset = buf.readVarInt();
        this.jingle = Jingle.read(buf);
    }

    public static class Handler implements BiConsumer<JingleMessage, ClientMessageContext> {
        @Override
        public void accept(final JingleMessage message, final ClientMessageContext context) {
            final Jingle jingle = message.jingle;
            if (jingle != null) {
                ConnectionMessage.<HangingLightsConnection>getConnection(message, c -> c instanceof HangingLightsConnection, Minecraft.getInstance().world).ifPresent(connection ->
                    connection.play(jingle, message.lightOffset));
            }
        }
    }
}
