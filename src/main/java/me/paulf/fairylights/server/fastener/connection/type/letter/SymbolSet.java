package me.paulf.fairylights.server.fastener.connection.type.letter;

import java.util.HashMap;
import java.util.Map;

public final class SymbolSet {
    private final Symbol[] symbols;

    private final Map<Character, Symbol> chars;

    private final int size;

    private SymbolSet(final Symbol[] symbols, final int size) {
        this.symbols = symbols;
        this.size = size;
        this.chars = new HashMap<>();
        for (final Symbol symbol : symbols) {
            this.chars.put(symbol.character, symbol);
        }
    }

    public int getHeight() {
        return this.size;
    }

    public boolean contains(final char character) {
        return this.chars.containsKey(character);
    }

    public int getWidth(final char character) {
        return this.chars.getOrDefault(character, Symbol.UNKNOWN).width;
    }

    public int getU(final char character) {
        return this.chars.getOrDefault(character, Symbol.UNKNOWN).u;
    }

    public int getV(final char character) {
        return this.chars.getOrDefault(character, Symbol.UNKNOWN).v;
    }

    private static class Symbol {
        public static final Symbol UNKNOWN = new Symbol('?', 0, 0, 0);

        private final char character;

        private final int width;

        private final int u, v;

        public Symbol(final char character, final int width, final int u, final int v) {
            this.character = character;
            this.width = width;
            this.u = u;
            this.v = v;
        }
    }

    public static SymbolSet from(final int columns, final int size, final String str) {
        final String[] chars = str.split(",");
        final Symbol[] symbols = new Symbol[chars.length / 2];
        for (int i = 0, j = 0; i < chars.length; j++) {
            final char c = chars[i++].charAt(0);
            final int w = Integer.valueOf(chars[i++]);
            symbols[j] = new Symbol(c, w, (j % columns) * size, (j / columns) * size);
        }
        return new SymbolSet(symbols, size);
    }
}
