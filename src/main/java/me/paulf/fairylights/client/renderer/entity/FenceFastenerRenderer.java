package me.paulf.fairylights.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.renderer.block.entity.FastenerRenderer;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public final class FenceFastenerRenderer extends EntityRenderer<FenceFastenerEntity> {
    public static final ResourceLocation MODEL = new ResourceLocation(FairyLights.ID, "block/fence_fastener");

    private final FastenerRenderer renderer = new FastenerRenderer();

    public FenceFastenerRenderer(final EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected int func_225624_a_(final FenceFastenerEntity entity, final BlockPos delta) {
        return entity.field_70170_p.func_226658_a_(LightType.BLOCK, entity.func_233580_cy_());
    }

    @Override
    public void func_225623_a_(final FenceFastenerEntity entity, final float yaw, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight) {
        final IVertexBuilder buf = source.getBuffer(Atlases.func_228783_h_());
        matrix.func_227860_a_();
        FastenerRenderer.renderBakedModel(MODEL, matrix, buf, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.field_229196_a_);
        matrix.func_227865_b_();
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> this.renderer.render(f, delta, matrix, source, packedLight, OverlayTexture.field_229196_a_));
        super.func_225623_a_(entity, yaw, delta, matrix, source, packedLight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation func_110775_a(final FenceFastenerEntity entity) {
        return AtlasTexture.field_110575_b;
    }
}
