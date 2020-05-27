package me.paulf.fairylights.server.fastener.connection.type.letter;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public final class SymbolSet {
    private final int height;

    private final String description;

    private final Int2ObjectMap<Symbol> chars;

    private SymbolSet(final Builder builder) {
        this.height = builder.height;
        this.description = builder.description;
        this.chars = Int2ObjectMaps.unmodifiable(new Int2ObjectOpenHashMap<>(builder.symbols));
    }

    public int getHeight() {
        return this.height;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean contains(final int character) {
        return this.chars.containsKey(character);
    }

    public int getWidth(final char character) {
        return this.chars.getOrDefault(character, Symbol.UNKNOWN).width;
    }

    private static class Symbol {
        public static final Symbol UNKNOWN = new Symbol(0);

        private final int width;

        public Symbol(final int width) {
            this.width = width;
        }
    }

    public static class Builder {
        final int height;

        final String description;

        final Int2ObjectMap<Symbol> symbols = new Int2ObjectOpenHashMap<>();

        public Builder(final int height, final String description) {
            this.height = height;
            this.description = description;
        }

        public Builder add(final String codepoints, final int width) {
            codepoints.chars().forEach(codepoint -> this.add(codepoint, width));
            return this;
        }

        public Builder add(final int codepoint, final int width) {
            this.symbols.put(codepoint, new Symbol(width));
            return this;
        }

        public SymbolSet build() {
            return new SymbolSet(this);
        }
    }
}
