package com.pau101.fairylights.tileentity.connection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.eggs.JinglePlayer;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.Light;
import com.pau101.fairylights.util.PatternLightData;
import com.pau101.fairylights.util.Segment;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public abstract class Connection {
	protected TileEntityFairyLightsFastener fairyLightsFastener;

	protected World worldObj;

	private boolean isOrigin;

	private Catenary catenary;

	private Catenary prevCatenary;

	private Light[] lightPoints;

	private Light[] prevLightPoints;

	private List<PatternLightData> pattern;

	private boolean twinkle;

	private boolean tight;

	protected boolean shouldRecalculateCatenary;

	public JinglePlayer jinglePlayer;

	private int toX;

	private int toY;

	private int toZ;

	private int fromX;

	private int fromY;

	private int fromZ;

	private boolean isDirty;

	public Connection(TileEntityFairyLightsFastener fairyLightsFastener, World worldObj) {
		this(fairyLightsFastener, worldObj, false, null);
	}

	public Connection(TileEntityFairyLightsFastener tileEntityFairyLightsFastener, World worldObj, boolean isOrigin, NBTTagCompound tagCompound) {
		fairyLightsFastener = tileEntityFairyLightsFastener;
		setWorldObj(worldObj);
		this.isOrigin = isOrigin;
		tight = false;
		twinkle = false;
		shouldRecalculateCatenary = true;
		pattern = new ArrayList<PatternLightData>();
		if (tagCompound != null) {
			readDetailsFromNBT(tagCompound);
		}
	}

	public Catenary getCatenary() {
		return catenary;
	}

	public Light[] getLightPoints() {
		return lightPoints;
	}

	public List<PatternLightData> getPattern() {
		return pattern;
	}

	public Catenary getPrevCatenary() {
		return prevCatenary;
	}

	public Light[] getPrevLightPoints() {
		return prevLightPoints;
	}

	public abstract Point3f getTo();

	public abstract int getToX();

	public abstract int getToY();

	public abstract int getToZ();

	public void setWorldObj(World worldObj) {
		this.worldObj = worldObj;
	}

	public World getWorldObj() {
		return worldObj;
	}

	public boolean isOrigin() {
		return isOrigin;
	}

	public boolean isTight() {
		return tight;
	}

	public boolean shouldRecalculateCatenery() {
		return shouldRecalculateCatenary;
	}

	public void onRemove() {}

	public void play(Jingle jingle, int lightOffset) {
		if (jinglePlayer == null) {
			jinglePlayer = new JinglePlayer();
		}
		if (!jinglePlayer.isPlaying()) {
			jinglePlayer.play(jingle, lightOffset);
		}
	}

	public void setPatern(List<PatternLightData> pattern) {
		this.pattern = pattern;
	}

	public abstract boolean shouldDisconnect();

	public void update(Point3f from) {
		prevCatenary = catenary;
		prevLightPoints = lightPoints;
		if (shouldRecalculateCatenary) {
			Point3f to = getTo();
			if (to == null) {
				return;
			}
			to.sub(from);
			if (to.x == 0 && to.y == 0 && to.z == 0) {
				return;
			}
			fromX = fairyLightsFastener.xCoord;
			fromY = fairyLightsFastener.yCoord;
			fromZ = fairyLightsFastener.zCoord;
			toX = getToX();
			toY = getToY();
			toZ = getToZ();
			catenary = Catenary.from(new Vector3f(to), tight);
			updateLightVertices();
			shouldRecalculateCatenary = false;
		}
		updateLights();
	}

	public void updateLights() {
		if (lightPoints != null) {
			for (int i = 0; i < lightPoints.length; i++) {
				Light light = lightPoints[i];
				if (pattern.size() > 0) {
					PatternLightData lightData = pattern.get(i % pattern.size());
					light.setVariant(lightData.getLightVariant());
					// black is actually a little red, got to account for that
					light.setColor(lightData.getColor() == 0 ? 0x1B1B1B : ItemDye.field_150922_c[lightData.getColor()]);
				}
				light.tick(this, twinkle);
				if (jinglePlayer != null && getWorldObj().isRemote) {
					jinglePlayer.play(i, fairyLightsFastener.getConnectionPoint(), light);
				}
			}
			if (jinglePlayer != null && jinglePlayer.isPlaying()) {
				jinglePlayer.tick();
			}
		}
	}

	private void updateLightVertices() {
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
		return jinglePlayer == null || !jinglePlayer.isPlaying();
	}

	public void writeDetailsToNBT(NBTTagCompound compound) {
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

	public void readDetailsFromNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = tagCompound.getTagList("pattern", 10);
		pattern = new ArrayList<PatternLightData>();
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
			LightVariant lightVariant = LightVariant.getLightVariant(lightCompound.getInteger("light"));
			byte color = Byte.valueOf(lightCompound.getByte("color"));
			pattern.add(new PatternLightData(lightVariant, color));
		}
		twinkle = tagCompound.getBoolean("twinkle");
		tight = tagCompound.getBoolean("tight");
	}

	public void readFromNBT(NBTTagCompound compound) {
		isOrigin = compound.getBoolean("isOrigin");
		isDirty = compound.getBoolean("isDirty");
		readDetailsFromNBT(compound);
	}

	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isOrigin", isOrigin);
		compound.setBoolean("isDirty", isDirty);
		writeDetailsToNBT(compound);
	}
}
