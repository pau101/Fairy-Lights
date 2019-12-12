package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.gui.GuiEditLetteredConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class GuiButtonToggle extends Button {
	private int u;

	private int v;

	private boolean value;

	private boolean pressed;

	public GuiButtonToggle(int x, int y, int u, int v, String msg, Button.IPressable pressable) {
		super(x, y, 20, 20, msg, pressable);
		this.u = u;
		this.v = v;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void onPress() {
		value = !value;
		pressed = true;
		super.onPress();
	}

	@Override
	public void onRelease(final double mouseX, final double mouseY) {
		pressed = false;
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (visible) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color3f(1, 1, 1);
			int t;
			if (isHovered) {
				if (pressed) {
					t = 2;
				} else {
					t = 1;
				}
			} else {
				if (value) {
					t = 2;
				} else {
					t = 0;
				}
			}
			blit(x, y, u, v + height * t, width, height);
		}
	}
}
