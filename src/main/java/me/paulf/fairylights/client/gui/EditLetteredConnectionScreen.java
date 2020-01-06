package me.paulf.fairylights.client.gui;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.gui.component.ColorButton;
import me.paulf.fairylights.client.gui.component.PaletteButton;
import me.paulf.fairylights.client.gui.component.StyledTextFieldWidget;
import me.paulf.fairylights.client.gui.component.ToggleButton;
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

    public EditLetteredConnectionScreen(final C connection) {
        super(NarratorChatListener.EMPTY);
        this.connection = connection;
    }

    @Override
    public void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        final int pad = 4;
        final int buttonWidth = 150;
        this.doneBtn = this.addButton(new Button(this.width / 2 - pad - buttonWidth, this.height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.done"), b -> {
            FairyLights.network.sendToServer(new EditLetteredConnectionMessage<>(this.connection, this.textField.getValue()));
            this.onClose();
        }));
        this.cancelBtn = this.addButton(new Button(this.width / 2 + pad, this.height / 4 + 120 + 12, buttonWidth, 20, I18n.format("gui.cancel"), b -> this.onClose()));
        final int textFieldX = this.width / 2 - 150;
        final int textFieldY = this.height / 2 - 10;
        int buttonX = textFieldX;
        final int buttonY = textFieldY - 25;
        final int bInc = 24;
        this.colorBtn = this.addButton(new ColorButton(buttonX, buttonY, "", b -> this.paletteBtn.visible = !this.paletteBtn.visible));
        this.paletteBtn = this.addButton(new PaletteButton(buttonX - 4, buttonY - 30, this.colorBtn, "fairylights.color", b -> this.textField.updateStyling(this.colorBtn.getDisplayColor(), true)));
        this.boldBtn = this.addButton(new ToggleButton(buttonX += bInc, buttonY, 40, 0, "", b -> this.updateStyleButton(TextFormatting.BOLD, this.boldBtn)));
        this.italicBtn = this.addButton(new ToggleButton(buttonX += bInc, buttonY, 60, 0, "", b -> this.updateStyleButton(TextFormatting.ITALIC, this.italicBtn)));
        this.underlineBtn = this.addButton(new ToggleButton(buttonX += bInc, buttonY, 80, 0, "", b -> this.updateStyleButton(TextFormatting.UNDERLINE, this.underlineBtn)));
        this.strikethroughBtn = this.addButton(new ToggleButton(buttonX += bInc, buttonY, 100, 0, "", b -> this.updateStyleButton(TextFormatting.STRIKETHROUGH, this.strikethroughBtn)));
        this.textField = new StyledTextFieldWidget(this.font, this.colorBtn, this.boldBtn, this.italicBtn, this.underlineBtn, this.strikethroughBtn, textFieldX, textFieldY, 300, 20, "fairylights.letteredText");
        this.textField.setValue(this.connection.getText());
        this.textField.setCaretStart();
        this.textField.setIsBlurable(false);
        this.textField.registerChangeListener(this::validateText);
        this.textField.setCharInputTransformer(this.connection.getCharInputTransformer());
        this.textField.setFocused(true);
        this.children.add(this.textField);
        this.paletteBtn.visible = false;
        final StylingPresence ss = this.connection.getSupportedStyling();
        this.colorBtn.visible = ss.hasColor();
        this.boldBtn.visible = ss.hasBold();
        this.italicBtn.visible = ss.hasItalic();
        this.underlineBtn.visible = ss.hasUnderline();
        this.strikethroughBtn.visible = ss.hasStrikethrough();
        this.setFocused(this.textField);
    }

    private void validateText(final StyledString text) {
        this.doneBtn.active = this.connection.isSuppportedText(text) && !this.connection.getText().equals(text);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void tick() {
        final Minecraft mc = Minecraft.getInstance();
        final int x = (int) (mc.mouseHelper.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth());
        final int y = (int) (mc.mouseHelper.getMouseY() * mc.mainWindow.getScaledHeight() / mc.mainWindow.getHeight());
        this.textField.update(x, y);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        this.paletteBtn.visible = false;
        if (isControlOp(keyCode, GLFW.GLFW_KEY_B)) {
            this.toggleStyleButton(TextFormatting.BOLD, this.boldBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_I)) {
            this.toggleStyleButton(TextFormatting.ITALIC, this.italicBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_U)) {
            this.toggleStyleButton(TextFormatting.UNDERLINE, this.underlineBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_S)) {
            this.toggleStyleButton(TextFormatting.STRIKETHROUGH, this.strikethroughBtn);
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.doneBtn.active) {
            this.doneBtn.onPress();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.cancelBtn.onPress();
            return true;
        }
        return false;
    }

    private void toggleStyleButton(final TextFormatting styling, final ToggleButton btn) {
        btn.setValue(!btn.getValue());
        this.updateStyleButton(styling, btn);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        this.paletteBtn.visible = false;
        return false;
    }

    private void updateStyleButton(final TextFormatting styling, final ToggleButton btn) {
        if (btn.visible) {
            this.textField.updateStyling(styling, btn.getValue());
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.format("gui.editLetteredConnection.name"), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
        this.textField.render(mouseX, mouseY, delta);
    }

    public static boolean isControlOp(final int key, final int controlKey) {
        return key == controlKey && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }
}
