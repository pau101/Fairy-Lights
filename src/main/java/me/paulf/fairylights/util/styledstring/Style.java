package me.paulf.fairylights.util.styledstring;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import net.minecraft.util.text.TextFormatting;

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

	public Style(TextFormatting color, TextFormatting... fancy) {
		Preconditions.checkArgument(color.isColor(), "Must be a color");
		this.color = color;
		if (fancy == null) {
			isObfuscated = isBold = isStrikethrough = isUnderline = isItalic = false;
		} else {
			boolean o = false, b = false, s = false, u = false, i = false;
			for (TextFormatting f : fancy) {
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
			isObfuscated = o;
			isBold = b;
			isStrikethrough = s;
			isUnderline = u;
			isItalic = i;
		}
	}

	public Style(TextFormatting color, boolean isBold, boolean isStrikethrough, boolean isUnderline, boolean isItalic, boolean isObfuscated) {
		Preconditions.checkArgument(color.isColor(), "Must be a color");
		this.color = color;
		this.isBold = isBold;
		this.isStrikethrough = isStrikethrough;
		this.isUnderline = isUnderline;
		this.isItalic = isItalic;
		this.isObfuscated = isObfuscated;
	}

	public Style(short style) {
		color = getColorFromStyle(style);
		isObfuscated = (style & (1 << 4)) != 0;
		isBold = (style & (1 << 5)) != 0;
		isStrikethrough = (style & (1 << 6)) != 0;
		isUnderline = (style & (1 << 7)) != 0;
		isItalic = (style & (1 << 8)) != 0;
	}

	public TextFormatting getColor() {
		return color;
	}

	public boolean isObfuscated() {
		return isObfuscated;
	}

	public boolean isBold() {
		return isBold;
	}

	public boolean isStrikethrough() {
		return isStrikethrough;
	}

	public boolean isUnderline() {
		return isUnderline;
	}

	public boolean isItalic() {
		return isItalic;
	}

	public boolean isPlain() {
		return !isObfuscated && !isBold && !isStrikethrough && !isUnderline && !isItalic;
	}

	public short getValue() {
		return getShortStyling(color, isBold, isStrikethrough, isUnderline, isItalic);
	}

	public Style withColor(TextFormatting color) {
		Preconditions.checkArgument(color.isColor(), "Must be color");
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withBold(boolean isBold) {
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withStrikethrough(boolean isStrikethrough) {
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withUnderline(boolean isUnderline) {
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withItalic(boolean isItalic) {
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withObfuscated(boolean isObfuscated) {
		return new Style(color, isBold, isStrikethrough, isUnderline, isItalic, isObfuscated);
	}

	public Style withStyling(TextFormatting styling, boolean state) {
		Preconditions.checkArgument(styling != TextFormatting.RESET, "Reset is not styling");
		if (styling.isColor()) {
			return withColor(styling);
		}
		boolean b = isBold, s = isStrikethrough, u = isUnderline, i = isItalic, o = isObfuscated;
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
		return new Style(color, b, s, u, i, o);
	}

	@Override
	public int hashCode() {
		int h = color.hashCode();
		h = 31 * h + (isBold ? 1231 : 1237);
		h = 31 * h + (isItalic ? 1231 : 1237);
		h = 31 * h + (isObfuscated ? 1231 : 1237);
		h = 31 * h + (isStrikethrough ? 1231 : 1237);
		h = 31 * h + (isUnderline ? 1231 : 1237);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Style) {
			Style other = (Style) obj;
			if (color != other.color) {
				return false;
			}
			if (isBold != other.isBold) {
				return false;
			}
			if (isItalic != other.isItalic) {
				return false;
			}
			if (isObfuscated != other.isObfuscated) {
				return false;
			}
			if (isStrikethrough != other.isStrikethrough) {
				return false;
			}
			if (isUnderline != other.isUnderline) {
				return false;
			}
		}
		return true;
	}

	public static final TextFormatting getColorFromStyle(short style) {
		return TextFormatting.values()[style & 0b1111];
	}

	public static final short getStylingAsShort(TextFormatting... styling) {
		TextFormatting color = null;
		short value = 0;
		for (TextFormatting style : styling) {
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

	public static final Set<TextFormatting> getFancyStylingFromStyle(short style) {
		Set<TextFormatting> fancy = new HashSet<>();
		TextFormatting[] formatting = TextFormatting.values();
		int field = style >> 4 & 0b11111;
		for (int i = 0; i < 5; i++) {
			if ((field & 1 << i) != 0) {
				fancy.add(formatting[16 + i]);
			}
		}
		return fancy;
	}

	public static final short getShortStyling(TextFormatting color, boolean isBold, boolean isStrikethrough, boolean isUnderline, boolean isItalic) {
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

	public static final boolean hasStyling(short style, TextFormatting formatting) {
		if (formatting.isColor()) {
			return (style & 0b1111) == formatting.ordinal();
		} else if (formatting.isFancyStyling()) {
			return (style >> 4 & 0b11111 & 1 << formatting.ordinal() - 16) != 0;
		}
		return false;
	}
}
