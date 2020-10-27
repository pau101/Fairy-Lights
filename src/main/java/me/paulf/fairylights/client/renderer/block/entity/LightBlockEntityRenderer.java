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
    public void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        this.render(entity, delta, matrix, source, packedLight, packedOverlay, entity.getLight());
    }

    private <T extends LightBehavior> void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay, final Light<T> light) {
        final LightModel<T> model = this.lights.getModel(light, -1);
        final AxisAlignedBB box = model.getBounds();
        final BlockState state = entity.getBlockState();
        final AttachFace face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.HORIZONTAL_FACING).getHorizontalAngle();
        matrix.push();
        matrix.translate(0.5D, 0.5D, 0.5D);
        matrix.rotate(Vector3f.YP.rotationDegrees(180.0F - rotation));
        if (light.getVariant().isOrientable()) {
            if (face == AttachFace.WALL) {
                matrix.rotate(Vector3f.XP.rotationDegrees(90.0F));
            } else if (face == AttachFace.FLOOR) {
                matrix.rotate(Vector3f.XP.rotationDegrees(-180.0F));
            }
            matrix.translate(0.0D, 0.5D, 0.0D);
        } else {
            if (face == AttachFace.CEILING) {
                matrix.translate(0.0D, 0.25D, 0.0D);
            } else if (face == AttachFace.WALL) {
                matrix.translate(0.0D, 3.0D / 16.0D, 0.125D);
            } else {
                matrix.translate(0.0D, -box.minY - model.getFloorOffset() - 0.5D, 0.0D);
            }
        }
        this.lights.render(matrix, this.lights.start(source), light, model, delta, packedLight, packedOverlay);
        matrix.pop();
    }
}
