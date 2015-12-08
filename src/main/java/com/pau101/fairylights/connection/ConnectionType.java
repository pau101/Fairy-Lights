package com.pau101.fairylights.connection;

import net.minecraft.util.MathHelper;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.model.connection.ModelConnection;
import com.pau101.fairylights.client.model.connection.ModelConnectionFairyLights;
import com.pau101.fairylights.client.model.connection.ModelConnectionGarland;
import com.pau101.fairylights.client.model.connection.ModelConnectionTinsel;
import com.pau101.fairylights.item.ItemConnection;
import com.pau101.fairylights.tileentity.connection.Connection;

public enum ConnectionType {
	FAIRY_LIGHTS() {
		@Override
		public ConnectionLogic createLogic(Connection connection) {
			return new ConnectionLogicFairyLights(connection);
		}

		@Override
		public ItemConnection getItem() {
			return FairyLights.fairyLights;
		}

		@Override
		public ModelConnection createRenderer() {
			return new ModelConnectionFairyLights();
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection.getLogic() instanceof ConnectionLogicFairyLights;
		}
	},
	GARLAND() {
		@Override
		public ConnectionLogic createLogic(Connection connection) {
			return new ConnectionLogicGarland(connection);
		}

		@Override
		public ItemConnection getItem() {
			return FairyLights.garland;
		}

		@Override
		public ModelConnection createRenderer() {
			return new ModelConnectionGarland();
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection.getLogic() instanceof ConnectionLogicGarland;
		}
	},
	TINSEL() {
		@Override
		public ConnectionLogic createLogic(Connection connection) {
			return new ConnectionLogicTinsel(connection);
		}

		@Override
		public ItemConnection getItem() {
			return FairyLights.tinsel;
		}

		@Override
		public ModelConnection createRenderer() {
			return new ModelConnectionTinsel();
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection.getLogic() instanceof ConnectionLogicTinsel;
		}
	};

	public abstract ConnectionLogic createLogic(Connection connection);

	public abstract ItemConnection getItem();

	public abstract ModelConnection createRenderer();

	public abstract boolean isConnectionThis(Connection connection);

	public static ConnectionType from(int ordinal) {
		ConnectionType[] values = values();
		return values[MathHelper.clamp_int(ordinal, 0, values.length - 1)];
	}
}
