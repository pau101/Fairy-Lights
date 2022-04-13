package me.paulf.fairylights.util.styledstring;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public final class StyledString implements Comparable<StyledString>, CharSequence {
    private final String value;

    private final FLStyle[] styling;

    private int hash;

    public StyledString() {
        this("", new FLStyle());
    }

    public StyledString(final String value, final FLStyle style) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(style, "style");
        this.value = value;
        this.styling = new FLStyle[value.length()];
        Arrays.fill(this.styling, style);
    }

    public StyledString(final String value, final FLStyle[] styling) {
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

    public FLStyle styleAt(final int index) {
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

    public FLStyle[] getStyling() {
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
        final FLStyle[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final FLStyle[] st2 = anotherString.styling;
        int k = 0;
        while (k < lim) {
            final char c1 = v1[k];
            final char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            final FLStyle s1 = st1[k];
            final FLStyle s2 = st2[k];
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
        final FLStyle[] st1 = this.styling;
        final char[] v2 = anotherString.toCharArray();
        final FLStyle[] st2 = anotherString.styling;
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
            final FLStyle s1 = st1[k];
            final FLStyle s2 = st2[k];
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
        final FLStyle[] s1 = this.styling;
        int to = toffset;
        final char[] pa = prefix.toCharArray();
        final FLStyle[] s2 = prefix.styling;
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

    public int indexOf(final char chr, final FLStyle style) {
        return this.indexOf(chr, 0, style);
    }

    public int indexOf(final char chr, int fromIndex, final FLStyle style) {
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

    public int lastIndexOf(final char chr, final FLStyle style) {
        return this.lastIndexOf(chr, this.length() - 1, style);
    }

    public int lastIndexOf(final char chr, final int fromIndex, final FLStyle style) {
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
        final FLStyle[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final FLStyle[] targetStyling = str.styling;
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
        final FLStyle firstStyling = targetStyling[0];
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
        final FLStyle[] sourceStyling = this.styling;
        final char[] target = str.toCharArray();
        final FLStyle[] targetStyling = str.styling;
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
        final FLStyle strLastStyling = targetStyling[strLastIndex];
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
        final FLStyle[] styling = new FLStyle[endIndex - beginIndex];
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
        final FLStyle[] styling = new FLStyle[this.styling.length + str.styling.length];
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

    public StyledString withStyling(final FLStyle style) {
        return this.withStyling(0, this.length(), style);
    }

    public StyledString withStyling(final int beginIndex, final int endIndex, final FLStyle style) {
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
        final FLStyle[] styling = this.styling;
        final FLStyle[] newStyling = new FLStyle[styling.length];
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        Arrays.fill(newStyling, beginIndex, endIndex, style);
        return new StyledString(value, newStyling);
    }

    public StyledString withStyling(final ChatFormatting formatting, final boolean state) {
        return this.withStyling(0, this.length(), formatting, state);
    }

    public StyledString withStyling(final int beginIndex, final int endIndex, final ChatFormatting formatting, final boolean state) {
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
        final FLStyle[] styling = this.styling;
        final FLStyle[] newStyling = new FLStyle[styling.length];
        System.arraycopy(styling, 0, newStyling, 0, beginIndex);
        System.arraycopy(styling, endIndex, newStyling, endIndex, styling.length - endIndex);
        for (int i = beginIndex; i < endIndex; i++) {
            newStyling[i] = styling[i].withStyling(formatting, state);
        }
        return new StyledString(value, newStyling);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public Component toTextComponent() {
        final String value = this.value;
        if (value.length() == 0) {
            return new TextComponent("");
        }
        TextComponent text = null;
        final FLStyle[] styling = this.styling;
        final StringBuilder bob = new StringBuilder();
        FLStyle currentStyle = styling[0];
        for (int i = 0; ; ) {
            final FLStyle style = i < value.length() ? styling[i] : null;
            if (!currentStyle.equals(style)) {
                TextComponent t = new TextComponent(bob.toString());
                t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withColor(currentStyle.getColor()));
                if (currentStyle.isObfuscated()) t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withObfuscated(true));
                if (currentStyle.isBold()) t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withBold(true));
                if (currentStyle.isStrikethrough()) t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withStrikethrough(true));
                if (currentStyle.isUnderline()) t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withUnderlined(true));
                if (currentStyle.isItalic()) t = (TextComponent) ComponentUtils.mergeStyles(t, Style.EMPTY.withItalic(true));
                if (text == null) {
                    text = t;
                } else {
                    text.append(t);
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

    public static CompoundTag serialize(final StyledString str) {
        final CompoundTag compound = new CompoundTag();
        compound.putString("value", str.value);
        compound.putIntArray("styling", Arrays.stream(str.styling).mapToInt(FLStyle::packed).toArray());
        return compound;
    }

    public static StyledString deserialize(final CompoundTag compound) {
        final String value = compound.getString("value");
        FLStyle[] styling = Arrays.stream(compound.getIntArray("styling"))
            .mapToObj(FLStyle::new)
            .toArray(FLStyle[]::new);
        if (styling.length != value.length()) {
            styling = Arrays.stream(Arrays.copyOf(styling, value.length()))
                .map(s -> Optional.ofNullable(s).orElseGet(FLStyle::new))
                .toArray(FLStyle[]::new);
        }
        return new StyledString(value, styling);
    }

    public static StyledString valueOf(final String str) {
        final StyledStringBuilder bldr = new StyledStringBuilder(str.length());
        final FLStyle plainStyle = new FLStyle();
        FLStyle style = plainStyle;
        final char[] chars = str.toCharArray();
        boolean hasFToken = false;
        for (final char chr : chars) {
            if (chr == '\u00A7') {
                hasFToken = true;
            } else if (hasFToken) {
                final char ch = Character.toLowerCase(chr);
                final int colorIndex = "0123456789abcdef".indexOf(ch);
                if (colorIndex != -1) {
                    final ChatFormatting color = ChatFormatting.getById(colorIndex);
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

    public static int getColor(final ChatFormatting color) {
        final Integer rgb = color.getColor();
        Preconditions.checkNotNull(rgb, "Must be a color");
        return rgb;
    }
}
