package me.paulf.fairylights.server.feature;

import me.paulf.fairylights.server.connection.SymbolSet;
import me.paulf.fairylights.util.styledstring.Style;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Letter extends HangingFeature {
    private final SymbolSet symbols;

    private char letter;

    private Style style;

    public Letter(final int index, final Vec3 point, final float yaw, final float pitch, final SymbolSet symbols, final char letter, final Style style) {
        super(index, point, yaw, pitch, 0.0F, 0.0F);
        this.symbols = symbols;
        this.letter = letter;
        this.style = style;
    }

    public void set(final char letter, final Style style) {
        this.letter = letter;
        this.style = style;
    }

    public char getLetter() {
        return this.letter;
    }

    public Style getStyle() {
        return this.style;
    }

    @Override
    public AABB getBounds() {
        final float w = this.symbols.getWidth(this.letter);
        final float h = this.symbols.getHeight();
        return new AABB(-w / 2.0D, -h, -w / 2.0D, w / 2.0D, 0.0D, w / 2.0D);
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
