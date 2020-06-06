package me.paulf.fairylights.server.block;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class FLBlocks {
    private FLBlocks() {}

    public static final DeferredRegister<Block> REG = new DeferredRegister<>(ForgeRegistries.BLOCKS, FairyLights.ID);

    public static final RegistryObject<FastenerBlock> FASTENER = REG.register("fastener", () -> new FastenerBlock(Block.Properties.create(Material.MISCELLANEOUS).noDrops()));

    public static final RegistryObject<LightBlock> FAIRY_LIGHT = REG.register("fairy_light", FLBlocks.createLight(SimpleLightVariant.FAIRY));

    public static final RegistryObject<LightBlock> PAPER_LANTERN = REG.register("paper_lantern", FLBlocks.createLight(SimpleLightVariant.PAPER));

    public static final RegistryObject<LightBlock> ORB_LANTERN = REG.register("orb_lantern", FLBlocks.createLight(SimpleLightVariant.ORB));

    public static final RegistryObject<LightBlock> FLOWER_LIGHT = REG.register("flower_light", FLBlocks.createLight(SimpleLightVariant.FLOWER));

    public static final RegistryObject<LightBlock> ORNATE_LANTERN = REG.register("ornate_lantern", FLBlocks.createLight(SimpleLightVariant.ORNATE));

    public static final RegistryObject<LightBlock> OIL_LANTERN = REG.register("oil_lantern", FLBlocks.createLight(SimpleLightVariant.OIL));

    public static final RegistryObject<LightBlock> JACK_O_LANTERN = REG.register("jack_o_lantern", FLBlocks.createLight(SimpleLightVariant.JACK_O_LANTERN));

    public static final RegistryObject<LightBlock> SKULL_LIGHT = REG.register("skull_light", FLBlocks.createLight(SimpleLightVariant.SKULL));

    public static final RegistryObject<LightBlock> GHOST_LIGHT = REG.register("ghost_light", FLBlocks.createLight(SimpleLightVariant.GHOST));

    public static final RegistryObject<LightBlock> SPIDER_LIGHT = REG.register("spider_light", FLBlocks.createLight(SimpleLightVariant.SPIDER));

    public static final RegistryObject<LightBlock> WITCH_LIGHT = REG.register("witch_light", FLBlocks.createLight(SimpleLightVariant.WITCH));

    public static final RegistryObject<LightBlock> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLBlocks.createLight(SimpleLightVariant.SNOWFLAKE));

    public static final RegistryObject<LightBlock> ICICLE_LIGHTS = REG.register("icicle_lights", FLBlocks.createLight(SimpleLightVariant.ICICLE));

    public static final RegistryObject<LightBlock> METEOR_LIGHT = REG.register("meteor_light", FLBlocks.createLight(SimpleLightVariant.METEOR));

    public static final RegistryObject<LightBlock> TORCH_LANTERN = REG.register("torch_lantern", FLBlocks.createLight(SimpleLightVariant.TORCH_LANTERN));

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant) {
        return createLight(variant, LightBlock::new);
    }

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant, final BiFunction<Block.Properties, LightVariant<?>, LightBlock> factory) {
        return () -> factory.apply(Block.Properties.create(Material.MISCELLANEOUS).lightValue(15).notSolid(), variant);
    }
}
