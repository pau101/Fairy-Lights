package com.pau101.fairylights.server.fastener.connection.type.pennant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pau101.fairylights.client.gui.GuiEditLetteredConnection;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.fastener.connection.type.ConnectionHangingFeature;
import com.pau101.fairylights.server.fastener.connection.type.Lettered;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.styledstring.StyledString;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ConnectionPennantBunting extends ConnectionHangingFeature<Pennant> implements Lettered {
	private List<EnumDyeColor> pattern;

	private StyledString text;

	public ConnectionPennantBunting(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public ConnectionPennantBunting(World world, Fastener<?> fastener, UUID uuid) {
		super(world, fastener, uuid);
		pattern = new ArrayList<>();
		text = new StyledString();
	}

	@Override
	public float getRadius() {
		return 0.045F;
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.PENNANT_BUNTING;
	}

	@Override
	public void processClientAction(EntityPlayer player, PlayerAction action, Intersection intersection) {
		if (openTextGui(player, action, intersection)) {
			super.processClientAction(player, action, intersection);
		}
	}

	@Override
	public boolean interact(EntityPlayer player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, EnumHand hand) {
		if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
			int index = feature % pattern.size();
			EnumDyeColor patternColor = pattern.get(index);
			EnumDyeColor color = EnumDyeColor.byDyeDamage(OreDictUtils.getDyeMetadata(heldStack));
			if (patternColor != color) {
				pattern.set(index, color);
				dataUpdateState = true;
				heldStack.func_190918_g(1);
				world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.FEATURE_COLOR_CHANGE, SoundCategory.BLOCKS, 1, 1);
				return true;
			}
		}
		return super.interact(player, hit, featureType, feature, heldStack, hand);
	}

	@Override
	protected Pennant[] createFeatures(int length) {
		return new Pennant[length];
	}

	@Override
	protected Pennant createFeature(int index, Vec3d point, Vec3d rotation) {
		Pennant pennant = new Pennant(index, point, rotation);
		if (pattern.size() > 0) {
			pennant.setColor(ItemLight.getColorValue(pattern.get(index % pattern.size())));
		}
		return pennant;
	}

	@Override
	protected float getFeatureSpacing() {
		return 11;
	}

	@Override
	public boolean isSuppportedText(StyledString text) {
		return text.length() <= features.length && Lettered.super.isSuppportedText(text);
	}

	@Override
	public void setText(StyledString text) {
		this.text = text;
		dataUpdateState = true;
	}

	@Override
	public StyledString getText() {
		return text;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen createTextGUI() {
		return new GuiEditLetteredConnection<>(this);
	}

	@Override
	public NBTTagCompound serializeLogic() {
		NBTTagCompound compound = super.serializeLogic();
		NBTTagList patternList = new NBTTagList();
		for (EnumDyeColor color : pattern) {
			NBTTagCompound colorCompound = new NBTTagCompound();
			colorCompound.setByte("color", (byte) color.getDyeDamage());
			patternList.appendTag(colorCompound);
		}
		compound.setTag("pattern", patternList);
		compound.setTag("text", StyledString.serialize(text));
		return compound;
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {
		super.deserializeLogic(compound);
		pattern = new ArrayList<>();
		NBTTagList patternList = compound.getTagList("pattern", NBT.TAG_COMPOUND);
		for (int i = 0; i < patternList.tagCount(); i++) {
			NBTTagCompound colorCompound = patternList.getCompoundTagAt(i);
			pattern.add(EnumDyeColor.byDyeDamage(colorCompound.getByte("color")));
		}
		text = StyledString.deserialize(compound.getCompoundTag("text"));
	}
}
