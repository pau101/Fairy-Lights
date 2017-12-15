package com.pau101.fairylights.server.block;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.util.Utils;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(FairyLights.ID)
@EventBusSubscriber(modid = FairyLights.ID)
public final class FLBlocks {
	public static final BlockFastener FASTENER = null;

	private FLBlocks() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockFastener());
		GameRegistry.registerTileEntity(BlockEntityFastener.class, FairyLights.ID + ":fastener");
	}
}
