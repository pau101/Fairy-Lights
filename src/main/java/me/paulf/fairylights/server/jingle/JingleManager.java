package me.paulf.fairylights.server.jingle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class JingleManager extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Gson GSON = new GsonBuilder().create();

    public static final JingleManager INSTANCE = new JingleManager();

    private Object2ObjectMap<String, JingleLibrary> libraries = Object2ObjectMaps.emptyMap();

    public JingleManager() {
        super(GSON, "jingles");
    }

    public JingleLibrary get(final String library) {
        return this.libraries.getOrDefault(library, JingleLibrary.empty());
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> elements, final IResourceManager manager, final IProfiler profiler) {
        final Object2ObjectMap<String, JingleLibrary.Builder> builders = new Object2ObjectOpenHashMap<>();
        elements.forEach((file, json) -> {
            final String path = file.getPath();
            final int sl = path.indexOf('/');
            final String library = path.substring(0, Math.max(0, sl));
            final String name = path.substring(sl + 1);
            Jingle.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.warn("Parsing error loading jingle {}: {}", file, error))
                .ifPresent(jingle -> builders.computeIfAbsent(library, l -> new JingleLibrary.Builder()).add(name, jingle));
        });
        final Object2ObjectMap<String, JingleLibrary> libraries = new Object2ObjectOpenHashMap<>(builders.size());
        Object2ObjectMaps.fastForEach(builders, e -> libraries.put(e.getKey(), e.getValue().build()));
        this.libraries = libraries;
    }
}
