package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.model.light.BowModel;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.GarlandTinselConnection;
import me.paulf.fairylights.server.connection.GarlandVineConnection;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.connection.LetterBuntingConnection;
import me.paulf.fairylights.server.connection.PennantBuntingConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

public class FastenerRenderer {
    private final HangingLightsRenderer hangingLights = new HangingLightsRenderer();
    private final GarlandVineRenderer garland = new GarlandVineRenderer();
    private final GarlandTinselRenderer tinsel = new GarlandTinselRenderer();
    private final PennantBuntingRenderer pennants = new PennantBuntingRenderer();
    private final LetterBuntingRenderer letters = new LetterBuntingRenderer();
    private final BowModel bow = new BowModel();

    public void render(final Fastener<?> fastener, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        boolean renderBow = true;
        for (final Connection conn : fastener.getAllConnections()) {
            if (conn.getFastener() == fastener) {
                this.renderConnection(delta, matrix, source, packedLight, packedOverlay, conn);
            }
            if (renderBow && conn instanceof GarlandVineConnection && fastener.getFacing().func_176740_k() != Direction.Axis.Y) {
                final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.func_229311_a_(source, RenderType::func_228638_b_);
                matrix.func_227860_a_();
                matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - fastener.getFacing().func_185119_l()));
                this.bow.func_225598_a_(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrix.func_227865_b_();
                renderBow = false;
            }
        }
    }

    private void renderConnection(final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay, final Connection conn) {
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

    public static void renderBakedModel(final ResourceLocation path, final MatrixStack matrix, final IVertexBuilder buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(Minecraft.func_71410_x().func_209506_al().getModel(path), matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    public static void renderBakedModel(final IBakedModel model, final MatrixStack matrix, final IVertexBuilder buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        renderBakedModel(model, ItemCameraTransforms.TransformType.FIXED, matrix, buf, r, g, b, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    // (refusing to use handlePerspective due to IForgeTransformationMatrix#push superfluous undocumented MatrixStack#push)
    public static void renderBakedModel(final IBakedModel model, final ItemCameraTransforms.TransformType type, final MatrixStack matrix, final IVertexBuilder buf, final float r, final float g, final float b, final int packedLight, final int packedOverlay) {
        model.func_177552_f().func_181688_b(type).func_228830_a_(false, matrix);
        for (final Direction side : Direction.values()) {
            for (final BakedQuad quad : model.getQuads(null, side, new Random(42L), EmptyModelData.INSTANCE)) {
                buf.func_227889_a_(matrix.func_227866_c_(), quad, r, g, b, packedLight, packedOverlay);
            }
        }
        for (final BakedQuad quad : model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE)) {
            buf.func_227889_a_(matrix.func_227866_c_(), quad, r, g, b, packedLight, packedOverlay);
        }
    }
}
