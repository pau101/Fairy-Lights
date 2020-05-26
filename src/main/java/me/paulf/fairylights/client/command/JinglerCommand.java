package me.paulf.fairylights.client.command;

import com.google.common.collect.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.*;
import me.paulf.fairylights.client.*;
import me.paulf.fairylights.client.midi.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.*;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.client.gui.*;
import org.apache.logging.log4j.*;

import javax.sound.midi.*;
import java.util.*;
import java.util.stream.*;

public final class JinglerCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final SimpleCommandExceptionType NO_HANGING_LIGHTS = new SimpleCommandExceptionType(new TranslationTextComponent("commands.jingler.open.failure.no_hanging_lights"));

    private static final DynamicCommandExceptionType DEVICE_UNAVAILABLE = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.failure.device_unavailable", name));

    private static final DynamicCommandExceptionType DEVICE_NOT_FOUND = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.failure.not_found", name));

    private static final SimpleCommandExceptionType CLOSE_FAILURE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.jingler.close.failure"));

    public static <S> LiteralArgumentBuilder<S> register(final ClientCommandProvider.Helper<S> helper) {
        return LiteralArgumentBuilder.<S>literal("jingler")
            .then(LiteralArgumentBuilder.<S>literal("open").then(helper.executes(
                RequiredArgumentBuilder.<S, String>argument("device", StringArgumentType.greedyString())
                    .suggests((context, builder) -> getDevices()
                        .reduce(builder, (b, device) -> b.suggest(device.getDeviceInfo().getName(), createDeviceText(device)), SuggestionsBuilder::add)
                        .buildFuture()
                    ),
                ctx -> {
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
                    ctx.getSource().sendFeedback(new TranslationTextComponent("commands.jingler.open.success", name), false);
                    return 1;
                })))
            .then(LiteralArgumentBuilder.<S>literal("close").then(helper.executes(
                RequiredArgumentBuilder.<S, String>argument("device", StringArgumentType.greedyString())
                    .suggests((context, builder) -> getDevices()
                        .filter(device -> device.getTransmitters().stream().anyMatch(t -> t.getReceiver() instanceof MidiJingler))
                        .reduce(builder, (b, device) -> builder.suggest(device.getDeviceInfo().getName(), createDeviceText(device)), SuggestionsBuilder::add)
                        .buildFuture()
                    ),
                ctx -> {
                    final String name = StringArgumentType.getString(ctx, "device");
                    final MidiDevice device = getMidiDevice(name);
                    final int closed = close(device);
                    if (closed == 0) {
                        throw CLOSE_FAILURE.create();
                    }
                    ctx.getSource().sendFeedback(new TranslationTextComponent(closed == 1 ? "commands.jingler.close.success.single" : "commands.jingler.close.success.multiple", closed), false);
                    return closed;
                }
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

    private static ITextComponent createDeviceText(final MidiDevice device) {
        final MidiDevice.Info info = device.getDeviceInfo();
        return new StringTextComponent("")
            .appendSibling(new TranslationTextComponent("commands.jingler.device.vendor", new StringTextComponent(info.getVendor()).applyTextStyle(TextFormatting.GOLD)))
            .appendText("\n")
            .appendSibling(new TranslationTextComponent("commands.jingler.device.description", new StringTextComponent(info.getDescription()).applyTextStyle(TextFormatting.GOLD)));
    }

    public static void register(final IEventBus bus) {
        bus.<RenderTooltipEvent.Pre>addListener(EventPriority.HIGH, e -> {
            if (!e.getStack().isEmpty()) return;
            final List<String> lines = e.getLines();
            if (lines.size() != 1) return;
            final String line = lines.get(0);
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
        });
        bus.<WorldEvent.Unload>addListener(e -> getDevices().forEach(MidiDevice::close));
    }
}
