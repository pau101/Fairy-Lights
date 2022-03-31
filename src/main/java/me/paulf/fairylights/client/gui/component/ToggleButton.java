package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class ToggleButton extends Button {
    private final int u;

    private final int v;

    private boolean value;

    private boolean pressed;

    public ToggleButton(final int x, final int y, final int u, final int v, final ITextComponent msg, final Button.IPressable pressable) {
        super(x, y, 20, 20, msg, pressable);
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
    public void func_230930_b_() {
        this.value = !this.value;
        this.pressed = true;
        super.func_230930_b_();
    }

    @Override
    public void func_231000_a__(final double mouseX, final double mouseY) {
        this.pressed = false;
    }

    @Override
    public void func_230431_b_(final MatrixStack stack, final int mouseX, final int mouseY, final float delta) {
        if (this.field_230694_p_) {
            Minecraft.func_71410_x().func_110434_K().func_110577_a(EditLetteredConnectionScreen.WIDGETS_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            final int t;
            if (this.field_230692_n_) {
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
            this.func_238474_b_(stack, this.field_230690_l_, this.field_230691_m_, this.u, this.v + this.field_230689_k_ * t, this.field_230688_j_, this.field_230689_k_);
        }
    }
}
