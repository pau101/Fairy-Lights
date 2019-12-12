package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.fastener.connection.ConnectionType;

public final class ItemConnectionGarland extends ItemConnection {
	public ItemConnectionGarland(Properties properties) {
		super(properties);
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.GARLAND;
	}
}
