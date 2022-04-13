package me.paulf.fairylights.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.util.styledstring.FLStyle;
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
import net.minecraft.util.text.ITextComponent;
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

    private FLStyle currentStyle;

    public StyledTextFieldWidget(
        final FontRenderer font,
        final ColorButton colorBtn,
        final ToggleButton boldBtn,
        final ToggleButton italicBtn,
        final ToggleButton underlineBtn,
        final ToggleButton strikethroughBtn,
        final int x, final int y, final int width, final int height,
        final ITextComponent msg
    ) {
        super(x, y, width, height, msg);
        this.font = font;
        this.colorBtn = colorBtn;
        this.boldBtn = boldBtn;
        this.italicBtn = italicBtn;
        this.underlineBtn = underlineBtn;
        this.strikethroughBtn = strikethroughBtn;
        this.setValue0(new StyledString());
        this.setStyle(new FLStyle());
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

    public void setStyle(final FLStyle style) {
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
        this.caret = MathHelper.func_76125_a(pos, 0, this.value.length());
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

    public FLStyle getStyle() {
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
    public void func_230996_d_(final boolean isFocused) {
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
    public boolean func_230999_j_() {
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
        return this.hasBackground ? this.field_230688_j_ - 8 : this.field_230688_j_;
    }

    public void setSelectionPos(final int pos) {
        final int len = this.value.length();
        this.selectionEnd = MathHelper.func_76125_a(pos, 0, len);
        if (this.lineScrollOffset > len) {
            this.lineScrollOffset = len;
        }
        final int w = trimToWidth(this.value.substring(this.lineScrollOffset), this.font, this.getInnerWidth(), true).length();
        if (this.selectionEnd > w + this.lineScrollOffset) {
            this.lineScrollOffset = this.selectionEnd - w;
        } else if (this.selectionEnd <= this.lineScrollOffset) {
            this.lineScrollOffset = this.selectionEnd;
        }
        this.lineScrollOffset = MathHelper.func_76125_a(this.lineScrollOffset, 0, len);
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
            final FLStyle s = selected.styleAt(i);
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
        this.setStyle(new FLStyle(color == null ? this.currentStyle.getColor() : color, bold, strikethrough, underline, italic, false));
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
                lower = this.field_230690_l_ + 15;
                upper = this.field_230690_l_ + this.field_230688_j_ - 16;
            } else {
                lower = this.field_230690_l_ + 11;
                upper = this.field_230690_l_ + this.field_230688_j_ - 12;
            }
            boolean scrolled = false;
            if (mouseX < lower) {
                if (this.lineScrollOffset > 0) {
                    final int rate = (2 - (mouseX - this.field_230690_l_) / 5) * 2 + 2;
                    this.lineScrollOffset -= rate;
                    if (this.lineScrollOffset < 0) {
                        this.lineScrollOffset = 0;
                    }
                    scrolled = true;
                }
            } else if (mouseX > upper) {
                final int max = this.value.length() - trimToWidth(this.value, this.font, this.getInnerWidth(), true).length();
                if (this.lineScrollOffset < max) {
                    final int rate = (2 + (mouseX - this.field_230690_l_ - this.field_230688_j_ + 1) / 5) * 2 + 2;
                    this.lineScrollOffset += rate;
                    if (this.lineScrollOffset > max) {
                        this.lineScrollOffset = max;
                    }
                    scrolled = true;
                }
            }
            if (scrolled && !this.hasDraggedSelecton) {
                int relativeX = mouseX - this.field_230690_l_;
                if (this.hasBackground) {
                    relativeX -= 2;
                }
                this.setSelectionPos(this.getIndexInTextByX(relativeX));
            }
        }
        this.tick++;
    }

    @Override
    public boolean func_231046_a_(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.isFocused) {
            return false;
        }
        if (Screen.func_231170_j_(keyCode)) {
            this.setCaretEnd();
            this.setSelectionPos(0);
        } else if (Screen.func_231169_i_(keyCode)) {
            this.setClipboardString(this.getSelectedText());
        } else if (Screen.func_231168_h_(keyCode)) {
            if (this.isWritable) {
                final StyledString str = this.getClipboardString();
                if (Screen.func_231173_s_()) {
                    this.writeText(str.toUnstyledString());
                } else {
                    this.writeText(str);
                }
            }
        } else if (Screen.func_231166_g_(keyCode)) {
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
                    if (Screen.func_231172_r_()) {
                        if (this.isWritable) {
                            this.deleteWords(-1);
                        }
                    } else if (this.isWritable) {
                        this.deleteFromCursor(-1);
                    }
                    break;
                case GLFW.GLFW_KEY_HOME:
                    if (Screen.func_231173_s_()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCaretStart();
                    }
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    if (Screen.func_231173_s_()) {
                        if (Screen.func_231172_r_()) {
                            this.setSelectionPos(this.skipWords(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (Screen.func_231172_r_()) {
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
                    if (Screen.func_231173_s_()) {
                        if (Screen.func_231172_r_()) {
                            this.setSelectionPos(this.skipWords(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (Screen.func_231172_r_()) {
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
                    if (Screen.func_231173_s_()) {
                        this.setSelectionPos(this.value.length());
                    } else {
                        this.setCaretEnd();
                    }
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    if (Screen.func_231172_r_()) {
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
    public boolean func_231042_a_(final char typedChar, final int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        if (SharedConstants.func_71566_a(typedChar)) {
            final String writeChar = this.charInputTransformer.apply(Character.toString(typedChar));
            if (this.isWritable) {
                this.writeText(writeChar);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean func_231044_a_(final double mouseX, final double mouseY, final int button) {
        final boolean hovered = mouseX >= this.field_230690_l_ && mouseX < this.field_230690_l_ + this.field_230688_j_ && mouseY >= this.field_230691_m_ && mouseY < this.field_230691_m_ + this.field_230689_k_;
        if (this.isBlurable) {
            this.func_230996_d_(hovered);
        } else if (!hovered) {
            this.setCaret(Math.min(this.caret, this.selectionEnd));
        }
        if (this.isFocused && hovered && button == 0) {
            int relativeX = MathHelper.func_76128_c(mouseX - this.field_230690_l_);
            if (this.hasBackground) {
                relativeX -= 2;
            }
            final int idx = this.getIndexInTextByX(relativeX);
            final long now = Util.func_211177_b();
            if (now - this.lastClickTime <= this.multiClickInterval) {
                this.multiClicks++;
                if (this.multiClicks > 3) {
                    this.multiClicks = 1;
                }
            } else {
                this.multiClicks = 1;
            }
            this.lastClickTime = now;
            if (Screen.func_231173_s_()) {
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
    public boolean func_231048_c_(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            this.isPressed = false;
            if (this.isDraggingSelection) {
                final boolean hovered = mouseX >= this.field_230690_l_ && mouseX < this.field_230690_l_ + this.field_230688_j_ && mouseY >= this.field_230691_m_ && mouseY < this.field_230691_m_ + this.field_230689_k_;
                if (hovered) {
                    int relativeX = MathHelper.func_76128_c(mouseX - this.field_230690_l_);
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
    public boolean func_231045_a_(final double mouseX, final double mouseY, final int button, final double dx, final double dy) {
        if (this.isFocused && this.isPressed && button == 0) {
            int relativeX = MathHelper.func_76128_c(mouseX - this.field_230690_l_);
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
                FLStyle style = null;
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
        this.setStyle(new FLStyle());
        this.tick = 0;
    }

    private void resetSelectedFormatting() {
        int start = this.caret, end = this.selectionEnd;
        if (end < start) {
            final int t = start;
            start = end;
            end = t;
        }
        this.setValue0(this.value.withStyling(start, end, new FLStyle()));
    }

    @Override
    public void func_230431_b_(final MatrixStack stack, final int mouseX, final int mouseY, final float delta) {
        if (!this.isVisible) {
            return;
        }
        if (this.hasBackground) {
            func_238467_a_(stack, this.field_230690_l_ - 1, this.field_230691_m_ - 1, this.field_230690_l_ + this.field_230688_j_ + 1, this.field_230691_m_ + this.field_230689_k_ + 1, 0xAAA0A0A0);
            func_238467_a_(stack, this.field_230690_l_, this.field_230691_m_, this.field_230690_l_ + this.field_230688_j_, this.field_230691_m_ + this.field_230689_k_, 0xFF000000);
        }
        final int textColor = this.isWritable ? this.writableTextColor : this.readonlyTextColor;
        final int visibleCaret = this.caret - this.lineScrollOffset;
        int visibleSelectionEnd = this.selectionEnd - this.lineScrollOffset;
        final StyledString visibleText = trimToWidth(this.value.substring(this.lineScrollOffset), this.font, this.getInnerWidth());
        final boolean isCaretVisible = visibleCaret >= 0 && visibleCaret <= visibleText.length();
        final boolean drawSelection = visibleSelectionEnd != visibleCaret;
        final boolean drawCaret = !drawSelection && this.isFocused && this.tick / 6 % 2 == 0 && isCaretVisible;
        final int offsetX = this.hasBackground ? this.field_230690_l_ + 4 : this.field_230690_l_;
        final int offsetY = this.hasBackground ? this.field_230691_m_ + (this.field_230689_k_ - 8) / 2 : this.field_230691_m_;
        int textX = offsetX;
        if (visibleSelectionEnd > visibleText.length()) {
            visibleSelectionEnd = visibleText.length();
        }
        if (visibleText.length() > 0) {
            final ITextComponent beforeCaret = (isCaretVisible ? visibleText.substring(0, visibleCaret) : visibleText).toTextComponent();
            textX = this.font.func_243246_a(stack, beforeCaret, offsetX, offsetY, textColor);
        }
        final int caretX;
        if (isCaretVisible) {
            caretX = --textX;
        } else {
            caretX = visibleCaret > 0 ? offsetX + this.field_230688_j_ - 6 : offsetX;
        }
        if (visibleText.length() > 0 && isCaretVisible && visibleCaret < visibleText.length()) {
            textX = this.font.func_243246_a(stack, visibleText.substring(visibleCaret).toTextComponent(), textX, offsetY, textColor);
        }
        if (drawCaret) {
            final int rgb = StyledString.getColor(this.currentStyle.getColor());
            if (this.currentStyle.isItalic()) {
                final float r = (rgb >> 16 & 0xFF) / 255F;
                final float g = (rgb >> 8 & 0xFF) / 255F;
                final float b = (rgb & 0xFF) / 255F;
                final Tessellator tes = Tessellator.func_178181_a();
                final BufferBuilder buf = tes.func_178180_c();
                buf.func_181668_a(GL11.GL_QUADS, DefaultVertexFormats.field_181706_f);
                buf.func_225582_a_(caretX + 2, offsetY - 2, 0).func_227885_a_(r, g, b, 1.0F).func_181675_d();
                buf.func_225582_a_(caretX + 1, offsetY - 2, 0).func_227885_a_(r, g, b, 1.0F).func_181675_d();
                buf.func_225582_a_(caretX - 1, offsetY + 1 + this.font.field_78288_b, 0).func_227885_a_(r, g, b, 1.0F).func_181675_d();
                buf.func_225582_a_(caretX, offsetY + 1 + this.font.field_78288_b, 0).func_227885_a_(r, g, b, 1.0F).func_181675_d();
                RenderSystem.disableTexture();
                tes.func_78381_a();
                RenderSystem.enableTexture();
            } else {
                func_238467_a_(stack, caretX, offsetY - 2, caretX + 1, offsetY + 1 + this.font.field_78288_b, 0xFF000000 | rgb);
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
            this.drawSelectionHighlight(start - 1, offsetY - 2, end, offsetY + 1 + this.font.field_78288_b);
        }
        if (this.hasDraggedSelecton) {
            if (this.field_230692_n_) {
                int relativeX = mouseX - this.field_230690_l_;
                if (this.hasBackground) {
                    relativeX -= 2;
                }
                final int pos = this.getIndexInTextByX(relativeX) - this.lineScrollOffset;
                if (pos >= 0 && pos <= visibleText.length()) {
                    final int x = getWidth(visibleText.substring(0, pos), this.font);
                    final int rgb = StyledString.getColor(this.currentStyle.getColor());
                    func_238467_a_(stack, offsetX + x, offsetY - 2, offsetX + x + 1, offsetY + 1 + this.font.field_78288_b, 0xFF000000 | rgb);
                }
            }
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.font.func_243246_a(stack, this.getSelectedText().toTextComponent(), mouseX + 5, mouseY + 5, textColor | 0xBF000000);
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
        if (endX > this.field_230690_l_ + this.field_230688_j_) {
            endX = this.field_230690_l_ + this.field_230688_j_;
        }
        if (startX > this.field_230690_l_ + this.field_230688_j_) {
            startX = this.field_230690_l_ + this.field_230688_j_;
        }
        final Tessellator tes = Tessellator.func_178181_a();
        final BufferBuilder buf = tes.func_178180_c();
        RenderSystem.disableTexture();
        buf.func_181668_a(GL11.GL_QUADS, DefaultVertexFormats.field_181705_e);
        buf.func_225582_a_(startX, endY, 0).func_181675_d();
        buf.func_225582_a_(endX, endY, 0).func_181675_d();
        buf.func_225582_a_(endX, endY + 1, 0).func_181675_d();
        buf.func_225582_a_(startX, endY + 1, 0).func_181675_d();
        buf.func_225582_a_(startX, startY - 1, 0).func_181675_d();
        buf.func_225582_a_(endX, startY - 1, 0).func_181675_d();
        buf.func_225582_a_(endX, startY, 0).func_181675_d();
        buf.func_225582_a_(startX, startY, 0).func_181675_d();
        buf.func_225582_a_(startX, endY, 0).func_181675_d();
        buf.func_225582_a_(startX - 1, endY, 0).func_181675_d();
        buf.func_225582_a_(startX - 1, startY, 0).func_181675_d();
        buf.func_225582_a_(startX, startY, 0).func_181675_d();
        buf.func_225582_a_(endX + 1, endY, 0).func_181675_d();
        buf.func_225582_a_(endX, endY, 0).func_181675_d();
        buf.func_225582_a_(endX, startY, 0).func_181675_d();
        buf.func_225582_a_(endX + 1, startY, 0).func_181675_d();
        tes.func_78381_a();
        RenderSystem.enableTexture();
    }

    public StyledString getClipboardString() {
        final String str = Minecraft.func_71410_x().field_195559_v.func_197965_a();
        if (str.indexOf('\u00a7') == -1) {
            return new StyledString(str, this.currentStyle);
        } else {
            return StyledString.valueOf(str);
        }
    }

    private void setClipboardString(final StyledString value) {
        Minecraft.func_71410_x().field_195559_v.func_197960_a(value.toString());
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
        final FLStyle[] styling = styledString.getStyling();
        int w = 0;
        for (int i = 0, len = styledString.length(); i < len; i++) {
            w += font.func_78256_a(Character.toString(chars[i]));
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
        final FLStyle[] styling = styledString.getStyling();
        final int len = styledString.length();
        final StyledStringBuilder str = new StyledStringBuilder();
        final int start = reverse ? len - 1 : 0;
        final int step = reverse ? -1 : 1;
        for (int i = start, w = 0; i >= 0 && i < len && w < width; i += step) {
            w += font.func_78256_a(Character.toString(chars[i]));
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
