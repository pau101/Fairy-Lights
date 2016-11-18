package com.pau101.fairylights.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Mouse;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.sun.jna.platform.win32.WinDef.HPALETTE;

public class GuiButtonToggle extends GuiButton {
	private int u;

	private int v;

	private boolean value;

	public GuiButtonToggle(int id, int x, int y, int u, int v) {
		super(id, x, y, 20, 20, "");
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
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			value = !value;
			return true;
		}
		return false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			mc.getTextureManager().bindTexture(GuiEditLetteredConnection.WIDGETS_TEXTURE);
			GlStateManager.color(1, 1, 1);
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int t;
			if (hovered) {
				if (Mouse.isButtonDown(0)) {
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
			drawTexturedModalRect(xPosition, yPosition, u, v + height * t, width, height);
		}
	}
}
