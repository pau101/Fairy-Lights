package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FLBlockEntities {
    private FLBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FairyLights.ID);

    public static final RegistryObject<BlockEntityType<FastenerBlockEntity>> FASTENER = REG.register("fastener", () -> BlockEntityType.Builder.of(FastenerBlockEntity::new, FLBlocks.FASTENER.get()).build(null));

    public static final RegistryObject<BlockEntityType<LightBlockEntity>> LIGHT = REG.register("light", () -> BlockEntityType.Builder.of(LightBlockEntity::new,
        FLBlocks.FAIRY_LIGHT.get(),
        FLBlocks.PAPER_LANTERN.get(),
        FLBlocks.ORB_LANTERN.get(),
        FLBlocks.FLOWER_LIGHT.get(),
        FLBlocks.CANDLE_LANTERN_LIGHT.get(),
        FLBlocks.OIL_LANTERN_LIGHT.get(),
        FLBlocks.JACK_O_LANTERN.get(),
        FLBlocks.SKULL_LIGHT.get(),
        FLBlocks.GHOST_LIGHT.get(),
        FLBlocks.SPIDER_LIGHT.get(),
        FLBlocks.WITCH_LIGHT.get(),
        FLBlocks.SNOWFLAKE_LIGHT.get(),
        FLBlocks.HEART_LIGHT.get(),
        FLBlocks.MOON_LIGHT.get(),
        FLBlocks.STAR_LIGHT.get(),
        FLBlocks.ICICLE_LIGHTS.get(),
        FLBlocks.METEOR_LIGHT.get(),
        FLBlocks.OIL_LANTERN.get(),
        FLBlocks.CANDLE_LANTERN.get(),
        FLBlocks.INCANDESCENT_LIGHT.get()
    ).build(null));
}
