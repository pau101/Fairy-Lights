package me.paulf.fairylights.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class RegistryObjects {
    private RegistryObjects() {}

    public static boolean nameEquals(final IForgeRegistryEntry<?> entry, final ResourceLocation name) {
        return name.equals(entry.getRegistryName());
    }

    public static boolean namespaceEquals(final IForgeRegistryEntry<?> entry, final String namespace) {
        final ResourceLocation name = entry.getRegistryName();
        return name != null && namespace.equals(name.getNamespace());
    }

    public static ResourceLocation getName(final IForgeRegistryEntry<?> type) {
        final ResourceLocation name = type.getRegistryName();
        if (name == null) {
            throw new NullPointerException("Missing registry name: " + type);
        }
        return name;
    }
}
