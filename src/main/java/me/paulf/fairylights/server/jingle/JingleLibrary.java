package me.paulf.fairylights.server.jingle;

import com.google.common.collect.*;
import me.paulf.fairylights.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;

public class JingleLibrary {
    private static final String UNKNOWN_ID = "";

    private static final DefaultedRegistry<JingleLibrary> REGISTRY = new DefaultedRegistry<>(UNKNOWN_ID);

    private static final JingleLibrary UNKNOWN = register(new JingleLibrary(UNKNOWN_ID) {
        @Override
        public void load(final MinecraftServer server) {}
    });

    private static final int MAX_RANGE = 25;

    private static int nextFeatureId;

    private final String name;

    private final Map<String, Jingle> jingles = new HashMap<>();

    private final Multimap<Integer, Jingle> jinglesWithinRange = ArrayListMultimap.create();

    private final Map<Integer, Integer> rangeWeights = new HashMap<>();

    private JingleLibrary(final String name) {
        this.name = name;
    }

    public int getId() {
        return REGISTRY.getId(this);
    }

    public boolean contains(final String id) {
        return this.jingles.containsKey(id);
    }

    @Nullable
    public Jingle get(final String id) {
        return this.jingles.get(id);
    }

    private void put(final String id, final Jingle jingle) {
        this.jingles.put(id, jingle);
        for (int range = jingle.getRange(); range <= MAX_RANGE; range++) {
            this.jinglesWithinRange.put(range, jingle);
            this.rangeWeights.merge(range, jingle.getRange(), Math::addExact);
        }
    }

    @Nullable
    public synchronized Jingle getRandom(final Random rng, final int range) {
        final int fitRange = Math.min(range, MAX_RANGE);
        final Collection<Jingle> jingles = this.jinglesWithinRange.get(fitRange);
        if (jingles.isEmpty()) {
            return null;
        }
        float choice = rng.nextFloat() * this.rangeWeights.get(fitRange);
        for (final Jingle jingle : jingles) {
            choice -= jingle.getRange();
            if (choice <= 0) {
                return jingle;
            }
        }
        return null;
    }

    public void load(final MinecraftServer server) {
        try (final IResource resource = server.getResourceManager().getResource(new ResourceLocation(FairyLights.ID, "/jingles/" + this.name + ".dat"))) {
            this.deserialize(CompressedStreamTools.readCompressed(resource.getInputStream()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void deserialize(final CompoundNBT library) {
        this.jingles.clear();
        this.jinglesWithinRange.clear();
        this.rangeWeights.clear();
        for (final String id : library.keySet()) {
            final CompoundNBT jingleCompound = library.getCompound(id);
            final Jingle jingle = Jingle.from(jingleCompound);
            if (jingle.isValid() && !this.contains(jingle.getId())) {
                this.put(jingle.getId(), jingle);
            }
        }
    }

    public static JingleLibrary create(final String name) {
        return register(new JingleLibrary(name));
    }

    private static JingleLibrary register(final JingleLibrary library) {
        REGISTRY.register(nextFeatureId++, new ResourceLocation(library.name), library);
        return library;
    }

    public static JingleLibrary fromId(final int id) {
        return REGISTRY.getByValue(id);
    }

    public static void loadAll(final MinecraftServer server) {
        for (final JingleLibrary library : REGISTRY) {
            library.load(server);
        }
    }
}
