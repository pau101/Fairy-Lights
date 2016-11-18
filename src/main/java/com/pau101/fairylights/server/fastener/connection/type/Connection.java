package com.pau101.fairylights.server.fastener.connection.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
import com.pau101.fairylights.util.NBTSerializable;

public abstract class Connection implements NBTSerializable {
	public static final int MAX_LENGTH = 32;

	public static final double PULL_RANGE = 5;

	public static final FeatureType CORD_FEATURE = FeatureType.create("cord");

	private final Fastener<?> fastener;

	private final UUID uuid;

	private FastenerAccessor destination;

	protected World world;

	private boolean isOrigin;

	private Catenary catenary;

	@Nullable
	private Catenary prevCatenary;

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

	public final Catenary getPrevCatenary() {
		return Objects.firstNonNull(prevCatenary, catenary);
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
		ItemStack stack = new ItemStack(getType().getItem(), 1);
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
		if (!removeListeners.contains(listener)) {
			removeListeners.add(listener);
		}
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

	public boolean interact(EntityPlayer player, Vec3d hit, FeatureType featureType, int feature, @Nullable ItemStack heldStack, EnumHand hand) {
		if (heldStack != null && heldStack.getItem() instanceof ItemConnection && destination.isLoaded(world)) {
			Fastener<?> dest = destination.get(world);
			fastener.removeConnection(this);
			dest.removeConnection(uuid);
			if (shouldDrop()) {
				player.inventory.addItemStackToInventory(getItemStack());
			}
			heldStack.func_190918_g(1);
			NBTTagCompound data = Objects.firstNonNull(heldStack.getTagCompound(), new NBTTagCompound());
			ConnectionType type = ((ItemConnection) heldStack.getItem()).getConnectionType();
			fastener.connectWith(world, dest, type, data).onConnect(player.worldObj, player, heldStack);
			world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.CORD_CONNECT, SoundCategory.BLOCKS, 1, 1);
			return true;
		}
		return false;
	}

	public void onConnect(World world, EntityPlayer user, @Nullable ItemStack heldStack) {}

	protected void onRemove() {}

	protected void onUpdateEarly() {}

	protected void onUpdateLate() {}

	protected void onCalculateCatenary() {}

	public abstract ConnectionType getType();

	public abstract Catenary createCatenary(Vec3d to);

	public abstract NBTTagCompound serializeLogic();

	public abstract void deserializeLogic(NBTTagCompound compound);

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
					catenary = createCatenary(vec);
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
	public final NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("isOrigin", isOrigin);
		compound.setTag("destination", FastenerType.serialize(destination));
		compound.setTag("logic", serializeLogic());
		return compound;
	}

	@Override
	public final void deserialize(NBTTagCompound compound) {
		isOrigin = compound.getBoolean("isOrigin");
		destination = FastenerType.deserialize(compound.getCompoundTag("destination"));
		if (world != null) {
			destination.update(world, fastener.getPos());
		}
		deserializeLogic(compound.getCompoundTag("logic"));
		updateCatenary = true;
	}
}
