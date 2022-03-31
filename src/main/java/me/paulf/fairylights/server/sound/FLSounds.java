package me.paulf.fairylights.server.sound;

import me.paulf.fairylights.FairyLights;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLSounds {
    private FLSounds() {}

    public static final DeferredRegister<SoundEvent> REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FairyLights.ID);

    public static final RegistryObject<SoundEvent> CORD_STRETCH = create("cord.stretch");

    public static final RegistryObject<SoundEvent> CORD_CONNECT = create("cord.connect");

    public static final RegistryObject<SoundEvent> CORD_DISCONNECT = create("cord.disconnect");

    public static final RegistryObject<SoundEvent> CORD_SNAP = create("cord.snap");

    public static final RegistryObject<SoundEvent> JINGLE_BELL = create("jingle_bell");

    public static final RegistryObject<SoundEvent> FEATURE_COLOR_CHANGE = create("feature.color_change");

    public static final RegistryObject<SoundEvent> FEATURE_LIGHT_TURNON = create("feature.light_turnon");

    public static final RegistryObject<SoundEvent> FEATURE_LIGHT_TURNOFF = create("feature.light_turnoff");

    private static RegistryObject<SoundEvent> create(final String name) {
        return REG.register(name, () -> new SoundEvent(new ResourceLocation(FairyLights.ID, name)));
    }
}
