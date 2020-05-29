package me.paulf.fairylights.server.net.serverbound;

import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.ConnectionMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EditLetteredConnectionMessage<C extends Connection & Lettered> extends ConnectionMessage<C> {
    private StyledString text;

    public EditLetteredConnectionMessage() {}

    public EditLetteredConnectionMessage(final C connection, final StyledString text) {
        super(connection);
        this.text = text;
    }

    public static void serialize(final EditLetteredConnectionMessage<?> message, final PacketBuffer buf) {
        ConnectionMessage.serialize(message, buf);
        buf.writeCompoundTag(StyledString.serialize(message.text));
    }

    public static <C extends Connection & Lettered> EditLetteredConnectionMessage<C> deserialize(final PacketBuffer buf) {
        final EditLetteredConnectionMessage<C> message = new EditLetteredConnectionMessage<>();
        ConnectionMessage.deserialize(message, buf);
        message.text = StyledString.deserialize(buf.readCompoundTag());
        return message;
    }

    public static final class Handler implements BiConsumer<EditLetteredConnectionMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final EditLetteredConnectionMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> this.accept((EditLetteredConnectionMessage<?>) message, player));
            context.setPacketHandled(true);
        }

        private <C extends Connection & Lettered> void accept(final EditLetteredConnectionMessage<C> message, final ServerPlayerEntity player) {
            if (player != null) {
                final C connection = ConnectionMessage.getConnection(message, c -> c instanceof Lettered, player.world);
                if (connection != null && connection.isModifiable(player) && connection.isSupportedText(message.text)) {
                    connection.setText(message.text);
                }
            }
        }
    }
}
