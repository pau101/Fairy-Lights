package me.paulf.fairylights.server.jingle;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Random;

public class JingleLibrary {
    public static final String CHRISTMAS = "christmas";

    public static final String HALLOWEEN = "halloween";

    public static final String RANDOM = "";

    private static final int MAX_RANGE = 25;

    private final Object2ObjectMap<ResourceLocation, Jingle> jingles;

    private final Int2ObjectMap<RangeSet> ranges;

    private JingleLibrary() {
        this.jingles = Object2ObjectMaps.emptyMap();
        this.ranges = Int2ObjectMaps.emptyMap();
    }

    private JingleLibrary(final Builder builder) {
        this.jingles = Object2ObjectMaps.unmodifiable(new Object2ObjectOpenHashMap<>(builder.jingles));
        final Int2ObjectMap<RangeSet> ranges = new Int2ObjectOpenHashMap<>();
        Int2ObjectMaps.fastForEach(builder.ranges, e -> ranges.put(e.getIntKey(), e.getValue().build()));
        this.ranges = Int2ObjectMaps.unmodifiable(ranges);
    }

    @Nullable
    public Jingle get(final ResourceLocation name) {
        return this.jingles.get(name);
    }

    @Nullable
    public Jingle getRandom(final Random rng, final int range) {
        final RangeSet jingles = this.ranges.get(Math.min(range, MAX_RANGE));
        return jingles == null ? null : jingles.get(rng);
    }

    public static JingleLibrary empty() {
        return new JingleLibrary();
    }

    private static class RangeSet {
        final ObjectList<Jingle> jingles;
        final int total;

        RangeSet(final Builder builder) {
            this.jingles = ObjectLists.unmodifiable(builder.jingles);
            this.total = builder.total;
        }

        @Nullable
        public Jingle get(final Random rng) {
            if (this.jingles.isEmpty()) {
                return null;
            }
            float choice = rng.nextFloat() * this.total;
            for (final Jingle jingle : this.jingles) {
                choice -= jingle.getRange();
                if (choice <= 0.0F) {
                    return jingle;
                }
            }
            return null;
        }

        static class Builder {
            final ObjectList<Jingle> jingles = new ObjectArrayList<>();
            int total;

            void add(final Jingle jingle) {
                this.jingles.add(jingle);
                this.total += jingle.getRange();
            }

            RangeSet build() {
                return new RangeSet(this);
            }
        }
    }

    public static class Builder {
        final Object2ObjectMap<ResourceLocation, Jingle> jingles = new Object2ObjectOpenHashMap<>();
        final Int2ObjectMap<RangeSet.Builder> ranges = new Int2ObjectOpenHashMap<>();

        public Builder add(final ResourceLocation id, final Jingle jingle) {
            this.jingles.put(id, jingle);
            for (int range = jingle.getRange(); range <= MAX_RANGE; range++) {
                this.ranges.computeIfAbsent(range, r -> new RangeSet.Builder()).add(jingle);
            }
            return this;
        }

        public JingleLibrary build() {
            return new JingleLibrary(this);
        }
    }
}
