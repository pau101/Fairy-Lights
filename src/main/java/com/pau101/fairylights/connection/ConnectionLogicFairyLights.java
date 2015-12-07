package com.pau101.fairylights.connection;

import java.util.ArrayList;
import java.util.List;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.eggs.JinglePlayer;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.Segment;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ConnectionLogicFairyLights extends ConnectionLogic {
	private Light[] lightPoints;

	private Light[] prevLightPoints;

	private List<PatternLightData> pattern;

	private boolean twinkle;

	private boolean tight;

	public JinglePlayer jinglePlayer;

	public ConnectionLogicFairyLights(Connection connection) {
		super(connection);
		pattern = new ArrayList<PatternLightData>();
		jinglePlayer = new JinglePlayer();
	}

	public Light[] getLightPoints() {
		return lightPoints;
	}

	public Light[] getPrevLightPoints() {
		return prevLightPoints;
	}

	public List<PatternLightData> getPattern() {
		return pattern;
	}

	public boolean isTight() {
		return tight;
	}

	public void play(Jingle jingle, int lightOffset) {
		if (!jinglePlayer.isPlaying()) {
			jinglePlayer.play(jingle, lightOffset);
		}
	}

	public void setPatern(List<PatternLightData> pattern) {
		this.pattern = pattern;
	}

	@Override
	public void onUpdate() {
		prevLightPoints = lightPoints;
	}

	@Override
	public void onUpdateEnd() {
		updateLights();
	}

	@Override
	public void onRecalculateCatenary() {
		updateLightVertices();
	}

	@Override
	public Catenary createCatenary(Point3f to) {
		return Catenary.from(new Vector3f(to), tight);
	}

	public void updateLights() {
		if (lightPoints != null) {
			for (int i = 0; i < lightPoints.length; i++) {
				Light light = lightPoints[i];
				if (pattern.size() > 0) {
					PatternLightData lightData = pattern.get(i % pattern.size());
					light.setVariant(lightData.getLightVariant());
					light.setColor(EnumDyeColor.byDyeDamage(lightData.getColor()).getMapColor().colorValue);
				}
				light.tick(this, twinkle);
				if (jinglePlayer.isPlaying() && getConnection().getWorldObj().isRemote) {
					jinglePlayer.play(i, getConnection().getFastener().getConnectionPoint(), light);
				}
			}
			if (jinglePlayer.isPlaying()) {
				jinglePlayer.tick();
			}
		}
	}

	private void updateLightVertices() {
		Catenary catenary = getConnection().getCatenary();
		if (catenary != null) {
			float spacing = 16;
			for (PatternLightData patternLightData : pattern) {
				float lightSpacing = patternLightData.getLightVariant().getSpacing();
				if (lightSpacing > spacing) {
					spacing = lightSpacing;
				}
			}
			float totalLength = catenary.getLength();
			// simplified version of t / 2 - ((int) (t / s) - 1) * s / 2
			float distance = (totalLength % spacing + spacing) / 2;
			Segment[] segments = catenary.getSegments();
			prevLightPoints = lightPoints;
			lightPoints = new Light[(int) (totalLength / spacing)];
			int lightIndex = 0;
			for (int i = 0; i < segments.length; i++) {
				Segment segment = segments[i];
				float length = segment.getLength();
				while (distance < length) {
					Light light = new Light(segment.pointAt(distance / length));
					if (prevLightPoints != null && lightIndex < prevLightPoints.length) {
						light.setTwinkleTime(prevLightPoints[lightIndex].getTwinkleTime());
					}
					light.setRotation(segment.getRotation());
					lightPoints[lightIndex++] = light;
					distance += spacing;
				}
				distance -= length;
			}
		}
	}

	public boolean canCurrentlyPlayAJingle() {
		return !jinglePlayer.isPlaying();
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList tagList = new NBTTagList();
		for (PatternLightData b : pattern) {
			NBTTagCompound patternCompound = new NBTTagCompound();
			patternCompound.setInteger("light", b.getLightVariant().ordinal());
			patternCompound.setByte("color", b.getColor());
			tagList.appendTag(patternCompound);
		}
		compound.setTag("pattern", tagList);
		compound.setBoolean("twinkle", twinkle);
		compound.setBoolean("tight", tight);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList tagList = compound.getTagList("pattern", 10);
		pattern = new ArrayList<PatternLightData>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
			LightVariant lightVariant = LightVariant.getLightVariant(lightCompound.getInteger("light"));
			byte color = Byte.valueOf(lightCompound.getByte("color"));
			pattern.add(new PatternLightData(lightVariant, color));
		}
		twinkle = compound.getBoolean("twinkle");
		tight = compound.getBoolean("tight");
	}
}
