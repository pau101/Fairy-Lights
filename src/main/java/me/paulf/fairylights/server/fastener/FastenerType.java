package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.PlayerFastenerAccessor;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum FastenerType {
    BLOCK(BlockFastenerAccessor::new),
    FENCE(FenceFastenerAccessor::new),
    PLAYER(PlayerFastenerAccessor::new);

    private static final Map<String, FastenerType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (final FastenerType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    private final Supplier<? extends FastenerAccessor> supplier;

    private final String name;

    FastenerType(final Supplier<? extends FastenerAccessor> supplier) {
        this.supplier = supplier;
        this.name = this.name().toLowerCase(Locale.ENGLISH);
    }

    public final FastenerAccessor createAccessor() {
        return this.supplier.get();
    }

    public static CompoundTag serialize(final FastenerAccessor accessor) {
        final CompoundTag compound = new CompoundTag();
        compound.putString("type", accessor.getType().name);
        compound.put("data", accessor.serialize());
        return compound;
    }

    public static FastenerAccessor deserialize(final CompoundTag compound) {
        final FastenerAccessor accessor = NAME_TO_TYPE.get(compound.getString("type")).createAccessor();
        accessor.deserialize(compound.getCompound("data"));
        return accessor;
    }
}
