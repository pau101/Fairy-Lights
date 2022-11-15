package me.paulf.fairylights.server.net.clientbound;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.Lettered;
import me.paulf.fairylights.server.net.ClientMessageContext;
import me.paulf.fairylights.server.net.ConnectionMessage;
import net.minecraft.client.Minecraft;

import java.util.function.BiConsumer;

public class OpenEditLetteredConnectionScreenMessage<C extends Connection & Lettered> extends ConnectionMessage {
    public OpenEditLetteredConnectionScreenMessage() {}

    public OpenEditLetteredConnectionScreenMessage(final C connection) {
        super(connection);
    }

    public static final class Handler implements BiConsumer<OpenEditLetteredConnectionScreenMessage<?>, ClientMessageContext> {
        @Override
        public void accept(final OpenEditLetteredConnectionScreenMessage<?> message, final ClientMessageContext context) {
            this.accept(message);
        }

        private <C extends Connection & Lettered> void accept(final OpenEditLetteredConnectionScreenMessage<C> message) {
            ConnectionMessage.<C>getConnection(message, c -> c instanceof Lettered, Minecraft.getInstance().level).ifPresent(connection -> {
                Minecraft.getInstance().setScreen(new EditLetteredConnectionScreen<>(connection));
            });
        }
    }
}
