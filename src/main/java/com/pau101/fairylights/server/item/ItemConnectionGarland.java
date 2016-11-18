package com.pau101.fairylights.server.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;

public final class ItemConnectionGarland extends ItemConnection {
	public ItemConnectionGarland() {
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.GARLAND;
	}
}
