package com.pau101.fairylights.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.pau101.fairylights.util.vectormath.Point3i;

public class PlayerData {
	public static PlayerData getPlayerData(EntityPlayer player) {
		PlayerData data = playerMap.get(player);
		if (data == null && player != null) {
			data = new PlayerData();
			playerMap.put(player, data);
		}
		return data;
	}

	public static void takeOutTheGarbage(World world) {
		Iterator<EntityPlayer> playerIterator = playerMap.keySet().iterator();
		ArrayList<EntityPlayer> removeList = new ArrayList();
		while (playerIterator.hasNext()) {
			EntityPlayer player = playerIterator.next();
			if (!world.playerEntities.contains(player) && player.worldObj == world) {
				removeList.add(player);
			}
		}
		playerMap.keySet().removeAll(removeList);
	}

	public static void update() {
		for (Entry<EntityPlayer, PlayerData> dataEntry : playerMap.entrySet()) {
			Point3i lastClicked = dataEntry.getValue().lastClicked;
			if (dataEntry.getKey().worldObj.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z) == null) {
				dataEntry.getValue().lastClicked = UNKNOWN;
			}
		}
	}

	private static Map<EntityPlayer, PlayerData> playerMap = new HashMap<EntityPlayer, PlayerData>();

	private static final Point3i UNKNOWN = new Point3i();

	private Point3i lastClicked;

	public PlayerData() {
		setUnknownLastClicked();
	}

	public Point3i getLastClicked() {
		return lastClicked;
	}

	public boolean hasLastClicked() {
		return lastClicked != UNKNOWN;
	}

	public void setLastClicked(int x, int y, int z) {
		lastClicked = new Point3i(x, y, z);
	}

	public void setUnknownLastClicked() {
		lastClicked = UNKNOWN;
	}
}
