package com.pau101.fairylights.util.styledstring;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public final class StyledString implements Comparable<StyledString>, CharSequence {
	public static final TextFormatting DEFAULT_COLOR = TextFormatting.WHITE;

	private static final Pattern TAG_PATTERN = Pattern.compile("(<\\s*/?\\s*)([0-9a-zA-Z]+)(\\s*([^>]*)?\\s*>)");

	private final String value;

	private final short[] styling;

	private int hash;

	public StyledString() {
		this("");
	}

	public StyledString(String value, TextFormatting... styling) {
		this(value, Style.getStylingAsShort(Objects.requireNonNull(styling, "styling")));
	}

	public StyledString(String value, Style style) {
		this(value, style.getValue());
	}

	private StyledString(String value, short styling) {
		this.value = Objects.requireNonNull(value, "value");
		this.styling = new short[value.length()];
		Arrays.fill(this.styling, styling);
	}

	public StyledString(String value, short[] styling) {
		Preconditions.checkArgument(value.length() == styling.length, "Value must be same length as styling");
		this.value = value;
		this.styling = styling;
	}

	@Override
	public int length() {
		return value.length();
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	public TextFormatting colorAt(int index) {
		return Style.getColorFromStyle(rawStyleAt(index));
	}

	public Style styleAt(int index) {
		return new Style(styling[index]);
	}

	public short rawStyleAt(int index) {
		return styling[index];
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		value.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		return value.getBytes(charsetName);
	}

	public byte[] getBytes(Charset charset) {
		return value.getBytes(charset);
	}

	public byte[] getBytes() {
		return value.getBytes();
	}

	public short[] getStyling() {
		return styling.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof StyledString) {
			StyledString other = (StyledString) obj;
			return value.equals(other.value) && Arrays.equals(styling, other.styling);
		}
		return false;
	}

	public boolean contentEquals(StringBuffer sb) {
		return value.contentEquals(sb);
	}

	public boolean contentEquals(CharSequence cs) {
		return value.contentEquals(cs);
	}

	public boolean equalsIgnoreCase(StyledString anotherString) {
		return equalsIgnoreCase(anotherString.value);
	}

	public boolean equalsIgnoreCase(String anotherString) {
		return value.equalsIgnoreCase(anotherString);
	}

	@Override
	public int compareTo(StyledString anotherString) {
		int len1 = value.length();
		int len2 = anotherString.value.length();
		int lim = Math.min(len1, len2);
		char v1[] = toCharArray();
		short[] st1 = styling;
		char v2[] = anotherString.toCharArray();
		short[] st2 = anotherString.styling;
		int k = 0;
		while (k < lim) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				return c1 - c2;
			}
			short s1 = st1[k];
			short s2 = st2[k];
			if (s1 != s2) {
				int b1 = s1 & 0b1111;
				int b2 = s2 & 0b1111;
				if (b1 != b2) {
					return b1 - b2;
				}
				b1 = s1 >> 4 & 0b11111;
				b2 = s2 >> 4 & 0b11111;
				if (b1 != b2) {
					return b1 - b2;
				}
			}
			k++;
		}
		return len1 - len2;
	}

	public int compareToIgnoreCase(StyledString anotherString) {
		int len1 = value.length();
		int len2 = anotherString.value.length();
		int lim = Math.min(len1, len2);
		char v1[] = toCharArray();
		short[] st1 = styling;
		char v2[] = anotherString.toCharArray();
		short[] st2 = anotherString.styling;
		int k = 0;
		while (k < lim) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				c1 = Character.toUpperCase(c1);
				c2 = Character.toUpperCase(c2);
				if (c1 != c2) {
					c1 = Character.toLowerCase(c1);
					c2 = Character.toLowerCase(c2);
					if (c1 != c2) {
						return c1 - c2;
					}
				}
			}
			short s1 = st1[k];
			short s2 = st2[k];
			if (s1 != s2) {
				int b1 = s1 & 0b1111;
				int b2 = s2 & 0b1111;
				if (b1 != b2) {
					return b1 - b2;
				}
				b1 = s1 >> 4 & 0b11111;
				b2 = s2 >> 4 & 0b11111;
				if (b1 != b2) {
					return b1 - b2;
				}
			}
			k++;
		}
		return len1 - len2;
	}

	public int compareToIgnoreCase(String anotherString) {
		return value.compareToIgnoreCase(anotherString);
	}

	public boolean regionMatches(int toffset, StyledString other, int ooffset, int len) {
		return regionMatches(toffset, other.value, ooffset, len);
	}

	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return value.regionMatches(toffset, other, ooffset, len);
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, StyledString other, int ooffset, int len) {
		return regionMatches(ignoreCase, toffset, other.value, ooffset, len);
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		return value.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	public boolean startsWith(StyledString prefix) {
		return startsWith(prefix, 0);
	}

	public boolean startsWith(StyledString prefix, int toffset) {
		char[] ta = toCharArray();
		short[] s1 = styling;
		int to = toffset;
		char[] pa = prefix.toCharArray();
		short[] s2 = prefix.styling;
		int po = 0;
		int pc = prefix.value.length();
		if (toffset < 0 || toffset > value.length() - pc) {
			return false;
		}
		while (--pc >= 0) {
			if (ta[to] != pa[po] || s1[to++] != s2[po++]) {
				return false;
			}
		}
		return true;
	}

	public boolean startsWith(String prefix) {
		return startsWith(prefix, 0);
	}

	public boolean startsWith(String prefix, int toffset) {
		return value.startsWith(prefix, toffset);
	}

	public boolean endsWith(StyledString suffix) {
		return startsWith(suffix, length() - suffix.length());
	}

	public boolean endsWith(String suffix) {
		return value.endsWith(suffix);
	}

	@Override
	public int hashCode() {
		if (hash == 0 && length() > 0) {
			hash = 31 * value.hashCode() + Arrays.hashCode(styling);
		}
		return hash;
	}

	public int indexOf(char chr) {
		return value.indexOf(chr);
	}

	public int indexOf(char chr, int fromIndex) {
		return value.indexOf(chr, fromIndex);
	}

	public int indexOf(char chr, TextFormatting... styling) {
		return indexOf(chr, 0, styling);
	}

	public int indexOf(char chr, int fromIndex, TextFormatting... styling) {
		final int max = length();
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= max) {
			return -1;
		}
		short style = Style.getStylingAsShort(styling);
		char[] value = toCharArray();
		for (int i = fromIndex; i < max; i++) {
			if (value[i] == chr && this.styling[i] == style) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(char chr) {
		return value.lastIndexOf(chr);
	}

	public int lastIndexOf(char chr, int fromIndex) {
		return value.lastIndexOf(chr, fromIndex);
	}

	public int lastIndexOf(char chr, TextFormatting... styling) {
		return lastIndexOf(chr, length() - 1, styling);
	}

	public int lastIndexOf(char chr, int fromIndex, TextFormatting... styling) {
		short style = Style.getStylingAsShort(styling);
		char[] value = toCharArray();
		int i = Math.min(fromIndex, length() - 1);
		for (; i >= 0; i--) {
			if (value[i] == chr && this.styling[i] == style) {
				return i;
			}
		}
		return -1;
	}

	public int indexOf(String str) {
		return value.indexOf(str);
	}

	public int indexOf(String str, int fromIndex) {
		return value.indexOf(str, fromIndex);
	}

	public int indexOf(StyledString str) {
		return indexOf(str, 0);
	}

	public int indexOf(StyledString str, int fromIndex) {
		char[] source = toCharArray();
		short[] sourceStyling = styling;
		char[] target = str.toCharArray();
		short[] targetStyling = str.styling;
		int sourceCount = length();
		int targetCount = str.length();
		if (fromIndex >= sourceCount) {
			return targetCount == 0 ? sourceCount : -1;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}
		char first = target[0];
		short firstStyling = targetStyling[0];
		int max = fromIndex + sourceCount - targetCount;
		for (int i = fromIndex + fromIndex; i <= max; i++) {
			if (source[i] != first) {
				while (++i <= max && (source[i] != first || sourceStyling[i] != firstStyling));
			}
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && source[j] == target[k] && sourceStyling[j] == targetStyling[k]; j++, k++);
				if (j == end) {
					return i - fromIndex;
				}
			}
		}
		return -1;
	}

	public int lastIndexOf(String str) {
		return value.lastIndexOf(str);
	}

	public int lastInddexOf(String str, int fromIndex) {
		return value.lastIndexOf(str, fromIndex);
	}

	public int lastIndexOf(StyledString str) {
		return lastIndexOf(str, length());
	}

	public int lastIndexOf(StyledString str, int fromIndex) {
		char[] source = toCharArray();
		short[] sourceStyling = styling;
		char[] target = str.toCharArray();
		short[] targetStyling = str.styling;
		int sourceCount = length();
		int targetCount = str.length();
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		if (targetCount == 0) {
			return fromIndex;
		}
		int strLastIndex = targetCount - 1;
		char strLastChar = target[strLastIndex];
		short strLastStyling = targetStyling[strLastIndex];
		int min = fromIndex + targetCount - 1;
		int i = min + fromIndex;
		startSearchForLastChar:
			while (true) {
				while (i >= min && (source[i] != strLastChar || sourceStyling[i] != strLastStyling)) {
					i--;
				}
				if (i < min) {
					return -1;
				}
				int j = i - 1;
				int start = j - (targetCount - 1);
				int k = strLastIndex - 1;
				while (j > start) {
					boolean isDifferent = source[j] != target[k] || sourceStyling[j] != targetStyling[k];
					j--;
					k--;
					if (isDifferent) {
						i--;
						continue startSearchForLastChar;
					}
				}
				return start - fromIndex + 1;
			}
	}

	public StyledString substring(int beginIndex) {
		return substring(beginIndex, length());
	}

	public StyledString substring(int beginIndex, int endIndex) {
		String value = this.value.substring(beginIndex, endIndex);
		short[] styling = new short[endIndex - beginIndex];
		System.arraycopy(this.styling, beginIndex, styling, 0, styling.length);
		return new StyledString(value, styling);
	}

	@Override
	public StyledString subSequence(int start, int end) {
		return substring(start, end);
	}

	public StyledString concat(StyledString str) {
		int otherLen = str.length();
		if (otherLen == 0) {
			return this;
		}
		int len = length();
		String value = this.value.concat(str.value);
		short[] styling = new short[len + otherLen];
		System.arraycopy(this.styling, 0, styling, 0, len);
		System.arraycopy(str.styling, 0, styling, len, otherLen);
		return new StyledString(value, styling);
	}

	public StyledString replace(char oldChar, char newChar) {
		return new StyledString(value.replace(oldChar, newChar), styling);
	}

	public boolean matches(String regex) {
		return Pattern.matches(regex, this);
	}

	public boolean contains(CharSequence s) {
		return indexOf(s.toString()) > -1;
	}

	public boolean contains(StyledString s) {
		return indexOf(s) > -1;
	}

	public StyledString toLowerCase() {
		return toLowerCase(Locale.getDefault());
	}

	public StyledString toLowerCase(Locale locale) {
		String value = this.value.toLowerCase(locale);
		if (value.length() != length()) {
			throw new UnsupportedOperationException("Characters with surrogate pairs not supported");
		}
		return new StyledString(value, styling);
	}

	public StyledString toUpperCase() {
		return toUpperCase(Locale.getDefault());
	}

	public StyledString toUpperCase(Locale locale) {
		String value = this.value.toUpperCase(locale);
		if (value.length() != length()) {
			throw new UnsupportedOperationException("Characters with surrogate pairs not supported");
		}
		return new StyledString(value, styling);
	}

	public StyledString trim() {
		int len = value.length();
		int st = 0;
		char[] val = toCharArray();
		while (st < len && val[st] <= ' ') {
			st++;
		}
		while (st < len && val[len - 1] <= ' ') {
			len--;
		}
		return st > 0 || len < value.length() ? substring(st, len) : this;
	}

	public StyledString addStyling(TextFormatting... formatting) {
		return addStyling(0, length(), formatting);
	}

	public StyledString addStyling(int beginIndex, int endIndex, TextFormatting... formatting) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
		String value = this.value;
		short[] styling = this.styling;
		short[] newStyling = new short[length()];
		short shortStyling = Style.getStylingAsShort(formatting);
		boolean hasColor = false;
		for (TextFormatting f : formatting) {
			if (f.isColor()) {
				hasColor = true;
				break;
			}
		}
		System.arraycopy(styling, 0, newStyling, 0, beginIndex);
		System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
		for (int i = beginIndex; i < endIndex; i++) {
			newStyling[i] = (short) (hasColor ? (styling[i] & ~0b1111) | shortStyling : (styling[i] | shortStyling & ~0b1111));
		}
		return new StyledString(value, newStyling);
	}

	public StyledString withStyling(TextFormatting... formatting) {
		return addStyling(0, length(), formatting);
	}

	public StyledString withStyling(int beginIndex, int endIndex, TextFormatting... formatting) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
		String value = this.value;
		short[] styling = this.styling;
		short[] newStyling = new short[length()];
		System.arraycopy(styling, 0, newStyling, 0, beginIndex);
		System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
		Arrays.fill(newStyling, beginIndex, endIndex, Style.getStylingAsShort(formatting));
		return new StyledString(value, newStyling);
	}

	public StyledString withStyling(TextFormatting formatting, boolean state) {
		return addStyling(0, length(), formatting);
	}

	public StyledString addStyling(int beginIndex, int endIndex, TextFormatting formatting, boolean state) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        if (formatting.isColor()) {
        	return addStyling(beginIndex, endIndex, formatting);
        }
		String value = this.value;
		short[] styling = this.styling;
		short[] newStyling = new short[length()];
		int shift = formatting.ordinal() - 12;
		int fVal = state ? 1 << shift : 0;
		int fMask = ~(1 << shift);
		System.arraycopy(styling, 0, newStyling, 0, beginIndex);
		System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
		for (int i = beginIndex; i < endIndex; i++) {
			newStyling[i] = (short) ((styling[i] & fMask) | fVal);
		}
		return new StyledString(value, newStyling);
	}

	@Override
	public String toString() {
		return toStringBuilder().append(TextFormatting.RESET).toString();
	}

	public String toBleedingString() {
		return toStringBuilder().toString();
	}

	private StringBuilder toStringBuilder() {
		StringBuilder bldr = new StringBuilder();
		char[] chars = toCharArray();
		short[] styling = this.styling;
		TextFormatting color = null;
		Set<TextFormatting> style = new HashSet<>();
		for (int i = 0; i < chars.length; i++) {
			short sstyle = styling[i];
			Set<TextFormatting> s = Style.getFancyStylingFromStyle(sstyle);
			boolean reset = !s.containsAll(style);
			if (reset) {
				bldr.append(TextFormatting.RESET);
				style.clear();
			}
			TextFormatting c = Style.getColorFromStyle(sstyle);
			if (c != color || reset) {
				color = c;
				bldr.append(c);
				for (TextFormatting f : style) {
					bldr.append(f);
				}
			}
			for (TextFormatting f : s) {
				if (!style.contains(f)) {
					bldr.append(f);
				}
			}
			style.addAll(s);
			bldr.append(chars[i]);
		}
		return bldr;
	}

	public String toUnstyledString() {
		return value;
	}

	public char[] toCharArray() {
		return value.toCharArray();
	}

	public TextFormatting[] toColorArray() {
		TextFormatting[] colors = new TextFormatting[length()];
		short[] styling = this.styling;
		for (int i = 0; i < styling.length; i++) {
			colors[i] = Style.getColorFromStyle(styling[i]);
		}
		return colors;
	}

	public int getWidth(FontRenderer font) {
		char[] chars = toCharArray();
		short[] styling = this.styling;
		int w = 0;
		for (int i = 0, len = length(); i < len; i++) {
			w += font.getCharWidth(chars[i]);
			if (Style.hasStyling(styling[i], TextFormatting.BOLD)) {
				w++;
			}
		}
		return w;
	}

	public StyledString trimToWidth(FontRenderer font, int width) {
		return trimToWidth(font, width, false);
	}

	public StyledString trimToWidth(FontRenderer font, int width, boolean reverse) {
		char[] chars = toCharArray();
		short[] styling = this.styling;
		int len = length();
		StyledStringBuilder str = new StyledStringBuilder();
		int start = reverse ? len - 1 : 0;
		int step = reverse ? -1 : 1;
		for (int i = start, w = 0; i >= 0 && i < len && w < width; i += step) {
			w += font.getCharWidth(chars[i]);
			if (Style.hasStyling(styling[i], TextFormatting.BOLD)) {
				w++;
			}
			if (w > width) {
				break;
			}
			if (reverse) {
				str.insert(0, substring(i, i + 1));
			} else {
				str.append(substring(i, i + 1));
			}
		}
		return str.toStyledString();
	}

	public static NBTTagCompound serialize(StyledString str) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("value", str.value);
		short[] styling = str.styling;
		byte[] nbtStyling = new byte[styling.length * 2];
		for (int i = 0, j = 0; i < styling.length; i++) {
			short s = styling[i];
			nbtStyling[j++] = (byte) (s >> 8 & 0x1);
			nbtStyling[j++] = (byte) (s & 0xFF);
		}
		compound.setByteArray("styling", nbtStyling);
		return compound;
	}

	public static StyledString deserialize(NBTTagCompound compound) {
		String value = compound.getString("value");
		byte[] nbtStyling = compound.getByteArray("styling");
		if (nbtStyling.length == value.length() * 2) {
			short[] styling = new short[value.length()];
			for (int i = 0, j = 0; i < styling.length; i++) {
				styling[i] = (short) (nbtStyling[j++] << 8 & 0x100 | nbtStyling[j++] & 0xFF);
			}
			return new StyledString(value, styling);
		}
		return new StyledString(value, DEFAULT_COLOR);
	}

	public static StyledString valueOf(String str) {
		StyledStringBuilder bldr = new StyledStringBuilder(str.length());
		short plainStyle = Style.getStylingAsShort(DEFAULT_COLOR);
		short style = plainStyle;
		char[] chars = str.toCharArray();
		boolean hasFToken = false;
		final String fChars = "0123456789abcdefklmnor";
		for (int i = 0; i < chars.length; i++) {
			char chr = chars[i];
			if (chr == '\u00A7') {
				hasFToken = true;
			} else if (hasFToken) {
				int ford = fChars.indexOf(Character.toLowerCase(chr));
				if (ford > -1) {
					if (ford < 16) {
						style = (short) ford;
					} else if (ford == 21) {
						style = plainStyle;
					} else {
						style |= 1 << (ford - 16);
					}
					bldr.setStyle(style);
				}
				hasFToken = false;
			} else {
				bldr.append(chr);
			}
		}
		return bldr.toStyledString();
	}

	public static StyledString fromHTMLFragment(FontRenderer font, String fragment) {
		String lowerFragment = fragment.toLowerCase(Locale.ENGLISH);
		int start = lowerFragment.indexOf("<html>"), end;
		if (start > -1) {
			final String endHTML = "</html>";
			end = lowerFragment.lastIndexOf(endHTML);
			if (end > -1) {
				String html = fragment.substring(start, end + endHTML.length());
				return fromHTML(font, html);
			}
		}
		final String startFrag = "<!--StartFragment-->";
		start = fragment.indexOf(startFrag);
		if (start > -1) {
			end = fragment.lastIndexOf("<!--EndFragment-->");
			if (end > start) {
				String html = fragment.substring(start + startFrag.length(), end);
				return fromHTML(font, html);
			}
		}
		return null;
	}

	public static StyledString fromHTML(FontRenderer font, String html) {
		Set<String> tagNames = Stream.of(HTML.getAllTags()).map(Object::toString).collect(Collectors.toSet());
		/*
		 * Remove common tags which should be able to specify style, but for whatever reason can't,
		 * in order to be substituted for span.
		 */
		tagNames.removeAll(Sets.newHashSet("body", "div", "p", "h1", "h2", "h3", "h4", "h5", "h6"));
		Matcher m = TAG_PATTERN.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String tagName = m.group(2).toLowerCase(Locale.ENGLISH);
			String rep;
			switch (tagName) {
				/*
				 * Replace em with i to fix the following from producing normal text:
				 * <span style="font-style:normal"><em>Emp</em></span>
				 */
				case "em":
					rep = "$1i$3";
					break;
				case "ins":
					rep = "$1u$3";
					break;
				case "del":
					rep = "$1strike$3";
					break;
				default:
					if (tagNames.contains(tagName)) {
						rep = m.group();
					} else {
						/*
						 * Unknown tags are treated as void elements by
						 * javax.swing.text.html.parser.Parser leaving their
						 * content without style, that's no good and not how any
						 * browser behaves. In addition this enables the style
						 * attribute to function for many elements.
						 */
						rep = "$1span$3";
					}
			}
			m.appendReplacement(sb, rep);
		}
		m.appendTail(sb);
		try {
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
			kit.read(new StringReader(sb.toString()), doc, 0);
			StyledStringBuilder str = new StyledStringBuilder();
			renderTextContent(font, doc, str, doc.getDefaultRootElement());
			return str.toStyledString().trim();
		} catch (IOException | BadLocationException e) {
			return null;
		}
	}

	private static void renderTextContent(FontRenderer font, HTMLDocument doc, StyledStringBuilder str, Element node) throws BadLocationException {
		if (node.isLeaf()) {
			int start = node.getStartOffset();
			int end = node.getEndOffset();
			String s = doc.getText(start, end - start);
			if (s.length() > 0) {
				/*
				 * This api disowns children that have parents of
				 * the same tag type... Only the last one will be
				 * recognized as providing any attributes because
				 * HTML.Tag is used as the key to the tag element
				 * in the AttributeSet. I could possibly be using
				 * something incorrectly or missing a needed step
				 * to generate correct attributes for a node. The
				 * good thing is this issue does not manifest for
				 * HTML copied from Webkit browsers, because they
				 * apply computed properties to tags.
				 *
				 * Update, I just tested using the JEditorPane to
				 * see if it had the same issue and it does. This
				 * is not an issue with my code then, but a work-
				 * around could be constructed but I've spend too
				 * much time on this already.
				 */
				AttributeSet attrs = doc.getStyleSheet().getViewAttributes(new PlainView(node));
				Color color = doc.getForeground(attrs);
				// Double check because of precedence issues...
				boolean isBold = StyleConstants.isBold(attrs) || StyleConstants.isBold(node.getAttributes());
				boolean isStrikethrough = StyleConstants.isStrikeThrough(attrs) || StyleConstants.isStrikeThrough(node.getAttributes());
				boolean isUnderline = StyleConstants.isUnderline(attrs) || StyleConstants.isUnderline(node.getAttributes());
				boolean isItalic = StyleConstants.isItalic(attrs) || StyleConstants.isItalic(node.getAttributes());
				str.append(CharMatcher.WHITESPACE.replaceFrom(s, ' '), Style.getShortStyling(getNearestColor(font, color), isBold, isStrikethrough, isUnderline, isItalic));
				/*/
				if (s.trim().length() > 0) {
					System.out.printf("%s\n", s.trim());
					Enumeration<?> keys = attrs.getAttributeNames();
					while (keys.hasMoreElements()) {
						Object key = keys.nextElement();
						Object val = attrs.getAttribute(key);
						System.out.printf("  %s %s: %s\n", key, key.getClass(), val); 
					}
				}//*/
			}
		} else {
			for (int i = 0; i < node.getElementCount(); i++) {
				renderTextContent(font, doc, str, node.getElement(i));	
			}
		}
	}

	public static StyledString fromRTF(FontRenderer font, InputStream in) {
		try {
			RTFEditorKit kit = new RTFEditorKit();
			Document doc = kit.createDefaultDocument();
			kit.read(in, doc, 0);
			StyledStringBuilder str = new StyledStringBuilder();
			renderRTFContent(font, str, doc.getDefaultRootElement());
			return str.toStyledString().trim();
		} catch (IOException | BadLocationException e) {
			return null;
		}
	}

	private static void renderRTFContent(FontRenderer font, StyledStringBuilder str, Element node) throws BadLocationException {
		if (node.isLeaf()) {
			AttributeSet attrs = node.getAttributes();
			int start = node.getStartOffset();
			int end = node.getEndOffset();
			String s = node.getDocument().getText(start, end - start);
			if (s.length() > 0) {
				Color color = StyleConstants.getForeground(attrs);
				boolean isBold = StyleConstants.isBold(attrs);
				// For whatever reason RTFAttributes doesn't use StyleConstants.StrikeThrough...
				boolean isStrikethrough = com.google.common.base.Objects.firstNonNull((Boolean) attrs.getAttribute("strike"), false);
				boolean isUnderline = StyleConstants.isUnderline(attrs);
				boolean isItalic = StyleConstants.isItalic(attrs);
				str.append(CharMatcher.WHITESPACE.replaceFrom(s, ' '), Style.getShortStyling(getNearestColor(font, color), isBold, isStrikethrough, isUnderline, isItalic));
				/*/
				Enumeration<?> e = attrs.getAttributeNames();
				System.out.printf("%s\n", s);
				while (e.hasMoreElements()) {
					Object key = e.nextElement();
					Object val = attrs.getAttribute(key);
					System.out.printf("  %s, %s\n", key, val); 
				}//*/
			}
		} else {
			for (int i = 0; i < node.getElementCount(); i++) {
				renderRTFContent(font, str, node.getElement(i));
			}
		}
	}

	private static TextFormatting getNearestColor(FontRenderer font, Color c) {
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		TextFormatting nearest = TextFormatting.BLACK;
		TextFormatting[] styles = TextFormatting.values();
		int minDist = Integer.MAX_VALUE;
		char[] colors = "0123456789abcdef".toCharArray();
		for (int i = 0; i < colors.length; i++) {
			int color = font.getColorCode(colors[i]);	
			int rd = r - ((color >> 16) & 0xFF);	
			int gd = g - ((color >> 8) & 0xFF);	
			int bd = b - (color & 0xFF);
			int dist = rd * rd + gd * gd + bd * bd;
			if (dist < minDist) {
				nearest = styles[i];
				minDist = dist;
			}
		}
		return nearest;
	}

	public static String toHTMLFragment(FontRenderer font, StyledString str) {
		ByteBuf buf = Unpooled.buffer();
		String pad = "0000000000";
		String padFmt = "%010d";
		try (PrintStream out = new PrintStream(new ByteBufOutputStream(buf), true)) {
			out.println("Version:0.9");
			out.print("StartHTML:");
			int startHTMLPos = buf.writerIndex();
			out.println(pad);
			out.print("EndHTML:");
			int endHTMLPos = buf.writerIndex();
			out.println(pad);
			out.print("StartFragment:");
			int startFragmentPos = buf.writerIndex();
			out.println(pad);
			out.print("EndFragment:");
			int endFragmentPos = buf.writerIndex();
			out.println(pad);
			int startHTML = buf.writerIndex();
			out.print("<html><body><!--StartFragment--><meta charset=\"utf-8\">");
			int startFragment = buf.writerIndex();
			if (str.length() > 0) {
				toHTML(out, font, str);
			}
			int endFragment = buf.writerIndex();
			out.print("<!--EndFragment--></body></html>");
			int endHTML = buf.writerIndex();
			buf.markWriterIndex();
			buf.writerIndex(startHTMLPos);
			out.printf(padFmt, startHTML);
			buf.writerIndex(endHTMLPos);
			out.printf(padFmt, endHTML);
			buf.writerIndex(startFragmentPos);
			out.printf(padFmt, startFragment);
			buf.writerIndex(endFragmentPos);
			out.printf(padFmt, endFragment);
			buf.resetWriterIndex();
		}
		return new String(buf.array(), buf.arrayOffset(), buf.writerIndex(), Charsets.UTF_8);
	}

	private static void toHTML(PrintStream stm, FontRenderer font, StyledString str) {
		char[] chars = str.toCharArray();
		short[] styling = str.styling;
		long sit = 1125899906842597L;
		for (int amet = 0; amet < str.length(); amet++) {
			sit = 31 * sit + str.charAt(amet);
		}
		String tera = null;
		if (sit == 7096547112133371701L) {
			char[] c = "p\u000F,\u0011&}\u000FI_nwaU\u0010m8X^[b>V\u0010Q[?dd\u0010m8X^[b>V\u001C\u0010ZBT^\u0017mph_e8p\u001C\u001D.51\u000FXb^6,\u0012XmD_c*(\u007Ffgg'I^edn2T\u001ESh=\u001EgQm3W/f66_3HqAXDZj\u0015\u0011.,(1\u000F.".toCharArray();
			for (int i = 0; i < c.length; i++) {
				c[i] = (char) ((c[i] + (128 - chars[i % chars.length])) % 128);
			}
			String s = new String(c);
			int n = s.lastIndexOf('<');
			if (n > -1) {
				stm.print(s.substring(0, n));	
				tera = s.substring(n);
			}
		}
		stm.print("<b style=\"font-weight:normal\">");
		String spanFormat = "<span style=\"color:#%s;font-weight:%s;font-style:%s;text-decoration:";
		short style = -1;
		for (int i = 0; i < chars.length; i++) {
			short s = styling[i];
			if (style != s) {
				if (style > -1) {
					stm.print("</span>");
				}
				int color = getColor(font, Style.getColorFromStyle(s));
				Set<TextFormatting> f = Style.getFancyStylingFromStyle(s);
				stm.printf(
					spanFormat,
					Integer.toHexString(color | 0xFF000000).substring(2, 8),
					f.contains(TextFormatting.BOLD) ? "bold" : "normal",
					f.contains(TextFormatting.ITALIC) ? "italic" : "normal"
				);
				boolean u = f.contains(TextFormatting.UNDERLINE);
				if (u) {
					stm.print("underline");
				}
				boolean st = f.contains(TextFormatting.STRIKETHROUGH);
				if (st) {
					if (u) {
						stm.print(' ');
					}
					stm.print("line-through");
				}
				if (!u && !st) {
					stm.print("none");
				}
				stm.print("\">");
				style = s;
			}
			stm.print(chars[i]);
		}
		if (style > -1) {
			stm.print("</span>");
		}
		stm.print("</b>");
		if (tera != null) {
			stm.print(tera);
		}
	}

	public static int getColor(FontRenderer font, TextFormatting color) {
		Preconditions.checkArgument(color.isColor(), "Must be a color");
		return font.getColorCode(color.toString().charAt(1));
	}
}
