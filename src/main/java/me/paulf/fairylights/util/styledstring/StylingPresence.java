package me.paulf.fairylights.util.styledstring;

public final class StylingPresence {
	public static final StylingPresence ALL = new StylingPresence(true, true, true, true, true, true);

	private final boolean hasColor;

	private final boolean hasObfuscated;

	private final boolean hasBold;

	private final boolean hasStrikethrough;

	private final boolean hasUnderline;

	private final boolean hasItalic;

	public StylingPresence(boolean hasColor, boolean hasObfuscated, boolean hasBold, boolean hasStrikethrough, boolean hasUnderline, boolean hasItalic) {
		this.hasColor = hasColor;
		this.hasObfuscated = hasObfuscated;
		this.hasBold = hasBold;
		this.hasStrikethrough = hasStrikethrough;
		this.hasUnderline = hasUnderline;
		this.hasItalic = hasItalic;
	}

	public boolean hasColor() {
		return hasColor;
	}

	public boolean hasObfuscated() {
		return hasObfuscated;
	}

	public boolean hasBold() {
		return hasBold;
	}

	public boolean hasStrikethrough() {
		return hasStrikethrough;
	}

	public boolean hasUnderline() {
		return hasUnderline;
	}

	public boolean hasItalic() {
		return hasItalic;
	}
}
