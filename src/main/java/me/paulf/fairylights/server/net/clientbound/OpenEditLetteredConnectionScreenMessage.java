package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.ConnectionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

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
            ConnectionMessage.getConnection(message, c -> c instanceof Lettered, Minecraft.getInstance().world).ifPresent(connection -> {
                Minecraft.getInstance().displayGuiScreen(new EditLetteredConnectionScreen<>(connection));
            });
        }
    }
}
