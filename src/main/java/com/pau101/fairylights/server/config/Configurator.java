package com.pau101.fairylights.server.config;

import com.pau101.fairylights.FairyLights;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class Configurator {
	private Configurator() {}

	private static Configuration config;

	private static boolean jingleEnabled;

	private static int jingleAmplitude;

	public static Configuration getConfig() {
		return config;
	}

	public static boolean isJingleEnabled() {
		return jingleEnabled;
	}

	public static int getJingleAmplitude() {
		return jingleAmplitude;
	}

	public static void initConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		updateConfig();
		MinecraftForge.EVENT_BUS.register(Configurator.class);
	}

	public static void updateConfig() {
		jingleEnabled = config.getBoolean("Enable Jingling", Configuration.CATEGORY_GENERAL, true, "If true jingles will play during Christmas.");
		jingleAmplitude = config.getInt("Jingle Volume", Configuration.CATEGORY_GENERAL, 40, 1, Integer.MAX_VALUE, "The distance that jingles can be heard in blocks.");
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (FairyLights.ID.equals(event.getModID())) {
			updateConfig();
		}
	}
}
