package com.pau101.fairylights.util.crafting.ingredient.behavior;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BehaviorAuxiliaryAugHangingLightsTwinkle implements BehaviorAuxiliary<MutableInt> {
	@Override
	public MutableInt accumulator() {
		return new MutableInt();
	}

	@Override
	public void consume(MutableInt count, ItemStack ingredient) {
		count.increment();
	}

	@Override
	public boolean finish(MutableInt count, ItemStack output) {
		if (count.intValue() > 0) {
			if (output.getTagCompound().getBoolean("twinkle")) {
				return true;
			}
			output.getTagCompound().setBoolean("twinkle", true);
		}
		return false;
	}
}
