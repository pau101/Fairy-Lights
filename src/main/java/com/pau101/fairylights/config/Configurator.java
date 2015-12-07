package com.pau101.fairylights.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.pau101.fairylights.FairyLights;

public class Configurator {
	private static File configFile;

	public static Configuration config;

	public static boolean jingleEnabled;

	public static String[] jingles;

	public static int jingleAmplitude;

	public static void initConfig(FMLPreInitializationEvent event) {
		File configFolder = event.getModConfigurationDirectory();
		configFile = new File(configFolder.getAbsolutePath() + File.separatorChar + FairyLights.MODID + ".cfg");
		config = new Configuration(configFile);
		updateConfig();
	}

	public static void updateConfig() {
		jingleEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Jingling", true).getBoolean(true);
		jingleAmplitude = config.get(Configuration.CATEGORY_GENERAL, "Jingle Volume", 20, "The distance that jingles can be heard in blocks.").getInt(20);
		String[] defaultJingles = new String[] {
			// Jingle Bells
			"17_4,17_4,17_9,17_4,17_4,17_9,17_4,17&20_4,20&13_7,13&15_2,15&17_14,17_4,18_4,18_4,18_7,18_2,18_4,18&17_4,17_4,17_2,17_2,17_4,17&15_4,15_4,15&17_4,17&15_9,15&20",
			// We Wish You a Merry Christmas
			"6_7,3&11_7,11_2,13_5,11_2,10_5,4&8_7,8_7,8_7,13&5_7,13_2,15_5,13_2,11_5,6&10_7,6_7,6_7,15&7_7,15_2,16_5,15_2,13_5,8&11_7,8_7,6_2,6_5,8&4_7,13_7,10&6_7,3&11",
			// Deck the Halls
			"18&15_12,16&13_4,15&11_8,13&10_8,11&8_8,13&10_8,15&11_8,11&6_8,13&10_4,15&11_4,16&13_4,13&10_4,15&11_12,13&8_4,11&6_8,10&6_8,11&6",
			// The Little Drummer Boy
			"11&6_20,11&6_10,13&10_10,15&11&6_20,15&11&6_10,15&11_10,16&6&11_5,15&11_5,16&13_10,15&11&6",
			// Silver Bells
			"16_6,13_6,4&11_13,8&8_13,4&16_6,13_6,8&11_13,3&8_13,4&20_6,18_6,9&16_13,1&13_13,4&13_13,6&13_13,9_13,1&18_6,16_6,11&6_13,3&11_13,6&10_13,9&11_13,6&11_13,6&3_6,9_6,9&4_13,8&11_13,4",
			// Sleighride
			"13_6,13_6,13_6,13_6,15_6,13_3,10_3,6_6,8_6,8_3,10_3,8_3,6_3,3_6,1",
			// Rudolph the Red-Nosed Reindeer
			"13_7,15_2,13_7,10_7,18_10,15_7,13_25,13_5,15_2,13_5,15_2,13_10,18_7,17_33,11_7,13_7,11_2,8_7,17_10,15_7,13_25,13_5,15_2,13_5,15_2,13_10,15_7,10",
			// Let it Go
			"4_4,4_8,9_8,9_8,4_8,4_8,9_8,11_13,9_4,11_8,9_8,11_8,13_8,14_8,16_8,9_13,4_4,4_8,9_8,9_8,4_8,9_8,4_4,11_31,13_8,14_62,11_4,13_4,14_26,9_4,9_4,16_26,14_4,11_4,11_8,11_4,11_4,11_8,13_8,14_8,16_4,14",
			// Joy to the World
			"12&20&8_13,20&12&8_10,19&15&8_3,17&13&8_20,15&12&8_6,13&10&5&1_13,12&8&3_6,3&1_6,10&7&1&3_20,8&15&0_6,15&13&8&0_20,17&13&8&1_6,17&13&8&1_20,19&10&3_6,19&10&3&8"
		};
		jingles = config.get(Configuration.CATEGORY_GENERAL, "Jingles", defaultJingles, "The list of jingles that are chosen from.").getStringList();
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (FairyLights.MODID.equals(event.modID)) {
			updateConfig();
		}
	}
}
