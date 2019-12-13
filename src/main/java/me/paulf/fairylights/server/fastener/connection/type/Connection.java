package me.paulf.fairylights.server.fastener.connection.type;

import com.google.common.base.MoreObjects;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.Segment;
import me.paulf.fairylights.server.fastener.connection.collision.Collidable;
import me.paulf.fairylights.server.fastener.connection.collision.ConnectionCollision;
import me.paulf.fairylights.server.fastener.connection.collision.FeatureCollisionTree;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.item.ItemConnection;
import me.paulf.fairylights.server.net.serverbound.MessageConnectionInteraction;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.NBTSerializable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Connection implements NBTSerializable {
	public static final int MAX_LENGTH = 32;

	public static final double PULL_RANGE = 5;

	public static final FeatureType CORD_FEATURE = FeatureType.create("cord");

	private static final CubicBezier SLACK_CURVE = new CubicBezier(0.495F, 0.505F, 0.495F, 0.505F);

	private static final float MAX_SLACK = 3;

	protected final Fastener<?> fastener;

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

	public Connection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
		this(world, fastener, uuid);
		this.destination = destination.createAccessor();
		this.isOrigin = isOrigin;
		deserializeLogic(compound);
	}

	public Connection(World world, Fastener<?> fastener, UUID uuid) {
		this.world = world;
		this.fastener = fastener;
		this.uuid = uuid;
		computeCatenary();
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
		computeCatenary();
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
		CompoundNBT tagCompound = serializeLogic();
		if (!tagCompound.isEmpty()) {
			stack.setTag(tagCompound);
		}
		return stack;
	}

	public float getRadius() {
		return 0.0625F;
	}

	public final boolean isDynamic() {
		if (destination.isLoaded(world)) {
			return fastener.isMoving() || destination.get(world).isMoving();
		}
		return false;
	}

	public final boolean isModifiable(PlayerEntity player) {
		return world.isBlockModifiable(player, fastener.getPos());
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

	public void computeCatenary() {
		updateCatenary = dataUpdateState = true;
	}

	public void processClientAction(PlayerEntity player, PlayerAction action, Intersection intersection) {
		FairyLights.network.sendToServer(new MessageConnectionInteraction(this, action, intersection));
	}

	public void disconnect(PlayerEntity player, Vec3d hit) {
		if (!destination.isLoaded(world)) {
			return;
		}
		fastener.removeConnection(this);
		destination.get(world).removeConnection(uuid);
		if (shouldDrop()) {
			ItemStack stack = getItemStack();
			ItemEntity item = new ItemEntity(world, hit.x, hit.y, hit.z, stack);
			float scale = 0.05F;
			item.setMotion(
				world.rand.nextGaussian() * scale,
				world.rand.nextGaussian() * scale + 0.2F,
				world.rand.nextGaussian() * scale
			);
			world.addEntity(item);
		}
		world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_DISCONNECT.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
	}

	public boolean interact(PlayerEntity player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, Hand hand) {
		Item item = heldStack.getItem();
		if (item instanceof ItemConnection) {
			if (destination.isLoaded(world)) {
				replace(player, hit, heldStack);
				return true;
			}
		} else if (heldStack.getItem().isIn(Tags.Items.STRING)) {
			if (slacken(hit, heldStack, 0.2F)) {
				return true;
			}
		} else if (heldStack.getItem() == Items.STICK) {
			if (slacken(hit, heldStack, -0.2F)) {
				return true;
			}
		}
		return false;
	}

	private void replace(PlayerEntity player, Vec3d hit, ItemStack heldStack) {
		Fastener<?> dest = destination.get(world);
		fastener.removeConnectionImmediately(this);
		dest.removeConnectionImmediately(uuid);
		if (shouldDrop()) {
			player.inventory.addItemStackToInventory(getItemStack());
		}
		CompoundNBT data = MoreObjects.firstNonNull(heldStack.getTag(), new CompoundNBT());
		ConnectionType type = ((ItemConnection) heldStack.getItem()).getConnectionType();
		fastener.connectWith(world, dest, type, data).onConnect(player.world, player, heldStack);
		heldStack.shrink(1);
		world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_CONNECT.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
	}

	private boolean slacken(Vec3d hit, ItemStack heldStack, float amount) {
		if (slack <= 0 && amount < 0 || slack >= MAX_SLACK && amount > 0) {
			return false;
		}
		slack = MathHelper.clamp(slack + amount, 0, MAX_SLACK);
		if (slack < 1e-2F) {
			slack = 0;
		}
		dataUpdateState = true;
		world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_STRETCH.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 0.8F + (MAX_SLACK - slack) * 0.4F);
		return true;
	}

	public void onConnect(World world, PlayerEntity user, ItemStack heldStack) {}

	protected void onRemove() {}

	protected void updatePrev() {}

	protected void onUpdateEarly() {}

	protected void onUpdateLate() {}

	protected void onCalculateCatenary() {}

	public abstract ConnectionType getType();

	public final void update(Vec3d from) {
		prevCatenary = catenary;
		updatePrev();
		destination.update(world, fastener.getPos());
		if (destination.isLoaded(world)) {
			onUpdateEarly();
			Fastener dest = destination.get(world);
			Vec3d point = dest.getConnectionPoint();
			updateCatenary(from, dest, point);
			double dist = point.distanceTo(from);
			double pull = dist - MAX_LENGTH + PULL_RANGE;
			if (pull > 0) {
				int stage = (int) (pull + 0.1F);
				if (stage > prevStretchStage) {
					world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_STRETCH.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 0.25F, 0.5F + stage / 8F);
				}
				prevStretchStage = stage;
			}
			if (dist > MAX_LENGTH + PULL_RANGE) {
				world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_SNAP.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 0.75F, 0.8F + world.rand.nextFloat() * 0.3F);
				forceRemove = true;
			} else if (dest.isMoving()) {
				dest.resistSnap(from);
			}
			onUpdateLate();
		}
	}

	public void updateCatenary(Vec3d from) {
		if (world.isBlockLoaded(fastener.getPos())) {
			destination.update(world, fastener.getPos());
			if (destination.isLoaded(world)) {
				Fastener dest = destination.get(world);
				Vec3d point = dest.getConnectionPoint();
				updateCatenary(from, dest, point);
				updateCatenary = false;
			}
		}
	}

	private void updateCatenary(Vec3d from, Fastener<?> dest, Vec3d point) {
		if (updateCatenary || isDynamic()) {
			Vec3d vec = point.subtract(from);
			if (vec.length() > 1e-6) {
				catenary = Catenary.from(vec, SLACK_CURVE, slack);
				onCalculateCatenary();
				collision.update(this, from);
			}
			catenaryUpdateState = true;
			updateCatenary = false;
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
		if (segments.length < 2) {
			return;
		}
		float radius = getRadius();
		collision.add(FeatureCollisionTree.build(CORD_FEATURE, segments, s -> {
			Vec3d start = s.getStart();
			Vec3d end = s.getEnd();
			return new AxisAlignedBB(
				origin.x + start.x / 16, origin.y + start.y / 16, origin.z + start.z / 16,
				origin.x + end.x / 16, origin.y + end.y / 16, origin.z + end.z / 16
			).grow(radius);
		}, 1, segments.length - 2));
	}

	@Override
	public CompoundNBT serialize() {
		CompoundNBT compound = new CompoundNBT();
		compound.putBoolean("isOrigin", isOrigin);
		compound.put("destination", FastenerType.serialize(destination));
		compound.put("logic", serializeLogic());
		compound.putFloat("slack", slack);
		return compound;
	}

	@Override
	public void deserialize(CompoundNBT compound) {
		isOrigin = compound.getBoolean("isOrigin");
		destination = FastenerType.deserialize(compound.getCompound("destination"));
		deserializeLogic(compound.getCompound("logic"));
		slack = compound.contains("slack", NBT.TAG_ANY_NUMERIC) ? compound.getFloat("slack") : 1;
		updateCatenary = true;
	}

	public CompoundNBT serializeLogic() {
		return new CompoundNBT();
	}

	public void deserializeLogic(CompoundNBT compound) {}
}
