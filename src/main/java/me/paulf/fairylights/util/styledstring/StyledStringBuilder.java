package me.paulf.fairylights.util.styledstring;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Shorts;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public final class StyledStringBuilder implements Appendable, CharSequence {
    private final StringBuilder strBldr;

    private final List<Short> styling;

    private short currentStyle;

    public StyledStringBuilder(final String str) {
        this();
        this.append(str);
    }

    public StyledStringBuilder() {
        this(16);
    }

    public StyledStringBuilder(final int capacity) {
        this.strBldr = new StringBuilder(capacity);
        this.styling = new ArrayList<>(capacity);
        this.currentStyle = Style.getStylingAsShort(TextFormatting.WHITE);
    }

    private StyledStringBuilder(final String str, final List<Short> styling, final short currentStyling) {
        this.strBldr = new StringBuilder(str);
        this.styling = styling;
        this.currentStyle = currentStyling;
    }

    public StyledStringBuilder setStyle(final short style) {
        this.currentStyle = (short) (style & 0b11111_1111);
        return this;
    }

    public StyledStringBuilder setColor(final TextFormatting color) {
        Preconditions.checkArgument(color.isColor(), "Must be a color");
        this.currentStyle = (short) (this.currentStyle & ~0b1111 | Style.getStylingAsShort(color));
        return this;
    }

    public StyledStringBuilder setBold(final boolean bold) {
        return this.setFlag(0, bold);
    }

    public StyledStringBuilder setStrikethrough(final boolean strikethrough) {
        return this.setFlag(1, strikethrough);
    }

    public StyledStringBuilder setUnderline(final boolean underline) {
        return this.setFlag(2, underline);
    }

    public StyledStringBuilder setItalic(final boolean italic) {
        return this.setFlag(3, italic);
    }

    private StyledStringBuilder setFlag(final int idx, final boolean value) {
        if (value) {
            this.currentStyle |= 1 << (idx + 4);
        } else {
            this.currentStyle &= ~(1 << (idx + 4));
        }
        return this;
    }

    @Override
    public int length() {
        return this.strBldr.length();
    }

    @Override
    public char charAt(final int index) {
        return this.strBldr.charAt(index);
    }

    @Override
    public StyledStringBuilder subSequence(final int start, final int end) {
        return new StyledStringBuilder(this.strBldr.substring(start, end), new ArrayList<>(this.styling).subList(start, end), this.currentStyle);
    }

    public StyledStringBuilder insert(final int index, final String str) {
        this.strBldr.insert(0, str);
        for (int i = str.length(); i > 0; i--) {
            this.styling.add(index, this.currentStyle);
        }
        return this;
    }

    public StyledStringBuilder insert(final int index, final StyledString str) {
        this.strBldr.insert(0, str.toUnstyledString());
        final short[] styling = str.getStyling();
        for (int i = styling.length - 1; i >= 0; i--) {
            this.styling.add(index, styling[i]);
        }
        return this;
    }

    public StyledStringBuilder append(final StyledString str) {
        this.strBldr.append(str.toUnstyledString());
        for (final short s : str.getStyling()) {
            this.styling.add(s);
        }
        return this;
    }

    public StyledStringBuilder append(final CharSequence csq, short style) {
        this.strBldr.append(csq);
        style &= 0b11111_1111;
        for (int i = 0, s = style & 0b11111_1111; i < csq.length(); i++) {
            this.styling.add(style);
        }
        return this;
    }

    @Override
    public StyledStringBuilder append(final CharSequence csq) {
        this.strBldr.append(csq);
        for (int i = 0; i < csq.length(); i++) {
            this.styling.add(this.currentStyle);
        }
        return this;
    }

    @Override
    public StyledStringBuilder append(final CharSequence csq, final int start, final int end) {
        return this.append(csq.subSequence(start, end));
    }

    @Override
    public StyledStringBuilder append(final char c) {
        return this.append(c, this.currentStyle);
    }

    public StyledStringBuilder append(final char c, final short s) {
        this.strBldr.append(c);
        this.styling.add(s);
        return this;
    }

    @Override
    public String toString() {
        return this.strBldr.toString();
    }

    public StyledString toStyledString() {
        return new StyledString(this.strBldr.toString(), Shorts.toArray(this.styling));
    }
}
