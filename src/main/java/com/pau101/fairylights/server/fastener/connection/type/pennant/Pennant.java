package com.pau101.fairylights.server.fastener.connection.type.pennant;

import com.pau101.fairylights.server.fastener.connection.type.ConnectionHangingFeature.HangingFeature;

import net.minecraft.util.math.Vec3d;

public class Pennant extends HangingFeature<Pennant> {
	private int color;

	public Pennant(int index, Vec3d point, Vec3d rotation) {
		super(index, point, rotation);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	@Override
	public double getWidth() {
		return 0.4F;
	}

	@Override
	public double getHeight() {
		return 0.65F;
	}

	@Override
	public boolean parallelsCord() {
		return true;
	}
}
