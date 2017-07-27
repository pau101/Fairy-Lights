package com.pau101.fairylights.util.crafting.ingredient.behavior.factory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public abstract class BehaviorNBTFactory extends GenericRecipeFactory.BehaviorFactory {
	private final Splitter splitter = Splitter.on('.');

	@Override
	public Behavior parse(JsonContext context, JsonObject json) {
		String key = JsonUtils.getString(json, "key");
		return parse(context, json, ImmutableList.copyOf(splitter.split(key)));
	}

	protected abstract Behavior parse(JsonContext context, JsonObject json, ImmutableList<String> key);
}
