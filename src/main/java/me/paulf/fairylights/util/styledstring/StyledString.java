package me.paulf.fairylights.util.styledstring;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class StyledString implements Comparable<StyledString>, CharSequence {
    private final String value;

    private final Style[] styling;

    private int hash;

    public StyledString() {
        this("", new Style());
    }

    public StyledString(final String value, final Style style) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(style, "style");
        this.value = value;
        this.styling = new Style[value.length()];
        Arrays.fill(this.styling, style);
    }

    public StyledString(final String value, final Style[] styling) {
        this.value = value;
        this.styling = Objects.requireNonNull(styling, "styling");
    }

    @Override
    public int length() {
        return this.value.length();
    }

    public boolean isEmpty() {
        return this.value.length() == 0;
    }

    @Override
    public char charAt(final int index) {
        return this.value.charAt(index);
    }

    public Style styleAt(final int index) {
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

    public Style[] getStyling() {
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
        final Style[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final Style[] st2 = anotherString.styling;
        int k = 0;
        while (k < lim) {
            final char c1 = v1[k];
            final char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            final Style s1 = st1[k];
            final Style s2 = st2[k];
            final int c = s1.compareTo(s2);
            if (c != 0) {
                return c;
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
        final Style[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final Style[] st2 = anotherString.styling;
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
            final Style s1 = st1[k];
            final Style s2 = st2[k];
            final int c = s1.compareTo(s2);
            if (c != 0) {
                return c;
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
        final Style[] s1 = this.styling;
        int to = toffset;
        final char[] pa = prefix.toCharArray();
        final Style[] s2 = prefix.styling;
        int po = 0;
        int pc = prefix.value.length();
        if (toffset < 0 || toffset > this.value.length() - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to] != pa[po] || !s1[to++].equals(s2[po++])) {
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

    public int indexOf(final char chr, final Style style) {
        return this.indexOf(chr, 0, style);
    }

    public int indexOf(final char chr, int fromIndex, final Style style) {
        final int max = this.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            return -1;
        }
        final char[] value = this.toCharArray();
        for (int i = fromIndex; i < max; i++) {
            if (value[i] == chr && this.styling[i].equals(style)) {
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

    public int lastIndexOf(final char chr, final Style style) {
        return this.lastIndexOf(chr, this.length() - 1, style);
    }

    public int lastIndexOf(final char chr, final int fromIndex, final Style style) {
        final char[] value = this.toCharArray();
        int i = Math.min(fromIndex, this.length() - 1);
        for (; i >= 0; i--) {
            if (value[i] == chr && this.styling[i].equals(style)) {
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
        final Style[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final Style[] targetStyling = str.styling;
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
        final Style firstStyling = targetStyling[0];
        final int max = fromIndex + sourceCount - targetCount;
        for (int i = fromIndex + fromIndex; i <= max; i++) {
            if (source[i] != first) {
                while (++i <= max && (source[i] != first || !sourceStyling[i].equals(firstStyling))) ;
            }
            if (i <= max) {
                int j = i + 1;
                final int end = j + targetCount - 1;
                for (int k = 1; j < end && source[j] == target[k] && sourceStyling[j].equals(targetStyling[k]); j++, k++)
                    ;
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
        final Style[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final Style[] targetStyling = str.styling;
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
        final Style strLastStyling = targetStyling[strLastIndex];
        final int min = fromIndex + targetCount - 1;
        int i = min + fromIndex;
                startSearchForLastChar:
        while (true) {
            while (i >= min && (source[i] != strLastChar || !sourceStyling[i].equals(strLastStyling))) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            final int start = j - (targetCount - 1);
            int k = strLastIndex - 1;
            while (j > start) {
                final boolean isDifferent = source[j] != target[k] || !sourceStyling[j].equals(targetStyling[k]);
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
        final Style[] styling = new Style[endIndex - beginIndex];
        System.arraycopy(this.styling, beginIndex, styling, 0, styling.length);
        return new StyledString(value, styling);
    }

    @Override
    public StyledString subSequence(final int start, final int end) {
        return this.substring(start, end);
    }

    public StyledString concat(final StyledString str) {
        if (str.value.length() == 0) {
            return this;
        }
        final String value = this.value.concat(str.value);
        final Style[] styling = new Style[this.styling.length + str.styling.length];
        System.arraycopy(this.styling, 0, styling, 0, this.styling.length);
        System.arraycopy(str.styling, 0, styling, this.styling.length, str.styling.length);
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
        final String val = this.value;
        int len = val.length();
        int st = 0;
        while (st < len && val.charAt(st) <= ' ') {
            st++;
        }
        while (st < len && val.charAt(len - 1) <= ' ') {
            len--;
        }
        return st > 0 || len < this.value.length() ? this.substring(st, len) : this;
    }

    public StyledString withStyling(final Style style) {
        return this.withStyling(0, this.length(), style);
    }

    public StyledString withStyling(final int beginIndex, final int endIndex, final Style style) {
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
        final Style[] styling = this.styling;
        final Style[] newStyling = new Style[styling.length];
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        Arrays.fill(newStyling, beginIndex, endIndex, style);
        return new StyledString(value, newStyling);
    }

    public StyledString withStyling(final TextFormatting formatting, final boolean state) {
        return this.withStyling(0, this.length(), formatting, state);
    }

    public StyledString withStyling(final int beginIndex, final int endIndex, final TextFormatting formatting, final boolean state) {
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
        final Style[] styling = this.styling;
        final Style[] newStyling = new Style[styling.length];
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        for (int i = beginIndex; i < endIndex; i++) {
            newStyling[i] = styling[i].withStyling(formatting, state);
        }
        return new StyledString(value, newStyling);
    }

    @Override
    public String toString() {
        return this.toTextComponent().getFormattedText();
    }

    public ITextComponent toTextComponent() {
        final String value = this.value;
        if (value.length() == 0) {
            return new StringTextComponent("");
        }
        ITextComponent text = null;
        final Style[] styling = this.styling;
        final StringBuilder bob = new StringBuilder();
        Style currentStyle = styling[0];
        for (int i = 0; ; ) {
            final Style style = i < value.length() ? styling[i] : null;
            if (!currentStyle.equals(style)) {
                final ITextComponent t = new StringTextComponent(bob.toString());
                t.getStyle().setColor(currentStyle.getColor());
                if (currentStyle.isObfuscated()) t.getStyle().setObfuscated(true);
                if (currentStyle.isBold()) t.getStyle().setBold(true);
                if (currentStyle.isStrikethrough()) t.getStyle().setStrikethrough(true);
                if (currentStyle.isUnderline()) t.getStyle().setUnderlined(true);
                if (currentStyle.isItalic()) t.getStyle().setItalic(true);
                if (text == null) {
                    text = t;
                } else {
                    text.appendSibling(t);
                }
                bob.setLength(0);
            }
            currentStyle = style;
            if (i < value.length()) {
                bob.appendCodePoint(value.charAt(i++));
            } else {
                break;
            }
        }
        return text;
    }

    public String toUnstyledString() {
        return this.value;
    }

    public char[] toCharArray() {
        return this.value.toCharArray();
    }

    public static CompoundNBT serialize(final StyledString str) {
        final CompoundNBT compound = new CompoundNBT();
        compound.putString("value", ITextComponent.Serializer.toJson(str.toTextComponent()));
        return compound;
    }

    public static StyledString deserialize(final CompoundNBT compound) {
        final ITextComponent text;
        try {
            text = ITextComponent.Serializer.fromJson(compound.getString("value"));
        } catch (final JsonParseException e) {
            return new StyledString(compound.getString("value"), new Style());
        }
        if (text == null) {
            return new StyledString();
        }
        return fromTextComponent(text);
    }

    public static StyledString fromTextComponent(final ITextComponent component) {
        return valueOf(component.getFormattedText());
    }

    public static StyledString valueOf(final String str) {
        final StyledStringBuilder bldr = new StyledStringBuilder(str.length());
        final Style plainStyle = new Style();
        Style style = plainStyle;
        final char[] chars = str.toCharArray();
        boolean hasFToken = false;
        for (final char chr : chars) {
            if (chr == '\u00A7') {
                hasFToken = true;
            } else if (hasFToken) {
                final char ch = Character.toLowerCase(chr);
                final int colorIndex = "0123456789abcdef".indexOf(ch);
                if (colorIndex != -1) {
                    final TextFormatting color = TextFormatting.fromColorIndex(colorIndex);
                    if (color != null) {
                        style = style.withColor(color);
                    }
                } else if (ch == 'k') {
                    style = style.withObfuscated(true);
                } else if (ch == 'l') {
                    style = style.withBold(true);
                } else if (ch == 'm') {
                    style = style.withStrikethrough(true);
                } else if (ch == 'n') {
                    style = style.withUnderline(true);
                } else if (ch == 'o') {
                    style = style.withItalic(true);
                } else if (ch == 'r') {
                    style = plainStyle;
                } else {
                    bldr.append(chr);
                }
                bldr.setStyle(style);
                hasFToken = false;
            } else {
                bldr.append(chr);
            }
        }
        return bldr.toStyledString();
    }

    public static int getColor(final TextFormatting color) {
        final Integer rgb = color.getColor();
        Preconditions.checkNotNull(rgb, "Must be a color");
        return rgb;
    }
}
