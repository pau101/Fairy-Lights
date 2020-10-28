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

    public static final DeferredRegister<Block> REG = DeferredRegister.create(ForgeRegistries.BLOCKS, FairyLights.ID);

    public static final RegistryObject<FastenerBlock> FASTENER = REG.register("fastener", () -> new FastenerBlock(Block.Properties.create(Material.MISCELLANEOUS).noDrops()));

    public static final RegistryObject<LightBlock> FAIRY_LIGHT = REG.register("fairy_light", FLBlocks.createLight(SimpleLightVariant.FAIRY_LIGHT));

    public static final RegistryObject<LightBlock> PAPER_LANTERN = REG.register("paper_lantern", FLBlocks.createLight(SimpleLightVariant.PAPER_LANTERN));

    public static final RegistryObject<LightBlock> ORB_LANTERN = REG.register("orb_lantern", FLBlocks.createLight(SimpleLightVariant.ORB_LANTERN));

    public static final RegistryObject<LightBlock> FLOWER_LIGHT = REG.register("flower_light", FLBlocks.createLight(SimpleLightVariant.FLOWER_LIGHT));

    public static final RegistryObject<LightBlock> CANDLE_LANTERN_LIGHT = REG.register("candle_lantern_light", FLBlocks.createLight(SimpleLightVariant.CANDLE_LANTERN_LIGHT));

    public static final RegistryObject<LightBlock> OIL_LANTERN_LIGHT = REG.register("oil_lantern_light", FLBlocks.createLight(SimpleLightVariant.OIL_LANTERN_LIGHT));

    public static final RegistryObject<LightBlock> JACK_O_LANTERN = REG.register("jack_o_lantern", FLBlocks.createLight(SimpleLightVariant.JACK_O_LANTERN));

    public static final RegistryObject<LightBlock> SKULL_LIGHT = REG.register("skull_light", FLBlocks.createLight(SimpleLightVariant.SKULL_LIGHT));

    public static final RegistryObject<LightBlock> GHOST_LIGHT = REG.register("ghost_light", FLBlocks.createLight(SimpleLightVariant.GHOST_LIGHT));

    public static final RegistryObject<LightBlock> SPIDER_LIGHT = REG.register("spider_light", FLBlocks.createLight(SimpleLightVariant.SPIDER_LIGHT));

    public static final RegistryObject<LightBlock> WITCH_LIGHT = REG.register("witch_light", FLBlocks.createLight(SimpleLightVariant.WITCH_LIGHT));

    public static final RegistryObject<LightBlock> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLBlocks.createLight(SimpleLightVariant.SNOWFLAKE_LIGHT));

    public static final RegistryObject<LightBlock> HEART_LIGHT = REG.register("heart_light", FLBlocks.createLight(SimpleLightVariant.HEART_LIGHT));

    public static final RegistryObject<LightBlock> MOON_LIGHT = REG.register("moon_light", FLBlocks.createLight(SimpleLightVariant.MOON_LIGHT));

    public static final RegistryObject<LightBlock> STAR_LIGHT = REG.register("star_light", FLBlocks.createLight(SimpleLightVariant.STAR_LIGHT));

    public static final RegistryObject<LightBlock> ICICLE_LIGHTS = REG.register("icicle_lights", FLBlocks.createLight(SimpleLightVariant.ICICLE_LIGHTS));

    public static final RegistryObject<LightBlock> METEOR_LIGHT = REG.register("meteor_light", FLBlocks.createLight(SimpleLightVariant.METEOR_LIGHT));

    public static final RegistryObject<LightBlock> OIL_LANTERN = REG.register("oil_lantern", FLBlocks.createLight(SimpleLightVariant.OIL_LANTERN));

    public static final RegistryObject<LightBlock> CANDLE_LANTERN = REG.register("candle_lantern", FLBlocks.createLight(SimpleLightVariant.CANDLE_LANTERN));

    public static final RegistryObject<LightBlock> INCANDESCENT_LIGHT = REG.register("incandescent_light", FLBlocks.createLight(SimpleLightVariant.INCANDESCENT_LIGHT));

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant) {
        return createLight(variant, LightBlock::new);
    }

    private static Supplier<LightBlock> createLight(final LightVariant<?> variant, final BiFunction<Block.Properties, LightVariant<?>, LightBlock> factory) {
        return () -> factory.apply(Block.Properties.create(Material.MISCELLANEOUS).setLightLevel(state -> state.get(LightBlock.LIT) ? 15 : 0).notSolid(), variant);
    }
}
