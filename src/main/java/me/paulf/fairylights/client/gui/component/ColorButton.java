package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public final class ColorButton extends Button {
    private static final int TEX_U = 0;

    private static final int TEX_V = 0;

    private TextFormatting displayColor;

    private float displayColorR;

    private float displayColorG;

    private float displayColorB;

    public ColorButton(final int x, final int y, final ITextComponent msg, final Button.IPressable onPress) {
        super(x, y, 20, 20, msg, onPress);
    }

    public void setDisplayColor(final TextFormatting color) {
        this.displayColor = color;
        final int rgb = StyledString.getColor(color);
        this.displayColorR = (rgb >> 16 & 0xFF) / 255F;
        this.displayColorG = (rgb >> 8 & 0xFF) / 255F;
        this.displayColorB = (rgb & 0xFF) / 255F;
    }

    public TextFormatting getDisplayColor() {
        return this.displayColor;
    }

    public void removeDisplayColor() {
        this.displayColor = null;
    }

    public boolean hasDisplayColor() {
        return this.displayColor != null;
    }

    @Override
    public void func_230431_b_(final MatrixStack stack, final int mouseX, final int mouseY, final float delta) {
        if (this.field_230694_p_) {
            Minecraft.func_71410_x().func_110434_K().func_110577_a(EditLetteredConnectionScreen.WIDGETS_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.func_238474_b_(stack, this.field_230690_l_, this.field_230691_m_, TEX_U, this.field_230692_n_ ? TEX_V + this.field_230689_k_ : TEX_V, this.field_230688_j_, this.field_230689_k_);
            if (this.displayColor != null) {
                this.func_238474_b_(stack, this.field_230690_l_, this.field_230691_m_, TEX_U + this.field_230688_j_, TEX_V, this.field_230688_j_, this.field_230689_k_);
                RenderSystem.color4f(this.displayColorR, this.displayColorG, this.displayColorB, 1.0F);
                this.func_238474_b_(stack, this.field_230690_l_, this.field_230691_m_, TEX_U + this.field_230688_j_, TEX_V + this.field_230689_k_, this.field_230688_j_, this.field_230689_k_);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
