package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.server.item.LightVariant;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.math.AxisAlignedBB;

public class LightBlockEntityRenderer extends TileEntityRenderer<LightBlockEntity> {
    private final LightRenderer lights = new LightRenderer();

    FastenerModel fastener = new FastenerModel();

    public LightBlockEntityRenderer(final TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    static class FastenerModel extends Model {
        final ModelRenderer model;

        FastenerModel() {
            super(RenderType::getEntityCutoutNoCull);
            this.textureWidth = 128;
            this.textureHeight = 128;
            this.model = new ModelRenderer(this, 82, 0);
            this.model.addBox(-1.0F, -1.0F, 0.05F, 2, 2, 8, -0.1F);
        }

        @Override
        public void render(final MatrixStack matrix, final IVertexBuilder buf, final int packedLight, final int packedOverlay, final float r, final float g, final float b, final float a) {
            this.model.render(matrix, buf, packedLight, packedOverlay, r, g, b, a);
        }
    }

    @Override
    public void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        this.render(entity, delta, matrix, source, packedLight, packedOverlay, entity.getLight());
    }

    private <T extends LightBehavior> void render(final LightBlockEntity entity, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay, final Light<T> light) {
        final LightModel<T> model = this.lights.getModel(light, -1);
        final AxisAlignedBB box = model.getBounds();
        final double h = -box.minY;
        final IVertexBuilder buf = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
        matrix.push();
        matrix.translate(0.5D, 0.5D, 0.5D);
        final BlockState state = entity.getBlockState();
        final AttachFace face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.HORIZONTAL_FACING).getHorizontalAngle();
        matrix.rotate(Vector3f.YP.rotationDegrees(180.0F - rotation));
        final LightVariant.Placement placement = light.getVariant().getPlacement();
        if (placement == LightVariant.Placement.UPRIGHT) {
            if (face == AttachFace.CEILING) {
                matrix.translate(0.0D, 0.25D, 0.0D);
                matrix.push();
                matrix.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                this.fastener.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0f, 1.0F, 1.0F);
                matrix.pop();
            } else if (face == AttachFace.WALL) {
                matrix.translate(0.0D, 0.15D, 0.125D);
                this.fastener.render(matrix, buf, packedLight, packedOverlay, 1.0F, 1.0f, 1.0F, 1.0F);
            } else {
                matrix.translate(0.0D, h - 0.5D, 0.0D);
            }
        } else {
            if (face == AttachFace.CEILING) {
                if (placement == LightVariant.Placement.ONWARD) {
                    matrix.rotate(Vector3f.XP.rotationDegrees(-180.0F));
                }
            } else if (face == AttachFace.WALL) {
                matrix.rotate(Vector3f.XP.rotationDegrees(placement == LightVariant.Placement.OUTWARD ? 90.0F : -90.0F));
            } else {
                if (placement == LightVariant.Placement.OUTWARD) {
                    matrix.rotate(Vector3f.XP.rotationDegrees(-180.0F));
                }
            }
            matrix.translate(0.0D, placement == LightVariant.Placement.OUTWARD ? 0.5D : h - 0.5D, 0.0D);
        }
        this.lights.render(matrix, this.lights.start(source), light, model, delta, packedLight, packedOverlay);
        matrix.pop();
    }
}
