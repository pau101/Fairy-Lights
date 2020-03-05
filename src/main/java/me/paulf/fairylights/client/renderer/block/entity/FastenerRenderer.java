package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

public class FastenerRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/connections.png");

    private final RenderType type = RenderType.getEntityTranslucent(TEXTURE);

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        for (final Connection conn : fastener.getConnections().values()) {
            if (conn.isOrigin()) {
                final Catenary catenary = conn.getCatenary();
                if (catenary != null) {
                    final Catenary.SegmentIterator it = catenary.iterator();
                    final IVertexBuilder buf = source.getBuffer(RenderType.getLines());
                    final Matrix4f m = matrix.peek().getModel();
                    while (it.next()) {
                        buf.vertex(m, it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F)).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                        buf.vertex(m, it.getX(1.0F), it.getY(1.0F), it.getZ(1.0F)).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
                    }
                }
                //conn.getType();
            }
        }
    }
}
