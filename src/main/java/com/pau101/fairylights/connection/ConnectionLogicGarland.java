package com.pau101.fairylights.connection;

import net.minecraft.util.MathHelper;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.CubicBezier;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ConnectionLogicGarland extends ConnectionLogic {
	private static final CubicBezier LENGTH_FUNC = new CubicBezier(0.4F, 0.45F, 0.6F, 0.6F);

	public ConnectionLogicGarland(Connection connection) {
		super(connection);
	}

	@Override
	public Catenary createCatenary(Point3f to) {
		Vector3f vec = new Vector3f(to);
		float length = MathHelper.clamp_float(vec.length(), 0, FairyLights.MAX_LENGTH);
		return Catenary.from(vec, LENGTH_FUNC.eval(length / FairyLights.MAX_LENGTH) * FairyLights.MAX_LENGTH);
	}
}
