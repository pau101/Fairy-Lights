package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ColorButton extends Button {
    private static final int TEX_U = 0;

    private static final int TEX_V = 0;

    private ChatFormatting displayColor;

    private float displayColorR;

    private float displayColorG;

    private float displayColorB;

    public ColorButton(final int x, final int y, final Component msg, final Button.OnPress onPress) {
        super(x, y, 20, 20, msg, onPress, DEFAULT_NARRATION);
    }

    public void setDisplayColor(final ChatFormatting color) {
        this.displayColor = color;
        final int rgb = StyledString.getColor(color);
        this.displayColorR = (rgb >> 16 & 0xFF) / 255F;
        this.displayColorG = (rgb >> 8 & 0xFF) / 255F;
        this.displayColorB = (rgb & 0xFF) / 255F;
    }

    public ChatFormatting getDisplayColor() {
        return this.displayColor;
    }

    public void removeDisplayColor() {
        this.displayColor = null;
    }

    public boolean hasDisplayColor() {
        return this.displayColor != null;
    }

    @Override
    public void renderWidget(final GuiGraphics stack, final int mouseX, final int mouseY, final float delta) {
        if (this.visible) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            stack.blit(EditLetteredConnectionScreen.WIDGETS_TEXTURE, this.getX(), this.getY(), TEX_U, this.isHovered ? TEX_V + this.height : TEX_V, this.width, this.height);
            if (this.displayColor != null) {
                stack.blit(EditLetteredConnectionScreen.WIDGETS_TEXTURE, this.getX(), this.getY(), TEX_U + this.width, TEX_V, this.width, this.height);
                RenderSystem.setShaderColor(this.displayColorR, this.displayColorG, this.displayColorB, 1.0F);
                stack.blit(EditLetteredConnectionScreen.WIDGETS_TEXTURE, this.getX(), this.getY(), TEX_U + this.width, TEX_V + this.height, this.width, this.height);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
