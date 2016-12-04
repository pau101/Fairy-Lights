package com.pau101.fairylights.util.styledstring;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Shorts;

import net.minecraft.util.text.TextFormatting;

public final class StyledStringBuilder implements Appendable, CharSequence {
	private final StringBuilder strBldr;

	private final List<Short> styling;

	private short currentStyle;

	public StyledStringBuilder(String str) {
		this();
		append(str);
	}

	public StyledStringBuilder() {
		this(16);
	}

	public StyledStringBuilder(int capacity) {
		strBldr = new StringBuilder(capacity);
		styling = new ArrayList<>(capacity);
		this.currentStyle = Style.getStylingAsShort(TextFormatting.WHITE);
	}

	private StyledStringBuilder(String str, List<Short> styling, short currentStyling) {
		this.strBldr = new StringBuilder(str);
		this.styling = styling;
		this.currentStyle = currentStyling; 
	}

	public StyledStringBuilder setStyle(short style) {
		currentStyle = (short) (style & 0b11111_1111);
		return this;
	}

	public StyledStringBuilder setColor(TextFormatting color) {
		Preconditions.checkArgument(color.isColor(), "Must be a color");
		currentStyle = (short) (currentStyle & ~0b1111 | Style.getStylingAsShort(color));
		return this;
	}

	public StyledStringBuilder setBold(boolean bold) {
		return setFlag(0, bold);
	}

	public StyledStringBuilder setStrikethrough(boolean strikethrough) {
		return setFlag(1, strikethrough);
	}

	public StyledStringBuilder setUnderline(boolean underline) {
		return setFlag(2, underline);
	}

	public StyledStringBuilder setItalic(boolean italic) {
		return setFlag(3, italic);
	}

	private StyledStringBuilder setFlag(int idx, boolean value) {
		if (value) {
			currentStyle |= 1 << (idx + 4);
		} else {
			currentStyle &= ~(1 << (idx + 4));
		}
		return this;
	}

	@Override
	public int length() {
		return strBldr.length();
	}

	@Override
	public char charAt(int index) {
		return strBldr.charAt(index);
	}

	@Override
	public StyledStringBuilder subSequence(int start, int end) {
		return new StyledStringBuilder(strBldr.substring(start, end), new ArrayList<>(styling).subList(start, end), currentStyle);
	}

	public StyledStringBuilder insert(int index, String str) {
		strBldr.insert(0, str);
		for (int i = str.length(); i > 0; i--) {
			styling.add(index, currentStyle);
		}
		return this;
	}

	public StyledStringBuilder insert(int index, StyledString str) {
		strBldr.insert(0, str.toUnstyledString());
		short[] styling = str.getStyling();
		for (int i = styling.length - 1; i >= 0; i--) {
			this.styling.add(index, styling[i]);
		}
		return this;
	}

	public StyledStringBuilder append(StyledString str) {
		strBldr.append(str.toUnstyledString());
		for (short s : str.getStyling()) {
			styling.add(s);
		}
		return this;
	}

	public StyledStringBuilder append(CharSequence csq, short style) {
		strBldr.append(csq);
		style &= 0b11111_1111;
		for (int i = 0, s = style & 0b11111_1111; i < csq.length(); i++) {
			styling.add(style);
		}
		return this;
	}

	@Override
	public StyledStringBuilder append(CharSequence csq) {
		strBldr.append(csq);
		for (int i = 0; i < csq.length(); i++) {
			styling.add(currentStyle);
		}
		return this;
	}

	@Override
	public StyledStringBuilder append(CharSequence csq, int start, int end){
		return append(csq.subSequence(start, end));
	}

	@Override
	public StyledStringBuilder append(char c) {
		return append(c, currentStyle);
	}

	public StyledStringBuilder append(char c, short s) {
		strBldr.append(c);
		styling.add(s);
		return this;
	}

	@Override
	public String toString() {
		return strBldr.toString();
	}

	public StyledString toStyledString() {
		return new StyledString(strBldr.toString(), Shorts.toArray(styling));
	}
}
