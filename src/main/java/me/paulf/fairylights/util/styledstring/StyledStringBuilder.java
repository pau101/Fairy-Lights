package me.paulf.fairylights.util.styledstring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class StyledStringBuilder implements Appendable, CharSequence {
    private final StringBuilder strBldr;

    private final List<FLStyle> styling;

    private FLStyle currentStyle;

    public StyledStringBuilder(final String str) {
        this();
        this.append(str);
    }

    public StyledStringBuilder() {
        this(16);
    }

    public StyledStringBuilder(final int capacity) {
        this(new StringBuilder(capacity), new ArrayList<>(capacity), new FLStyle());
    }

    private StyledStringBuilder(final StringBuilder strBldr, final List<FLStyle> styling, final FLStyle currentStyle) {
        this.strBldr = strBldr;
        this.styling = styling;
        this.currentStyle = currentStyle;
    }

    public StyledStringBuilder setStyle(final FLStyle style) {
        this.currentStyle = Objects.requireNonNull(style);
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
        return new StyledStringBuilder(new StringBuilder(this.strBldr.substring(start, end)), new ArrayList<>(this.styling).subList(start, end), this.styling.get(start));
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
        final FLStyle[] styling = str.getStyling();
        for (int i = styling.length - 1; i >= 0; i--) {
            this.styling.add(index, styling[i]);
        }
        return this;
    }

    public StyledStringBuilder append(final StyledString str) {
        this.strBldr.append(str.toUnstyledString());
        this.styling.addAll(Arrays.asList(str.getStyling()));
        return this;
    }

    public StyledStringBuilder append(final CharSequence csq, FLStyle style) {
        this.strBldr.append(csq);
        for (int i = 0; i < csq.length(); i++) {
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

    public StyledStringBuilder append(final char c, final FLStyle s) {
        this.strBldr.append(c);
        this.styling.add(s);
        return this;
    }

    @Override
    public String toString() {
        return this.strBldr.toString();
    }

    public StyledString toStyledString() {
        return new StyledString(this.strBldr.toString(), this.styling.toArray(new FLStyle[0]));
    }
}
