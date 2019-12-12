package com.pau101.fairylights.server.fastener.connection;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.garland.ConnectionGarlandTinsel;
import com.pau101.fairylights.server.fastener.connection.type.garland.ConnectionGarlandVine;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.server.fastener.connection.type.letter.ConnectionLetterBunting;
import com.pau101.fairylights.server.fastener.connection.type.pennant.ConnectionPennantBunting;
import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.item.ItemConnection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public enum ConnectionType {
	HANGING_LIGHTS {
		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
			return new ConnectionHangingLights(world, fastener, uuid, destination, isOrigin, compound);
		}

		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid) {
			return new ConnectionHangingLights(world, fastener, uuid);
		}

		@Override
		public ItemConnection getItem() {
			return FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new);
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection instanceof ConnectionHangingLights;
		}
	},
	GARLAND {
		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
			return new ConnectionGarlandVine(world, fastener, uuid, destination, isOrigin, compound);
		}

		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid) {
			return new ConnectionGarlandVine(world, fastener, uuid);
		}

		@Override
		public ItemConnection getItem() {
			return FLItems.GARLAND.orElseThrow(IllegalStateException::new);
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection instanceof ConnectionGarlandVine;
		}
	},
	TINSEL {
		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
			return new ConnectionGarlandTinsel(world, fastener, uuid, destination, isOrigin, compound);
		}

		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid) {
			return new ConnectionGarlandTinsel(world, fastener, uuid);
		}

		@Override
		public ItemConnection getItem() {
			return FLItems.TINSEL.orElseThrow(IllegalStateException::new);
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection instanceof ConnectionGarlandTinsel;
		}
	},
	PENNANT_BUNTING {
		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
			return new ConnectionPennantBunting(world, fastener, uuid, destination, isOrigin, compound);
		}

		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid) {
			return new ConnectionPennantBunting(world, fastener, uuid);
		}

		@Override
		public ItemConnection getItem() {
			return FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new);
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection instanceof ConnectionPennantBunting;
		}
	},
	LETTER_BUNTING {
		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
			return new ConnectionLetterBunting(world, fastener, uuid, destination, isOrigin, compound);
		}

		@Override
		public Connection createConnection(World world, Fastener<?> fastener, UUID uuid) {
			return new ConnectionLetterBunting(world, fastener, uuid);
		}

		@Override
		public ItemConnection getItem() {
			return FLItems.LETTER_BUNTING.orElseThrow(IllegalStateException::new);
		}

		@Override
		public boolean isConnectionThis(Connection connection) {
			return connection instanceof ConnectionLetterBunting;
		}
	};

	public abstract Connection createConnection(World world, Fastener<?> fastenerOrigin, UUID uuid, Fastener<?> fastenerDestination, boolean isOrigin, CompoundNBT compound);

	public abstract Connection createConnection(World world, Fastener<?> fastenerOrigin, UUID uuid);

	public abstract ItemConnection getItem();

	public abstract boolean isConnectionThis(Connection connection);

	public static ConnectionType from(int ordinal) {
		ConnectionType[] values = values();
		return values[MathHelper.clamp(ordinal, 0, values.length - 1)];
	}
}
