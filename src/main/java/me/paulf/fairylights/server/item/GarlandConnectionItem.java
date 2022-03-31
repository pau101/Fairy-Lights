package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;

import net.minecraft.item.Item.Properties;

public final class GarlandConnectionItem extends ConnectionItem {
    public GarlandConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.VINE_GARLAND);
    }
}
