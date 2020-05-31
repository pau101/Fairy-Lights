package me.paulf.fairylights.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClippyController {
    private State state = new InitialState();

    public void register(final IEventBus bus) {
        bus.addListener(this::tick);
    }

    private void tick(final TickEvent.ClientTickEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && !mc.isGamePaused() && mc.player != null) {
            this.state.tick(mc.player);
        }
    }

    private void setState(final State state) {
        this.state.stop();
        this.state = state;
    }

    interface State {
        void tick(final ClientPlayerEntity player);

        void stop();
    }

    class InitialState implements State {
        @Override
        public void tick(final ClientPlayerEntity player) {
            if (player.inventory.hasTag(FLCraftingRecipes.LIGHTS)) {
                final Balloon balloon = new Balloon(new StringTextComponent("Craft Hanging Lights"), new StringTextComponent("lorem ipsum"));
                Minecraft.getInstance().getToastGui().add(balloon);
                ClippyController.this.setState(new WaitingHangingLightsState(balloon));
            }
        }

        @Override
        public void stop() {
        }
    }

    class WaitingHangingLightsState implements State {
        final Balloon balloon;

        WaitingHangingLightsState(final Balloon balloon) {
            this.balloon = balloon;
        }

        @Override
        public void tick(final ClientPlayerEntity player) {
            if (player.inventory.getItemStack().getItem() == FLItems.HANGING_LIGHTS.get() ||
                player.inventory.hasItemStack(FLItems.HANGING_LIGHTS.get().getDefaultInstance()) ||
                player.getStats().getValue(Stats.ITEM_CRAFTED.get(FLItems.HANGING_LIGHTS.get())) > 0) {
                this.balloon.hide();
                ClippyController.this.setState(new CompleteState());
            }
        }

        @Override
        public void stop() {
            this.balloon.hide();
        }
    }

    static class CompleteState implements State {
        @Override
        public void tick(final ClientPlayerEntity player) {
        }

        @Override
        public void stop() {
        }
    }

    static class Balloon implements IToast {
        final String title;
        final String subtitle;
        IToast.Visibility visibility = IToast.Visibility.SHOW;

        Balloon(final ITextComponent title, final ITextComponent subtitle) {
            this.title = title.getFormattedText();
            this.subtitle = subtitle.getFormattedText();
        }

        void hide() {
            this.visibility = IToast.Visibility.HIDE;
        }

        @Override
        public Visibility draw(final ToastGui toastGui, final long delta) {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            toastGui.blit(0, 0, 0, 96, 160, 32);
            toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, FLItems.HANGING_LIGHTS.get().getDefaultInstance(), 6 + 2, 6 + 2);
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
