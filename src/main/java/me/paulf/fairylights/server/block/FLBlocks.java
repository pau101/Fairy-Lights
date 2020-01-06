package me.paulf.fairylights.server.block;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.LightVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class FLBlocks {
    private FLBlocks() {}

    public static final DeferredRegister<Block> REG = new DeferredRegister<>(ForgeRegistries.BLOCKS, FairyLights.ID);

    public static final RegistryObject<FastenerBlock> FASTENER = REG.register("fastener", () -> new FastenerBlock(Block.Properties.create(Material.MISCELLANEOUS)));

    public static final RegistryObject<LightBlock> FAIRY_LIGHT = REG.register("fairy_light", FLBlocks.createLight(LightVariant.FAIRY));

    public static final RegistryObject<LightBlock> PAPER_LANTERN = REG.register("paper_lantern", FLBlocks.createLight(LightVariant.PAPER));

    public static final RegistryObject<LightBlock> ORB_LANTERN = REG.register("orb_lantern", FLBlocks.createLight(LightVariant.ORB));

    public static final RegistryObject<LightBlock> FLOWER_LIGHT = REG.register("flower_light", FLBlocks.createLight(LightVariant.FLOWER));

    public static final RegistryObject<LightBlock> ORNATE_LANTERN = REG.register("ornate_lantern", FLBlocks.createLight(LightVariant.ORNATE));

    public static final RegistryObject<LightBlock> OIL_LANTERN = REG.register("oil_lantern", FLBlocks.createLight(LightVariant.OIL));

    public static final RegistryObject<LightBlock> JACK_O_LANTERN = REG.register("jack_o_lantern", FLBlocks.createLight(LightVariant.JACK_O_LANTERN));

    public static final RegistryObject<LightBlock> SKULL_LIGHT = REG.register("skull_light", FLBlocks.createLight(LightVariant.SKULL));

    public static final RegistryObject<LightBlock> GHOST_LIGHT = REG.register("ghost_light", FLBlocks.createLight(LightVariant.GHOST));

    public static final RegistryObject<LightBlock> SPIDER_LIGHT = REG.register("spider_light", FLBlocks.createLight(LightVariant.SPIDER));

    public static final RegistryObject<LightBlock> WITCH_LIGHT = REG.register("witch_light", FLBlocks.createLight(LightVariant.WITCH));

    public static final RegistryObject<LightBlock> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLBlocks.createLight(LightVariant.SNOWFLAKE));

    public static final RegistryObject<LightBlock> ICICLE_LIGHTS = REG.register("icicle_lights", FLBlocks.createLight(LightVariant.ICICLE));

    public static final RegistryObject<LightBlock> METEOR_LIGHT = REG.register("meteor_light", FLBlocks.createLight(LightVariant.METEOR));

    private static Supplier<LightBlock> createLight(final LightVariant variant) {
        return () -> new LightBlock(Block.Properties.create(Material.MISCELLANEOUS).lightValue(15), variant);
    }
}
