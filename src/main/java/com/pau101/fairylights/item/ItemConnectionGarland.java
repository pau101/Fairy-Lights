package com.pau101.fairylights.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.connection.ConnectionType;

import net.minecraft.block.Block;

public class ItemConnectionGarland extends ItemConnection {
	public ItemConnectionGarland() {
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.GARLAND;
	}
}
