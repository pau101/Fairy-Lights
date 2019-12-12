package com.pau101.fairylights.server.item;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;

public final class ItemConnectionGarland extends ItemConnection {
	public ItemConnectionGarland(Properties properties) {
		super(properties);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.GARLAND;
	}
}
