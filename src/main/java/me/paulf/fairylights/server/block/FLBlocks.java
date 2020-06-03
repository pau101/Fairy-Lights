package me.paulf.fairylights.server.block;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.StandardLightVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class FLBlocks {
    private FLBlocks() {}

    public static final DeferredRegister<Block> REG = new DeferredRegister<>(ForgeRegistries.BLOCKS, FairyLights.ID);

    public static final RegistryObject<FastenerBlock> FASTENER = REG.register("fastener", () -> new FastenerBlock(Block.Properties.create(Material.MISCELLANEOUS).noDrops()));

    public static final RegistryObject<LightBlock> FAIRY_LIGHT = REG.register("fairy_light", FLBlocks.createLight(StandardLightVariant.FAIRY));

    public static final RegistryObject<LightBlock> PAPER_LANTERN = REG.register("paper_lantern", FLBlocks.createLight(StandardLightVariant.PAPER));

    public static final RegistryObject<LightBlock> ORB_LANTERN = REG.register("orb_lantern", FLBlocks.createLight(StandardLightVariant.ORB));

    public static final RegistryObject<LightBlock> FLOWER_LIGHT = REG.register("flower_light", FLBlocks.createLight(StandardLightVariant.FLOWER));

    public static final RegistryObject<LightBlock> ORNATE_LANTERN = REG.register("ornate_lantern", FLBlocks.createLight(StandardLightVariant.ORNATE));

    public static final RegistryObject<LightBlock> OIL_LANTERN = REG.register("oil_lantern", FLBlocks.createLight(StandardLightVariant.OIL));

    public static final RegistryObject<LightBlock> JACK_O_LANTERN = REG.register("jack_o_lantern", FLBlocks.createLight(StandardLightVariant.JACK_O_LANTERN));

    public static final RegistryObject<LightBlock> SKULL_LIGHT = REG.register("skull_light", FLBlocks.createLight(StandardLightVariant.SKULL));

    public static final RegistryObject<LightBlock> GHOST_LIGHT = REG.register("ghost_light", FLBlocks.createLight(StandardLightVariant.GHOST));

    public static final RegistryObject<LightBlock> SPIDER_LIGHT = REG.register("spider_light", FLBlocks.createLight(StandardLightVariant.SPIDER));

    public static final RegistryObject<LightBlock> WITCH_LIGHT = REG.register("witch_light", FLBlocks.createLight(StandardLightVariant.WITCH));

    public static final RegistryObject<LightBlock> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLBlocks.createLight(StandardLightVariant.SNOWFLAKE));

    public static final RegistryObject<LightBlock> ICICLE_LIGHTS = REG.register("icicle_lights", FLBlocks.createLight(StandardLightVariant.ICICLE));

    public static final RegistryObject<LightBlock> METEOR_LIGHT = REG.register("meteor_light", FLBlocks.createLight(StandardLightVariant.METEOR));

    private static Supplier<LightBlock> createLight(final StandardLightVariant variant) {
        return () -> new LightBlock(Block.Properties.create(Material.MISCELLANEOUS).lightValue(15).notSolid(), variant);
    }
}
