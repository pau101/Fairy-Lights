package me.paulf.fairylights.server.net.serverbound;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.Lettered;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.server.net.ServerMessageContext;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class EditLetteredConnectionMessage<C extends Connection & Lettered> extends ConnectionMessage {
    private StyledString text;

    public EditLetteredConnectionMessage() {}

    public EditLetteredConnectionMessage(final C connection, final StyledString text) {
        super(connection);
        this.text = text;
    }

    @Override
    public void encode(final FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeNbt(StyledString.serialize(this.text));
    }

    @Override
    public void decode(final FriendlyByteBuf buf) {
        super.decode(buf);
        this.text = StyledString.deserialize(buf.readNbt());
    }

    public static final class Handler implements BiConsumer<EditLetteredConnectionMessage<?>, ServerMessageContext> {
        @Override
        public void accept(final EditLetteredConnectionMessage<?> message, final ServerMessageContext context) {
            final ServerPlayer player = context.getPlayer();
            this.accept(message, player);
        }

        private <C extends Connection & Lettered> void accept(final EditLetteredConnectionMessage<C> message, final ServerPlayer player) {
            if (player != null) {
                ConnectionMessage.<C>getConnection(message, c -> c instanceof Lettered, player.level).ifPresent(connection -> {
                    if (connection.isModifiable(player) && connection.isSupportedText(message.text)) {
                        connection.setText(message.text);
                    }
                });
            }
        }
    }
}
