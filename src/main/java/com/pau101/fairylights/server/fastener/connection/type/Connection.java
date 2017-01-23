package com.pau101.fairylights.server.fastener.connection.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerType;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.Segment;
import com.pau101.fairylights.server.fastener.connection.collision.Collidable;
import com.pau101.fairylights.server.fastener.connection.collision.ConnectionCollision;
import com.pau101.fairylights.server.fastener.connection.collision.FeatureCollisionTree;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.server.net.serverbound.MessageConnectionInteraction;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.CubicBezier;
import com.pau101.fairylights.util.NBTSerializable;
import com.pau101.fairylights.util.OreDictUtils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class Connection implements NBTSerializable {
	public static final int MAX_LENGTH = 32;

	public static final double PULL_RANGE = 5;

	public static final FeatureType CORD_FEATURE = FeatureType.create("cord");

	private static final CubicBezier SLACK_CURVE = new CubicBezier(0.495F, 0.505F, 0.495F, 0.505F);

	private static final float MAX_SLACK = 3;

	private final Fastener<?> fastener;

	private final UUID uuid;

	private FastenerAccessor destination;

	protected World world;

	private boolean isOrigin;

	@Nullable
	private Catenary catenary;

	@Nullable
	private Catenary prevCatenary;

	protected float slack = 1;

	private final ConnectionCollision collision = new ConnectionCollision();

	private boolean updateCatenary;

	private boolean catenaryUpdateState;

	protected boolean dataUpdateState;

	public boolean forceRemove;

	private int prevStretchStage;

	private boolean removed;

	@Nullable
	private List<Runnable> removeListeners;

	public Connection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		this(world, fastener, uuid);
		this.destination = destination.createAccessor();
		this.isOrigin = isOrigin;
		deserializeLogic(compound);
	}

	public Connection(World world, Fastener<?> fastener, UUID uuid) {
		this.world = world;
		this.fastener = fastener;
		this.uuid = uuid;
		updateCatenary = true;
		dataUpdateState = true;
	}

	@Nullable
	public final Catenary getCatenary() {
		return catenary;
	}

	@Nullable
	public final Catenary getPrevCatenary() {
		return prevCatenary == null ? catenary : prevCatenary;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public final World getWorld() {
		return world;
	}

	public final boolean isOrigin() {
		return isOrigin;
	}

	public final ConnectionCollision getCollision() {
		return collision;
	}

	public final Fastener<?> getFastener() {
		return fastener;
	}

	public final UUID getUUID() {
		return uuid;
	}

	public final void setDestination(Fastener<?> destination) {
		this.destination = destination.createAccessor();
		updateCatenary = dataUpdateState = true;
	}

	public final FastenerAccessor getDestination() {
		return destination;
	}

	public boolean isDestination(FastenerAccessor location) {
		return destination.equals(location);
	}

	public boolean shouldDrop() {
		return fastener.shouldDropConnection() && destination.isLoaded(world) && destination.get(world).shouldDropConnection();
	}

	public boolean shouldDisconnect() {
		return !destination.exists(world) || forceRemove;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(getType().getItem());
		NBTTagCompound tagCompound = serializeLogic();
		if (!tagCompound.hasNoTags()) {
			stack.setTagCompound(tagCompound);
		}
		return stack;
	}

	public float getRadius() {
		return 0.0625F;
	}

	public final void addRemoveListener(Runnable listener) {
		if (removeListeners == null) {
			removeListeners = new ArrayList<>();
		}
		removeListeners.add(listener);
	}

	public final void remove() {
		if (!removed) {
			removed = true;
			onRemove();
			if (removeListeners != null) {
				removeListeners.forEach(Runnable::run);
			}
		}
	}

	public void processClientAction(EntityPlayer player, PlayerAction action, Intersection intersection) {
		FairyLights.network.sendToServer(new MessageConnectionInteraction(this, action, intersection));
	}

	public void disconnect(EntityPlayer player, Vec3d hit) {
		if (!destination.isLoaded(world)) {
			return;
		}
		fastener.removeConnection(this);
		destination.get(world).removeConnection(uuid);
		if (shouldDrop()) {
			ItemStack stack = getItemStack();
			EntityItem item = new EntityItem(world, hit.xCoord, hit.yCoord, hit.zCoord, stack);
			float scale = 0.05F;
			item.motionX = world.rand.nextGaussian() * scale;
			item.motionY = world.rand.nextGaussian() * scale + 0.2F;
			item.motionZ = world.rand.nextGaussian() * scale;
			world.spawnEntityInWorld(item);
		}
		Vec3d pos = fastener.getConnectionPoint();
		world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.CORD_DISCONNECT, SoundCategory.BLOCKS, 1, 1);
	}

	public boolean interact(EntityPlayer player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, EnumHand hand) {
		Item item = heldStack.getItem();
		if (item instanceof ItemConnection) {
			if (destination.isLoaded(world)) {
				replace(player, hit, heldStack);
				return true;
			}
		} else if (OreDictUtils.matches(heldStack, "string")) {
			if (slacken(hit, heldStack, 0.2F)) {
				return true;
			}
		} else if (OreDictUtils.matches(heldStack, "stickWood")) {
			if (slacken(hit, heldStack, -0.2F)) {
				return true;
			}
		}
		return false;
	}

	private void replace(EntityPlayer player, Vec3d hit, ItemStack heldStack) {
		Fastener<?> dest = destination.get(world);
		fastener.removeConnection(this);
		dest.removeConnection(uuid);
		if (shouldDrop()) {
			player.inventory.addItemStackToInventory(getItemStack());
		}
		NBTTagCompound data = Objects.firstNonNull(heldStack.getTagCompound(), new NBTTagCompound());
		ConnectionType type = ((ItemConnection) heldStack.getItem()).getConnectionType();
		fastener.connectWith(world, dest, type, data).onConnect(player.worldObj, player, heldStack);
		world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.CORD_CONNECT, SoundCategory.BLOCKS, 1, 1);
	}

	private boolean slacken(Vec3d hit, ItemStack heldStack, float amount) {
		if (slack <= 0 && amount < 0 || slack >= MAX_SLACK && amount > 0) {
			return false;
		}
		slack = MathHelper.clamp_float(slack + amount, 0, MAX_SLACK);
		if (slack < 1e-2F) {
			slack = 0;
		}
		dataUpdateState = true;
		world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.CORD_STRETCH, SoundCategory.BLOCKS, 1, 0.8F + (MAX_SLACK - slack) * 0.4F);
		return true;
	}

	public void onConnect(World world, EntityPlayer user, ItemStack heldStack) {}

	protected void onRemove() {}

	protected void onUpdateEarly() {}

	protected void onUpdateLate() {}

	protected void onCalculateCatenary() {}

	public abstract ConnectionType getType();

	public final void update(Vec3d from) {
		prevCatenary = catenary;
		destination.update(world, fastener.getPos());
		if (destination.isLoaded(world)) {
			onUpdateEarly();
			Fastener dest = destination.get(world);
			Vec3d point = dest.getConnectionPoint();
			if (updateCatenary || dest.isDynamic() || fastener.isDynamic()) {
				Vec3d vec = point.subtract(from);
				if (vec.lengthVector() > 1e-6) {
					catenary = Catenary.from(vec, SLACK_CURVE, slack);
					onCalculateCatenary();
					collision.update(this, from);
				}
				catenaryUpdateState = true;
				updateCatenary = false;
			}
			double dist = point.distanceTo(from);
			double pull = dist - MAX_LENGTH + PULL_RANGE;
			if (pull > 0) {
				int stage = (int) (pull + 0.1F);
				if (stage > prevStretchStage) {
					world.playSound(null, point.xCoord, point.yCoord, point.zCoord, FLSounds.CORD_STRETCH, SoundCategory.BLOCKS, 0.25F, 0.5F + stage / 8F);
				}
				prevStretchStage = stage;
			}
			if (dist > MAX_LENGTH + PULL_RANGE) {
				world.playSound(null, point.xCoord, point.yCoord, point.zCoord, FLSounds.CORD_SNAP, SoundCategory.BLOCKS, 0.75F, 0.8F + world.rand.nextFloat() * 0.3F);
				forceRemove = true;
			} else if (dest.isDynamic()) {
				dest.resistSnap(from);
			}
			onUpdateLate();
		}
	}

	public final boolean pollCateneryUpdate() {
		boolean state = catenaryUpdateState;
		catenaryUpdateState = false;
		return state;
	}

	public final boolean pollDataUpdate() {
		boolean state = dataUpdateState;
		dataUpdateState = false;
		return state;
	}

	public void addCollision(List<Collidable> collision, Vec3d origin) {
		Segment[] segments = catenary.getSegments();
		float radius = getRadius();
		collision.add(FeatureCollisionTree.build(CORD_FEATURE, segments, s -> {
			Vec3d start = s.getStart();
			Vec3d end = s.getEnd();
			return new AxisAlignedBB(
				origin.xCoord + start.xCoord / 16, origin.yCoord + start.yCoord / 16, origin.zCoord + start.zCoord / 16,
				origin.xCoord + end.xCoord / 16, origin.yCoord + end.yCoord / 16, origin.zCoord + end.zCoord / 16
			).expandXyz(radius);
		}, 1, segments.length - 2));
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("isOrigin", isOrigin);
		compound.setTag("destination", FastenerType.serialize(destination));
		compound.setTag("logic", serializeLogic());
		compound.setFloat("slack", slack);
		return compound;
	}

	@Override
	public void deserialize(NBTTagCompound compound) {
		isOrigin = compound.getBoolean("isOrigin");
		destination = FastenerType.deserialize(compound.getCompoundTag("destination"));
		if (world != null) {
			destination.update(world, fastener.getPos());
		}
		deserializeLogic(compound.getCompoundTag("logic"));
		slack = compound.hasKey("slack", NBT.TAG_ANY_NUMERIC) ? compound.getFloat("slack") : 1;
		updateCatenary = true;
	}

	public NBTTagCompound serializeLogic() {
		return new NBTTagCompound();
	}

	public void deserializeLogic(NBTTagCompound compound) {}
}
