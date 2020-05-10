package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;

public class FastenerRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/connections.png");

    private final HangingLightsRenderer hangingLights = new HangingLightsRenderer();

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        for (final Connection conn : fastener.getConnections().values()) {
            if (conn.isOrigin()) {
                if (conn instanceof HangingLightsConnection) {
                    this.hangingLights.render((HangingLightsConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                }
            }
        }
    }
}
