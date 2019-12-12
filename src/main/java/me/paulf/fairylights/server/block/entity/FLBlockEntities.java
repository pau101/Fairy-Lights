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

    public static final RegistryObject<TileEntityType<?>> FASTENER = REG.register("fastener", () -> TileEntityType.Builder.create(BlockEntityFastener::new, FLBlocks.FASTENER.orElseThrow(IllegalStateException::new)).build(null));
}
