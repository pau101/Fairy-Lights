package me.paulf.fairylights.util.styledstring;

import com.google.common.base.Preconditions;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.Set;

public final class Style {
    private final TextFormatting color;

    private final boolean isObfuscated;

    private final boolean isBold;

    private final boolean isStrikethrough;

    private final boolean isUnderline;

    private final boolean isItalic;

    public Style() {
        this(StyledString.DEFAULT_COLOR);
    }

    public Style(final TextFormatting color, final TextFormatting... fancy) {
        Preconditions.checkArgument(color.isColor(), "Must be a color");
        this.color = color;
        if (fancy == null) {
            this.isObfuscated = this.isBold = this.isStrikethrough = this.isUnderline = this.isItalic = false;
        } else {
            boolean o = false, b = false, s = false, u = false, i = false;
            for (final TextFormatting f : fancy) {
                switch (f) {
                    case OBFUSCATED:
                        o = true;
                        break;
                    case BOLD:
                        b = true;
                        break;
                    case STRIKETHROUGH:
                        s = true;
                    case UNDERLINE:
                        u = true;
                        break;
                    case ITALIC:
                        i = true;
                        break;
                    default:
                        throw new IllegalArgumentException("Cannot use non-fancy formatting");
                }
            }
            this.isObfuscated = o;
            this.isBold = b;
            this.isStrikethrough = s;
            this.isUnderline = u;
            this.isItalic = i;
        }
    }

    public Style(final TextFormatting color, final boolean isBold, final boolean isStrikethrough, final boolean isUnderline, final boolean isItalic, final boolean isObfuscated) {
        Preconditions.checkArgument(color.isColor(), "Must be a color");
        this.color = color;
        this.isBold = isBold;
        this.isStrikethrough = isStrikethrough;
        this.isUnderline = isUnderline;
        this.isItalic = isItalic;
        this.isObfuscated = isObfuscated;
    }

    public Style(final short style) {
        this.color = getColorFromStyle(style);
        this.isObfuscated = (style & (1 << 4)) != 0;
        this.isBold = (style & (1 << 5)) != 0;
        this.isStrikethrough = (style & (1 << 6)) != 0;
        this.isUnderline = (style & (1 << 7)) != 0;
        this.isItalic = (style & (1 << 8)) != 0;
    }

    public TextFormatting getColor() {
        return this.color;
    }

    public boolean isObfuscated() {
        return this.isObfuscated;
    }

    public boolean isBold() {
        return this.isBold;
    }

    public boolean isStrikethrough() {
        return this.isStrikethrough;
    }

    public boolean isUnderline() {
        return this.isUnderline;
    }

    public boolean isItalic() {
        return this.isItalic;
    }

    public boolean isPlain() {
        return !this.isObfuscated && !this.isBold && !this.isStrikethrough && !this.isUnderline && !this.isItalic;
    }

    public short getValue() {
        return getShortStyling(this.color, this.isBold, this.isStrikethrough, this.isUnderline, this.isItalic);
    }

    public Style withColor(final TextFormatting color) {
        Preconditions.checkArgument(color.isColor(), "Must be color");
        return new Style(color, this.isBold, this.isStrikethrough, this.isUnderline, this.isItalic, this.isObfuscated);
    }

    public Style withBold(final boolean isBold) {
        return new Style(this.color, isBold, this.isStrikethrough, this.isUnderline, this.isItalic, this.isObfuscated);
    }

    public Style withStrikethrough(final boolean isStrikethrough) {
        return new Style(this.color, this.isBold, isStrikethrough, this.isUnderline, this.isItalic, this.isObfuscated);
    }

    public Style withUnderline(final boolean isUnderline) {
        return new Style(this.color, this.isBold, this.isStrikethrough, isUnderline, this.isItalic, this.isObfuscated);
    }

    public Style withItalic(final boolean isItalic) {
        return new Style(this.color, this.isBold, this.isStrikethrough, this.isUnderline, isItalic, this.isObfuscated);
    }

    public Style withObfuscated(final boolean isObfuscated) {
        return new Style(this.color, this.isBold, this.isStrikethrough, this.isUnderline, this.isItalic, isObfuscated);
    }

    public Style withStyling(final TextFormatting styling, final boolean state) {
        Preconditions.checkArgument(styling != TextFormatting.RESET, "Reset is not styling");
        if (styling.isColor()) {
            return this.withColor(styling);
        }
        boolean b = this.isBold, s = this.isStrikethrough, u = this.isUnderline, i = this.isItalic, o = this.isObfuscated;
        switch (styling) {
            case BOLD:
                b = state;
                break;
            case STRIKETHROUGH:
                s = state;
                break;
            case UNDERLINE:
                u = state;
                break;
            case ITALIC:
                i = state;
                break;
            case OBFUSCATED:
                o = state;
                break;
            default:
        }
        return new Style(this.color, b, s, u, i, o);
    }

    @Override
    public int hashCode() {
        int h = this.color.hashCode();
        h = 31 * h + (this.isBold ? 1231 : 1237);
        h = 31 * h + (this.isItalic ? 1231 : 1237);
        h = 31 * h + (this.isObfuscated ? 1231 : 1237);
        h = 31 * h + (this.isStrikethrough ? 1231 : 1237);
        h = 31 * h + (this.isUnderline ? 1231 : 1237);
        return h;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Style) {
            final Style other = (Style) obj;
            if (this.color != other.color) {
                return false;
            }
            if (this.isBold != other.isBold) {
                return false;
            }
            if (this.isItalic != other.isItalic) {
                return false;
            }
            if (this.isObfuscated != other.isObfuscated) {
                return false;
            }
            if (this.isStrikethrough != other.isStrikethrough) {
                return false;
            }
            return this.isUnderline == other.isUnderline;
        }
        return true;
    }

    public static final TextFormatting getColorFromStyle(final short style) {
        return TextFormatting.values()[style & 0b1111];
    }

    public static final short getStylingAsShort(final TextFormatting... styling) {
        TextFormatting color = null;
        short value = 0;
        for (final TextFormatting style : styling) {
            if (style.isColor()) {
                color = style;
            } else if (style.isFancyStyling()) {
                value |= 1 << 4 + style.ordinal() - 16;
            }
        }
        if (color == null) {
            color = StyledString.DEFAULT_COLOR;
        }
        return (short) (value | color.ordinal());
    }

    public static final Set<TextFormatting> getFancyStylingFromStyle(final short style) {
        final Set<TextFormatting> fancy = new HashSet<>();
        final TextFormatting[] formatting = TextFormatting.values();
        final int field = style >> 4 & 0b11111;
        for (int i = 0; i < 5; i++) {
            if ((field & 1 << i) != 0) {
                fancy.add(formatting[16 + i]);
            }
        }
        return fancy;
    }

    public static final short getShortStyling(final TextFormatting color, final boolean isBold, final boolean isStrikethrough, final boolean isUnderline, final boolean isItalic) {
        short style = (short) color.ordinal();
        if (isBold) {
            style |= 1 << 5;
        }
        if (isStrikethrough) {
            style |= 1 << 6;
        }
        if (isUnderline) {
            style |= 1 << 7;
        }
        if (isItalic) {
            style |= 1 << 8;
        }
        return style;
    }

    public static final boolean hasStyling(final short style, final TextFormatting formatting) {
        if (formatting.isColor()) {
            return (style & 0b1111) == formatting.ordinal();
        } else if (formatting.isFancyStyling()) {
            return (style >> 4 & 0b11111 & 1 << formatting.ordinal() - 16) != 0;
        }
        return false;
    }
}
