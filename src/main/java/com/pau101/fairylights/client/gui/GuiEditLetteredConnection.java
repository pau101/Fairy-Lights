package com.pau101.fairylights.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.ClientProxy;
import com.pau101.fairylights.client.gui.component.GuiButtonColor;
import com.pau101.fairylights.client.gui.component.GuiButtonPalette;
import com.pau101.fairylights.client.gui.component.GuiButtonToggle;
import com.pau101.fairylights.client.gui.component.GuiStyledTextField;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.net.serverbound.MessageEditLetteredConnection;
import com.pau101.fairylights.util.styledstring.StyledString;
import com.pau101.fairylights.util.styledstring.StylingPresence;

public final class GuiEditLetteredConnection<C extends Connection & Lettered> extends GuiScreen {
	public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(FairyLights.ID, "textures/gui/widgets.png");

	private static final int DONE_ID = 0;

	private static final int CANCEL_ID = 1;

	private static final int COLOR_ID = 2;

	private static final int BOLD_ID = 3;

	private static final int ITALIC_ID = 4;

	private static final int UNDERLINE_ID = 5;

	private static final int STRIKETHROUGH_ID = 6;

	private static final int PALETTE_ID = 7;

	private final C connection;

	private GuiStyledTextField textField;

	private GuiButton doneBtn;

	private GuiButton cancelBtn;

	private GuiButtonColor colorBtn;

	private GuiButtonToggle boldBtn;

	private GuiButtonToggle italicBtn;

	private GuiButtonToggle underlineBtn;

	private GuiButtonToggle strikethroughBtn;

	private GuiButtonPalette paletteBtn;

	private GuiButton selectedButton;

