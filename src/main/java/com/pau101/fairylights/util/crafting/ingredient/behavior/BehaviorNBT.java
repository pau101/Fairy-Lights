package com.pau101.fairylights.util.crafting.ingredient.behavior;

import java.util.Objects;
import java.util.function.BiConsumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public abstract class BehaviorNBT implements Behavior {
	private final ImmutableList<String> keys;

	public BehaviorNBT(ImmutableList<String> keys) {
		Objects.requireNonNull(keys, "keys");
		Preconditions.checkArgument(keys.size() > 0, "Must have at least one key");
		this.keys = keys;
	}

	protected final void setBoolean(ItemStack output, boolean value) {
		accept(output.getTagCompound(), (key, nbt) -> nbt.setBoolean(key, value));
	}

	protected final void accept(NBTTagCompound nbt, BiConsumer<String, NBTTagCompound> consumer) {
		NBTTagCompound compound = nbt;
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			if (compound.hasKey(key, Constants.NBT.TAG_COMPOUND)) {
				compound = compound.getCompoundTag(key);
			} else {
				compound.setTag(key, compound = new NBTTagCompound());
			}
		}
		consumer.accept(keys.get(keys.size() - 1), compound);
	}
}
