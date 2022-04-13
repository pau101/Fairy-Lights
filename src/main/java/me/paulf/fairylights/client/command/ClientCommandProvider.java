package me.paulf.fairylights.client.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.gson.internal.UnsafeAllocator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * @author Paul Fulham
 * @version 1.0.0
 */
public final class ClientCommandProvider {
    private static final UnsafeAllocator ALLOCATOR = UnsafeAllocator.create();

    private final Commands commands;

    private final ImmutableMap<String, CommandBuilder> builders;

    private final Pattern chatPredicate;

    public ClientCommandProvider(final Commands commands, final ImmutableMap<String, CommandBuilder> builders, final Pattern chatPredicate) {
        this.commands = commands;
        this.builders = builders;
        this.chatPredicate = chatPredicate;
    }

    private void onKeyPressedEvent(final ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (event.getScreen() instanceof ChatScreen) {
            final ClientPacketListener net = Minecraft.getInstance().getConnection();
            if (net == null) {
                return;
            }
            final RootCommandNode<SharedSuggestionProvider> root = net.getCommands().getRoot();
            for (final ImmutableMap.Entry<String, CommandBuilder> e : this.builders.entrySet()) {
                if (root.getChild(e.getKey()) == null) {
                    root.addChild(e.getValue().build(new SuggestionHelper()).build());
                }
            }
        }
    }

    private void onChatEvent(final ClientChatEvent event) {
        final String message = event.getMessage();
        if (this.chatPredicate.matcher(message).matches()) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(message);
            final LocalPlayer user = Minecraft.getInstance().player;
            if (user != null) {
                this.commands.performCommand(this.createSource(user), message);
            }
        }
    }

    private CommandSourceStack createSource(final Entity entity) {
        //noinspection ConstantConditions
        return new CommandSourceStack(new NoLoggingSource(entity), entity.position(), entity.getRotationVector(), null, 4, entity.getName().getString(), entity.getDisplayName(), DummyServer.INSTANCE, entity);
    }

    public void register(final IEventBus bus) {
        bus.addListener(this::onKeyPressedEvent);
        bus.addListener(this::onChatEvent);
    }

    private static <T> T instantiate(final Class<T> clazz) {
        try {
            return ALLOCATOR.newInstance(clazz);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Builder {
        private final List<CommandBuilder> builders = new ArrayList<>();

        public <S> Builder add(final CommandBuilder builder) {
            this.builders.add(builder);
            return this;
        }

        public ClientCommandProvider build() {
            final Commands commands = instantiate(Commands.class);
            final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
            final ImmutableMap.Builder<String, CommandBuilder> map = new ImmutableMap.Builder<>();
            for (final CommandBuilder builder : this.builders) {
                map.put(dispatcher.register(builder.build(new ExecutionHelper())).getName(), builder);
            }
            ObfuscationReflectionHelper.setPrivateValue(Commands.class, commands, dispatcher, "field_197062_b");
            final ImmutableMap<String, CommandBuilder> builders = map.build();
            final Pattern pattern = Pattern.compile(
                String.format(
                    "^/(%s)(\\p{javaWhitespace}.*|$)",
                    builders.keySet().stream()
                        .map(Pattern::quote)
                        .collect(Collectors.joining("|"))
                ),
                Pattern.DOTALL
            );
            return new ClientCommandProvider(commands, builders, pattern);
        }
    }

    public interface CommandBuilder {
        <S> LiteralArgumentBuilder<S> build(Helper<S> helper);
    }

    public interface Helper<S> {
        <T extends ArgumentBuilder<S, T>> T executes(final T builder, final Command<CommandSource> command);
    }

    private static final class ExecutionHelper implements Helper<CommandSource> {
        @Override
        public <T extends ArgumentBuilder<CommandSource, T>> T executes(final T builder, final Command<CommandSource> command) {
            return builder.executes(command);
        }
    }

    private static final class SuggestionHelper implements Helper<SuggestionProvider> {
        @Override
        public <T extends ArgumentBuilder<SuggestionProvider, T>> T executes(final T builder, final Command<CommandSource> command) {
            return builder;
        }
    }

    private static final class DummyServer extends IntegratedServer {
        private static final DummyServer INSTANCE = instantiate(DummyServer.class);

        public DummyServer() {
            //noinspection ConstantConditions
            super(null, null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public ProfilerFiller getProfiler() {
            return InactiveProfiler.INSTANCE;
        }
    }

    private static final class NoLoggingSource implements CommandSource {
        private final Entity entity;

        public NoLoggingSource(final Entity entity) {
            this.entity = entity;
        }

        @Override
        public void sendMessage(final Component component, final UUID sender) {
            this.entity.sendMessage(component, sender);
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }
    }
}
