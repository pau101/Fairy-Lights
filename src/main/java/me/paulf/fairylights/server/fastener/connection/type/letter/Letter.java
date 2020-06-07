package me.paulf.fairylights.server.fastener.connection.type.letter;

import me.paulf.fairylights.server.fastener.connection.type.HangingFeature;
import me.paulf.fairylights.util.styledstring.Style;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public final class Letter extends HangingFeature {
    private final SymbolSet symbols;

    private final char letter;

    private final Style style;

    public Letter(final int index, final Vec3d point, final float yaw, final float pitch, final SymbolSet symbols, final char letter, final Style style) {
        super(index, point, yaw, pitch, 0.0F);
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

    @Override
    public AxisAlignedBB getBounds() {
        final float w = this.symbols.getWidth(this.letter) / 16F;
        final float h = this.symbols.getHeight() / 16F;
        return new AxisAlignedBB(-w / 2.0D, -h, -w / 2.0D, w / 2.0D, 0.0D, w / 2.0D);
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
