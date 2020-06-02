package me.paulf.fairylights.server.jingle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.paulf.fairylights.FairyLights;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.resources.IResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JingleLibrary {
    private static final ResourceLocation DEFAULT_ID = new ResourceLocation(FairyLights.ID, "unknown");

    private static final DefaultedRegistry<JingleLibrary> REGISTRY = new DefaultedRegistry<>(DEFAULT_ID.toString());

    private static final JingleLibrary DEFAULT = register(new JingleLibrary(DEFAULT_ID) {
        @Override
        public void load(final MinecraftServer server) {
        }
    });

    public static final JingleLibrary CHRISTMAS = JingleLibrary.create("christmas");

    public static final JingleLibrary RANDOM = JingleLibrary.create("random");

    private static final int MAX_RANGE = 25;

    private final ResourceLocation name;

    private final Map<String, Jingle> jingles = new HashMap<>();

    private final Multimap<Integer, Jingle> jinglesWithinRange = ArrayListMultimap.create();

    private final Map<Integer, Integer> rangeWeights = new HashMap<>();

    private JingleLibrary(final ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation getName() {
        return this.name;
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
    public Jingle getRandom(final Random rng, final int range) {
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
        try (final IResource resource = server.getResourceManager().getResource(new ResourceLocation(this.name.getNamespace(), "/jingles/" + this.name.getPath() + ".dat"))) {
            this.deserialize(CompressedStreamTools.readCompressed(resource.getInputStream()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deserialize(final CompoundNBT library) {
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
        return register(new JingleLibrary(new ResourceLocation(FairyLights.ID, name)));
    }

    private static JingleLibrary register(final JingleLibrary library) {
        return Registry.register(REGISTRY, library.name, library);
    }

    public static JingleLibrary fromName(final ResourceLocation name) {
        return REGISTRY.getOrDefault(name);
    }

    public static void loadAll(final MinecraftServer server) {
        for (final JingleLibrary library : REGISTRY) {
            library.load(server);
        }
    }
}
