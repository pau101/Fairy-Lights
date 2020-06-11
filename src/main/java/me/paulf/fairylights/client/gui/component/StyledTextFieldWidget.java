package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StyledStringBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StyledTextFieldWidget extends Widget implements IRenderable, IGuiEventListener {
    private static final Predicate<String> ALWAYS_TRUE = str -> true;

    private static final Function<String, String> IDENTITY_CHARACTER_TRANSFORMER = c -> c;

    private final FontRenderer font;

    private final int multiClickInterval = getMultiClickInterval();

    private final ColorButton colorBtn;

    private final ToggleButton boldBtn;

    private final ToggleButton italicBtn;

    private final ToggleButton underlineBtn;

    private final ToggleButton strikethroughBtn;

    private StyledString value;

    private int maxLength = 32;

    private int tick;

    private boolean hasBackground = true;

    private boolean isBlurable = true;

    private boolean isFocused;

    private boolean isWritable = true;

    private boolean isVisible = true;

    private int lineScrollOffset;

    private int caret;

    private int selectionEnd;

    private int writableTextColor = 0xE0E0E0;

    private int readonlyTextColor = 0x707070;

    private boolean isDraggingSelection;

    private boolean hasDraggedSelecton;

    private boolean isPressed;

    private long lastClickTime;

    private int multiClicks;

    private Function<String, String> charInputTransformer = IDENTITY_CHARACTER_TRANSFORMER;

    private Predicate<String> validator = ALWAYS_TRUE;

    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private Style currentStyle;

    public StyledTextFieldWidget(
        final FontRenderer font,
        final ColorButton colorBtn,
        final ToggleButton boldBtn,
        final ToggleButton italicBtn,
        final ToggleButton underlineBtn,
        final ToggleButton strikethroughBtn,
        final int x, final int y, final int width, final int height,
        final String msg
    ) {
        super(x, y, width, height, msg);
        this.font = font;
        this.colorBtn = colorBtn;
        this.boldBtn = boldBtn;
        this.italicBtn = italicBtn;
        this.underlineBtn = underlineBtn;
        this.strikethroughBtn = strikethroughBtn;
        this.setValue0(new StyledString());
        this.setStyle(new Style());
    }

    public void setIsBlurable(final boolean isBlurable) {
        this.isBlurable = isBlurable;
    }

    public void setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void updateStyling(final TextFormatting styling, final boolean state) {
        int start = this.caret;
        int end = this.selectionEnd;
        if (start == end) {
            this.withStyling(styling, state);
        } else {
            if (end < start) {
                final int t = start;
                start = end;
                end = t;
            }
            this.setValue0(this.value.withStyling(start, end, styling, state));
            this.updateSelectionControls();
        }
    }

    public void setColor(final TextFormatting color) {
        this.setStyle(this.currentStyle.withColor(color));
    }

    public void withStyling(final TextFormatting styling, final boolean state) {
        this.setStyle(this.currentStyle.withStyling(styling, state));
    }

    public void setStyle(final Style style) {
        this.currentStyle = style;
        this.colorBtn.setDisplayColor(this.currentStyle.getColor());
        this.boldBtn.setValue(style.isBold());
        this.italicBtn.setValue(style.isItalic());
        this.underlineBtn.setValue(style.isUnderline());
        this.strikethroughBtn.setValue(style.isStrikethrough());
    }

    public TextFormatting getColor() {
        return this.currentStyle.getColor();
    }

    public void setValue(final String value) {
        this.setValue(new StyledString(value, this.currentStyle));
    }

    public void setValue(final StyledString value) {
        if (this.validator.test(value.toUnstyledString())) {
            if (value.length() > this.maxLength) {
                this.setValue0(value.substring(0, this.maxLength));
            } else {
                this.setValue0(value);
            }
        }
    }

    private void setValue0(final StyledString value) {
        this.value = value;
        for (final ChangeListener listener : this.changeListeners) {
            listener.onChange(value);
        }
    }

    public String getUnstyledValue() {
        return this.value.toUnstyledString();
    }

    public StyledString getValue() {
        return this.value;
    }

    public String getUnstyledSelectedText() {
        return this.getSelectedText().toUnstyledString();
    }

    public StyledString getSelectedText() {
        int start = this.caret, end = this.selectionEnd;
        if (end < start) {
            final int t = start;
            start = end;
            end = t;
        }
        return this.value.substring(start, end);
    }

    public void setCaretStart() {
        this.setCaret(0);
    }

    public void setCaretEnd() {
        this.setCaret(this.value.length());
    }

    public void setCaret(final int pos) {
        this.setCaret(pos, true);
    }

    public void setCaret(final int pos, final boolean changeColor) {
        this.caret = MathHelper.clamp(pos, 0, this.value.length());
        this.setSelectionPos(this.caret);
        if (changeColor) {
            this.setCurrentStyleByIndex(this.caret);
        }
        this.tick = 0;
    }

    private void setCurrentStyleByIndex(final int index) {
        if (this.value.length() > 0) {
            this.setStyle(this.value.styleAt(index <= 0 ? 0 : index - 1));
        }
    }

    public int getCaret() {
        return this.caret;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public Style getStyle() {
        return this.currentStyle;
    }

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    public void setHasBackground(final boolean hasBackground) {
        this.hasBackground = hasBackground;
    }

    public void setTextColor(final int writableTextColor) {
        this.writableTextColor = writableTextColor & 0xFFFFFF;
    }

    public void setReadonlyTextColor(final int readonlyTextColor) {
        this.readonlyTextColor = readonlyTextColor & 0xFFFFFF;
    }

    @Override
    public void setFocused(final boolean isFocused) {
        if (isFocused) {
            if (!this.isFocused) {
                this.tick = 0;
            }
        } else if (this.selectionEnd != this.caret) {
            this.setSelectionPos(this.caret);
        }
        this.isFocused = isFocused;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    public void setWritable(final boolean isWritable) {
        this.isWritable = isWritable;
    }

    public void setCharInputTransformer(final Function<String, String> charTransformer) {
        this.charInputTransformer = charTransformer;
    }

    public void setValidator(final Predicate<String> validator) {
        this.validator = validator;
    }

    public int getInnerWidth() {
        return this.hasBackground ? this.width - 8 : this.width;
    }

    public void setSelectionPos(final int pos) {
        final int len = this.value.length();
        this.selectionEnd = MathHelper.clamp(pos, 0, len);
        if (this.lineScrollOffset > len) {
            this.lineScrollOffset = len;
        }
        final int w = trimToWidth(this.value.substring(this.lineScrollOffset), this.font, this.getInnerWidth(), true).length();
        if (this.selectionEnd > w + this.lineScrollOffset) {
            this.lineScrollOffset = this.selectionEnd - w;
        } else if (this.selectionEnd <= this.lineScrollOffset) {
            this.lineScrollOffset = this.selectionEnd;
        }
        this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, len);
        if (this.caret != this.selectionEnd) {
            this.updateSelectionControls();
        }
    }

    private void updateSelectionControls() {
        final StyledString selected = this.getSelectedText();
        TextFormatting color = null;
        boolean consistantColor = true;
        boolean bold = true, italic = true, underline = true, strikethrough = true;
        for (int i = 0; i < selected.length(); i++) {
            final Style s = selected.styleAt(i);
            if (color != null && color != s.getColor()) {
                color = null;
                consistantColor = false;
            }
            if (consistantColor) {
                color = s.getColor();
            }
            if (!s.isBold()) {
                bold = false;
            }
            if (!s.isItalic()) {
                italic = false;
            }
            if (!s.isUnderline()) {
                underline = false;
            }
            if (!s.isStrikethrough()) {
                strikethrough = false;
            }
        }
        this.setStyle(new Style(color == null ? this.currentStyle.getColor() : color, bold, strikethrough, underline, italic, false));
        if (!consistantColor) {
            this.colorBtn.removeDisplayColor();
        }
    }

    public void registerChangeListener(final ChangeListener listener) {
        if (!this.changeListeners.contains(listener)) {
            this.changeListeners.add(listener);
        }
        listener.onChange(this.value);
    }

    public boolean removeChangeListener(final ChangeListener listener) {
        return this.changeListeners.remove(listener);
    }

    public void update(final int mouseX, final int mouseY) {
        if (this.isPressed && this.caret != this.selectionEnd) {
            final int lower;
            final int upper;
            if (this.hasBackground) {
                lower = this.x + 15;
                upper = this.x + this.width - 16;
            } else {
                lower = this.x + 11;
                upper = this.x + this.width - 12;
            }
            boolean scrolled = false;
            if (mouseX < lower) {
                if (this.lineScrollOffset > 0) {
                    final int rate = (2 - (mouseX - this.x) / 5) * 2 + 2;
                    this.lineScrollOffset -= rate;
                    if (this.lineScrollOffset < 0) {
                        this.lineScrollOffset = 0;
                    }
                    scrolled = true;
                }
            } else if (mouseX > upper) {
                final int max = this.value.length() - trimToWidth(this.value, this.font, this.getInnerWidth(), true).length();
                if (this.lineScrollOffset < max) {
                    final int rate = (2 + (mouseX - this.x - this.width + 1) / 5) * 2 + 2;
                    this.lineScrollOffset += rate;
                    if (this.lineScrollOffset > max) {
                        this.lineScrollOffset = max;
                    }
                    scrolled = true;
                }
            }
            if (scrolled && !this.hasDraggedSelecton) {
                int relativeX = mouseX - this.x;
                if (this.hasBackground) {
                    relativeX -= 2;
                }
                this.setSelectionPos(this.getIndexInTextByX(relativeX));
            }
        }
        this.tick++;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.isFocused) {
            return false;
        }
        if (Screen.isSelectAll(keyCode)) {
            this.setCaretEnd();
            this.setSelectionPos(0);
        } else if (Screen.isCopy(keyCode)) {
            this.setClipboardString(this.getSelectedText());
        } else if (Screen.isPaste(keyCode)) {
            if (this.isWritable) {
                final StyledString str = this.getClipboardString();
                if (Screen.hasShiftDown()) {
                    this.writeText(str.toUnstyledString());
                } else {
                    this.writeText(str);
                }
            }
        } else if (Screen.isCut(keyCode)) {
            this.setClipboardString(this.getSelectedText());
            if (this.isWritable) {
                this.writeText("");
            }
        } else if (EditLetteredConnectionScreen.isControlOp(keyCode, GLFW.GLFW_KEY_BACKSLASH)) {
            if (this.caret == this.selectionEnd) {
                this.resetCurrentFormatting();
            } else {
                this.resetSelectedFormatting();
            }
        } else {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE:
                    if (Screen.hasControlDown()) {
                        if (this.isWritable) {
                            this.deleteWords(-1);
                        }
                    } else if (this.isWritable) {
                        this.deleteFromCursor(-1);
                    }
                    break;
                case GLFW.GLFW_KEY_HOME:
                    if (Screen.hasShiftDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCaretStart();
                    }
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    if (Screen.hasShiftDown()) {
                        if (Screen.hasControlDown()) {
                            this.setSelectionPos(this.skipWords(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (Screen.hasControlDown()) {
                        this.setCaret(this.skipWords(-1));
                    } else {
                        if (this.getSelectedText().isEmpty()) {
                            this.moveCursorBy(-1);
                        } else {
                            this.setCaret(Math.min(this.caret, this.selectionEnd));
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    if (Screen.hasShiftDown()) {
                        if (Screen.hasControlDown()) {
                            this.setSelectionPos(this.skipWords(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (Screen.hasControlDown()) {
                        this.setCaret(this.skipWords(1));
                    } else {
                        if (this.getSelectedText().isEmpty()) {
                            this.moveCursorBy(1);
                        } else {
                            this.setCaret(Math.max(this.caret, this.selectionEnd));
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_END:
                    if (Screen.hasShiftDown()) {
                        this.setSelectionPos(this.value.length());
                    } else {
                        this.setCaretEnd();
                    }
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    if (Screen.hasControlDown()) {
                        if (this.isWritable) {
                            this.deleteWords(1);
                        }
                    } else if (this.isWritable) {
                        this.deleteFromCursor(1);
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean charTyped(final char typedChar, final int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        if (SharedConstants.isAllowedCharacter(typedChar)) {
            final String writeChar = this.charInputTransformer.apply(Character.toString(typedChar));
            if (this.isWritable) {
                this.writeText(writeChar);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final boolean hovered = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        if (this.isBlurable) {
            this.setFocused(hovered);
        } else if (!hovered) {
            this.setCaret(Math.min(this.caret, this.selectionEnd));
        }
        if (this.isFocused && hovered && button == 0) {
            int relativeX = MathHelper.floor(mouseX - this.x);
            if (this.hasBackground) {
                relativeX -= 2;
            }
            final int idx = this.getIndexInTextByX(relativeX);
            final long now = Util.milliTime();
            if (now - this.lastClickTime <= this.multiClickInterval) {
                this.multiClicks++;
                if (this.multiClicks > 3) {
                    this.multiClicks = 1;
                }
            } else {
                this.multiClicks = 1;
            }
            this.lastClickTime = now;
            if (Screen.hasShiftDown()) {
                final int end = this.selectionEnd;
                this.setCaret(idx);
                this.setSelectionPos(end);
            } else {
                this.clickIndex(idx);
            }
            this.isPressed = true;
            return true;
        }
        return false;
    }

    private void clickIndex(final int pos) {
        switch (this.multiClicks) {
            case 1: {
                int start = this.caret, end = this.selectionEnd;
                if (end < start) {
                    final int t = start;
                    start = end;
                    end = t;
                }
                this.isDraggingSelection = this.caret != this.selectionEnd && pos >= start && pos < end;
                if (!this.isDraggingSelection) {
                    this.setCaret(pos);
                    this.hasDraggedSelecton = false;
                }
                break;
            }
            case 2: {
                if (pos < this.value.length() && this.value.charAt(pos) == ' ') {
                    int low = pos - 1;
                    int high = pos;
                    final int max = this.value.length() - 1;
                    while (low >= 0) {
                        if (this.value.charAt(low) != ' ') {
                            low++;
                            break;
                        }
                        low--;
                    }
                    while (true) {
                        if (high < max) {
                            if (this.value.charAt(++high) != ' ') {
                                break;
                            }
                        } else {
                            high++;
                            break;
                        }
                    }
                    this.setCaret(high);
                    this.setSelectionPos(low);
                } else {
                    final int low = this.value.lastIndexOf(' ', pos);
                    final int high = this.value.indexOf(' ', pos);
                    this.setCaret(high == -1 ? this.value.length() : high);
                    this.setSelectionPos(low + 1);
                }
                break;
            }
            case 3:
                this.setCaretEnd();
                this.setSelectionPos(0);
                break;
            default:
        }
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            this.isPressed = false;
            if (this.isDraggingSelection) {
                final boolean hovered = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
                if (hovered) {
                    int relativeX = MathHelper.floor(mouseX - this.x);
                    if (this.hasBackground) {
                        relativeX -= 2;
                    }
                    int pos = this.getIndexInTextByX(relativeX);
                    // Is pos outside of selection
                    if ((pos - this.caret) * (pos - this.selectionEnd) > 0) {
                        final StyledString selection = this.getSelectedText();
                        this.writeText("");
                        // If pos after selection
                        if ((pos - this.caret) - (this.selectionEnd - pos) > 0) {
                            pos -= selection.length();
                        }
                        this.setCaret(pos);
                        this.writeText(selection);
                        this.setCaret(pos);
                        this.setSelectionPos(pos + selection.length());
                    } else {
                        this.setCaret(pos);
                    }
                }
                this.isDraggingSelection = false;
                this.hasDraggedSelecton = false;
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double dx, final double dy) {
        if (this.isFocused && this.isPressed && button == 0) {
            int relativeX = MathHelper.floor(mouseX - this.x);
            if (this.hasBackground) {
                relativeX -= 2;
            }
            if (this.isDraggingSelection) {
                this.hasDraggedSelecton = true;
            } else {
                this.setSelectionPos(this.getIndexInTextByX(relativeX));
            }
            return true;
        }
        return false;
    }

    public void writeText(final String input) {
        if (!this.colorBtn.hasDisplayColor()) {
            this.setCurrentStyleByIndex(Math.min(this.selectionEnd, this.caret));
        }
        this.writeText(new StyledString(input, this.currentStyle), false);
    }

    public void writeText(final StyledString input) {
        this.writeText(input, true);
    }

    public void writeText(final StyledString input, final boolean changeColor) {
        final StyledStringBuilder val = new StyledStringBuilder();
        final StyledString finput = filterAllowedCharacters(input);
        int startIdx = this.caret, endIdx = this.selectionEnd;
        if (endIdx < startIdx) {
            final int t = startIdx;
            startIdx = endIdx;
            endIdx = t;
        }
        final int available = this.maxLength - this.value.length() - (startIdx - endIdx);
        if (this.value.length() > 0) {
            val.append(this.value.substring(0, startIdx));
        }
        final int length;
        if (available < finput.length()) {
            val.append(finput.substring(0, available));
            length = available;
        } else {
            val.append(finput);
            length = finput.length();
        }
        if (this.value.length() > 0 && endIdx < this.value.length()) {
            val.append(this.value.substring(endIdx));
        }
        final StyledString v = val.toStyledString();
        if (this.validator.test(v.toUnstyledString())) {
            this.setValue0(v);
            this.moveCursorBy(startIdx - this.selectionEnd + length, changeColor);
        }
    }

    public static boolean isAllowedCharacter(final char character) {
        return character >= ' ' && character != '\u007F';
    }

    private static StyledString filterAllowedCharacters(final StyledString str) {
        final StyledStringBuilder bldr = new StyledStringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char chr = str.charAt(i);
            if (isAllowedCharacter(chr)) {
                bldr.append(chr, str.styleAt(i));
            }
        }
        return bldr.toStyledString();
    }

    public void deleteWords(final int num) {
        this.deleteFromCursor(this.skipWords(num) - this.caret);
    }

    public void deleteFromCursor(final int num) {
        if (this.value.length() > 0) {
            if (this.selectionEnd != this.caret) {
                this.writeText("");
            } else {
                final boolean reverse = num < 0;
                final int endIdx = reverse ? this.caret + num : this.caret;
                final int startIdx = reverse ? this.caret : this.caret + num;
                final StyledStringBuilder val = new StyledStringBuilder();
                Style style = null;
                if (endIdx > 0) {
                    if (endIdx < this.value.length()) {
                        style = this.value.styleAt(endIdx);
                    }
                    val.append(this.value.substring(0, endIdx));
                }
                if (startIdx < this.value.length()) {
                    if (startIdx > 0 && style == null) {
                        style = this.value.styleAt(startIdx - 1);
                    }
                    val.append(this.value.substring(startIdx));
                }
                final StyledString v = val.toStyledString();
                if (this.validator.test(v.toUnstyledString())) {
                    this.setValue0(v);
                    if (reverse) {
                        this.moveCursorBy(num, false);
                    }
                    if (style == null) {
                        style = this.currentStyle;
                    }
                    this.setStyle(style);
                }
            }
        }
    }

    public int skipWords(final int n) {
        return this.skipWords(n, n < 0 ? Math.min(this.selectionEnd, this.caret) : Math.max(this.selectionEnd, this.caret));
    }

    public int skipWords(final int n, final int pos) {
        int idx = pos;
        final boolean reverse = n < 0;
        for (int word = 0, len = this.value.length(), count = Math.abs(n); word < count; word++) {
            if (reverse) {
                while (idx > 0 && this.value.charAt(idx - 1) == ' ') {
                    idx--;
                }
                while (idx > 0 && this.value.charAt(idx - 1) != ' ') {
                    idx--;
                }
                if (idx == 0) {
                    break;
                }
            } else {
                while (idx < len && this.value.charAt(idx) == ' ') {
                    idx++;
                }
                while (idx < len && this.value.charAt(idx) != ' ') {
                    idx++;
                }
                if (idx == len) {
                    break;
                }
            }
        }
        return idx;
    }

    public void moveCursorBy(final int num) {
        this.setCaret(this.selectionEnd + num, true);
    }

    public void moveCursorBy(final int num, final boolean changeColor) {
        this.setCaret(this.selectionEnd + num, changeColor);
    }

    private int getIndexInTextByX(final int x) {
        final StyledString s = trimToWidth(this.value.substring(this.lineScrollOffset), this.font, this.getInnerWidth());
        return trimToWidth(s, this.font, x).length() + this.lineScrollOffset;
    }

    private void resetCurrentFormatting() {
        this.setStyle(new Style());
        this.tick = 0;
    }

    private void resetSelectedFormatting() {
        int start = this.caret, end = this.selectionEnd;
        if (end < start) {
            final int t = start;
            start = end;
            end = t;
        }
        this.setValue0(this.value.withStyling(start, end, new Style()));
    }

    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (!this.isVisible) {
            return;
        }
        if (this.hasBackground) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xAAA0A0A0);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF000000);
        }
        final int textColor = this.isWritable ? this.writableTextColor : this.readonlyTextColor;
        final int visibleCaret = this.caret - this.lineScrollOffset;
        int visibleSelectionEnd = this.selectionEnd - this.lineScrollOffset;
        final StyledString visibleText = trimToWidth(this.value.substring(this.lineScrollOffset), this.font, this.getInnerWidth());
        final boolean isCaretVisible = visibleCaret >= 0 && visibleCaret <= visibleText.length();
        final boolean drawSelection = visibleSelectionEnd != visibleCaret;
        final boolean drawCaret = !drawSelection && this.isFocused && this.tick / 6 % 2 == 0 && isCaretVisible;
        final int offsetX = this.hasBackground ? this.x + 4 : this.x;
        final int offsetY = this.hasBackground ? this.y + (this.height - 8) / 2 : this.y;
        int textX = offsetX;
        if (visibleSelectionEnd > visibleText.length()) {
            visibleSelectionEnd = visibleText.length();
        }
        if (visibleText.length() > 0) {
            final String beforeCaret = (isCaretVisible ? visibleText.substring(0, visibleCaret) : visibleText).toString();
            textX = this.font.drawStringWithShadow(beforeCaret, offsetX, offsetY, textColor);
        }
        final int caretX;
        if (isCaretVisible) {
            caretX = --textX;
        } else {
            caretX = visibleCaret > 0 ? offsetX + this.width - 6 : offsetX;
        }
        if (visibleText.length() > 0 && isCaretVisible && visibleCaret < visibleText.length()) {
            textX = this.font.drawStringWithShadow(visibleText.substring(visibleCaret).toString(), textX, offsetY, textColor);
        }
        if (drawCaret) {
            final int rgb = StyledString.getColor(this.currentStyle.getColor());
            if (this.currentStyle.isItalic()) {
                final float r = (rgb >> 16 & 0xFF) / 255F;
                final float g = (rgb >> 8 & 0xFF) / 255F;
                final float b = (rgb & 0xFF) / 255F;
                final Tessellator tes = Tessellator.getInstance();
                final BufferBuilder buf = tes.getBuffer();
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                buf.pos(caretX + 2, offsetY - 2, 0).color(r, g, b, 1.0F).endVertex();
                buf.pos(caretX + 1, offsetY - 2, 0).color(r, g, b, 1.0F).endVertex();
                buf.pos(caretX - 1, offsetY + 1 + this.font.FONT_HEIGHT, 0).color(r, g, b, 1.0F).endVertex();
                buf.pos(caretX, offsetY + 1 + this.font.FONT_HEIGHT, 0).color(r, g, b, 1.0F).endVertex();
                RenderSystem.disableTexture();
                tes.draw();
                RenderSystem.enableTexture();
            } else {
                fill(caretX, offsetY - 2, caretX + 1, offsetY + 1 + this.font.FONT_HEIGHT, 0xFF000000 | rgb);
            }
        }
        if (drawSelection) {
            final int selectionX = offsetX + (visibleSelectionEnd < 0 ? 0 : getWidth(visibleText.substring(0, visibleSelectionEnd), this.font));
            int start = caretX, end = selectionX;
            if (end < start) {
                final int t = start;
                start = end;
                end = t;
            }
            this.drawSelectionHighlight(start - 1, offsetY - 2, end, offsetY + 1 + this.font.FONT_HEIGHT);
        }
        if (this.hasDraggedSelecton) {
            if (this.isHovered) {
                int relativeX = mouseX - this.x;
                if (this.hasBackground) {
                    relativeX -= 2;
                }
                final int pos = this.getIndexInTextByX(relativeX) - this.lineScrollOffset;
                if (pos >= 0 && pos <= visibleText.length()) {
                    final int x = getWidth(visibleText.substring(0, pos), this.font);
                    final int rgb = StyledString.getColor(this.currentStyle.getColor());
                    fill(offsetX + x, offsetY - 2, offsetX + x + 1, offsetY + 1 + this.font.FONT_HEIGHT, 0xFF000000 | rgb);
                }
            }
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.font.drawStringWithShadow(this.getSelectedText().toString(), mouseX + 5, mouseY + 5, textColor | 0xBF000000);
            RenderSystem.disableBlend();
        }
    }

    private void drawSelectionHighlight(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            final int t = startX;
            startX = endX;
            endX = t;
        }
        if (startY < endY) {
            final int j = startY;
            startY = endY;
            endY = j;
        }
        if (endX > this.x + this.width) {
            endX = this.x + this.width;
        }
        if (startX > this.x + this.width) {
            startX = this.x + this.width;
        }
        final Tessellator tes = Tessellator.getInstance();
        final BufferBuilder buf = tes.getBuffer();
        RenderSystem.disableTexture();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buf.pos(startX, endY, 0).endVertex();
        buf.pos(endX, endY, 0).endVertex();
        buf.pos(endX, endY + 1, 0).endVertex();
        buf.pos(startX, endY + 1, 0).endVertex();
        buf.pos(startX, startY - 1, 0).endVertex();
        buf.pos(endX, startY - 1, 0).endVertex();
        buf.pos(endX, startY, 0).endVertex();
        buf.pos(startX, startY, 0).endVertex();
        buf.pos(startX, endY, 0).endVertex();
        buf.pos(startX - 1, endY, 0).endVertex();
        buf.pos(startX - 1, startY, 0).endVertex();
        buf.pos(startX, startY, 0).endVertex();
        buf.pos(endX + 1, endY, 0).endVertex();
        buf.pos(endX, endY, 0).endVertex();
        buf.pos(endX, startY, 0).endVertex();
        buf.pos(endX + 1, startY, 0).endVertex();
        tes.draw();
        RenderSystem.enableTexture();
    }

    public StyledString getClipboardString() {
        final String str = Minecraft.getInstance().keyboardListener.getClipboardString();
        if (str.indexOf('\u00a7') == -1) {
            return new StyledString(str, this.currentStyle);
        } else {
            return StyledString.valueOf(str);
        }
    }

    private void setClipboardString(final StyledString value) {
        Minecraft.getInstance().keyboardListener.setClipboardString(value.toString());
    }

    private static int getMultiClickInterval() {
        return 250;
    }

    @FunctionalInterface
    public interface ChangeListener {
        void onChange(StyledString value);
    }

    private static int getWidth(final StyledString styledString, final FontRenderer font) {
        final char[] chars = styledString.toCharArray();
        final Style[] styling = styledString.getStyling();
        int w = 0;
        for (int i = 0, len = styledString.length(); i < len; i++) {
            w += font.getCharWidth(chars[i]);
            if (styling[i].isBold()) {
                w++;
            }
        }
        return w;
    }

    private static StyledString trimToWidth(final StyledString styledString, final FontRenderer font, final int width) {
        return trimToWidth(styledString, font, width, false);
    }

    private static StyledString trimToWidth(final StyledString styledString, final FontRenderer font, final int width, final boolean reverse) {
        final char[] chars = styledString.toCharArray();
        final Style[] styling = styledString.getStyling();
        final int len = styledString.length();
        final StyledStringBuilder str = new StyledStringBuilder();
        final int start = reverse ? len - 1 : 0;
        final int step = reverse ? -1 : 1;
        for (int i = start, w = 0; i >= 0 && i < len && w < width; i += step) {
            w += font.getCharWidth(chars[i]);
            if (styling[i].isBold()) {
                w++;
            }
            if (w > width) {
                break;
            }
            if (reverse) {
                str.insert(0, styledString.substring(i, i + 1));
            } else {
                str.append(styledString.substring(i, i + 1));
            }
        }
        return str.toStyledString();
    }
}
