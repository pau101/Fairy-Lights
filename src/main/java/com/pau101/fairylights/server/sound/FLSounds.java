package com.pau101.fairylights.server.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.pau101.fairylights.FairyLights;

public final class FLSounds {
	private FLSounds() {}

	public static final SoundEvent CORD_STRETCH = create("cord.stretch");

	public static final SoundEvent CORD_CONNECT = create("cord.connect");

	public static final SoundEvent CORD_DISCONNECT = create("cord.disconnect");

	public static final SoundEvent CORD_SNAP = create("cord.snap");

	public static final SoundEvent JINGLE_BELL = create("jingle_bell");

	public static final SoundEvent FEATURE_COLOR_CHANGE = create("feature.color_change");

	public static void init() {}

	private static final SoundEvent create(String name) {
		ResourceLocation id = new ResourceLocation(FairyLights.ID, name);
		SoundEvent sound = new SoundEvent(id).setRegistryName(name);
		GameRegistry.register(sound);
		return sound;
	}
}
