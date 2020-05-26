package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import me.paulf.fairylights.client.*;
import me.paulf.fairylights.client.model.light.*;
import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.server.fastener.connection.type.garland.*;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.*;
import me.paulf.fairylights.server.fastener.connection.type.letter.*;
import me.paulf.fairylights.server.fastener.connection.type.pennant.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

public class FastenerRenderer {
    private final HangingLightsRenderer hangingLights = new HangingLightsRenderer();
    private final GarlandVineRenderer garland = new GarlandVineRenderer();
    private final GarlandTinselRenderer tinsel = new GarlandTinselRenderer();
    private final PennantBuntingRenderer pennants = new PennantBuntingRenderer();
    private final LetterBuntingRenderer letters = new LetterBuntingRenderer();
    private final BowModel bow = new BowModel();

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        boolean renderBow = true;
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
            if (renderBow && conn instanceof GarlandVineConnection && fastener.getFacing().getAxis() != Direction.Axis.Y) {
                final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
                matrix.push();
                matrix.rotate(Vector3f.YP.rotationDegrees(180.0F - fastener.getFacing().getHorizontalAngle()));
                this.bow.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrix.pop();
                renderBow = false;
            }
        }
    }
}
