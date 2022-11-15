package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class ForwardingVertexConsumer implements VertexConsumer {
    protected abstract VertexConsumer delegate();

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return this.delegate().vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        return this.delegate().color(r, g, b, a);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return this.delegate().uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this.delegate().overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return this.delegate().uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this.delegate().normal(x, y, z);
    }

    @Override
    public void endVertex() {
        this.delegate().endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        this.delegate().defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        this.delegate().unsetDefaultColor();
    }
}
