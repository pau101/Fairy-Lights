package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.server.net.ServerMessageContext;
import net.minecraft.client.Minecraft;

import java.util.function.BiConsumer;

public class OpenEditLetteredConnectionScreenMessage<C extends Connection & Lettered> extends ConnectionMessage<C> {
    public OpenEditLetteredConnectionScreenMessage() {}

    public OpenEditLetteredConnectionScreenMessage(final C connection) {
        super(connection);
    }

    public static final class Handler implements BiConsumer<OpenEditLetteredConnectionScreenMessage, ServerMessageContext> {
        @Override
        public void accept(final OpenEditLetteredConnectionScreenMessage message, final ServerMessageContext context) {
            this.accept((OpenEditLetteredConnectionScreenMessage<?>) message);
        }

        private <C extends Connection & Lettered> void accept(final OpenEditLetteredConnectionScreenMessage<C> message) {
            ConnectionMessage.getConnection(message, c -> c instanceof Lettered, Minecraft.getInstance().world).ifPresent(connection -> {
                Minecraft.getInstance().displayGuiScreen(new EditLetteredConnectionScreen<>(connection));
            });
        }
    }
}
