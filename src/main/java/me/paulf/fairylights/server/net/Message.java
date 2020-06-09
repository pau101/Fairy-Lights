package me.paulf.fairylights.server.net;

import net.minecraft.network.PacketBuffer;

public interface Message {
    void encode(final PacketBuffer buf);

    void decode(final PacketBuffer buf);
}
