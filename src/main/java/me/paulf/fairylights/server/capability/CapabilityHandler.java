package me.paulf.fairylights.server.capability;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class CapabilityHandler {
    private CapabilityHandler() {}

    public static final ResourceLocation FASTENER_ID = new ResourceLocation(FairyLights.ID, "fastener");

    public static final Capability<Fastener<?>> FASTENER_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register() {
    }
}
