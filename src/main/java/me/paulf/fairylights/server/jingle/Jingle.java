package me.paulf.fairylights.server.jingle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class Jingle {
    public static final Codec<Jingle> CODEC = RecordCodecBuilder.create(builder -> builder
        .group(
            Codec.STRING.fieldOf("title").forGetter(j -> j.name),
            Codec.STRING.fieldOf("artist").forGetter(j -> j.artist),
            PlayTick.CODEC.listOf().fieldOf("ticks").xmap(l -> ObjectLists.unmodifiable(new ObjectArrayList<>(l)), l -> l).forGetter(j -> j.ticks)
        )
        .apply(builder, Jingle::new)
    );

    private String name;

    private String artist;

    private ObjectList<PlayTick> ticks;

    private int range = -1;

    private int min;

    private Jingle(final String name, final String artist, final ObjectList<PlayTick> ticks) {
        this.name = name;
        this.artist = artist;
        this.ticks = ticks;
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
            length += playTick.getDuration();
        }
        return length;
    }

    public List<PlayTick> getPlayTicks() {
        return this.ticks;
    }

    public int getLowestNote() {
        this.calculateRange();
        return this.min;
    }

    public int getRange() {
        this.calculateRange();
        return this.range;
    }

    private void calculateRange() {
        if (this.range == -1) {
            if (this.ticks.isEmpty()) {
                this.min = 0;
                this.range = 1;
            } else {
                int minNote = 24;
                int maxNote = 0;
                for (final PlayTick tick : this.ticks) {
                    for (final int note : tick.notes) {
                        maxNote = Math.max(maxNote, note);
                        minNote = Math.min(minNote, note);
                    }
                }
                this.min = minNote;
                this.range = maxNote - minNote + 1;
            }
        }
    }

    static final class PlayTick {
        public static final Codec<PlayTick> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                Codec.INT.fieldOf("duration").forGetter(t -> t.duration),
                Codec.INT_STREAM.fieldOf("notes").xmap(IntStream::toArray, Arrays::stream).forGetter(t -> t.notes)
            )
            .apply(builder, PlayTick::new)
        );

        final int duration;

        final int[] notes;

        PlayTick(final int duration, final int[] notes) {
            this.duration = duration;
            this.notes = notes;
        }

        public int getDuration() {
            return this.duration;
        }

        public int[] getNotes() {
            return this.notes;
        }
    }
}
