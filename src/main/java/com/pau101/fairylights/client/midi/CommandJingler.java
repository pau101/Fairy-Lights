package com.pau101.fairylights.client.midi;

import com.pau101.fairylights.client.ClientEventHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class CommandJingler extends CommandBase {
	private static final List<String> OPTIONS = Arrays.asList("open", "close", "list");

	private static final Set<MidiDevice> DEVICES = new HashSet<>();

	private static final Map<ConnectionHangingLights, List<MidiDeviceTransmitter>> TRANSMITTERS = new WeakHashMap<>();

	private static final Field CHAT_INPUT_FIELD = Utils.getFieldOfType(GuiChat.class, GuiTextField.class);

	@Nullable
	private static Thread shutdownCloser;

	@Override
	public String getUsage(ICommandSender sender) {
		return "/jingler <open|close|list>";
	}

	@Override
	public String getName() {
		return "jingler";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// Hide the command in the command list
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if (screen instanceof GuiChat) {
			try {
				// Let tab complete work
				GuiTextField field = (GuiTextField) CHAT_INPUT_FIELD.get(screen);
				if (field.getText().startsWith("/jingler")) {
					return true;
				}
			} catch (Exception e) {}
		}
		// false for getTabCompletionOptions, true for executeCommand
		return Thread.currentThread().getStackTrace()[2].getClassName().contains("forge");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new WrongUsageException(getUsage(sender));
		}
		String option = args[0];
		if ("open".equalsIgnoreCase(option)) {
			if (args.length < 2) {
				throw new WrongUsageException("/jingler open <device name>");
			}
			Connection connection = ClientEventHandler.getHitConnection();
			if (!(connection instanceof ConnectionHangingLights)) {
				throw new CommandException("Must be looking at hanging lights");
			}
			ConnectionHangingLights hangingLights = (ConnectionHangingLights) connection;
			MidiDevice device = getDeviceFromArgs(args, 1);
			for (Transmitter transmitter : device.getTransmitters()) {
				Receiver rec = transmitter.getReceiver();
				if (rec instanceof MidiJingler && ((MidiJingler) rec).getConnection() == connection) {
					throw new CommandException("Device already connected");
				}
			}
			MidiDeviceTransmitter transmitter;
			try {
				transmitter = toMidiDeviceTransmitter(device.getTransmitter(), device);
				if (!device.isOpen()) {
					device.open();
					trackDevice(device);
				}
			} catch (MidiUnavailableException e) {
				throw new CommandException("Unable to open connection", e);
			}
			List<MidiDeviceTransmitter> transmitters = TRANSMITTERS.get(connection);
			if (transmitters == null) {
				transmitters = new ArrayList<>();
				TRANSMITTERS.put(hangingLights, transmitters);
			}
			transmitters.add(transmitter);
			transmitter.setReceiver(new MidiJingler(hangingLights));
			hangingLights.addRemoveListener(new ConnectionRemoveListener(hangingLights));
			sender.sendMessage(new TextComponentString("Connection opened"));
		} else if ("close".equalsIgnoreCase(option)) {
			MidiDevice device = args.length == 1 ? null : getDeviceFromArgs(args, 1);
			Connection connection = ClientEventHandler.getHitConnection();
			int closed = 0;
			if (connection instanceof ConnectionHangingLights) {
				closed = closeTransmitters((ConnectionHangingLights) connection, device);
			} else {
				Iterator<List<MidiDeviceTransmitter>> iter = TRANSMITTERS.values().iterator();
				while (iter.hasNext()) {
					List<MidiDeviceTransmitter> transmitters = iter.next();
					closed += closeTransmitters(transmitters, device);
					if (transmitters.isEmpty()) {
						iter.remove();
					}
				}
			}
			StringBuilder msg = new StringBuilder();
			if (closed == 0) {
				msg.append("No");
			} else {
				msg.append(closed);
			}
			msg.append(" connection");
			if (closed != 1) {
				msg.append('s');
			}
			msg.append(" closed");
			if (closed > 0 && TRANSMITTERS.isEmpty()) {
				msg.append(", non remaining");
			}
			sender.sendMessage(new TextComponentString(msg.toString()));
		} else if ("list".equalsIgnoreCase(option)) {
			if (TRANSMITTERS.isEmpty()) {
				sender.sendMessage(new TextComponentString("No connections to list"));
			} else {
				List<ConnectionHangingLights> allConnections = new ArrayList<>(TRANSMITTERS.keySet());
				allConnections.sort(Comparator.comparingDouble(c -> sender.getPositionVector().distanceTo(c.getFastener().getConnectionPoint())));
				Map<MidiDevice, List<ConnectionHangingLights>> deviceConnections = new HashMap<>();
				for (ConnectionHangingLights connection : allConnections) {
					for (MidiDeviceTransmitter t : TRANSMITTERS.get(connection)) {
						MidiDevice device = t.getMidiDevice();
						deviceConnections.computeIfAbsent(device, k -> new ArrayList<>()).add(connection);
					}
				}
				List<MidiDevice> devices = new ArrayList<>(deviceConnections.keySet());
				devices.sort(Comparator.comparing(d -> d.getDeviceInfo().getName()));
				for (MidiDevice device : devices) {
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + device.getDeviceInfo().getName()));
					for (ConnectionHangingLights connection : deviceConnections.get(device)) {
						Fastener<?> fastener = connection.getFastener();
						FastenerAccessor dest = connection.getDestination();
						String destStr;
						if (dest.isLoaded(sender.getEntityWorld())) {
							destStr = dest.get(sender.getEntityWorld()).toString();
						} else {
							destStr = "?";
						}
						sender.sendMessage(new TextComponentString(String.format("  Connection: %s to %s", fastener, destStr)));
					}
				}
			}
		} else {
			throw new WrongUsageException(getUsage(sender));
		}
	}

	private MidiDevice getDeviceFromArgs(String[] args, int n) throws CommandException {
		MidiDevice device = getDevice(getNoSpaceName(String.join(" ", Arrays.copyOfRange(args, n, args.length))));
		if (device == null) {
			throw new CommandException("Device not found");
		}
		return device;
	}

	private void trackDevice(MidiDevice device) {
		DEVICES.add(device);
		if (shutdownCloser == null) {
			Runtime.getRuntime().addShutdownHook(shutdownCloser = new Thread("MIDI Device Closer") {
				@Override
				public void run() {
					DEVICES.forEach(MidiDevice::close);
					DEVICES.clear();
				}
			});
		}
	}

	private MidiDeviceTransmitter toMidiDeviceTransmitter(Transmitter transmitter, MidiDevice device) {
		if (transmitter instanceof MidiDeviceTransmitter) {
			return (MidiDeviceTransmitter) transmitter;
		}
		return new MidiDeviceTransmitter() {
			@Override
			public void setReceiver(Receiver receiver) {
				transmitter.setReceiver(receiver);
			}

			@Override
			public Receiver getReceiver() {
				return transmitter.getReceiver();
			}

			@Override
			public void close() {
				transmitter.close();
			}

			@Override
			public MidiDevice getMidiDevice() {
				return device;
			}
		};
	}

	private int closeTransmitters(ConnectionHangingLights connection, MidiDevice device) {
		int closed = 0;
		List<MidiDeviceTransmitter> transmitters = TRANSMITTERS.get(connection);
		if (transmitters != null) {
			closed = closeTransmitters(transmitters, device);
			if (transmitters.isEmpty()) {
				TRANSMITTERS.remove(connection);
			}
		}
		return closed;
	}

	private int closeTransmitters(List<MidiDeviceTransmitter> transmitters, MidiDevice device) {
		int closed;
		if (device == null) {
			closed = transmitters.size(); 
			transmitters.forEach(Transmitter::close);
			transmitters.clear();
		} else {
			closed = 0;
			Iterator<MidiDeviceTransmitter> iter = transmitters.iterator();
			while (iter.hasNext()) {
				MidiDeviceTransmitter t = iter.next();
				if (t.getMidiDevice() == device) {
					closed++;
					t.close();
					iter.remove();
				}
			}
		}
		return closed;
	}

	private MidiDevice getDevice(String name) {
		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);
				if (isUseableDevice(device) && name.equals(getNoSpaceName(info.getName()))) {
					return device;
				}
			} catch (MidiUnavailableException e) {}
		}
		return null;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		int len = args.length;
		if (len == 1) {
			return OPTIONS;
		} else if (len == 2 && "open".equals(args[0]) || "close".equals(args[0])) {
			List<String> devices = new ArrayList<>();
			for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
				try {
					MidiDevice device = MidiSystem.getMidiDevice(info);
					if (shouldShowDevice(device)) {
						devices.add(getNoSpaceName(info.getName()));
					}
				} catch (MidiUnavailableException e) {}
			}
			return devices;
		}
		return Collections.EMPTY_LIST;
	}

	private String getNoSpaceName(String name) {
		return WordUtils.capitalize(StringUtils.normalizeSpace(name)).replace(" ", "");
	}

	private boolean shouldShowDevice(MidiDevice device) { 
		// Reject Sequencers since internal midi devices aren't really what the user would want
		return isUseableDevice(device) && !(device instanceof Sequencer);
	}

	private boolean isUseableDevice(MidiDevice device) { 
		return device.getMaxTransmitters() != 0;
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		closeAllTransmitters();
	}

	@SubscribeEvent
	public void unload(WorldEvent.Unload event) {
		closeAllTransmitters();
	}

	private void closeAllTransmitters() {
		Iterator<List<MidiDeviceTransmitter>> iter = TRANSMITTERS.values().iterator();
		while (iter.hasNext()) {
			closeTransmitters(iter.next(), null);
			iter.remove();
		}
	}

	private class ConnectionRemoveListener implements Runnable {
		private final ConnectionHangingLights connection;

		public ConnectionRemoveListener(ConnectionHangingLights connection) {
			this.connection = connection;
		}

		@Override
		public void run() {
			closeTransmitters(connection, null);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ConnectionRemoveListener && ((ConnectionRemoveListener) obj).connection == connection;
		}

		@Override
		public int hashCode() {
			return connection.hashCode();
		}
	}
}
