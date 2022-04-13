package me.paulf.fairylights.server.net;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class ClientMessageContext extends MessageContext {
    public ClientMessageContext(final NetworkEvent.Context context) {
        super(context);
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public ClientLevel getWorld() {
        return Objects.requireNonNull(this.getMinecraft().level);
    }

    public Player getPlayer() {
        return Objects.requireNonNull(this.context.getSender());
    }
}
