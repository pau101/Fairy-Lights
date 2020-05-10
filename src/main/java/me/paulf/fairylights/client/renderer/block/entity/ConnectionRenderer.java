package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public abstract class ConnectionRenderer<C extends Connection> {
    public final void render(final C conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        final Catenary currCat = conn.getCatenary();
        final Catenary prevCat = conn.getPrevCatenary();
        if (currCat != null && prevCat != null) {
            final Catenary cat = prevCat.lerp(currCat, delta);
            this.render(cat, delta, matrix, source, packedLight, packedOverlay);
        }
    }

    protected abstract void render(final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay);
}
