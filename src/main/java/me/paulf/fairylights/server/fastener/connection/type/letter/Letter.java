package me.paulf.fairylights.server.fastener.connection.type.letter;

import me.paulf.fairylights.server.fastener.connection.type.ConnectionHangingFeature;
import net.minecraft.util.math.Vec3d;

public final class Letter extends ConnectionHangingFeature.HangingFeature<Letter> {
	private final SymbolSet symbols;

	private final char letter;

	public Letter(int index, Vec3d point, Vec3d rotation, SymbolSet symbols, char letter) {
		super(index, point, rotation);
		this.symbols = symbols;
		this.letter = letter;
	}

	public char getLetter() {
		return letter;
	}

	public int getU() {
		return symbols.getU(letter);
	}

	public int getV() {
		return symbols.getV(letter);
	}

	@Override
	public double getWidth() {
		return symbols.getWidth(letter) / 16F;
	}

	public int getSymbolWidth() {
		return symbols.getWidth(letter);
	}

	public int getSymbolHeight() {
		return symbols.getHeight();
	}

	@Override
	public double getHeight() {
		return symbols.getHeight() / 16F;
	}

	@Override
	public boolean parallelsCord() {
		return true;
	}

	public void tick() {
		prevRotation = rotation;
	}
}
