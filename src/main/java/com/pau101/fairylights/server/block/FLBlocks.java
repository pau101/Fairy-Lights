package com.pau101.fairylights.server.block;

import com.pau101.fairylights.FairyLights;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FLBlocks {
	private FLBlocks() {}

	public static final DeferredRegister<Block> REG = new DeferredRegister<>(ForgeRegistries.BLOCKS, FairyLights.ID);

	public static final RegistryObject<BlockFastener> FASTENER = REG.register("fastener", () -> new BlockFastener(Block.Properties.create(Material.MISCELLANEOUS)));
}
