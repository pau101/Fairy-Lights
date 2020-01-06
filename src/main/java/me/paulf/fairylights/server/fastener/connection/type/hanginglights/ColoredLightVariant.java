package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.util.NBTSerializable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;

public final class ColoredLightVariant implements NBTSerializable {
    private LightVariant variant;

    private DyeColor color;

    private ColoredLightVariant() {}

    public ColoredLightVariant(final LightVariant variant, final DyeColor color) {
        this.variant = variant;
        this.color = color;
    }

    public LightVariant getVariant() {
        return this.variant;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public ColoredLightVariant withColor(final DyeColor color) {
        return new ColoredLightVariant(this.variant, color);
    }

    @Override
    public CompoundNBT serialize() {
        final CompoundNBT compound = new CompoundNBT();
        compound.putInt("light", this.variant.ordinal());
        compound.putByte("color", (byte) this.color.getId());
        return compound;
    }

    @Override
    public void deserialize(final CompoundNBT compound) {
        this.variant = LightVariant.getLightVariant(compound.getInt("light"));
        this.color = DyeColor.byId(compound.getByte("color"));
    }

    public static ColoredLightVariant from(final CompoundNBT compound) {
        final ColoredLightVariant light = new ColoredLightVariant();
        light.deserialize(compound);
        return light;
    }
}
