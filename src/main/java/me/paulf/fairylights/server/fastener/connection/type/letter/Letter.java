package me.paulf.fairylights.server.fastener.connection.type.letter;

import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import me.paulf.fairylights.util.styledstring.Style;
import net.minecraft.util.math.Vec3d;

public final class Letter extends HangingFeatureConnection.HangingFeature<Letter> {
    private final SymbolSet symbols;

    private final char letter;

    private final Style style;

    public Letter(final int index, final Vec3d point, final Vec3d rotation, final SymbolSet symbols, final char letter, final Style style) {
        super(index, point, rotation);
        this.symbols = symbols;
        this.letter = letter;
        this.style = style;
    }

    public char getLetter() {
        return this.letter;
    }

    public Style getStyle() {
        return this.style;
    }

    public int getU() {
        return this.symbols.getU(this.letter);
    }

    public int getV() {
        return this.symbols.getV(this.letter);
    }

    @Override
    public double getWidth() {
        return this.symbols.getWidth(this.letter) / 16F;
    }

    public int getSymbolWidth() {
        return this.symbols.getWidth(this.letter);
    }

    public int getSymbolHeight() {
        return this.symbols.getHeight();
    }

    @Override
    public double getHeight() {
        return this.symbols.getHeight() / 16F;
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }

    public void tick() {
        this.prevRotation = this.rotation;
    }
}
