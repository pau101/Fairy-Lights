package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.client.gui.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.server.net.*;
import net.minecraft.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.network.*;

import java.util.function.*;

public class OpenEditLetteredConnectionScreenMessage<C extends Connection & Lettered> extends ConnectionMessage<C> {
    public OpenEditLetteredConnectionScreenMessage() {}

    public OpenEditLetteredConnectionScreenMessage(final C connection) {
        super(connection);
    }


    public static <C extends Connection & Lettered> OpenEditLetteredConnectionScreenMessage<C> deserialize(final PacketBuffer buf) {
        final OpenEditLetteredConnectionScreenMessage<C> message = new OpenEditLetteredConnectionScreenMessage<>();
        ConnectionMessage.deserialize(message, buf);
        return message;
    }

    public static final class Handler implements BiConsumer<OpenEditLetteredConnectionScreenMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final OpenEditLetteredConnectionScreenMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> this.accept((OpenEditLetteredConnectionScreenMessage<?>) message));
            context.setPacketHandled(true);
        }

        private <C extends Connection & Lettered> void accept(final OpenEditLetteredConnectionScreenMessage<C> message) {
            final C connection = ConnectionMessage.getConnection(message, c -> c instanceof Lettered, Minecraft.getInstance().world);
            if (connection != null) {
                Minecraft.getInstance().displayGuiScreen(new EditLetteredConnectionScreen<>(connection));
            }
        }
    }
}
