package com.pau101.fairylights.server.fastener.connection.type.hanginglights;

import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.util.NBTSerializable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;

public final class ColoredLightVariant implements NBTSerializable {
	private LightVariant variant;

	private DyeColor color;

	private ColoredLightVariant() {}

	public ColoredLightVariant(LightVariant variant, DyeColor color) {
		this.variant = variant;
		this.color = color;
	}

	public LightVariant getVariant() {
		return variant;
	}

	public DyeColor getColor() {
		return color;
	}

	public ColoredLightVariant withColor(DyeColor color) {
		return new ColoredLightVariant(variant, color);
	}

	@Override
	public CompoundNBT serialize() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("light", variant.ordinal());
		compound.putByte("color", (byte) color.getId());
		return compound;
	}

	@Override
	public void deserialize(CompoundNBT compound) {
		variant = LightVariant.getLightVariant(compound.getInt("light"));
		color = DyeColor.byId(compound.getByte("color"));
	}

	public static ColoredLightVariant from(CompoundNBT compound) {
		ColoredLightVariant light = new ColoredLightVariant();
		light.deserialize(compound);
		return light;
	}
}
