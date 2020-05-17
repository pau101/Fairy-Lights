package me.paulf.fairylights.client;

import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class DefaultNormalVertexBuilder extends DefaultColorVertexBuilder implements IVertexConsumer {

    @Override
    public VertexFormatElement getCurrentElement() {
        return null;
    }

    @Override
    public void nextVertexFormatIndex() {

    }

    @Override
    public void putByte(final int indexIn, final byte byteIn) {

    }

    @Override
    public void putShort(final int indexIn, final short shortIn) {

    }

    @Override
    public void putFloat(final int indexIn, final float floatIn) {

    }

    @Override
    public void endVertex() {

    }
}
