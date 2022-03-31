package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;

public class LightBlockEntityRenderer extends TileEntityRenderer<LightBlockEntity> {
    private final LightRenderer lights = new LightRenderer();

    public LightBlockEntityRenderer(final TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        this.render(entity, delta, matrix, source, packedLight, packedOverlay, entity.getLight());
    }

    private <T extends LightBehavior> void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay, final Light<T> light) {
        final LightModel<T> model = this.lights.getModel(light, -1);
        final AxisAlignedBB box = model.getBounds();
        final BlockState state = entity.func_195044_w();
        final AttachFace face = state.func_177229_b(LightBlock.field_196366_M);
        final float rotation = state.func_177229_b(LightBlock.field_185512_D).func_185119_l();
        matrix.func_227860_a_();
        matrix.func_227861_a_(0.5D, 0.5D, 0.5D);
        matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - rotation));
        if (light.getVariant().isOrientable()) {
            if (face == AttachFace.WALL) {
                matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90.0F));
            } else if (face == AttachFace.FLOOR) {
                matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-180.0F));
            }
            matrix.func_227861_a_(0.0D, 0.5D, 0.0D);
        } else {
            if (face == AttachFace.CEILING) {
                matrix.func_227861_a_(0.0D, 0.25D, 0.0D);
            } else if (face == AttachFace.WALL) {
                matrix.func_227861_a_(0.0D, 3.0D / 16.0D, 0.125D);
            } else {
                matrix.func_227861_a_(0.0D, -box.field_72338_b - model.getFloorOffset() - 0.5D, 0.0D);
            }
        }
        this.lights.render(matrix, this.lights.start(source), light, model, delta, packedLight, packedOverlay);
        matrix.func_227865_b_();
    }
}
