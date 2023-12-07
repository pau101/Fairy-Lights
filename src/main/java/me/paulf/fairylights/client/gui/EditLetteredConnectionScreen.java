package me.paulf.fairylights.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.gui.component.ColorButton;
import me.paulf.fairylights.client.gui.component.PaletteButton;
import me.paulf.fairylights.client.gui.component.StyledTextFieldWidget;
import me.paulf.fairylights.client.gui.component.ToggleButton;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.Lettered;
import me.paulf.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StylingPresence;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
        super(Component.empty());
        this.connection = connection;
    }

    @Override
    public void init() {
        final int pad = 4;
        final int buttonWidth = 150;
        this.doneBtn = this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
            FairyLights.NETWORK.sendToServer(new EditLetteredConnectionMessage<>(this.connection, this.textField.getValue()));
            this.onClose();
        }).pos(this.width / 2 - pad - buttonWidth, this.height / 4 + 120 + 12).size(buttonWidth, 20).build());
        this.cancelBtn = this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> this.onClose()).pos(this.width / 2 + pad, this.height / 4 + 120 + 12).size(buttonWidth, 20).build());
        final int textFieldX = this.width / 2 - 150;
        final int textFieldY = this.height / 2 - 10;
        int buttonX = textFieldX;
        final int buttonY = textFieldY - 25;
        final int bInc = 24;
        this.colorBtn = this.addRenderableWidget(new ColorButton(buttonX, buttonY, Component.empty(), b -> this.paletteBtn.visible = !this.paletteBtn.visible));
        this.paletteBtn = this.addRenderableWidget(new PaletteButton(buttonX - 4, buttonY - 30, this.colorBtn, Component.translatable("fairylights.color"), b -> this.textField.updateStyling(this.colorBtn.getDisplayColor(), true)));
        this.boldBtn = this.addRenderableWidget(new ToggleButton(buttonX += bInc, buttonY, 40, 0, Component.empty(), b -> this.updateStyleButton(ChatFormatting.BOLD, this.boldBtn)));
        this.italicBtn = this.addRenderableWidget(new ToggleButton(buttonX += bInc, buttonY, 60, 0, Component.empty(), b -> this.updateStyleButton(ChatFormatting.ITALIC, this.italicBtn)));
        this.underlineBtn = this.addRenderableWidget(new ToggleButton(buttonX += bInc, buttonY, 80, 0, Component.empty(), b -> this.updateStyleButton(ChatFormatting.UNDERLINE, this.underlineBtn)));
        this.strikethroughBtn = this.addRenderableWidget(new ToggleButton(buttonX += bInc, buttonY, 100, 0, Component.empty(), b -> this.updateStyleButton(ChatFormatting.STRIKETHROUGH, this.strikethroughBtn)));
        this.textField = new StyledTextFieldWidget(this.font, this.colorBtn, this.boldBtn, this.italicBtn, this.underlineBtn, this.strikethroughBtn, textFieldX, textFieldY, 300, 20, Component.translatable("fairylights.letteredText"));
        this.textField.setValue(this.connection.getText());
        this.textField.setCaretStart();
        this.textField.setIsBlurable(false);
        this.textField.registerChangeListener(this::validateText);
        this.textField.setCharInputTransformer(this.connection.getInputTransformer());
        this.textField.setFocused(true);
        this.addWidget(this.textField);
        this.paletteBtn.visible = false;
        final StylingPresence ss = this.connection.getSupportedStyling();
        this.colorBtn.visible = ss.hasColor();
        this.boldBtn.visible = ss.hasBold();
        this.italicBtn.visible = ss.hasItalic();
        this.underlineBtn.visible = ss.hasUnderline();
        this.strikethroughBtn.visible = ss.hasStrikethrough();
        this.setInitialFocus(this.textField);
    }

    private void validateText(final StyledString text) {
        this.doneBtn.active = this.connection.isSupportedText(text) && !this.connection.getText().equals(text);
    }

    @Override
    public void mouseMoved(final double x, final double y) {
        this.textField.mouseMoved(x, y);
    }

    @Override
    public void tick() {
        this.textField.tick();
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        this.paletteBtn.visible = false;
        if (isControlOp(keyCode, GLFW.GLFW_KEY_B)) {
            this.toggleStyleButton(ChatFormatting.BOLD, this.boldBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_I)) {
            this.toggleStyleButton(ChatFormatting.ITALIC, this.italicBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_U)) {
            this.toggleStyleButton(ChatFormatting.UNDERLINE, this.underlineBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_S)) {
            this.toggleStyleButton(ChatFormatting.STRIKETHROUGH, this.strikethroughBtn);
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

    private void toggleStyleButton(final ChatFormatting styling, final ToggleButton btn) {
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

    private void updateStyleButton(final ChatFormatting styling, final ToggleButton btn) {
        if (btn.visible) {
            this.textField.updateStyling(styling, btn.getValue());
        }
    }

    @Override
    public void render(final GuiGraphics stack, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(stack);
        stack.drawCenteredString(this.font, Component.translatable("fairylights.editLetteredConnection"), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, delta);
        this.textField.render(stack, mouseX, mouseY, delta);
        final String allowed = this.connection.getAllowedDescription();
        if (!allowed.isEmpty()) {
            stack.drawString(this.font,
                Component.translatable("fairylights.editLetteredConnection.allowed_characters", allowed)
                    .withStyle(ChatFormatting.GRAY),
                this.textField.getX(),
                this.textField.getY() + 24,
                0xFFFFFFFF
            );
        }
    }

    public static boolean isControlOp(final int key, final int controlKey) {
        return key == controlKey && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }
}
