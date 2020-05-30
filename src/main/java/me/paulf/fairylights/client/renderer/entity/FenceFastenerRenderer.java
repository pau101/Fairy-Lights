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
    protected int getBlockLight(final FenceFastenerEntity entity, final float delta) {
        return entity.world.getLightFor(LightType.BLOCK, new BlockPos(entity));
    }

    @Override
    public void render(final FenceFastenerEntity entity, final float yaw, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight) {
        final IVertexBuilder buf = source.getBuffer(Atlases.getCutoutBlockType());
        matrix.push();
        FastenerRenderer.renderBakedModel(MODEL, matrix, buf, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        matrix.pop();
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> this.renderer.render(f, delta, matrix, source, packedLight, OverlayTexture.NO_OVERLAY));
        super.render(entity, yaw, delta, matrix, source, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(final FenceFastenerEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
