package me.paulf.fairylights.util;

public final class Blender {
    private int red, green, blue, brightness, count;

    public void add(final int rgb) {
        this.add(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
    }

    public void add(final int r, final int g, final int b) {
        this.red += r;
        this.green += g;
        this.blue += b;
        this.brightness += Math.max(r, Math.max(g, b));
        this.count++;
    }

    public int blend() {
        final int num = this.brightness;
        final int den = this.count * Math.max(this.red, Math.max(this.green, this.blue));
        if (den == 0) {
            return 0;
        }
        return (this.red * num / den) << 16 | (this.green * num / den) << 8 | (this.blue * num / den);
    }
}
