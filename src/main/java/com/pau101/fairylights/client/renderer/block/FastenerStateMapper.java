package com.pau101.fairylights.client.renderer.block;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.util.ForwardingBlockState;

public final class FastenerStateMapper extends StateMapperBase {
	private static final ModelResourceLocation FASTENER = new ModelResourceLocation(FairyLights.ID + ":fastener", "fence");

	// Dummy blockstate to represent the fence fastener model
	public static final IBlockState FENCE_FASTENER_STATE = new ForwardingBlockState(FairyLights.fastener.getDefaultState());

	private final StateMap mapper = new StateMap.Builder().ignore(BlockFastener.TRIGGERED).build();

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		Map<IBlockState, ModelResourceLocation> map = mapper.putStateModelLocations(block);
		map.put(FENCE_FASTENER_STATE, FASTENER);
		return map;
	}
}
