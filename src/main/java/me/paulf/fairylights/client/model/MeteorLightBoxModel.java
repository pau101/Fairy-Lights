package me.paulf.fairylights.client.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.client.renderer.model.PositionTextureVertex;
import net.minecraft.client.renderer.model.TexturedQuad;

public final class MeteorLightBoxModel extends ModelBox {
    private final PositionTextureVertex[] vertexPositions;

    private final TexturedQuad[] quadList;

    private final float posX1;

    private final float posY1;

    private final float posZ1;

    private final float posX2;

    private final float posY2;

    private final float posZ2;

    private final int type;

    public MeteorLightBoxModel(final RendererModel renderer, final int textureX, final int textureY, float x, final float y, final float z, final int width, final int height, final int depth, final int type) {
        super(renderer, textureX, textureY, x, y, z, width, height, depth, 0);
        this.type = type;
        this.posX1 = x;
        this.posY1 = y;
        this.posZ1 = z;
        this.posX2 = x + width;
        this.posY2 = y + height;
        this.posZ2 = z + depth;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float x2 = x + width;
        final float y2 = y + height;
        final float z2 = z + depth;
        if (renderer.mirror) {
            final float t = x2;
            x2 = x;
            x = t;
        }
        final PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        final PositionTextureVertex positiontexturevertex0 = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
        final PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(x2, y2, z, 8.0F, 8.0F);
        final PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, y2, z, 8.0F, 0.0F);
        final PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
        final PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
        final PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(x2, y2, z2, 8.0F, 8.0F);
        final PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, y2, z2, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex0;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex0, positiontexturevertex1, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, textureX, textureY + depth, textureX + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex0}, textureX + depth, textureY, textureX + depth + width, textureY + depth, renderer.textureWidth, renderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + width, textureY, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex0, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, textureX + depth + width + depth, textureY + depth, textureX + depth + width + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);
        if (renderer.mirror) {
            for (int i = 0; i < this.quadList.length; i++) {
                this.quadList[i].flipFace();
            }
        }
    }

    @Override
    public void render(final BufferBuilder buf, final float scale) {
        for (int i = 0; i < this.quadList.length; i++) {
            if (this.type != 1 && i == 2 || this.type != 0 && i == 3) {
                continue;
            }
            this.quadList[i].draw(buf, scale);
        }
    }
}
