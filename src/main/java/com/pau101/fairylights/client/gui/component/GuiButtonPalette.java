package com.pau101.fairylights.client.gui.component;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BLACK;
import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_BLUE;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.LIGHT_PURPLE;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.WHITE;
import static net.minecraft.util.text.TextFormatting.YELLOW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.text.TextFormatting;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.styledstring.StyledString;

public class GuiButtonPalette extends GuiButton {
	private static final int TEX_U = 0;

	private static final int TEX_V = 40;

	private static final int SELECT_U = 28;

	private static final int SELECT_V = 40;

	private static final int COLOR_U = 34;

	private static final int COLOR_V = 40;

	private static final int COLOR_WIDTH = 6;

	private static final int COLOR_HEIGHT = 6;

	private static final TextFormatting[] IDX_COLOR = { WHITE, GRAY, DARK_GRAY, BLACK, RED, DARK_RED, YELLOW, GOLD, LIGHT_PURPLE, DARK_PURPLE, GREEN, DARK_GREEN, BLUE, DARK_BLUE, AQUA, DARK_AQUA };

	private static final int[] COLOR_IDX = Mth.invertMap(IDX_COLOR, TextFormatting::ordinal);

	private final FontRenderer font;

	private final GuiButtonColor colorBtn;

	public GuiButtonPalette(int id, int x, int y, FontRenderer font, GuiButtonColor colorBtn) {
		super(id, x, y, 28, 28, "");
		this.font = font;
		this.colorBtn = colorBtn;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			int idx = getMouseOverIndex(mouseX, mouseY);
			if (idx > -1) {
				colorBtn.setDisplayColor(font, IDX_COLOR[idx]);
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			mc.getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color(1, 1, 1);
			drawTexturedModalRect(xPosition, yPosition, TEX_U, TEX_V, width, height);
			if (colorBtn.hasDisplayColor()) {
				int idx = COLOR_IDX[colorBtn.getDisplayColor().ordinal()];
				int selectX = xPosition + 2 + (idx % 4) * 6;
				int selectY = yPosition + 2 + (idx / 4) * 6;
				drawTexturedModalRect(selectX, selectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
			}
			for (int i = 0; i < IDX_COLOR.length; i++) {
				TextFormatting color = IDX_COLOR[i];
				int rgb = StyledString.getColor(font, color);
				float r = (rgb >> 16 & 0xFF) / 255F;
				float g = (rgb >> 8 & 0xFF) / 255F;
				float b = (rgb & 0xFF) / 255F;
				GlStateManager.color(r, g, b);
				drawTexturedModalRect(xPosition + 2 + (i % 4) * 6, yPosition + 2 + i / 4 * 6, COLOR_U, COLOR_V, COLOR_WIDTH, COLOR_HEIGHT);
			}
			int selectIndex = getMouseOverIndex(mouseX, mouseY);
			if (selectIndex > -1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1, 1, 1, 0.5F);
				int hoverSelectX = xPosition + 2 + selectIndex % 4 * 6;
				int hoverSelectY = yPosition + 2 + selectIndex / 4 * 6;
				drawTexturedModalRect(hoverSelectX, hoverSelectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
				GlStateManager.disableBlend();
			}
			GlStateManager.color(1, 1, 1);
		}
	}

	private int getMouseOverIndex(int mouseX, int mouseY) {
		int relX = mouseX - xPosition - 3, relY = mouseY - yPosition - 3;
		if (relX < 0 || relY < 0 || relX > 22 || relY > 22) {
			return -1;
		}
		int bucketX = relX % 6, bucketY = relY % 6;
		if (bucketX > 3 || bucketY > 3) {
			return -1;
		}
		int x = relX / 6, y = relY / 6;
		return x + y * 4;
	}
}
