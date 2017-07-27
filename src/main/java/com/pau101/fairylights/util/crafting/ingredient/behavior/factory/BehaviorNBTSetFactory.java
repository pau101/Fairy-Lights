package com.pau101.fairylights.util.crafting.ingredient.behavior.factory;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorNBTSet;
import net.minecraftforge.common.crafting.JsonContext;

public final class BehaviorNBTSetFactory extends BehaviorNBTFactory {
	@Override
	protected Behavior parse(JsonContext context, JsonObject json, ImmutableList<String> key) {
		return new BehaviorNBTSet(key);
	}
}
