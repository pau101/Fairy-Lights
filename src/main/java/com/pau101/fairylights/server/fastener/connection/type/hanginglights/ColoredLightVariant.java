package com.pau101.fairylights.server.fastener.connection.type.hanginglights;

import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.util.NBTSerializable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;

public final class ColoredLightVariant implements NBTSerializable {
	private LightVariant variant;

	private EnumDyeColor color;

	private ColoredLightVariant() {}

	public ColoredLightVariant(LightVariant variant, EnumDyeColor color) {
		this.variant = variant;
		this.color = color;
	}

	public LightVariant getVariant() {
		return variant;
	}

	public EnumDyeColor getColor() {
		return color;
	}

	public ColoredLightVariant withColor(EnumDyeColor color) {
		return new ColoredLightVariant(variant, color);
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("light", variant.ordinal());
		compound.setByte("color", (byte) color.getDyeDamage());
		return compound;
	}

	@Override
	public void deserialize(NBTTagCompound compound) {
		variant = LightVariant.getLightVariant(compound.getInteger("light"));
		color = EnumDyeColor.byDyeDamage(compound.getByte("color"));
	}

	public static ColoredLightVariant from(NBTTagCompound compound) {
		ColoredLightVariant light = new ColoredLightVariant();
		light.deserialize(compound);
		return light;
	}
}
