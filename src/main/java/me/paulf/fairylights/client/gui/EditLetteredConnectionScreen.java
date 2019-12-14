package me.paulf.fairylights.client.gui;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.gui.component.ColorButton;
import me.paulf.fairylights.client.gui.component.PaletteButton;
import me.paulf.fairylights.client.gui.component.ToggleButton;
import me.paulf.fairylights.client.gui.component.StyledTextFieldWidget;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StylingPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

public final class EditLetteredConnectionScreen<C extends Connection & Lettered> extends Screen {
	public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(FairyLights.ID, "textures/gui/widgets.png");

	private final C connection;

	private StyledTextFieldWidget textField;

	private Button doneBtn;

	private Button cancelBtn;

	private ColorButton colorBtn;

	private ToggleButton boldBtn;

	private ToggleButton italicBtn;

	private ToggleButton underlineBtn;

	private ToggleButton strikethroughBtn;

	private PaletteButton paletteBtn;

	public EditLetteredConnectionScreen(C connection) {
		super(NarratorChatListener.EMPTY);
		this.connection = connection;
	}

	@Override
	public void init() {
		minecraft.keyboardListener.enableRepeatEvents(true);
		final int pad = 4;
		final int buttonWidth = 150;
		doneBtn = addButton(new Button(width / 2 - pad - buttonWidth, height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.done"), b -> {
			FairyLights.network.sendToServer(new EditLetteredConnectionMessage<>(connection, textField.getValue()));
			onClose();
		}));
		cancelBtn = addButton(new Button(width / 2 + pad, height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.cancel"), b -> onClose()));
		int textFieldX = width / 2 - 150, textFieldY = height / 2 - 10;
		int buttonX = textFieldX, buttonY = textFieldY - 25, bInc = 24;
		colorBtn = addButton(new ColorButton(buttonX, buttonY, "", b -> paletteBtn.visible = !paletteBtn.visible));
		paletteBtn = addButton(new PaletteButton(buttonX - 4, buttonY - 30, colorBtn, "fairylights.color", b -> textField.updateStyling(colorBtn.getDisplayColor(), true)));
		boldBtn = addButton(new ToggleButton(buttonX += bInc, buttonY, 40, 0, "", b -> updateStyleButton(TextFormatting.BOLD, boldBtn)));
		italicBtn = addButton(new ToggleButton(buttonX += bInc, buttonY, 60, 0, "", b -> updateStyleButton(TextFormatting.ITALIC, italicBtn)));
		underlineBtn = addButton(new ToggleButton(buttonX += bInc, buttonY, 80, 0, "", b -> updateStyleButton(TextFormatting.UNDERLINE, underlineBtn)));
		strikethroughBtn = addButton(new ToggleButton(buttonX += bInc, buttonY, 100, 0, "", b -> updateStyleButton(TextFormatting.STRIKETHROUGH, strikethroughBtn)));
		textField = new StyledTextFieldWidget(font, colorBtn, boldBtn, italicBtn, underlineBtn, strikethroughBtn, textFieldX, textFieldY, 300, 20, "fairylights.letteredText");
		textField.setValue(connection.getText());
		textField.setCaretStart();
		textField.setIsBlurable(false);
		textField.registerChangeListener(this::validateText);
		textField.setCharInputTransformer(connection.getCharInputTransformer());
		textField.setFocused(true);
		children.add(textField);
		paletteBtn.visible = false;
		StylingPresence ss = connection.getSupportedStyling();
		colorBtn.visible = ss.hasColor();
		boldBtn.visible = ss.hasBold();
		italicBtn.visible = ss.hasItalic();
		underlineBtn.visible = ss.hasUnderline();
		strikethroughBtn.visible = ss.hasStrikethrough();
		setFocused(textField);
	}

	private void validateText(StyledString text) {
		doneBtn.active = connection.isSuppportedText(text) && !connection.getText().equals(text);
	}

	@Override
	public void removed() {
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void tick() {
		Minecraft mc = Minecraft.getInstance();
		int x = (int) (mc.mouseHelper.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth());
		int y = (int) (mc.mouseHelper.getMouseY() * mc.mainWindow.getScaledHeight() / mc.mainWindow.getHeight());
		textField.update(x, y);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		paletteBtn.visible = false;
		if (isControlOp(keyCode, GLFW.GLFW_KEY_B)) {
			toggleStyleButton(TextFormatting.BOLD, boldBtn);
			return true;
		} else if (isControlOp(keyCode, GLFW.GLFW_KEY_I)) {
			toggleStyleButton(TextFormatting.ITALIC, italicBtn);
			return true;
		} else if (isControlOp(keyCode, GLFW.GLFW_KEY_U)) {
			toggleStyleButton(TextFormatting.UNDERLINE, underlineBtn);
			return true;
		} else if (isControlOp(keyCode, GLFW.GLFW_KEY_S)) {
			toggleStyleButton(TextFormatting.STRIKETHROUGH, strikethroughBtn);
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && doneBtn.active) {
			doneBtn.onPress();
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			cancelBtn.onPress();
			return true;
		}
		return false;
	}

	private void toggleStyleButton(TextFormatting styling, ToggleButton btn) {
		btn.setValue(!btn.getValue());
		updateStyleButton(styling, btn);
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		paletteBtn.visible = false;
		return false;
	}

	private void updateStyleButton(TextFormatting styling, ToggleButton btn) {
		if (btn.visible) {
			textField.updateStyling(styling, btn.getValue());
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		drawCenteredString(font, I18n.format("gui.editLetteredConnection.name"), width / 2, 20, 0xFFFFFF);
		super.render(mouseX, mouseY, delta);
		textField.render(mouseX, mouseY, delta);
	}

	public static boolean isControlOp(int key, int controlKey) {
		return key == controlKey && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}
}
