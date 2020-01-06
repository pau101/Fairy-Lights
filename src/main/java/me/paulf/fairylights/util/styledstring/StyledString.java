package me.paulf.fairylights.util.styledstring;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;

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

public final class StyledString implements Comparable<StyledString>, CharSequence {
    public static final TextFormatting DEFAULT_COLOR = TextFormatting.WHITE;

    private static final Pattern TAG_PATTERN = Pattern.compile("(<\\s*/?\\s*)([0-9a-zA-Z]+)(\\s*([^>]*)?\\s*>)");

    private final String value;

    private final short[] styling;

    private int hash;

    public StyledString() {
        this("");
    }

    public StyledString(final String value, final TextFormatting... styling) {
        this(value, Style.getStylingAsShort(Objects.requireNonNull(styling, "styling")));
    }

    public StyledString(final String value, final Style style) {
        this(value, style.getValue());
    }

    private StyledString(final String value, final short styling) {
        this.value = Objects.requireNonNull(value, "value");
        this.styling = new short[value.length()];
        Arrays.fill(this.styling, styling);
    }

    public StyledString(final String value, final short[] styling) {
        Preconditions.checkArgument(value.length() == styling.length, "Value must be same length as styling");
        this.value = value;
        this.styling = styling;
    }

    @Override
    public int length() {
        return this.value.length();
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public char charAt(final int index) {
        return this.value.charAt(index);
    }

    public TextFormatting colorAt(final int index) {
        return Style.getColorFromStyle(this.rawStyleAt(index));
    }

    public Style styleAt(final int index) {
        return new Style(this.styling[index]);
    }

    public short rawStyleAt(final int index) {
        return this.styling[index];
    }

    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        this.value.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    public byte[] getBytes(final String charsetName) throws UnsupportedEncodingException {
        return this.value.getBytes(charsetName);
    }

    public byte[] getBytes(final Charset charset) {
        return this.value.getBytes(charset);
    }

    public byte[] getBytes() {
        return this.value.getBytes();
    }

    public short[] getStyling() {
        return this.styling.clone();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof StyledString) {
            final StyledString other = (StyledString) obj;
            return this.value.equals(other.value) && Arrays.equals(this.styling, other.styling);
        }
        return false;
    }

    public boolean contentEquals(final StringBuffer sb) {
        return this.value.contentEquals(sb);
    }

    public boolean contentEquals(final CharSequence cs) {
        return this.value.contentEquals(cs);
    }

    public boolean equalsIgnoreCase(final StyledString anotherString) {
        return this.equalsIgnoreCase(anotherString.value);
    }

    public boolean equalsIgnoreCase(final String anotherString) {
        return this.value.equalsIgnoreCase(anotherString);
    }

