package com.pau101.fairylights.connection;

import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.util.CubicBezier;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class Light {
	private static final CubicBezier EASE_IN_OUT = new CubicBezier(0.4F, 0, 0.6F, 1);

	private static final int NORMAL_LIGHT = -1;

	private Point3f point;

	private Vector3f rotation;

	private LightVariant variant;

	private Vector3f color;

	private int prevTwinkleTime;

	private int twinkleTime;

	private boolean isTwinkling;

	private int sway;

	private boolean swaying;

	private boolean swayDirection;

	private int swayRate = 10;

	private int swayPeakCount = 5;

	private int swayCycle = swayRate * swayPeakCount;

	public Light(Point3f point) {
		this.point = point;
		rotation = new Vector3f();
		color = new Vector3f(0xFF / (float) 0xFF, 0xEA / (float) 0xFF, 0xC1 / (float) 0xFF);
		twinkleTime = NORMAL_LIGHT;
		sway = 0;
		swaying = false;
	}

	public Point3f getAbsolutePoint(TileEntityConnectionFastener fastener) {
		Point3f point = getPoint();
		point.scale(0.0625F);
		point.add(fastener.getConnectionPoint());
		return point;
	}

	public boolean isTwinkling() {
		return isTwinkling;
	}

	public float getBrightness(float delta) {
		return twinkleTime == NORMAL_LIGHT ? isTwinkling ? 0 : 1 : brightnessFunc((prevTwinkleTime + (twinkleTime - prevTwinkleTime) * delta) / getVariant().getTickCycle());
	}

	public float getTwinkleTimePercent(float delta) {
		return twinkleTime == NORMAL_LIGHT ? 0 : (prevTwinkleTime + (twinkleTime - prevTwinkleTime) * delta) / getVariant().getTickCycle();
	}

	private float brightnessFunc(float x) {
		return x < 0.25F ? EASE_IN_OUT.eval(x / 0.25F) : 1 - EASE_IN_OUT.eval(MathUtils.linearTransformf(x, 0.25F, 1, 0, 1));
	}

	public LightVariant getVariant() {
		return variant == null ? LightVariant.FAIRY : variant;
	}

	public Vector3f getLight() {
		return color;
	}

	public Point3f getPoint() {
		return new Point3f(point);
	}

	public Vector3f getRotation() {
		return new Vector3f(rotation);
	}

	public void setVariant(LightVariant variant) {
		this.variant = variant;
	}

	public void setColor(int colorValue) {
		color = new Vector3f((colorValue >> 16 & 0xFF) / (float) 0xFF, (colorValue >> 8 & 0xFF) / (float) 0xff, (colorValue & 0xFF) / (float) 0xFF);
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void setTwinkleTime(int twinkleTime) {
		this.twinkleTime = twinkleTime;
	}

	public int getTwinkleTime() {
		return twinkleTime;
	}

	public void startSwaying() {
		swayDirection = Math.random() < 0.5F;
		swaying = true;
		sway = 0;
	}

	public void stopSwaying() {
		sway = 0;
		rotation.z = 0;
		swaying = false;
	}

	public void tick(ConnectionLogicFairyLights connectionLogic, boolean twinkle) {
		prevTwinkleTime = twinkleTime;
		isTwinkling = twinkle;
		if (isTwinkling || getVariant().alwaysDoTwinkleLogic()) {
			if (connectionLogic.getConnection().getWorldObj().rand.nextFloat() < getVariant().getTwinkleChance() && twinkleTime == NORMAL_LIGHT) {
				twinkleTime = 0;
			}
			if (twinkleTime >= 0) {
				twinkleTime++;
			}
			if (twinkleTime == getVariant().getTickCycle()) {
				twinkleTime = NORMAL_LIGHT;
			}
		}
		if (swaying) {
			if (sway == swayCycle) {
				stopSwaying();
			}
			rotation.z = (float) (Math.sin((swayDirection ? 1 : -1) * 2 * Math.PI / swayRate * sway) * Math.pow(180 / Math.PI * 2, -sway++ / (float) swayCycle));
		}
	}
}
