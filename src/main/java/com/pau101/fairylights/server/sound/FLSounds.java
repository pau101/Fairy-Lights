package com.pau101.fairylights.server.sound;

import com.pau101.fairylights.FairyLights;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLSounds {
	private FLSounds() {}

	public static final DeferredRegister<SoundEvent> REG = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, FairyLights.ID);

	public static final RegistryObject<SoundEvent> CORD_STRETCH = create("cord.stretch");

	public static final RegistryObject<SoundEvent> CORD_CONNECT = create("cord.connect");

	public static final RegistryObject<SoundEvent> CORD_DISCONNECT = create("cord.disconnect");

	public static final RegistryObject<SoundEvent> CORD_SNAP = create("cord.snap");

	public static final RegistryObject<SoundEvent> JINGLE_BELL = create("jingle_bell");

	public static final RegistryObject<SoundEvent> FEATURE_COLOR_CHANGE = create("feature.color_change");

	public static final RegistryObject<SoundEvent> FEATURE_LIGHT_TURNON = create("feature.light_turnon");

	public static final RegistryObject<SoundEvent> FEATURE_LIGHT_TURNOFF = create("feature.light_turnoff");

	public static final RegistryObject<SoundEvent> LADDER_BREAK = create("entity.ladder.break");

	public static final RegistryObject<SoundEvent> LADDER_FALL = create("entity.ladder.fall");

	public static final RegistryObject<SoundEvent> LADDER_HIT = create("entity.ladder.hit");

	public static final RegistryObject<SoundEvent> LADDER_PLACE = create("entity.ladder.place");

	private static RegistryObject<SoundEvent> create(String name) {
		return REG.register(name, () -> new SoundEvent(new ResourceLocation(FairyLights.ID, name)));
	}
}
