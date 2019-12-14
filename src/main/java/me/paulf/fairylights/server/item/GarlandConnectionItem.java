package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.ConnectionType;

public final class GarlandConnectionItem extends ConnectionItem {
	public GarlandConnectionItem(Properties properties) {
		super(properties);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.GARLAND;
	}
}
