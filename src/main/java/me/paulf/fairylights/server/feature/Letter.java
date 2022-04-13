package me.paulf.fairylights.server.feature;

import me.paulf.fairylights.server.connection.SymbolSet;
import me.paulf.fairylights.util.styledstring.FLStyle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public final class Letter extends HangingFeature {
    private final SymbolSet symbols;

    private char letter;

    private FLStyle style;

    public Letter(final int index, final Vector3d point, final float yaw, final float pitch, final SymbolSet symbols, final char letter, final FLStyle style) {
        super(index, point, yaw, pitch, 0.0F, 0.0F);
        this.symbols = symbols;
        this.letter = letter;
        this.style = style;
    }

    public void set(final char letter, final FLStyle style) {
        this.letter = letter;
        this.style = style;
    }

    public char getLetter() {
        return this.letter;
    }

    public FLStyle getStyle() {
        return this.style;
    }

    @Override
    public AxisAlignedBB getBounds() {
        final float w = this.symbols.getWidth(this.letter);
        final float h = this.symbols.getHeight();
        return new AxisAlignedBB(-w / 2.0D, -h, -w / 2.0D, w / 2.0D, 0.0D, w / 2.0D);
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
