package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.net.ConnectionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class JingleMessage extends ConnectionMessage<HangingLightsConnection> {
    private int lightOffset;

    private JingleLibrary library;

    @Nullable
    public Jingle jingle;

    public JingleMessage() {}

    public JingleMessage(final HangingLightsConnection connection, final int lightOffset, final JingleLibrary library, final Jingle jingle) {
        super(connection);
        this.lightOffset = lightOffset;
        this.library = library;
        this.jingle = jingle;
    }

    public static void serialize(final JingleMessage message, final PacketBuffer buf) {
        ConnectionMessage.serialize(message, buf);
        buf.writeVarInt(message.lightOffset);
        buf.writeByte(message.library.getId());
        buf.writeString(message.jingle.getId());
    }

    public static JingleMessage deserialize(final PacketBuffer buf) {
        final JingleMessage message = new JingleMessage();
        ConnectionMessage.deserialize(message, buf);
        message.lightOffset = buf.readVarInt();
        message.library = JingleLibrary.fromId(buf.readUnsignedByte());
        message.jingle = message.library.get(buf.readString());
        return message;
    }

    public static class Handler implements BiConsumer<JingleMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final JingleMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final Jingle jingle = message.jingle;
            if (jingle != null) {
                context.enqueueWork(() ->
                    ConnectionMessage.getConnection(message, c -> true, Minecraft.getInstance().world).ifPresent(connection ->
                        connection.play(message.library, jingle, message.lightOffset)));
            }
            context.setPacketHandled(true);
        }
    }
}
