package me.paulf.fairylights.client.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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

public final class JinglerCommand {
    private static final DynamicCommandExceptionType DEVICE_UNAVAILABLE = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.unavailable", name));

    private static final DynamicCommandExceptionType DEVICE_NOT_FOUND = new DynamicCommandExceptionType(name -> new TranslationTextComponent("commands.jingler.open.not_found", name));

    public static <S> LiteralArgumentBuilder<S> register(final ClientCommandProvider.Helper<S> helper) {
        return LiteralArgumentBuilder.<S>literal("jingler")
            .then(LiteralArgumentBuilder.<S>literal("open").then(helper.executes(
                RequiredArgumentBuilder.<S, String>argument("device", StringArgumentType.greedyString())
                    .suggests(((context, builder) -> {
                        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                            MidiDevice device;
                            try {
                                device = MidiSystem.getMidiDevice(info);
                            } catch (final MidiUnavailableException e) {
                                LogManager.getLogger().debug("Midi device unavailable: \"{}\", reason: \"{}\"", info.getName(), e.getMessage());
                                continue;
                            }
                            if (device.getMaxTransmitters() != 0 && !(device instanceof Sequencer)) {
                                builder.suggest(info.getName(), createDeviceText(info));
                            }
                        }
                        return builder.buildFuture();
                    })),
                ctx -> {
                    final String name = StringArgumentType.getString(ctx, "device");
                    final MidiDevice device = getMidiDevice(name);
                    final Transmitter transmitter;
                    try {
                        transmitter = device.getTransmitter();
                    } catch (final MidiUnavailableException e) {
                        throw DEVICE_UNAVAILABLE.create(name);
                    }
//                    transmitter.setReceiver(new MidiJingler());
                    return 1;
                })))
            .then(LiteralArgumentBuilder.<S>literal("close"))
            .then(LiteralArgumentBuilder.<S>literal("list"));
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

    private static ITextComponent createDeviceText(final MidiDevice.Info info) {
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
