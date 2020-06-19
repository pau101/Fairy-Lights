package me.paulf.fairylights.server.string;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class StringType extends ForgeRegistryEntry<StringType> {
    private final int color;

    public StringType(final int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
