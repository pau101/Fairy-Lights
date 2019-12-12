package me.paulf.fairylights.client.midi;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.Minecraft;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public final class MidiJingler implements Receiver {
	private ConnectionHangingLights connection;

	public MidiJingler(ConnectionHangingLights connection) {
		this.connection = connection;
	}

	public ConnectionHangingLights getConnection() {
		return connection;
	}

	@Override
	public void send(MidiMessage msg, long timestamp) {
		if (!(msg instanceof ShortMessage)) {
			return;
		}
		ShortMessage shortMsg = (ShortMessage) msg;
		if (shortMsg.getCommand() != ShortMessage.NOTE_ON || shortMsg.getData2() == 0) {
			return;
		}
		int inMCSpace = shortMsg.getData1() - 53; // F#3 is 0
		// Keep F#5 the same
		int note = inMCSpace == 24 ? 24 : Mth.mod(inMCSpace, 24);
		Light[] lights = connection.getFeatures();
		int offset = lights.length / 2 - 12;
		int idx = note + offset;
		if (idx >= 0 && idx < lights.length) {
			Minecraft.getInstance().execute(() -> lights[idx].jingle(connection.getWorld(), connection.getFastener().getConnectionPoint(), note));
		}
	}

	@Override
	public void close() {
		connection = null;
	}
}
