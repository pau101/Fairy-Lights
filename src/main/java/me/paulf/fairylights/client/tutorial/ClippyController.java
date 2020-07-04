package me.paulf.fairylights.client.tutorial;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.FLClientConfig;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.LazyItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;

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
            if (event.getWorld() instanceof ClientWorld) {
                this.reload();
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.ClientTickEvent event) -> {
            final Minecraft mc = Minecraft.getInstance();
            if (event.phase == TickEvent.Phase.END && !mc.isGamePaused() && mc.player != null) {
                this.state.tick(mc.player, this);
            }
        });
        modBus.<ModConfig.Loading>addListener(e -> {
            if (e.getConfig().getSpec() == FLClientConfig.SPEC && Minecraft.getInstance().player != null) {
                this.reload();
            }
        });
        modBus.<ClientPlayerNetworkEvent.LoggedInEvent>addListener(e -> {
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

        default void tick(final ClientPlayerEntity player, final ClippyController controller) {}

        default void stop() {}
    }

    static class NoProgressState implements State {
        @Override
        public String name() {
            return "none";
        }

        @Override
        public void tick(final ClientPlayerEntity player, final ClippyController controller) {
            if (player.inventory.hasTag(FLCraftingRecipes.LIGHTS)) {
                controller.setState(new CraftHangingLightsState());
            }
        }
    }

    static class CraftHangingLightsState implements State {
        final Balloon balloon;

        CraftHangingLightsState() {
            this.balloon = new Balloon(new LazyItemStack(FLItems.HANGING_LIGHTS, Item::getDefaultInstance),
                new TranslationTextComponent("tutorial.fairylights.craft_hanging_lights.title"),
                new TranslationTextComponent("tutorial.fairylights.craft_hanging_lights.description")
            );
        }

        @Override
        public String name() {
            return "hanging_lights";
        }

        @Override
        public void start() {
            Minecraft.getInstance().getToastGui().add(this.balloon);
        }

        @Override
        public void tick(final ClientPlayerEntity player, final ClippyController controller) {
            if (player.openContainer.getInventory().stream().noneMatch(stack -> stack.getItem().isIn(FLCraftingRecipes.LIGHTS)) &&
                    !player.inventory.getItemStack().getItem().isIn(FLCraftingRecipes.LIGHTS)) {
                controller.setState(new NoProgressState());
            } else if (FLItems.HANGING_LIGHTS.filter(i ->
                    player.inventory.getItemStack().getItem() == i ||
                    player.inventory.hasItemStack(new ItemStack(i)) ||
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

    static class Balloon implements IToast {
        final LazyItemStack stack;
        final String title;
        final String subtitle;
        IToast.Visibility visibility;

        Balloon(final LazyItemStack stack, final ITextComponent title, final ITextComponent subtitle) {
            this.stack = stack;
            this.title = title.getFormattedText();
            this.subtitle = subtitle.getFormattedText();
            this.visibility = Visibility.SHOW;
        }

        void hide() {
            this.visibility = IToast.Visibility.HIDE;
        }

        @Override
        public Visibility draw(final ToastGui toastGui, final long delta) {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            toastGui.blit(0, 0, 0, 96, 160, 32);
            toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, this.stack.get(), 6 + 2, 6 + 2);
            if (this.subtitle.isEmpty()) {
                toastGui.getMinecraft().fontRenderer.drawString(this.title, 30.0F, 12.0F, 0xFF500050);
            } else {
                toastGui.getMinecraft().fontRenderer.drawString(this.title, 30.0F, 7.0F, 0xFF500050);
                toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 30.0F, 18.0F, 0xFF000000);
            }
            return this.visibility;
        }
    }
}
