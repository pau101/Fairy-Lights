package me.paulf.fairylights.util.styledstring;

public final class StylingPresence {
    public static final StylingPresence ALL = new StylingPresence(true, true, true, true, true, true);

    private final boolean hasColor;

    private final boolean hasObfuscated;

    private final boolean hasBold;

    private final boolean hasStrikethrough;

    private final boolean hasUnderline;

    private final boolean hasItalic;

    public StylingPresence(final boolean hasColor, final boolean hasObfuscated, final boolean hasBold, final boolean hasStrikethrough, final boolean hasUnderline, final boolean hasItalic) {
        this.hasColor = hasColor;
        this.hasObfuscated = hasObfuscated;
        this.hasBold = hasBold;
        this.hasStrikethrough = hasStrikethrough;
        this.hasUnderline = hasUnderline;
        this.hasItalic = hasItalic;
    }

    public boolean hasColor() {
        return this.hasColor;
    }

    public boolean hasObfuscated() {
        return this.hasObfuscated;
    }

    public boolean hasBold() {
        return this.hasBold;
    }

    public boolean hasStrikethrough() {
        return this.hasStrikethrough;
    }

    public boolean hasUnderline() {
        return this.hasUnderline;
    }

    public boolean hasItalic() {
        return this.hasItalic;
    }
}