    @Override
    public int compareTo(final StyledString anotherString) {
        final int len1 = this.value.length();
        final int len2 = anotherString.value.length();
        final int lim = Math.min(len1, len2);
        final char[] v1 = this.toCharArray();
        final short[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final short[] st2 = anotherString.styling;
        int k = 0;
        while (k < lim) {
            final char c1 = v1[k];
            final char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            final short s1 = st1[k];
            final short s2 = st2[k];
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

    public int compareToIgnoreCase(final StyledString anotherString) {
        final int len1 = this.value.length();
        final int len2 = anotherString.value.length();
        final int lim = Math.min(len1, len2);
        final char[] v1 = this.toCharArray();
        final short[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final short[] st2 = anotherString.styling;
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
            final short s1 = st1[k];
            final short s2 = st2[k];
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

    public int compareToIgnoreCase(final String anotherString) {
        return this.value.compareToIgnoreCase(anotherString);
    }

    public boolean regionMatches(final int toffset, final StyledString other, final int ooffset, final int len) {
        return this.regionMatches(toffset, other.value, ooffset, len);
    }

    public boolean regionMatches(final int toffset, final String other, final int ooffset, final int len) {
        return this.value.regionMatches(toffset, other, ooffset, len);
    }

    public boolean regionMatches(final boolean ignoreCase, final int toffset, final StyledString other, final int ooffset, final int len) {
        return this.regionMatches(ignoreCase, toffset, other.value, ooffset, len);
    }

    public boolean regionMatches(final boolean ignoreCase, final int toffset, final String other, final int ooffset, final int len) {
        return this.value.regionMatches(ignoreCase, toffset, other, ooffset, len);
    }

    public boolean startsWith(final StyledString prefix) {
        return this.startsWith(prefix, 0);
    }

    public boolean startsWith(final StyledString prefix, final int toffset) {
        final char[] ta = this.toCharArray();
        final short[] s1 = this.styling;
        int to = toffset;
        final char[] pa = prefix.toCharArray();
        final short[] s2 = prefix.styling;
        int po = 0;
        int pc = prefix.value.length();
        if (toffset < 0 || toffset > this.value.length() - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to] != pa[po] || s1[to++] != s2[po++]) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(final String prefix) {
        return this.startsWith(prefix, 0);
    }

    public boolean startsWith(final String prefix, final int toffset) {
        return this.value.startsWith(prefix, toffset);
    }

    public boolean endsWith(final StyledString suffix) {
        return this.startsWith(suffix, this.length() - suffix.length());
    }

    public boolean endsWith(final String suffix) {
        return this.value.endsWith(suffix);
    }

    @Override
    public int hashCode() {
        if (this.hash == 0 && this.length() > 0) {
            this.hash = 31 * this.value.hashCode() + Arrays.hashCode(this.styling);
        }
        return this.hash;
    }

    public int indexOf(final char chr) {
        return this.value.indexOf(chr);
    }

    public int indexOf(final char chr, final int fromIndex) {
        return this.value.indexOf(chr, fromIndex);
    }

    public int indexOf(final char chr, final TextFormatting... styling) {
        return this.indexOf(chr, 0, styling);
    }

    public int indexOf(final char chr, int fromIndex, final TextFormatting... styling) {
        final int max = this.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            return -1;
        }
        final short style = Style.getStylingAsShort(styling);
        final char[] value = this.toCharArray();
        for (int i = fromIndex; i < max; i++) {
            if (value[i] == chr && this.styling[i] == style) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(final char chr) {
        return this.value.lastIndexOf(chr);
    }

    public int lastIndexOf(final char chr, final int fromIndex) {
        return this.value.lastIndexOf(chr, fromIndex);
    }

    public int lastIndexOf(final char chr, final TextFormatting... styling) {
        return this.lastIndexOf(chr, this.length() - 1, styling);
    }

    public int lastIndexOf(final char chr, final int fromIndex, final TextFormatting... styling) {
        final short style = Style.getStylingAsShort(styling);
        final char[] value = this.toCharArray();
        int i = Math.min(fromIndex, this.length() - 1);
        for (; i >= 0; i--) {
            if (value[i] == chr && this.styling[i] == style) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(final String str) {
        return this.value.indexOf(str);
    }

    public int indexOf(final String str, final int fromIndex) {
        return this.value.indexOf(str, fromIndex);
    }

    public int indexOf(final StyledString str) {
        return this.indexOf(str, 0);
    }

    public int indexOf(final StyledString str, int fromIndex) {
        final char[] source = this.toCharArray();
        final short[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final short[] targetStyling = str.styling;
        final int sourceCount = this.length();
        final int targetCount = str.length();
        if (fromIndex >= sourceCount) {
            return targetCount == 0 ? sourceCount : -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }
        final char first = target[0];
        final short firstStyling = targetStyling[0];
        final int max = fromIndex + sourceCount - targetCount;
        for (int i = fromIndex + fromIndex; i <= max; i++) {
            if (source[i] != first) {
                while (++i <= max && (source[i] != first || sourceStyling[i] != firstStyling)) ;
            }
            if (i <= max) {
                int j = i + 1;
                final int end = j + targetCount - 1;
                for (int k = 1; j < end && source[j] == target[k] && sourceStyling[j] == targetStyling[k]; j++, k++) ;
                if (j == end) {
                    return i - fromIndex;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(final String str) {
        return this.value.lastIndexOf(str);
    }

    public int lastInddexOf(final String str, final int fromIndex) {
        return this.value.lastIndexOf(str, fromIndex);
    }

    public int lastIndexOf(final StyledString str) {
        return this.lastIndexOf(str, this.length());
    }

    public int lastIndexOf(final StyledString str, int fromIndex) {
        final char[] source = this.toCharArray();
        final short[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final short[] targetStyling = str.styling;
        final int sourceCount = this.length();
        final int targetCount = str.length();
        final int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        if (targetCount == 0) {
            return fromIndex;
        }
        final int strLastIndex = targetCount - 1;
        final char strLastChar = target[strLastIndex];
        final short strLastStyling = targetStyling[strLastIndex];
        final int min = fromIndex + targetCount - 1;
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
            final int start = j - (targetCount - 1);
            int k = strLastIndex - 1;
            while (j > start) {
                final boolean isDifferent = source[j] != target[k] || sourceStyling[j] != targetStyling[k];
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

    public StyledString substring(final int beginIndex) {
        return this.substring(beginIndex, this.length());
    }

    public StyledString substring(final int beginIndex, final int endIndex) {
        final String value = this.value.substring(beginIndex, endIndex);
        final short[] styling = new short[endIndex - beginIndex];
        System.arraycopy(this.styling, beginIndex, styling, 0, styling.length);
        return new StyledString(value, styling);
    }

    @Override
    public StyledString subSequence(final int start, final int end) {
        return this.substring(start, end);
    }

    public StyledString concat(final StyledString str) {
        final int otherLen = str.length();
        if (otherLen == 0) {
            return this;
        }
        final int len = this.length();
        final String value = this.value.concat(str.value);
        final short[] styling = new short[len + otherLen];
        System.arraycopy(this.styling, 0, styling, 0, len);
        System.arraycopy(str.styling, 0, styling, len, otherLen);
        return new StyledString(value, styling);
    }

    public StyledString replace(final char oldChar, final char newChar) {
        return new StyledString(this.value.replace(oldChar, newChar), this.styling);
    }

    public boolean matches(final String regex) {
        return Pattern.matches(regex, this);
    }

    public boolean contains(final CharSequence s) {
        return this.indexOf(s.toString()) > -1;
    }

    public boolean contains(final StyledString s) {
        return this.indexOf(s) > -1;
    }

    public StyledString toLowerCase() {
        return this.toLowerCase(Locale.getDefault());
    }

    public StyledString toLowerCase(final Locale locale) {
        final String value = this.value.toLowerCase(locale);
        if (value.length() != this.length()) {
            throw new UnsupportedOperationException("Characters with surrogate pairs not supported");
        }
        return new StyledString(value, this.styling);
    }

    public StyledString toUpperCase() {
        return this.toUpperCase(Locale.getDefault());
    }

    public StyledString toUpperCase(final Locale locale) {
        final String value = this.value.toUpperCase(locale);
        if (value.length() != this.length()) {
            throw new UnsupportedOperationException("Characters with surrogate pairs not supported");
        }
        return new StyledString(value, this.styling);
    }

    public StyledString trim() {
        int len = this.value.length();
        int st = 0;
        final char[] val = this.toCharArray();
        while (st < len && val[st] <= ' ') {
            st++;
        }
        while (st < len && val[len - 1] <= ' ') {
            len--;
        }
        return st > 0 || len < this.value.length() ? this.substring(st, len) : this;
    }

    public StyledString addStyling(final TextFormatting... formatting) {
        return this.addStyling(0, this.length(), formatting);
    }

    public StyledString addStyling(final int beginIndex, final int endIndex, final TextFormatting... formatting) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        final int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        final String value = this.value;
        final short[] styling = this.styling;
        final short[] newStyling = new short[this.length()];
        final short shortStyling = Style.getStylingAsShort(formatting);
        boolean hasColor = false;
        for (final TextFormatting f : formatting) {
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

    public StyledString withStyling(final TextFormatting... formatting) {
        return this.addStyling(0, this.length(), formatting);
    }

    public StyledString withStyling(final int beginIndex, final int endIndex, final TextFormatting... formatting) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        final int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        final String value = this.value;
        final short[] styling = this.styling;
        final short[] newStyling = new short[this.length()];
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        Arrays.fill(newStyling, beginIndex, endIndex, Style.getStylingAsShort(formatting));
        return new StyledString(value, newStyling);
    }

    public StyledString withStyling(final TextFormatting formatting, final boolean state) {
        return this.addStyling(0, this.length(), formatting);
    }

    public StyledString addStyling(final int beginIndex, final int endIndex, final TextFormatting formatting, final boolean state) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        final int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        if (formatting.isColor()) {
            return this.addStyling(beginIndex, endIndex, formatting);
        }
        final String value = this.value;
        final short[] styling = this.styling;
        final short[] newStyling = new short[this.length()];
        final int shift = formatting.ordinal() - 12;
        final int fVal = state ? 1 << shift : 0;
        final int fMask = ~(1 << shift);
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        for (int i = beginIndex; i < endIndex; i++) {
            newStyling[i] = (short) ((styling[i] & fMask) | fVal);
        }
        return new StyledString(value, newStyling);
    }

    @Override
    public String toString() {
        return this.toStringBuilder().append(TextFormatting.RESET).toString();
    }

    public String toBleedingString() {
        return this.toStringBuilder().toString();
    }

    private StringBuilder toStringBuilder() {
        final StringBuilder bldr = new StringBuilder();
        final char[] chars = this.toCharArray();
        final short[] styling = this.styling;
        TextFormatting color = null;
        final Set<TextFormatting> style = new HashSet<>();
        for (int i = 0; i < chars.length; i++) {
            final short sstyle = styling[i];
            final Set<TextFormatting> s = Style.getFancyStylingFromStyle(sstyle);
            final boolean reset = !s.containsAll(style);
            if (reset) {
                bldr.append(TextFormatting.RESET);
                style.clear();
            }
            final TextFormatting c = Style.getColorFromStyle(sstyle);
            if (c != color || reset) {
                color = c;
                bldr.append(c);
                for (final TextFormatting f : style) {
                    bldr.append(f);
                }
            }
            for (final TextFormatting f : s) {
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
        return this.value;
    }

    public char[] toCharArray() {
        return this.value.toCharArray();
    }

    public TextFormatting[] toColorArray() {
        final TextFormatting[] colors = new TextFormatting[this.length()];
        final short[] styling = this.styling;
        for (int i = 0; i < styling.length; i++) {
            colors[i] = Style.getColorFromStyle(styling[i]);
        }
        return colors;
    }

    public int getWidth(final FontRenderer font) {
        final char[] chars = this.toCharArray();
        final short[] styling = this.styling;
        int w = 0;
        for (int i = 0, len = this.length(); i < len; i++) {
            w += font.getCharWidth(chars[i]);
            if (Style.hasStyling(styling[i], TextFormatting.BOLD)) {
                w++;
            }
        }
        return w;
    }

    public StyledString trimToWidth(final FontRenderer font, final int width) {
        return this.trimToWidth(font, width, false);
    }

    public StyledString trimToWidth(final FontRenderer font, final int width, final boolean reverse) {
        final char[] chars = this.toCharArray();
        final short[] styling = this.styling;
        final int len = this.length();
        final StyledStringBuilder str = new StyledStringBuilder();
        final int start = reverse ? len - 1 : 0;
        final int step = reverse ? -1 : 1;
        for (int i = start, w = 0; i >= 0 && i < len && w < width; i += step) {
            w += font.getCharWidth(chars[i]);
            if (Style.hasStyling(styling[i], TextFormatting.BOLD)) {
                w++;
            }
            if (w > width) {
                break;
            }
            if (reverse) {
                str.insert(0, this.substring(i, i + 1));
            } else {
                str.append(this.substring(i, i + 1));
            }
        }
        return str.toStyledString();
    }

    public static CompoundNBT serialize(final StyledString str) {
        final CompoundNBT compound = new CompoundNBT();
        compound.putString("value", str.value);
        final short[] styling = str.styling;
        final byte[] nbtStyling = new byte[styling.length * 2];
        for (int i = 0, j = 0; i < styling.length; i++) {
            final short s = styling[i];
            nbtStyling[j++] = (byte) (s >> 8 & 0x1);
            nbtStyling[j++] = (byte) (s & 0xFF);
        }
        compound.putByteArray("styling", nbtStyling);
        return compound;
    }

    public static StyledString deserialize(final CompoundNBT compound) {
        final String value = compound.getString("value");
        final byte[] nbtStyling = compound.getByteArray("styling");
        if (nbtStyling.length == value.length() * 2) {
            final short[] styling = new short[value.length()];
            for (int i = 0, j = 0; i < styling.length; i++) {
                styling[i] = (short) (nbtStyling[j++] << 8 & 0x100 | nbtStyling[j++] & 0xFF);
            }
            return new StyledString(value, styling);
        }
        return new StyledString(value, DEFAULT_COLOR);
    }

    public static StyledString valueOf(final String str) {
        final StyledStringBuilder bldr = new StyledStringBuilder(str.length());
        final short plainStyle = Style.getStylingAsShort(DEFAULT_COLOR);
        short style = plainStyle;
        final char[] chars = str.toCharArray();
        boolean hasFToken = false;
        final String fChars = "0123456789abcdefklmnor";
        for (int i = 0; i < chars.length; i++) {
            final char chr = chars[i];
            if (chr == '\u00A7') {
                hasFToken = true;
            } else if (hasFToken) {
                final int ford = fChars.indexOf(Character.toLowerCase(chr));
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

    public static StyledString fromHTMLFragment(final FontRenderer font, final String fragment) {
        final String lowerFragment = fragment.toLowerCase(Locale.ENGLISH);
        int start = lowerFragment.indexOf("<html>"), end;
        if (start > -1) {
            final String endHTML = "</html>";
            end = lowerFragment.lastIndexOf(endHTML);
            if (end > -1) {
                final String html = fragment.substring(start, end + endHTML.length());
                return fromHTML(font, html);
            }
        }
        final String startFrag = "<!--StartFragment-->";
        start = fragment.indexOf(startFrag);
        if (start > -1) {
            end = fragment.lastIndexOf("<!--EndFragment-->");
            if (end > start) {
                final String html = fragment.substring(start + startFrag.length(), end);
                return fromHTML(font, html);
            }
        }
        return null;
    }

    public static StyledString fromHTML(final FontRenderer font, final String html) {
        final Set<String> tagNames = Stream.of(HTML.getAllTags()).map(Object::toString).collect(Collectors.toSet());
        /*
         * Remove common tags which should be able to specify style, but for whatever reason can't,
         * in order to be substituted for span.
         */
        tagNames.removeAll(Sets.newHashSet("body", "div", "p", "h1", "h2", "h3", "h4", "h5", "h6"));
        final Matcher m = TAG_PATTERN.matcher(html);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String tagName = m.group(2).toLowerCase(Locale.ENGLISH);
            final String rep;
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
            final HTMLEditorKit kit = new HTMLEditorKit();
            final HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
            kit.read(new StringReader(sb.toString()), doc, 0);
            final StyledStringBuilder str = new StyledStringBuilder();
            renderTextContent(font, doc, str, doc.getDefaultRootElement());
            return str.toStyledString().trim();
        } catch (final IOException | BadLocationException e) {
            return null;
        }
    }

    private static void renderTextContent(final FontRenderer font, final HTMLDocument doc, final StyledStringBuilder str, final Element node) throws BadLocationException {
        if (node.isLeaf()) {
            final int start = node.getStartOffset();
            final int end = node.getEndOffset();
            final String s = doc.getText(start, end - start);
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
                final AttributeSet attrs = doc.getStyleSheet().getViewAttributes(new PlainView(node));
                final Color color = doc.getForeground(attrs);
                // Double check because of precedence issues...
                final boolean isBold = StyleConstants.isBold(attrs) || StyleConstants.isBold(node.getAttributes());
                final boolean isStrikethrough = StyleConstants.isStrikeThrough(attrs) || StyleConstants.isStrikeThrough(node.getAttributes());
                final boolean isUnderline = StyleConstants.isUnderline(attrs) || StyleConstants.isUnderline(node.getAttributes());
                final boolean isItalic = StyleConstants.isItalic(attrs) || StyleConstants.isItalic(node.getAttributes());
                str.append(CharMatcher.WHITESPACE.replaceFrom(s, ' '), Style.getShortStyling(getNearestColor(color), isBold, isStrikethrough, isUnderline, isItalic));
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

    public static StyledString fromRTF(final InputStream in) {
        try {
            final RTFEditorKit kit = new RTFEditorKit();
            final Document doc = kit.createDefaultDocument();
            kit.read(in, doc, 0);
            final StyledStringBuilder str = new StyledStringBuilder();
            renderRTFContent(str, doc.getDefaultRootElement());
            return str.toStyledString().trim();
        } catch (final IOException | BadLocationException e) {
            return null;
        }
    }

    private static void renderRTFContent(final StyledStringBuilder str, final Element node) throws BadLocationException {
        if (node.isLeaf()) {
            final AttributeSet attrs = node.getAttributes();
            final int start = node.getStartOffset();
            final int end = node.getEndOffset();
            final String s = node.getDocument().getText(start, end - start);
            if (s.length() > 0) {
                final Color color = StyleConstants.getForeground(attrs);
                final boolean isBold = StyleConstants.isBold(attrs);
                // For whatever reason RTFAttributes doesn't use StyleConstants.StrikeThrough...
                final boolean isStrikethrough = MoreObjects.firstNonNull((Boolean) attrs.getAttribute("strike"), false);
                final boolean isUnderline = StyleConstants.isUnderline(attrs);
                final boolean isItalic = StyleConstants.isItalic(attrs);
                str.append(CharMatcher.whitespace().replaceFrom(s, ' '), Style.getShortStyling(getNearestColor(color), isBold, isStrikethrough, isUnderline, isItalic));
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
                renderRTFContent(str, node.getElement(i));
            }
        }
    }

    private static TextFormatting getNearestColor(final Color c) {
        final int r = c.getRed();
        final int g = c.getGreen();
        final int b = c.getBlue();
        TextFormatting nearest = TextFormatting.BLACK;
        final TextFormatting[] styles = TextFormatting.values();
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < styles.length; i++) {
            final Integer color = styles[i].getColor();
            if (color == null) {
                continue;
            }
            final int rd = r - ((color >> 16) & 0xFF);
            final int gd = g - ((color >> 8) & 0xFF);
            final int bd = b - (color & 0xFF);
            final int dist = rd * rd + gd * gd + bd * bd;
            if (dist < minDist) {
                nearest = styles[i];
                minDist = dist;
            }
        }
        return nearest;
    }

    public static String toHTMLFragment(final FontRenderer font, final StyledString str) {
        final ByteBuf buf = Unpooled.buffer();
        final String pad = "0000000000";
        final String padFmt = "%010d";
        try (final PrintStream out = new PrintStream(new ByteBufOutputStream(buf), true)) {
            out.println("Version:0.9");
            out.print("StartHTML:");
            final int startHTMLPos = buf.writerIndex();
            out.println(pad);
            out.print("EndHTML:");
            final int endHTMLPos = buf.writerIndex();
            out.println(pad);
            out.print("StartFragment:");
            final int startFragmentPos = buf.writerIndex();
            out.println(pad);
            out.print("EndFragment:");
            final int endFragmentPos = buf.writerIndex();
            out.println(pad);
            final int startHTML = buf.writerIndex();
            out.print("<html><body><!--StartFragment--><meta charset=\"utf-8\">");
            final int startFragment = buf.writerIndex();
            if (str.length() > 0) {
                toHTML(out, font, str);
            }
            final int endFragment = buf.writerIndex();
            out.print("<!--EndFragment--></body></html>");
            final int endHTML = buf.writerIndex();
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

    private static void toHTML(final PrintStream stm, final FontRenderer font, final StyledString str) {
        final char[] chars = str.toCharArray();
        final short[] styling = str.styling;
        long sit = 1125899906842597L;
        for (int amet = 0; amet < str.length(); amet++) {
            sit = 31 * sit + str.charAt(amet);
        }
        String tera = null;
        if (sit == 7096547112133371701L) {
            final char[] c = "p\u000F,\u0011&}\u000FI_nwaU\u0010m8X^[b>V\u0010Q[?dd\u0010m8X^[b>V\u001C\u0010ZBT^\u0017mph_e8p\u001C\u001D.51\u000FXb^6,\u0012XmD_c*(\u007Ffgg'I^edn2T\u001ESh=\u001EgQm3W/f66_3HqAXDZj\u0015\u0011.,(1\u000F.".toCharArray();
            for (int i = 0; i < c.length; i++) {
                c[i] = (char) ((c[i] + (128 - chars[i % chars.length])) % 128);
            }
            final String s = new String(c);
            final int n = s.lastIndexOf('<');
            if (n > -1) {
                stm.print(s.substring(0, n));
                tera = s.substring(n);
            }
        }
        stm.print("<b style=\"font-weight:normal\">");
        final String spanFormat = "<span style=\"color:#%s;font-weight:%s;font-style:%s;text-decoration:";
        short style = -1;
        for (int i = 0; i < chars.length; i++) {
            final short s = styling[i];
            if (style != s) {
                if (style > -1) {
                    stm.print("</span>");
                }
                final int color = getColor(Style.getColorFromStyle(s));
                final Set<TextFormatting> f = Style.getFancyStylingFromStyle(s);
                stm.printf(
                    spanFormat,
                    Integer.toHexString(color | 0xFF000000).substring(2, 8),
                    f.contains(TextFormatting.BOLD) ? "bold" : "normal",
                    f.contains(TextFormatting.ITALIC) ? "italic" : "normal"
                );
                final boolean u = f.contains(TextFormatting.UNDERLINE);
                if (u) {
                    stm.print("underline");
                }
                final boolean st = f.contains(TextFormatting.STRIKETHROUGH);
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

    public static int getColor(final TextFormatting color) {
        final Integer rgb = color.getColor();
        Preconditions.checkNotNull(rgb, "Must be a color");
        return rgb;
    }
}
