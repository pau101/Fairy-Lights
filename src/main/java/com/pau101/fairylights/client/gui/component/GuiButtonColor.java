package com.pau101.fairylights.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.util.styledstring.StyledString;

public final class GuiButtonColor extends GuiButton {
	private static final int TEX_U = 0;

	private static final int TEX_V = 0;

	private TextFormatting displayColor;

	private float displayColorR;

	private float displayColorG;

	private float displayColorB;

	public GuiButtonColor(int id, int x, int y) {
		super(id, x, y, 20, 20, "");
	}

	public void setDisplayColor(FontRenderer font, TextFormatting color) {
		displayColor = color;
		int rgb = StyledString.getColor(font, color);
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
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			mc.getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color(1, 1, 1);
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			drawTexturedModalRect(xPosition, yPosition, TEX_U, hovered ? TEX_V + height : TEX_V, width, height);
			if (displayColor != null) {
				drawTexturedModalRect(xPosition, yPosition, TEX_U + width, TEX_V, width, height);
				GlStateManager.color(displayColorR, displayColorG, displayColorB);
				drawTexturedModalRect(xPosition, yPosition, TEX_U + width, TEX_V + height, width, height);
				GlStateManager.color(1, 1, 1);
			}
		}
	}
}
