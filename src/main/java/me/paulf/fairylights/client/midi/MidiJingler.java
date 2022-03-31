package me.paulf.fairylights.client.midi;

import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.Minecraft;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public final class MidiJingler implements Receiver {
    private HangingLightsConnection connection;

    public MidiJingler(final HangingLightsConnection connection) {
        this.connection = connection;
    }

    public HangingLightsConnection getConnection() {
        return this.connection;
    }

    @Override
    public void send(final MidiMessage msg, final long timestamp) {
        if (this.connection == null) {
            return;
        }
        if (!(msg instanceof ShortMessage)) {
            return;
        }
        final ShortMessage shortMsg = (ShortMessage) msg;
        if (shortMsg.getCommand() != ShortMessage.NOTE_ON || shortMsg.getData2() == 0) {
            return;
        }
        final int inMCSpace = shortMsg.getData1() - 53; // F#3 is 0
        // Keep F#5 the same
        final int note = inMCSpace == 24 ? 24 : Mth.mod(inMCSpace, 24);
        final Light<?>[] lights = this.connection.getFeatures();
        final int offset = lights.length / 2 - 12;
        final int idx = note + offset;
        if (idx >= 0 && idx < lights.length) {
            Minecraft.func_71410_x().execute(() -> lights[idx].jingle(this.connection.getWorld(), this.connection.getFastener().getConnectionPoint(), note));
        }
    }

    @Override
    public void close() {
        this.connection = null;
    }
}
