package me.paulf.fairylights.server.net;

import net.minecraft.network.FriendlyByteBuf;

public interface Message {
    void encode(final FriendlyByteBuf buf);

    void decode(final FriendlyByteBuf buf);
}
