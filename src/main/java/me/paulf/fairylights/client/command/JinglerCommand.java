package me.paulf.fairylights.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.paulf.fairylights.client.ClientEventHandler;
import me.paulf.fairylights.client.midi.MidiJingler;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public final class JinglerCommand {
    private static final AtomicBoolean USED_COMMAND = new AtomicBoolean(false);

    private static final Logger LOGGER = LogManager.getLogger();

    private static final SimpleCommandExceptionType NO_HANGING_LIGHTS = new SimpleCommandExceptionType(Component.translatable("commands.jingler.open.failure.no_hanging_lights"));

    private static final DynamicCommandExceptionType DEVICE_UNAVAILABLE = new DynamicCommandExceptionType(name -> Component.translatable("commands.jingler.open.failure.device_unavailable", name));

    private static final DynamicCommandExceptionType DEVICE_NOT_FOUND = new DynamicCommandExceptionType(name -> Component.translatable("commands.jingler.open.failure.not_found", name));

    private static final SimpleCommandExceptionType CLOSE_FAILURE = new SimpleCommandExceptionType(Component.translatable("commands.jingler.close.failure"));

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jingler")
            .then(Commands.literal("open").then(Commands.argument("device", StringArgumentType.greedyString())
                .suggests((context, builder) -> getDevices()
                    .reduce(builder, (b, device) -> b.suggest(device.getDeviceInfo().getName(), createDeviceText(device)), SuggestionsBuilder::add)
                    .buildFuture()
                ).executes(ctx -> {
                    final Connection conn = ClientEventHandler.getHitConnection();
                    if (!(conn instanceof HangingLightsConnection)) {
                        throw NO_HANGING_LIGHTS.create();
                    }
                    final String name = StringArgumentType.getString(ctx, "device");
                    final MidiDevice device = getMidiDevice(name);
                    final Transmitter transmitter;
                    try {
                        transmitter = device.getTransmitter();
                    } catch (final MidiUnavailableException e) {
                        throw DEVICE_UNAVAILABLE.create(name);
                    }
                    if (!device.isOpen()) {
                        try {
                            device.open();
                        } catch (final MidiUnavailableException e) {
                            throw DEVICE_UNAVAILABLE.create(name);
                        }
                    }
                    transmitter.setReceiver(new MidiJingler((HangingLightsConnection) conn));
                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.jingler.open.success", name), false);
                    USED_COMMAND.compareAndSet(false, true);
                    return 1;
                })))
            .then(Commands.literal("close").then(Commands.argument("device", StringArgumentType.greedyString())
                .suggests((context, builder) -> getDevices()
                    .filter(device -> device.getTransmitters().stream().anyMatch(t -> t.getReceiver() instanceof MidiJingler))
                    .reduce(builder, (b, device) -> builder.suggest(device.getDeviceInfo().getName(), createDeviceText(device)), SuggestionsBuilder::add)
                    .buildFuture()
                ).executes(ctx -> {
                    final String name = StringArgumentType.getString(ctx, "device");
                    final MidiDevice device = getMidiDevice(name);
                    final int closed = close(device);
                    if (closed == 0) {
                        throw CLOSE_FAILURE.create();
                    }
                    ctx.getSource().sendSuccess(() -> Component.translatable(closed == 1 ? "commands.jingler.close.success.single" : "commands.jingler.close.success.multiple", closed), false);
                    return closed;
                })
            )));
    }

    private static MidiDevice getMidiDevice(final String name) throws CommandSyntaxException {
        for (final MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (name.equals(info.getName())) {
                try {
                    return MidiSystem.getMidiDevice(info);
                } catch (final MidiUnavailableException e) {
                    throw DEVICE_UNAVAILABLE.create(name);
                }
            }
        }
        throw DEVICE_NOT_FOUND.create(name);
    }

    private static Stream<MidiDevice> getDevices() {
        final Stream.Builder<MidiDevice> bob = Stream.builder();
        for (final MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            final MidiDevice device;
            try {
                device = MidiSystem.getMidiDevice(info);
            } catch (final IllegalArgumentException ignored) {
                continue;
            } catch (final MidiUnavailableException e) {
                LOGGER.debug("Midi device unavailable: \"{}\", reason: \"{}\"", info.getName(), e.getMessage());
                continue;
            }
            if (device.getMaxTransmitters() != 0 && !(device instanceof Sequencer)) {
                bob.accept(device);
            }
        }
        return bob.build();
    }

    private static int close(final MidiDevice device) {
        final int closed = device.getTransmitters().stream()
            .filter(t -> t.getReceiver() instanceof MidiJingler)
            .reduce(0, (count, transmitter) -> {
                transmitter.close();
                return count + 1;
            }, Integer::sum);
        if (device.getTransmitters().isEmpty()) {
            device.close();
        }
        return closed;
    }

    private static Component createDeviceText(final MidiDevice device) {
        final MidiDevice.Info info = device.getDeviceInfo();
        return Component.translatable("commands.jingler.device.vendor", Component.literal(info.getVendor()).withStyle(ChatFormatting.GOLD))
            .append("\n")
            .append(Component.translatable("commands.jingler.device.description", Component.literal(info.getDescription()).withStyle(ChatFormatting.GOLD)));
    }

    public static void register(final IEventBus bus) {
        // TODO: jingler tooltip line splitting
        /*bus.<RenderTooltipEvent.Pre>addListener(EventPriority.HIGH, e -> {
            if (!e.getStack().isEmpty()) return;
            final List<? extends ITextProperties> lines = e.getLines();
            if (lines.size() != 1) return;
            final ITextProperties line = lines.get(0);
            final String[] split = line.split("\n");
            if (split.length == 1) return;
            e.setCanceled(true);
            GuiUtils.drawHoveringText(
                ImmutableList.copyOf(split),
                e.getX(),
                e.getY(),
                e.getScreenWidth(),
                e.getScreenHeight(),
                e.getMaxWidth(),
                e.getFontRenderer()
            );
        });*/
        bus.<LevelEvent.Unload>addListener(e -> {
            if (e.getLevel().isClientSide() && USED_COMMAND.compareAndSet(true, false)) {
                getDevices().forEach(JinglerCommand::close);
            }
        });
    }
}
