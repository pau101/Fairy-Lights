package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLBlockEntities {
    private FLBlockEntities() {}

    public static final DeferredRegister<TileEntityType<?>> REG = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, FairyLights.ID);

    public static final RegistryObject<TileEntityType<FastenerBlockEntity>> FASTENER = REG.register("fastener", () -> TileEntityType.Builder.create(FastenerBlockEntity::new, FLBlocks.FASTENER.get()).build(null));

    public static final RegistryObject<TileEntityType<LightBlockEntity>> LIGHT = REG.register("light", () -> TileEntityType.Builder.create(LightBlockEntity::new,
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
        FLBlocks.ICICLE_LIGHTS.get(),
        FLBlocks.METEOR_LIGHT.get(),
        FLBlocks.OIL_LANTERN.get(),
        FLBlocks.CANDLE_LANTERN.get(),
        FLBlocks.INCANDESCENT_LIGHT.get()
    ).build(null));
}
