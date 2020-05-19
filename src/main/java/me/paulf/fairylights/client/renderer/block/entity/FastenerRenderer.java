package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandTinselConnection;
import me.paulf.fairylights.server.fastener.connection.type.garland.GarlandVineConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.letter.LetterBuntingConnection;
import me.paulf.fairylights.server.fastener.connection.type.pennant.PennantBuntingConnection;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public class FastenerRenderer {
    private final HangingLightsRenderer hangingLights = new HangingLightsRenderer();
    private final GarlandVineRenderer garland = new GarlandVineRenderer();
    private final GarlandTinselRenderer tinsel = new GarlandTinselRenderer();
    private final PennantBuntingRenderer pennants = new PennantBuntingRenderer();
    private final LetterBuntingRenderer letters = new LetterBuntingRenderer();

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        for (final Connection conn : fastener.getConnections().values()) {
            if (conn.isOrigin()) {
                if (conn instanceof HangingLightsConnection) {
                    this.hangingLights.render((HangingLightsConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                } else if (conn instanceof GarlandVineConnection) {
                    this.garland.render((GarlandVineConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                } else if (conn instanceof GarlandTinselConnection) {
                    this.tinsel.render((GarlandTinselConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                } else if (conn instanceof PennantBuntingConnection) {
                    this.pennants.render((PennantBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                } else if (conn instanceof LetterBuntingConnection) {
                    this.letters.render((LetterBuntingConnection) conn, delta, matrix, source, packedLight, packedOverlay);
                }
            }
        }
    }
}
