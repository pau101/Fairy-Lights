package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.*;
import me.paulf.fairylights.server.block.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.registries.*;

public final class FLBlockEntities {
    private FLBlockEntities() {}

    public static final DeferredRegister<TileEntityType<?>> REG = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, FairyLights.ID);

    public static final RegistryObject<TileEntityType<FastenerBlockEntity>> FASTENER = REG.register("fastener", () -> TileEntityType.Builder.create(FastenerBlockEntity::new, FLBlocks.FASTENER.orElseThrow(IllegalStateException::new)).build(null));

    public static final RegistryObject<TileEntityType<LightBlockEntity>> LIGHT = REG.register("light", () -> TileEntityType.Builder.create(LightBlockEntity::new,
        FLBlocks.FAIRY_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.PAPER_LANTERN.orElseThrow(IllegalStateException::new),
        FLBlocks.ORB_LANTERN.orElseThrow(IllegalStateException::new),
        FLBlocks.FLOWER_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.ORNATE_LANTERN.orElseThrow(IllegalStateException::new),
        FLBlocks.OIL_LANTERN.orElseThrow(IllegalStateException::new),
        FLBlocks.JACK_O_LANTERN.orElseThrow(IllegalStateException::new),
        FLBlocks.SKULL_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.GHOST_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.SPIDER_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.WITCH_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.SNOWFLAKE_LIGHT.orElseThrow(IllegalStateException::new),
        FLBlocks.ICICLE_LIGHTS.orElseThrow(IllegalStateException::new),
        FLBlocks.METEOR_LIGHT.orElseThrow(IllegalStateException::new)
    ).build(null));
}
