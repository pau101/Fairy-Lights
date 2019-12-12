package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.gui.GuiEditLetteredConnection;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;

public final class GuiButtonColor extends Button {
	private static final int TEX_U = 0;

	private static final int TEX_V = 0;

	private TextFormatting displayColor;

	private float displayColorR;

	private float displayColorG;

	private float displayColorB;

	public GuiButtonColor(int x, int y, String msg, Button.IPressable onPress) {
		super(x, y, 20, 20, msg, onPress);
	}

	public void setDisplayColor(TextFormatting color) {
		displayColor = color;
		int rgb = StyledString.getColor(color);
		displayColorR = (rgb >> 16 & 0xFF) / 255F;
		displayColorG = (rgb >> 8 & 0xFF) / 255F;
		displayColorB = (rgb & 0xFF) / 255F;
	}

	public TextFormatting getDisplayColor() {
		return displayColor;
	}

	public void removeDisplayColor() {
		displayColor = null;
	}

	public boolean hasDisplayColor() {
		return displayColor != null;
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (visible) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color3f(1, 1, 1);
			blit(x, y, TEX_U, isHovered ? TEX_V + height : TEX_V, width, height);
			if (displayColor != null) {
				blit(x, y, TEX_U + width, TEX_V, width, height);
				GlStateManager.color3f(displayColorR, displayColorG, displayColorB);
				blit(x, y, TEX_U + width, TEX_V + height, width, height);
				GlStateManager.color3f(1, 1, 1);
			}
		}
	}
}
