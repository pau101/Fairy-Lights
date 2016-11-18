package com.pau101.fairylights.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.config.Configurator;

public final class GuiConfigFairyLights extends GuiConfig {
	public GuiConfigFairyLights(GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), FairyLights.ID, false, false, I18n.format("fairylights.config"));
	}

	private static List<IConfigElement> getConfigElements() {
		return new ConfigElement(Configurator.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
	}
}