	public GuiEditLetteredConnection(C connection) {
		this.connection = connection;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		final int pad = 4;
		final int buttonWidth = 150;
		buttonList.clear();
		doneBtn = addButton(new GuiButton(DONE_ID, width / 2 - pad - buttonWidth, height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.done")));
		cancelBtn = addButton(new GuiButton(CANCEL_ID, width / 2 + pad, height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.cancel")));
		int textFieldX = width / 2 - 150, textFieldY = height / 2 - 10;
		int buttonX = textFieldX, buttonY = textFieldY - 25, bInc = 24;
		colorBtn = addButton(new GuiButtonColor(COLOR_ID, buttonX, buttonY));
		paletteBtn = addButton(new GuiButtonPalette(PALETTE_ID, buttonX - 4, buttonY - 30, ClientProxy.recoloredFont, colorBtn));
		boldBtn = addButton(new GuiButtonToggle(BOLD_ID, buttonX += bInc, buttonY, 40, 0));
		italicBtn = addButton(new GuiButtonToggle(ITALIC_ID, buttonX += bInc, buttonY, 60, 0));
		underlineBtn = addButton(new GuiButtonToggle(UNDERLINE_ID, buttonX += bInc, buttonY, 80, 0));
		strikethroughBtn = addButton(new GuiButtonToggle(STRIKETHROUGH_ID, buttonX += bInc, buttonY, 100, 0));
		textField = new GuiStyledTextField(0, ClientProxy.recoloredFont, colorBtn,  boldBtn, italicBtn, underlineBtn, strikethroughBtn, textFieldX, textFieldY, 300, 20);
		textField.setValue(connection.getText());
		textField.setCaretStart();
		textField.setFocused(true);
		textField.setIsBlurable(false);
		textField.registerChangeListener(this::validateText);
		textField.setCharInputTransformer(connection.getCharInputTransformer());
		paletteBtn.visible = false;
		StylingPresence ss = connection.getSupportedStyling();
		colorBtn.visible = ss.hasColor();
		boldBtn.visible = ss.hasBold();
		italicBtn.visible = ss.hasItalic();
		underlineBtn.visible = ss.hasUnderline();
		strikethroughBtn.visible = ss.hasStrikethrough();
	}

	private void validateText(StyledString text) {
		doneBtn.enabled = connection.isSuppportedText(text) && !connection.getText().equals(text);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {
        int x = Mouse.getX() * width / mc.displayWidth;
        int y = height - Mouse.getY() * height / mc.displayHeight - 1;
		textField.update(x, y);
	}

	@Override
	protected void keyTyped(char chr, int keyCode) {
		paletteBtn.visible = false;
		if (isControlOp(keyCode, Keyboard.KEY_B)) {
			toggleStyleButton(TextFormatting.BOLD, boldBtn);
		} else if (isControlOp(keyCode, Keyboard.KEY_I)) {
			toggleStyleButton(TextFormatting.ITALIC, italicBtn);
		} else if (isControlOp(keyCode, Keyboard.KEY_U)) {
			toggleStyleButton(TextFormatting.UNDERLINE, underlineBtn);
		} else if (isControlOp(keyCode, Keyboard.KEY_S)) {
			toggleStyleButton(TextFormatting.STRIKETHROUGH, strikethroughBtn);
		}
		textField.keyTyped(chr, keyCode);
		if ((keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) && doneBtn.enabled) {
			actionPerformed(doneBtn);
		} else if (keyCode == Keyboard.KEY_ESCAPE) {
			actionPerformed(cancelBtn);
		}
	}

	private void toggleStyleButton(TextFormatting styling, GuiButtonToggle btn) {
		btn.setValue(!btn.getValue());
		updateStyleButton(styling, btn);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		boolean blurTextField = true;
		if (button == 0) {
			for (GuiButton btn : buttonList) {
				if (btn.mousePressed(mc, mouseX, mouseY)) {
					ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(this, btn, buttonList);
					if (MinecraftForge.EVENT_BUS.post(event)) {
						break;
					}
					btn = event.getButton();
					selectedButton = btn;
					btn.playPressSound(mc.getSoundHandler());
					actionPerformed(btn);
					MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
					blurTextField = false;
				}
			}
		}
		textField.mouseClicked(mouseX, mouseY, button, blurTextField);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (selectedButton != colorBtn) {
			paletteBtn.visible = false;
		}
		if (selectedButton != null && button == 0) {
			selectedButton.mouseReleased(mouseX, mouseY);
			selectedButton = null;
		}
		textField.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long lastClickTime) {
		textField.mouseClickMove(mouseX, mouseY, button);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case COLOR_ID:
				paletteBtn.visible = !paletteBtn.visible;
				break;
			case BOLD_ID:
				updateStyleButton(TextFormatting.BOLD, boldBtn);
				break;
			case ITALIC_ID:
				updateStyleButton(TextFormatting.ITALIC, italicBtn);
				break;
			case UNDERLINE_ID:
				updateStyleButton(TextFormatting.UNDERLINE, underlineBtn);
				break;
			case STRIKETHROUGH_ID:
				updateStyleButton(TextFormatting.STRIKETHROUGH, strikethroughBtn);
				break;
			case PALETTE_ID:
				textField.updateStyling(colorBtn.getDisplayColor(), true);
				break;
			case DONE_ID:
				FairyLights.network.sendToServer(new MessageEditLetteredConnection<>(connection, textField.getValue()));
			case CANCEL_ID:
				mc.displayGuiScreen(null);
			default:
		}
	}

	private void updateStyleButton(TextFormatting styling, GuiButtonToggle btn) {
		if (btn.visible) {
			textField.updateStyling(styling, btn.getValue());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float delta) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("gui.editLetteredConnection.name"), width / 2, 20, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, delta);
        float mx = Mouse.getX() * width / (float) mc.displayWidth;
        float my = height - Mouse.getY() * height / (float) mc.displayHeight - 1;
		textField.draw(mouseX, mouseY, mx, my);
	}

	public static boolean isControlOp(int key, int controlKey) {
		return key == controlKey && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
	}
}
