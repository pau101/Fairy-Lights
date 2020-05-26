package me.paulf.fairylights.server.jingle;

import com.google.common.base.*;
import com.google.common.primitives.*;
import me.paulf.fairylights.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants.*;

import java.util.*;
import java.util.regex.*;

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
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public int getLength() {
        int length = 0;
        for (final PlayTick playTick : this.ticks) {
            length += playTick.getLength();
        }
        return length;
    }

    public List<PlayTick> getPlayTicks() {
        return this.ticks;
    }

    public int getLowestNote() {
        return this.minNote;
    }

    public int getRange() {
        return this.maxNote - this.minNote + 1;
    }

    public boolean isValid() {
        if (Strings.isNullOrEmpty(this.id) || Strings.isNullOrEmpty(this.name) || Strings.isNullOrEmpty(this.artist)) {
            return false;
        }
        if (!LOWER_UNDERSCORE_CASE.matcher(this.id).matches()) {
            return false;
        }
        if (this.ticks == null || this.ticks.isEmpty()) {
            return false;
        }
        if (this.minNote > this.maxNote || this.minNote < 0) {
            return false;
        }
        for (final PlayTick tick : this.ticks) {
            if (tick.length <= 0 || tick.length > 255 || tick.notes == null || tick.notes.length == 0) {
                return false;
            }
            for (final int note : tick.notes) {
                if (note < 0 || note > 24) {
                    return false;
                }
            }
        }
        return true;
    }

    private void calculateRange() {
        this.minNote = 24;
        this.maxNote = 0;
        for (final PlayTick tick : this.ticks) {
            for (final int note : tick.notes) {
                if (note > this.maxNote) {
                    this.maxNote = note;
                }
                if (note < this.minNote) {
                    this.minNote = note;
                }
            }
        }
    }

    @Override
    public CompoundNBT serialize() {
        final CompoundNBT compound = new CompoundNBT();
        compound.putString("id", this.id);
        compound.putString("name", this.name);
        compound.putString("artist", this.artist);
        final ListNBT tickList = new ListNBT();
        for (final PlayTick tick : this.ticks) {
            final CompoundNBT tickCompound = new CompoundNBT();
            int notes = 0;
            for (final int note : tick.notes) {
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
    public void deserialize(final CompoundNBT compound) {
        this.id = compound.getString("id");
        this.name = compound.getString("name");
        this.artist = compound.getString("artist");
        final ListNBT tickList = compound.getList("ticks", NBT.TAG_COMPOUND);
        this.ticks = new ArrayList<>(tickList.size());
        for (int i = 0; i < tickList.size(); i++) {
            final CompoundNBT tickCompound = tickList.getCompound(i);
            final int noteBits = tickCompound.getInt("notes");
            final int[] notes = new int[Integer.bitCount(noteBits)];
            for (int idx = 0, note = 0; note < 25; note++) {
                if ((noteBits & (1 << note)) > 0) {
                    notes[idx++] = note;
                }
            }
            final int length;
            if (tickCompound.contains("length", NBT.TAG_ANY_NUMERIC)) {
                length = tickCompound.getByte("length") & 0xFF;
            } else {
                length = DEFAULT_LENGTH;
            }
            this.ticks.add(new PlayTick(notes, length));
        }
        this.calculateRange();
    }

    public static final class PlayTick {
        private final int[] notes;

        private final int length;

        public PlayTick(final int[] notes, final int length) {
            this.notes = notes;
            this.length = length;
        }

        public int getLength() {
            return this.length;
        }

        public int[] getNotes() {
            return this.notes;
        }
    }

    public static Jingle from(final CompoundNBT compound) {
        final Jingle jingle = new Jingle();
        jingle.deserialize(compound);
        return jingle;
    }
}
