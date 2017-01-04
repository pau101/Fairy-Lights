package com.pau101.fairylights.server.integration.jei;

import com.pau101.fairylights.FairyLights;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public final class FairyLightsJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(new GenericRecipeHandler());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
		registry.registerSubtypeInterpreter(FairyLights.tinsel, new TinselSubtypeInterpreter());
	}
}
