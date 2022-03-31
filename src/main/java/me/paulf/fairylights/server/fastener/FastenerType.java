package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.PlayerFastenerAccessor;
import net.minecraft.nbt.CompoundNBT;

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

    public static CompoundNBT serialize(final FastenerAccessor accessor) {
        final CompoundNBT compound = new CompoundNBT();
        compound.func_74778_a("type", accessor.getType().name);
        compound.func_218657_a("data", accessor.serialize());
        return compound;
    }

    public static FastenerAccessor deserialize(final CompoundNBT compound) {
        final FastenerAccessor accessor = NAME_TO_TYPE.get(compound.func_74779_i("type")).createAccessor();
        accessor.deserialize(compound.func_74775_l("data"));
        return accessor;
    }
}
