package me.paulf.fairylights.client.tutorial;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.paulf.fairylights.client.FLClientConfig;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.LazyItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClippyController {
    private final ImmutableMap<String, Supplier<State>> states = Stream.<Supplier<State>>of(
            NoProgressState::new,
            CraftHangingLightsState::new,
            CompleteState::new
        ).collect(ImmutableMap.toImmutableMap(s -> s.get().name(), Function.identity()));

    private State state = new NoProgressState();

    public void init(final IEventBus modBus) {
        MinecraftForge.EVENT_BUS.addListener((final WorldEvent.Load event) -> {
            if (event.getWorld() instanceof ClientLevel) {
                this.reload();
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.ClientTickEvent event) -> {
            final Minecraft mc = Minecraft.getInstance();
            if (event.phase == TickEvent.Phase.END && !mc.isPaused() && mc.player != null) {
                this.state.tick(mc.player, this);
            }
        });
        modBus.<ModConfigEvent.Loading>addListener(e -> {
            if (e.getConfig().getSpec() == FLClientConfig.SPEC && Minecraft.getInstance().player != null) {
                this.reload();
            }
        });
        MinecraftForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggedInEvent>addListener(e -> {
            this.reload();
            this.state.tick(e.getPlayer(), this);
        });
    }

    private void reload() {
        this.setState(this.states.getOrDefault(FLClientConfig.TUTORIAL.progress.get(), NoProgressState::new).get());
    }

    private void setState(final State state) {
        this.state.stop();
        this.state = state;
        this.state.start();
        FLClientConfig.TUTORIAL.progress.set(this.state.name());
        FLClientConfig.TUTORIAL.progress.save();
    }

    interface State {
        String name();

        default void start() {}

        default void tick(final LocalPlayer player, final ClippyController controller) {}

        default void stop() {}
    }

    static class NoProgressState implements State {
        @Override
        public String name() {
            return "none";
        }

        @Override
        public void tick(final LocalPlayer player, final ClippyController controller) {
            if (player.getInventory().contains(FLCraftingRecipes.LIGHTS)) {
                controller.setState(new CraftHangingLightsState());
            }
        }
    }

    static class CraftHangingLightsState implements State {
        final Balloon balloon;

        CraftHangingLightsState() {
            this.balloon = new Balloon(new LazyItemStack(FLItems.HANGING_LIGHTS, Item::getDefaultInstance),
                new TranslatableComponent("tutorial.fairylights.craft_hanging_lights.title"),
                new TranslatableComponent("tutorial.fairylights.craft_hanging_lights.description")
            );
        }

        @Override
        public String name() {
            return "hanging_lights";
        }

        @Override
        public void start() {
            Minecraft.getInstance().getToasts().addToast(this.balloon);
        }

        @Override
        public void tick(final LocalPlayer player, final ClippyController controller) {
            if (!player.getInventory().contains(FLCraftingRecipes.LIGHTS) &&
                    !player.getInventory().getSelected().is(FLCraftingRecipes.LIGHTS)) {
                controller.setState(new NoProgressState());
            } else if (FLItems.HANGING_LIGHTS.filter(i ->
                    player.getInventory().getSelected().getItem() == i ||
                    player.getInventory().contains(new ItemStack(i)) ||
                    player.getStats().getValue(Stats.ITEM_CRAFTED.get(i)) > 0).isPresent()) {
                controller.setState(new CompleteState());
            }
        }

        @Override
        public void stop() {
            this.balloon.hide();
        }
    }

    static class CompleteState implements State {
        @Override
        public String name() {
            return "complete";
        }
    }

    static class Balloon implements Toast {
        final LazyItemStack stack;
        final Component title;
        @Nullable
        final Component subtitle;
        Toast.Visibility visibility;

        Balloon(final LazyItemStack stack, final Component title, @Nullable final Component subtitle) {
            this.stack = stack;
            this.title = title;
            this.subtitle = subtitle;
            this.visibility = Visibility.SHOW;
        }

        void hide() {
            this.visibility = Toast.Visibility.HIDE;
        }

        @Override
        public Visibility render(final PoseStack stack, final ToastComponent toastGui, final long delta) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            toastGui.blit(stack, 0, 0, 0, 96, 160, 32);
            toastGui.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(this.stack.get(), 6 + 2, 6 + 2);
            if (this.subtitle == null) {
                toastGui.getMinecraft().font.draw(stack, this.title, 30.0F, 12.0F, 0xFF500050);
            } else {
                toastGui.getMinecraft().font.draw(stack, this.title, 30.0F, 7.0F, 0xFF500050);
                toastGui.getMinecraft().font.draw(stack, this.subtitle, 30.0F, 18.0F, 0xFF000000);
            }
            return this.visibility;
        }
    }
}
