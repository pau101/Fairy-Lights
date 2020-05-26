package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.*;
import me.paulf.fairylights.client.gui.*;
import me.paulf.fairylights.util.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.widget.button.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.*;

import static net.minecraft.util.text.TextFormatting.*;

public class PaletteButton extends Button {
    private static final int TEX_U = 0;

    private static final int TEX_V = 40;

    private static final int SELECT_U = 28;

    private static final int SELECT_V = 40;

    private static final int COLOR_U = 34;

    private static final int COLOR_V = 40;

    private static final int COLOR_WIDTH = 6;

    private static final int COLOR_HEIGHT = 6;

    private static final TextFormatting[] IDX_COLOR = {WHITE, GRAY, DARK_GRAY, BLACK, RED, DARK_RED, YELLOW, GOLD, LIGHT_PURPLE, DARK_PURPLE, GREEN, DARK_GREEN, BLUE, DARK_BLUE, AQUA, DARK_AQUA};

    private static final int[] COLOR_IDX = Mth.invertMap(IDX_COLOR, TextFormatting::ordinal);

    private final ColorButton colorBtn;

    public PaletteButton(final int x, final int y, final ColorButton colorBtn, final String msg, final Button.IPressable pressable) {
        super(x, y, 28, 28, msg, pressable);
        this.colorBtn = colorBtn;
    }

    @Override
    public void onPress() {
        this.colorBtn.setDisplayColor(IDX_COLOR[(ArrayUtils.indexOf(IDX_COLOR, this.colorBtn.getDisplayColor()) + 1) % IDX_COLOR.length]);
        super.onPress();
    }

    @Override
    public void onClick(final double mouseX, final double mouseY) {
        final int idx = this.getMouseOverIndex(mouseX, mouseY);
        if (idx > -1) {
            this.colorBtn.setDisplayColor(IDX_COLOR[idx]);
            super.onPress();
        }
    }

    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(EditLetteredConnectionScreen.WIDGETS_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(this.x, this.y, TEX_U, TEX_V, this.width, this.height);
            if (this.colorBtn.hasDisplayColor()) {
                final int idx = COLOR_IDX[this.colorBtn.getDisplayColor().ordinal()];
                final int selectX = this.x + 2 + (idx % 4) * 6;
                final int selectY = this.y + 2 + (idx / 4) * 6;
                this.blit(selectX, selectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
            }
            for (int i = 0; i < IDX_COLOR.length; i++) {
                final TextFormatting color = IDX_COLOR[i];
                final int rgb = StyledString.getColor(color);
                final float r = (rgb >> 16 & 0xFF) / 255F;
                final float g = (rgb >> 8 & 0xFF) / 255F;
                final float b = (rgb & 0xFF) / 255F;
                RenderSystem.color4f(r, g, b, 1.0F);
                this.blit(this.x + 2 + (i % 4) * 6, this.y + 2 + i / 4 * 6, COLOR_U, COLOR_V, COLOR_WIDTH, COLOR_HEIGHT);
            }
            final int selectIndex = this.getMouseOverIndex(mouseX, mouseY);
            if (selectIndex > -1) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.color4f(1, 1, 1, 0.5F);
                final int hoverSelectX = this.x + 2 + selectIndex % 4 * 6;
                final int hoverSelectY = this.y + 2 + selectIndex / 4 * 6;
                this.blit(hoverSelectX, hoverSelectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
                RenderSystem.disableBlend();
            }
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private int getMouseOverIndex(final double mouseX, final double mouseY) {
        final int relX = MathHelper.floor(mouseX - this.x - 3);
        final int relY = MathHelper.floor(mouseY - this.y - 3);
        if (relX < 0 || relY < 0 || relX > 22 || relY > 22) {
            return -1;
        }
        final int bucketX = relX % 6;
        final int bucketY = relY % 6;
        if (bucketX > 3 || bucketY > 3) {
            return -1;
        }
        final int x = relX / 6;
        final int y = relY / 6;
        return x + y * 4;
    }
}
