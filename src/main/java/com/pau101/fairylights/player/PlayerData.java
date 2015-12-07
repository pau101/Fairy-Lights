package com.pau101.fairylights.player;

import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class PlayerData {
	public static PlayerData getPlayerData(EntityPlayer player) {
		PlayerData data = playerMap.get(player);
		if (data == null) {
			data = new PlayerData();
			playerMap.put(player, data);
		}
		return data;
	}

	public static void update() {
		for (Entry<EntityPlayer, PlayerData> dataEntry : playerMap.entrySet()) {
			BlockPos lastClicked = dataEntry.getValue().lastClicked;
			if (dataEntry.getKey().worldObj.getTileEntity(lastClicked) == null) {
				dataEntry.getValue().lastClicked = UNKNOWN;
			}
		}
	}

	private static Map<EntityPlayer, PlayerData> playerMap = new WeakHashMap<EntityPlayer, PlayerData>();

	private static final BlockPos UNKNOWN = new BlockPos(0, 0, 0);

	private BlockPos lastClicked;

	public PlayerData() {
		setUnknownLastClicked();
	}

	public BlockPos getLastClicked() {
		return lastClicked;
	}

	public boolean hasLastClicked() {
		return lastClicked != UNKNOWN;
	}

	public void setLastClicked(BlockPos pos) {
		lastClicked = pos;
	}

	public void setUnknownLastClicked() {
		lastClicked = UNKNOWN;
	}
}
