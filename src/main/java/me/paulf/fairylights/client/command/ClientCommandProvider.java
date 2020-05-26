package me.paulf.fairylights.client.command;

import com.google.common.collect.*;
import com.google.gson.internal.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.player.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.network.play.*;
import net.minecraft.command.*;
import net.minecraft.entity.*;
import net.minecraft.profiler.*;
import net.minecraft.server.integrated.*;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

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

    private void onKeyPressedEvent(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            final ClientPlayNetHandler net = Minecraft.getInstance().getConnection();
            if (net == null) {
                return;
            }
            final RootCommandNode<ISuggestionProvider> root = net.getCommandDispatcher().getRoot();
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
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(message);
            final ClientPlayerEntity user = Minecraft.getInstance().player;
            if (user != null) {
                this.commands.handleCommand(this.createSource(user), message);
            }
        }
    }

    private CommandSource createSource(final Entity entity) {
        //noinspection ConstantConditions
        return new CommandSource(new NoLoggingSource(entity), entity.getPositionVec(), entity.getPitchYaw(), null, 4, entity.getName().getString(), entity.getDisplayName(), DummyServer.INSTANCE, entity);
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

    private static final class SuggestionHelper implements Helper<ISuggestionProvider> {
        @Override
        public <T extends ArgumentBuilder<ISuggestionProvider, T>> T executes(final T builder, final Command<CommandSource> command) {
            return builder;
        }
    }

    private static final class DummyServer extends IntegratedServer {
        private static final DummyServer INSTANCE = instantiate(DummyServer.class);

        public DummyServer() {
            //noinspection ConstantConditions
            super(null, null, null, null, null, null, null, null, null);
        }

        @Override
        public DebugProfiler getProfiler() {
            return new DebugProfiler(() -> 0);
        }
    }

    private static final class NoLoggingSource implements ICommandSource {
        private final Entity entity;

        public NoLoggingSource(final Entity entity) {
            this.entity = entity;
        }

        @Override
        public void sendMessage(final ITextComponent message) {
            this.entity.sendMessage(message);
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return true;
        }

        @Override
        public boolean shouldReceiveErrors() {
            return true;
        }

        @Override
        public boolean allowLogging() {
            return false;
        }
    }
}
