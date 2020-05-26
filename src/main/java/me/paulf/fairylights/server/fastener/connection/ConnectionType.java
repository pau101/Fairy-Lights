package me.paulf.fairylights.server.fastener.connection;

import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.server.fastener.connection.type.garland.*;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.*;
import me.paulf.fairylights.server.fastener.connection.type.letter.*;
import me.paulf.fairylights.server.fastener.connection.type.pennant.*;
import me.paulf.fairylights.server.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public enum ConnectionType {
    HANGING_LIGHTS {
        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
            return new HangingLightsConnection(world, fastener, uuid, destination, isOrigin, compound);
        }

        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
            return new HangingLightsConnection(world, fastener, uuid);
        }

        @Override
        public ConnectionItem getItem() {
            return FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new);
        }

        @Override
        public boolean isInstance(final Connection connection) {
            return connection instanceof HangingLightsConnection;
        }
    },
    GARLAND {
        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
            return new GarlandVineConnection(world, fastener, uuid, destination, isOrigin, compound);
        }

        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
            return new GarlandVineConnection(world, fastener, uuid);
        }

        @Override
        public ConnectionItem getItem() {
            return FLItems.GARLAND.orElseThrow(IllegalStateException::new);
        }

        @Override
        public boolean isInstance(final Connection connection) {
            return connection instanceof GarlandVineConnection;
        }
    },
    TINSEL {
        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
            return new GarlandTinselConnection(world, fastener, uuid, destination, isOrigin, compound);
        }

        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
            return new GarlandTinselConnection(world, fastener, uuid);
        }

        @Override
        public ConnectionItem getItem() {
            return FLItems.TINSEL.orElseThrow(IllegalStateException::new);
        }

        @Override
        public boolean isInstance(final Connection connection) {
            return connection instanceof GarlandTinselConnection;
        }
    },
    PENNANT_BUNTING {
        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
            return new PennantBuntingConnection(world, fastener, uuid, destination, isOrigin, compound);
        }

        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
            return new PennantBuntingConnection(world, fastener, uuid);
        }

        @Override
        public ConnectionItem getItem() {
            return FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new);
        }

        @Override
        public boolean isInstance(final Connection connection) {
            return connection instanceof PennantBuntingConnection;
        }
    },
    LETTER_BUNTING {
        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
            return new LetterBuntingConnection(world, fastener, uuid, destination, isOrigin, compound);
        }

        @Override
        public Connection createConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
            return new LetterBuntingConnection(world, fastener, uuid);
        }

        @Override
        public ConnectionItem getItem() {
            return FLItems.LETTER_BUNTING.orElseThrow(IllegalStateException::new);
        }

        @Override
        public boolean isInstance(final Connection connection) {
            return connection instanceof LetterBuntingConnection;
        }
    };

    public abstract Connection createConnection(World world, Fastener<?> fastenerOrigin, UUID uuid, Fastener<?> fastenerDestination, boolean isOrigin, CompoundNBT compound);

    public abstract Connection createConnection(World world, Fastener<?> fastenerOrigin, UUID uuid);

    public abstract ConnectionItem getItem();

    public abstract boolean isInstance(Connection connection);

    public static ConnectionType from(final int ordinal) {
        final ConnectionType[] values = values();
        return values[MathHelper.clamp(ordinal, 0, values.length - 1)];
    }
}
