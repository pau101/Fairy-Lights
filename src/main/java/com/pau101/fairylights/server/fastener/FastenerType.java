package com.pau101.fairylights.server.fastener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorBlock;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorFence;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorPlayer;

import net.minecraft.nbt.NBTTagCompound;

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

	private Supplier<? extends FastenerAccessor> supplier;

	private String name;

	private FastenerType(Supplier<? extends FastenerAccessor> supplier) {
		this.supplier = supplier;
		name = name().toLowerCase(Locale.ENGLISH);
	}

	public String getName() {
		return name;
	}

	public FastenerAccessor createAccessor() {
		return supplier.get();
	}

	public static NBTTagCompound serialize(FastenerAccessor accessor) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("type", accessor.getType().getName());
		compound.setTag("data", accessor.serialize());
		return compound;
	}

	public static FastenerAccessor deserialize(NBTTagCompound compound) {
		FastenerAccessor accessor = NAME_TO_TYPE.get(compound.getString("type")).createAccessor();
		accessor.deserialize(compound.getCompoundTag("data"));
		return accessor;
	}
}
