package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.client.gui.GuiEditLetteredConnection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.ConnectionHangingFeature;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.item.ItemLight;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConnectionPennantBunting extends ConnectionHangingFeature<Pennant> implements Lettered {
	private List<DyeColor> pattern;

	private StyledString text;

	public ConnectionPennantBunting(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
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
	public void processClientAction(PlayerEntity player, PlayerAction action, Intersection intersection) {
		if (openTextGui(player, action, intersection)) {
			super.processClientAction(player, action, intersection);
		}
	}

	@Override
	public boolean interact(PlayerEntity player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, Hand hand) {
		if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
			int index = feature % pattern.size();
			DyeColor patternColor = pattern.get(index);
			DyeColor color = DyeColor.getColor(heldStack);
			if (patternColor != color) {
				pattern.set(index, color);
				dataUpdateState = true;
				heldStack.shrink(1);
				world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
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
	@OnlyIn(Dist.CLIENT)
	public Screen createTextGUI() {
		return new GuiEditLetteredConnection<>(this);
	}

	@Override
	public CompoundNBT serializeLogic() {
		CompoundNBT compound = super.serializeLogic();
		ListNBT patternList = new ListNBT();
		for (DyeColor color : pattern) {
			CompoundNBT colorCompound = new CompoundNBT();
			colorCompound.putByte("color", (byte) color.getId());
			patternList.add(colorCompound);
		}
		compound.put("pattern", patternList);
		compound.put("text", StyledString.serialize(text));
		return compound;
	}

	@Override
	public void deserializeLogic(CompoundNBT compound) {
		super.deserializeLogic(compound);
		pattern = new ArrayList<>();
		ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
		for (int i = 0; i < patternList.size(); i++) {
			CompoundNBT colorCompound = patternList.getCompound(i);
			pattern.add(DyeColor.byId(colorCompound.getByte("color")));
		}
		text = StyledString.deserialize(compound.getCompound("text"));
	}
}
