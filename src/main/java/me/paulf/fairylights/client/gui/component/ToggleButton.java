package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ToggleButton extends Button {
    private final int u;

    private final int v;

    private boolean value;

    private boolean pressed;

    public ToggleButton(final int x, final int y, final int u, final int v, final Component msg, final Button.OnPress pressable) {
        super(x, y, 20, 20, msg, pressable, DEFAULT_NARRATION);
        this.u = u;
        this.v = v;
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public void onPress() {
        this.value = !this.value;
        this.pressed = true;
        super.onPress();
    }

    @Override
    public void onRelease(final double mouseX, final double mouseY) {
        this.pressed = false;
    }

    @Override
    public void renderWidget(final GuiGraphics stack, final int mouseX, final int mouseY, final float delta) {
        if (this.visible) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            final int t;
            if (this.isHovered) {
                if (this.pressed) {
                    t = 2;
                } else {
                    t = 1;
                }
            } else {
                if (this.value) {
                    t = 2;
                } else {
                    t = 0;
                }
            }
            stack.blit(EditLetteredConnectionScreen.WIDGETS_TEXTURE, this.getX(), this.getY(), this.u, this.v + this.height * t, this.width, this.height);
        }
    }
}
