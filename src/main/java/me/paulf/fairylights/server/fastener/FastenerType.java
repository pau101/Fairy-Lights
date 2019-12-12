package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessorBlock;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessorFence;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessorPlayer;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum FastenerType {
	BLOCK(FastenerAccessorBlock::new),
	FENCE(FastenerAccessorFence::new),
	PLAYER(FastenerAccessorPlayer::new);

	private static final Map<String, FastenerType> NAME_TO_TYPE = new HashMap<>();

	static {
		for (FastenerType type : values()) {
			NAME_TO_TYPE.put(type.name, type);
		}
	}

	private final Supplier<? extends FastenerAccessor> supplier;

	private final String name;

	private FastenerType(Supplier<? extends FastenerAccessor> supplier) {
		this.supplier = supplier;
		name = name().toLowerCase(Locale.ENGLISH);
	}

	public final FastenerAccessor createAccessor() {
		return supplier.get();
	}

	public static CompoundNBT serialize(FastenerAccessor accessor) {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("type", accessor.getType().name);
		compound.put("data", accessor.serialize());
		return compound;
	}

	public static FastenerAccessor deserialize(CompoundNBT compound) {
		FastenerAccessor accessor = NAME_TO_TYPE.get(compound.getString("type")).createAccessor();
		accessor.deserialize(compound.getCompound("data"));
		return accessor;
	}
}
