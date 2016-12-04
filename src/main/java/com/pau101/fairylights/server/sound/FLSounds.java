package com.pau101.fairylights.server.sound;

import com.pau101.fairylights.FairyLights;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class FLSounds {
	private FLSounds() {}

	public static final SoundEvent CORD_STRETCH = create("cord.stretch");

	public static final SoundEvent CORD_CONNECT = create("cord.connect");

	public static final SoundEvent CORD_DISCONNECT = create("cord.disconnect");

	public static final SoundEvent CORD_SNAP = create("cord.snap");

	public static final SoundEvent JINGLE_BELL = create("jingle_bell");

	public static final SoundEvent FEATURE_COLOR_CHANGE = create("feature.color_change");

	public static final SoundEvent LADDER_BREAK = create("entity.ladder.break");

	public static final SoundEvent LADDER_FALL = create("entity.ladder.fall");

	public static final SoundEvent LADDER_HIT = create("entity.ladder.hit");

	public static final SoundEvent LADDER_PLACE = create("entity.ladder.place");

	public static void init() {}

	private static final SoundEvent create(String name) {
		ResourceLocation id = new ResourceLocation(FairyLights.ID, name);
		SoundEvent sound = new SoundEvent(id).setRegistryName(name);
		GameRegistry.register(sound);
		return sound;
	}
}
