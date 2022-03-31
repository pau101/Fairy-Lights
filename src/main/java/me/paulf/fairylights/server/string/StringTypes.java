package me.paulf.fairylights.server.string;

import me.paulf.fairylights.FairyLights;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class StringTypes {
    private StringTypes() {}

    public static final DeferredRegister<StringType> REG = DeferredRegister.create(FairyLights.STRING_TYPES, FairyLights.ID);

    public static final RegistryObject<StringType> BLACK_STRING = REG.register("black_string", () -> new StringType(0x323232));

    public static final RegistryObject<StringType> WHITE_STRING = REG.register("white_string", () -> new StringType(0xF0F0F0));
}
