package com.pau101.fairylights.server.fastener.connection.type.letter;

import java.util.HashMap;
import java.util.Map;

public final class SymbolSet {
	private final Symbol[] symbols;

	private final Map<Character, Symbol> chars;

	private final int size;

	private SymbolSet(Symbol[] symbols, int size) {
		this.symbols = symbols;
		this.size = size;
		chars = new HashMap<>();
		for (Symbol symbol : symbols) {
			chars.put(symbol.character, symbol);
		}
	}

	public int getHeight() {
		return size;
	}

	public boolean contains(char character) {
		return chars.containsKey(character);
	}

	public int getWidth(char character) {
		return chars.getOrDefault(character, Symbol.UNKNOWN).width;
	}

	public int getU(char character) {
		return chars.getOrDefault(character, Symbol.UNKNOWN).u;
	}

	public int getV(char character) {
		return chars.getOrDefault(character, Symbol.UNKNOWN).v;
	}

	private static class Symbol {
		public static final Symbol UNKNOWN = new Symbol('?', 0, 0, 0);

		private final char character;

		private final int width;

		private final int u, v;

		public Symbol(char character, int width, int u, int v) {
			this.character = character;
			this.width = width;
			this.u = u;
			this.v = v;
		}
	}

	public static SymbolSet from(int columns, int size, String str) {
		String[] chars = str.split(",");
		Symbol[] symbols = new Symbol[chars.length / 2];
		for (int i = 0, j = 0; i < chars.length; j++) {
			char c = chars[i++].charAt(0);
			int w = Integer.valueOf(chars[i++]);
			symbols[j] = new Symbol(c, w, (j % columns) * size, (j / columns) * size);
		}
		return new SymbolSet(symbols, size);
	}
}
