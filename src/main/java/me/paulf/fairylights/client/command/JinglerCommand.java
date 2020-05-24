package me.paulf.fairylights.client.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.paulf.fairylights.client.ClientEventHandler;
import me.paulf.fairylights.client.midi.MidiJingler;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.LogManager;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import java.util.List;
import java.util.stream.Stream;

public final class JinglerCommand {
    private static final SimpleCommandExceptionType NO_HANGING_LIGHTS = new SimpleCommandExceptionType(new TranslationTextComponent("commands.jingler.open.no_hanging_lights"));

    private static final DynamicCommandExceptionType DEVICE_UNAVAILABLE = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.unavailable", name));

    private static final DynamicCommandExceptionType DEVICE_NOT_FOUND = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.not_found", name));

    private static final SimpleCommandExceptionType CLOSE_FAILURE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.jingler.close.failure"));

    public static <S> LiteralArgumentBuilder<S> register(final ClientCommandProvider.Helper<S> helper) {
        return LiteralArgumentBuilder.<S>literal("jingler")
            .then(LiteralArgumentBuilder.<S>literal("open").then(helper.executes(
                RequiredArgumentBuilder.<S, String>argument("device", StringArgumentType.greedyString())
                    .suggests((context, builder) -> {
                        getDevices().forEach(device -> builder.suggest(device.getDeviceInfo().getName(), createDeviceText(device)));
                        return builder.buildFuture();
                    }),
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
                    .suggests((context, builder) -> {
                        getDevices().filter(device -> device.getTransmitters().stream().anyMatch(t -> t.getReceiver() instanceof MidiJingler)).forEach(device -> {
                            builder.suggest(device.getDeviceInfo().getName(), createDeviceText(device));
                        });
                        return builder.buildFuture();
                    }),
                ctx -> {
                    final String name = StringArgumentType.getString(ctx, "device");
                    final MidiDevice device = getMidiDevice(name);
                    final int closed = device.getTransmitters().stream()
                        .filter(t -> t.getReceiver() instanceof MidiJingler)
                        .reduce(0, (count, transmitter) -> {
                            transmitter.close();
                            return count + 1;
                        }, Integer::sum);
                    if (closed == 0) {
                        throw CLOSE_FAILURE.create();
                    }
                    ctx.getSource().sendFeedback(new TranslationTextComponent("commands.jingler.close.success", closed), false);
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
                LogManager.getLogger().debug("Midi device unavailable: \"{}\", reason: \"{}\"", info.getName(), e.getMessage());
                continue;
            }
            if (device.getMaxTransmitters() != 0 && !(device instanceof Sequencer)) {
                bob.accept(device);
            }
        }
        return bob.build();
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
    }
}
