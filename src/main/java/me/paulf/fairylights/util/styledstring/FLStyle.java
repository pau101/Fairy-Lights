package me.paulf.fairylights.util.styledstring;

import net.minecraft.ChatFormatting;

public final class FLStyle implements Comparable<FLStyle> {
    private static final int COLOR_MASK = 0xF;

    private static final int OBFUSCATED_MASK = 0x10;

    private static final int BOLD_MASK = 0x20;

    private static final int STRIKETHROUGH_MASK = 0x40;

    private static final int UNDERLINE_MASK = 0x80;

    private static final int ITALIC_MASK = 0x100;

    private static final int FANCY_MASK = OBFUSCATED_MASK | BOLD_MASK | STRIKETHROUGH_MASK | UNDERLINE_MASK | ITALIC_MASK;

    private final int value;

    public FLStyle() {
        this(ChatFormatting.WHITE , false, false, false, false, false);
    }

    public FLStyle(final ChatFormatting color, final boolean isBold, final boolean isStrikethrough, final boolean isUnderline, final boolean isItalic, final boolean isObfuscated) {
        this(FLStyle.pack(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated));
    }

    public FLStyle(final int value) {
        this.value = value;
    }

    public int packed() {
        return this.value;
    }

    public ChatFormatting getColor() {
        return ChatFormatting.getById(this.value & COLOR_MASK);
    }

    public boolean isObfuscated() {
        return (this.value & OBFUSCATED_MASK) != 0;
    }

    public boolean isBold() {
        return (this.value & BOLD_MASK) != 0;
    }

    public boolean isStrikethrough() {
        return (this.value & STRIKETHROUGH_MASK) != 0;
    }

    public boolean isUnderline() {
        return (this.value & UNDERLINE_MASK) != 0;
    }

    public boolean isItalic() {
        return (this.value & ITALIC_MASK) != 0;
    }

    public boolean isPlain() {
        return (this.value & FANCY_MASK) == 0;
    }

    public FLStyle withColor(final ChatFormatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("Invalid color formatting: " + color.getName());
        }
        return new FLStyle(color.getId() | this.value & FANCY_MASK);
    }

    public FLStyle withBold(final boolean isBold) {
        return new FLStyle(isBold ? this.value | BOLD_MASK : this.value & ~BOLD_MASK);
    }

    public FLStyle withStrikethrough(final boolean isStrikethrough) {
        return new FLStyle(isStrikethrough ? this.value | STRIKETHROUGH_MASK : this.value & ~STRIKETHROUGH_MASK);
    }

    public FLStyle withUnderline(final boolean isUnderline) {
        return new FLStyle(isUnderline ? this.value | UNDERLINE_MASK : this.value & ~UNDERLINE_MASK);
    }

    public FLStyle withItalic(final boolean isItalic) {
        return new FLStyle(isItalic ? this.value | ITALIC_MASK : this.value & ~ITALIC_MASK);
    }

    public FLStyle withObfuscated(final boolean isObfuscated) {
        return new FLStyle(isObfuscated ? this.value | OBFUSCATED_MASK : this.value & ~OBFUSCATED_MASK);
    }

    public FLStyle withStyling(final ChatFormatting formatting, final boolean state) {
        if (formatting.isColor()) {
            return this.withColor(formatting);
        }
        switch (formatting) {
            case BOLD:
                return this.withBold(state);
            case STRIKETHROUGH:
                return this.withStrikethrough(state);
            case UNDERLINE:
                return this.withUnderline(state);
            case ITALIC:
                return this.withItalic(state);
            case OBFUSCATED:
                return this.withObfuscated(state);
            default:
                throw new IllegalArgumentException("Invalid fancy formatting: " + formatting.getName());
        }
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof FLStyle && this.value == ((FLStyle) obj).value;
    }

    @Override
    public int compareTo(final FLStyle other) {
        if (this == other) {
            return 0;
        }
        return this.value - other.value;
    }

    private static int pack(final ChatFormatting color, final boolean isBold, final boolean isStrikethrough, final boolean isUnderline, final boolean isItalic, final boolean isObfuscated) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("Invalid color formatting: " + color.getName());
        }
        int value = color.getId();
        if (isObfuscated) value |= OBFUSCATED_MASK;
        if (isBold) value |= BOLD_MASK;
        if (isStrikethrough) value |= STRIKETHROUGH_MASK;
        if (isUnderline) value |= UNDERLINE_MASK;
        if (isItalic) value |= ITALIC_MASK;
        return value;
    }
}
