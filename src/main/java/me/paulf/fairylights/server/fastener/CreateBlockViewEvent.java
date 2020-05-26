package me.paulf.fairylights.server.fastener;

import net.minecraftforge.eventbus.api.*;

public class CreateBlockViewEvent extends Event {
    private BlockView view;

    public CreateBlockViewEvent(final BlockView view) {
        this.view = view;
    }

    public BlockView getView() {
        return this.view;
    }

    public void setView(final BlockView view) {
        this.view = view;
    }
}
