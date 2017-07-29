package com.pau101.fairylights.server.sound;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import static com.pau101.fairylights.FairyLights.ID;

@EventBusSubscriber(modid = FairyLights.ID)
public final class FLSounds {
	private FLSounds() {}

	private static final SoundEvent NIL = Utils.nil();

	@ObjectHolder(ID + ":cord.stretch")
	public static final SoundEvent CORD_STRETCH = NIL;

	@ObjectHolder(ID + ":cord.connect")
	public static final SoundEvent CORD_CONNECT = NIL;

	@ObjectHolder(ID + ":cord.disconnect")
	public static final SoundEvent CORD_DISCONNECT = NIL;

	@ObjectHolder(ID + ":cord.snap")
	public static final SoundEvent CORD_SNAP = NIL;

	@ObjectHolder(ID + ":jingle_bell")
	public static final SoundEvent JINGLE_BELL = NIL;

	@ObjectHolder(ID + ":feature.color_change")
	public static final SoundEvent FEATURE_COLOR_CHANGE = NIL;

	@ObjectHolder(ID + ":feature.light_turnon")
	public static final SoundEvent FEATURE_LIGHT_TURNON = NIL;

	@ObjectHolder(ID + ":feature.light_turnoff")
	public static final SoundEvent FEATURE_LIGHT_TURNOFF = NIL;

	@ObjectHolder(ID + ":entity.ladder.break")
	public static final SoundEvent LADDER_BREAK = NIL;

	@ObjectHolder(ID + ":entity.ladder.fall")
	public static final SoundEvent LADDER_FALL = NIL;

	@ObjectHolder(ID + ":entity.ladder.hit")
	public static final SoundEvent LADDER_HIT = NIL;

	@ObjectHolder(ID + ":entity.ladder.place")
	public static final SoundEvent LADDER_PLACE = NIL;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(
			create("cord.stretch"),
			create("cord.connect"),
			create("cord.disconnect"),
			create("cord.snap"),
			create("jingle_bell"),
			create("feature.color_change"),
			create("feature.light_turnon"),
			create("feature.light_turnoff"),
			create("entity.ladder.break"),
			create("entity.ladder.fall"),
			create("entity.ladder.hit"),
			create("entity.ladder.place")
		);
	}

	private static SoundEvent create(String name) {
		return new SoundEvent(new ResourceLocation(FairyLights.ID, name)).setRegistryName(name);
	}
}
