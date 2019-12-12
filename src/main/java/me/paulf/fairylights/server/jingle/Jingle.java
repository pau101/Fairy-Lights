package me.paulf.fairylights.server.jingle;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;
import me.paulf.fairylights.util.NBTSerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Jingle implements NBTSerializable {
	private static final Pattern LOWER_UNDERSCORE_CASE = Pattern.compile("[a-z0-9]+(_[a-z0-9]+)*");

	private static final int DEFAULT_LENGTH = 2;

	private String id;

	private String name;

	private String artist;

	private List<PlayTick> ticks;

	private int minNote = -1, maxNote = -1;

	private Jingle() {}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public int getLength() {
		int length = 0;
		for (PlayTick playTick : ticks) {
			length += playTick.getLength();
		}
		return length;
	}

	public List<PlayTick> getPlayTicks() {
		return ticks;
	}

	public int getLowestNote() {
		return minNote;
	}

	public int getRange() {
		return maxNote - minNote + 1;
	}

	public boolean isValid() {
		if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(artist)) {
			return false;
		}
		if (!LOWER_UNDERSCORE_CASE.matcher(id).matches()) {
			return false;
		}
		if (ticks == null || ticks.isEmpty()) {
			return false;
		}
		if (minNote > maxNote || minNote < 0) {
			return false;
		}
		for (PlayTick tick : ticks) {
			if (tick.length <= 0 || tick.length > 255 || tick.notes == null || tick.notes.length == 0) {
				return false;
			}
			for (int note : tick.notes) {
				if (note < 0 || note > 24) {
					return false;
				}
			}
		}
		return true;
	}

	private void calculateRange() {
		minNote = 24;
		maxNote = 0;
		for (PlayTick tick : ticks) {
			for (int note : tick.notes) {
				if (note > maxNote) {
					maxNote = note;
				}
				if (note < minNote) {
					minNote = note;
				}
			}
		}
	}

	@Override
	public CompoundNBT serialize() {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("id", id);
		compound.putString("name", name);
		compound.putString("artist", artist);
		ListNBT tickList = new ListNBT();
		for (PlayTick tick : ticks) {
			CompoundNBT tickCompound = new CompoundNBT();
			int notes = 0;
			for (int note : tick.notes) {
				notes |= 1 << note;
			}
			tickCompound.putInt("notes", notes);
			tickCompound.putByte("length", UnsignedBytes.checkedCast(tick.length));
			tickList.add(tickCompound);
		}
		compound.put("ticks", tickList);
		return compound;
	}

	@Override
	public void deserialize(CompoundNBT compound) {
		id = compound.getString("id");
		name = compound.getString("name");
		artist = compound.getString("artist");
		ListNBT tickList = compound.getList("ticks", NBT.TAG_COMPOUND);
		ticks = new ArrayList<>(tickList.size());
		for (int i = 0; i < tickList.size(); i++) {
			CompoundNBT tickCompound = tickList.getCompound(i);
			int noteBits = tickCompound.getInt("notes");
			int[] notes = new int[Integer.bitCount(noteBits)];
			for (int idx = 0, note = 0; note < 25; note++) {
				if ((noteBits & (1 << note)) > 0) {
					notes[idx++] = note;
				}
			}
			int length;
			if (tickCompound.contains("length", NBT.TAG_ANY_NUMERIC)) {
				length = tickCompound.getByte("length") & 0xFF;
			} else {
				length = DEFAULT_LENGTH;
			}
			ticks.add(new PlayTick(notes, length));
		}
		calculateRange();
	}

	public static final class PlayTick {
		private final int[] notes;

		private final int length;

		public PlayTick(int[] notes, int length) {
			this.notes = notes;
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public int[] getNotes() {
			return notes;
		}
	}

	public static Jingle from(CompoundNBT compound) {
		Jingle jingle = new Jingle();
		jingle.deserialize(compound);
		return jingle;
	}
}
