package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.gui.GuiEditLetteredConnection;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ArrayUtils;

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

public class GuiButtonPalette extends Button {
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

	private final GuiButtonColor colorBtn;

	public GuiButtonPalette(int x, int y, GuiButtonColor colorBtn, String msg, Button.IPressable pressable) {
		super(x, y, 28, 28, msg, pressable);
		this.colorBtn = colorBtn;
	}

	@Override
	public void onPress() {
		colorBtn.setDisplayColor(IDX_COLOR[(ArrayUtils.indexOf(IDX_COLOR, colorBtn.getDisplayColor()) + 1) % IDX_COLOR.length]);
		super.onPress();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		int idx = getMouseOverIndex(mouseX, mouseY);
		if (idx > -1) {
			colorBtn.setDisplayColor(IDX_COLOR[idx]);
			super.onPress();
		}
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (visible) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color3f(1, 1, 1);
			blit(x, y, TEX_U, TEX_V, width, height);
			if (colorBtn.hasDisplayColor()) {
				int idx = COLOR_IDX[colorBtn.getDisplayColor().ordinal()];
				int selectX = x + 2 + (idx % 4) * 6;
				int selectY = y + 2 + (idx / 4) * 6;
				blit(selectX, selectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
			}
			for (int i = 0; i < IDX_COLOR.length; i++) {
				TextFormatting color = IDX_COLOR[i];
				int rgb = StyledString.getColor(color);
				float r = (rgb >> 16 & 0xFF) / 255F;
				float g = (rgb >> 8 & 0xFF) / 255F;
				float b = (rgb & 0xFF) / 255F;
				GlStateManager.color3f(r, g, b);
				blit(x + 2 + (i % 4) * 6, y + 2 + i / 4 * 6, COLOR_U, COLOR_V, COLOR_WIDTH, COLOR_HEIGHT);
			}
			int selectIndex = getMouseOverIndex(mouseX, mouseY);
			if (selectIndex > -1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color4f(1, 1, 1, 0.5F);
				int hoverSelectX = x + 2 + selectIndex % 4 * 6;
				int hoverSelectY = y + 2 + selectIndex / 4 * 6;
				blit(hoverSelectX, hoverSelectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
				GlStateManager.disableBlend();
			}
			GlStateManager.color3f(1, 1, 1);
		}
	}

	private int getMouseOverIndex(double mouseX, double mouseY) {
		int relX = MathHelper.floor(mouseX - x - 3), relY = MathHelper.floor(mouseY - y - 3);
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
