package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.collision.Collidable;
import me.paulf.fairylights.server.collision.CollidableList;
import me.paulf.fairylights.server.collision.FeatureCollisionTree;
import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.FenceFastener;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.feature.Feature;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.Curve3d;
import me.paulf.fairylights.util.NBTSerializable;
import me.paulf.fairylights.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Connection implements NBTSerializable {
    public static final int MAX_LENGTH = 32;

    public static final double PULL_RANGE = 5;

    public static final FeatureType CORD_FEATURE = FeatureType.register("cord");

    private static final CubicBezier SLACK_CURVE = new CubicBezier(0.495F, 0.505F, 0.495F, 0.505F);

    private static final float MAX_SLACK = 3;

    private final ConnectionType<?> type;

    protected final Fastener<?> fastener;

    private final UUID uuid;

    private FastenerAccessor destination;

    @Nullable
    private FastenerAccessor prevDestination;

    protected Level world;

    @Nullable
    private Curve catenary;

    @Nullable
    protected Curve prevCatenary;

    protected float slack = 1;

    private Collidable collision = Collidable.empty();

    private boolean updateCatenary;

    private int prevStretchStage;

    private boolean removed;

    private boolean drop;

    public Connection(final ConnectionType<?> type, final Level world, final Fastener<?> fastener, final UUID uuid) {
        this.type = type;
        this.world = world;
        this.fastener = fastener;
        this.uuid = uuid;
        this.computeCatenary();
    }

    public ConnectionType<?> getType() {
        return this.type;
    }

    @Nullable
    public final Curve getCatenary() {
        return this.catenary;
    }

    @Nullable
    public final Curve getPrevCatenary() {
        return this.prevCatenary == null ? this.catenary : this.prevCatenary;
    }

    public void setWorld(final Level world) {
        this.world = world;
    }

    public final Level getWorld() {
        return this.world;
    }

    public final Collidable getCollision() {
        return this.collision;
    }

    public final Fastener<?> getFastener() {
        return this.fastener;
    }

    public final UUID getUUID() {
        return this.uuid;
    }

    public final void setDestination(final Fastener<?> destination) {
        this.prevDestination = this.destination;
        this.destination = destination.createAccessor();
        this.computeCatenary();
    }

    public final FastenerAccessor getDestination() {
        return this.destination;
    }

    public boolean isDestination(final FastenerAccessor location) {
        return this.destination.equals(location);
    }

    public void setDrop() {
        this.drop = true;
    }

    public void noDrop() {
        this.drop = false;
    }

    public boolean shouldDrop() {
        return this.drop;
    }

    public ItemStack getItemStack() {
        final ItemStack stack = new ItemStack(this.getType().getItem());
        final CompoundTag tagCompound = this.serializeLogic();
        if (!tagCompound.isEmpty()) {
            stack.setTag(tagCompound);
        }
        return stack;
    }

    public float getRadius() {
        return 0.0625F;
    }

    public final boolean isDynamic() {
        return this.fastener.isMoving() || this.destination.get(this.world, false).filter(Fastener::isMoving).isPresent();
    }

    public final boolean isModifiable(final Player player) {
        return this.world.mayInteract(player, this.fastener.getPos());
    }

    public final void remove() {
        if (!this.removed) {
            this.removed = true;
            this.onRemove();
        }
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    public void computeCatenary() {
        this.updateCatenary = true;
    }

    public void processClientAction(final Player player, final PlayerAction action, final Intersection intersection) {
        FairyLights.NETWORK.sendToServer(new InteractionConnectionMessage(this, action, intersection));
    }

    public void disconnect(final Player player, final Vec3 hit) {
        this.destination.get(this.world).ifPresent(f -> this.disconnect(f, hit));
    }

    private void disconnect(final Fastener<?> destinationFastener, final Vec3 hit) {
        this.fastener.removeConnection(this);
        destinationFastener.removeConnection(this.uuid);
        if (this.shouldDrop()) {
            final ItemStack stack = this.getItemStack();
            final ItemEntity item = new ItemEntity(this.world, hit.x, hit.y, hit.z, stack);
            final float scale = 0.05F;
            item.setDeltaMovement(
                this.world.random.nextGaussian() * scale,
                this.world.random.nextGaussian() * scale + 0.2F,
                this.world.random.nextGaussian() * scale
            );
            this.world.addFreshEntity(item);
        }
        this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_DISCONNECT.get(), SoundSource.BLOCKS, 1, 1);
    }

    public boolean reconnect(final Fastener<?> destination) {
        return this.fastener.reconnect(this.world, this, destination);
    }

    public boolean interact(final Player player, final Vec3 hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final InteractionHand hand) {
        final Item item = heldStack.getItem();
        if (item instanceof ConnectionItem && !this.matches(heldStack)) {
            return this.replace(player, hit, heldStack);
        } else if (heldStack.is(Tags.Items.STRING)) {
            return this.slacken(hit, heldStack, 0.2F);
        } else if (heldStack.is(Items.STICK)) {
            return this.slacken(hit, heldStack, -0.2F);
        }
        return false;
    }

    public boolean matches(final ItemStack stack) {
        if (this.getType().getItem().equals(stack.getItem())) {
            final CompoundTag tag = stack.getTag();
            return tag == null || Utils.impliesNbt(this.serializeLogic(), tag);
        }
        return false;
    }

    private boolean replace(final Player player, final Vec3 hit, final ItemStack heldStack) {
        return this.destination.get(this.world).map(dest -> {
            this.fastener.removeConnection(this);
            dest.removeConnection(this.uuid);
            if (this.shouldDrop()) {
                ItemHandlerHelper.giveItemToPlayer(player, this.getItemStack());
            }
            final CompoundTag data = heldStack.getTag();
            final ConnectionType<? extends Connection> type = ((ConnectionItem) heldStack.getItem()).getConnectionType();
            final Connection conn = this.fastener.connect(this.world, dest, type, data == null ? new CompoundTag() : data, true);
            conn.slack = this.slack;
            conn.onConnect(player.level(), player, heldStack);
            heldStack.shrink(1);
            this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_CONNECT.get(), SoundSource.BLOCKS, 1, 1);
            return true;
        }).orElse(false);
    }

    private boolean slacken(final Vec3 hit, final ItemStack heldStack, final float amount) {
        if (this.slack <= 0 && amount < 0 || this.slack >= MAX_SLACK && amount > 0) {
            return true;
        }
        this.slack = Mth.clamp(this.slack + amount, 0, MAX_SLACK);
        if (this.slack < 1e-2F) {
            this.slack = 0;
        }
        this.computeCatenary();
        this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.CORD_STRETCH.get(), SoundSource.BLOCKS, 1, 0.8F + (MAX_SLACK - this.slack) * 0.4F);
        return true;
    }

    public void onConnect(final Level world, final Player user, final ItemStack heldStack) {}

    protected void onRemove() {}

    protected void onUpdate() {}

    protected void onCalculateCatenary(final boolean relocated) {}

    public final boolean update(final Vec3 from) {
        this.prevCatenary = this.catenary;
        final boolean changed = this.destination.get(this.world, false).map(dest -> {
            final Vec3 point = dest.getConnectionPoint();
            final boolean c = this.updateCatenary(from, dest, point);
            this.onUpdate();
            final double dist = point.distanceTo(from);
            final double pull = dist - MAX_LENGTH + PULL_RANGE;
            if (pull > 0) {
                final int stage = (int) (pull + 0.1F);
                if (stage > this.prevStretchStage) {
                    this.world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_STRETCH.get(), SoundSource.BLOCKS, 0.25F, 0.5F + stage / 8F);
                }
                this.prevStretchStage = stage;
            }
            if (dist > MAX_LENGTH + PULL_RANGE) {
                this.world.playSound(null, point.x, point.y, point.z, FLSounds.CORD_SNAP.get(), SoundSource.BLOCKS, 0.75F, 0.8F + this.world.random.nextFloat() * 0.3F);
                this.remove();
            } else if (dest.isMoving()) {
                dest.resistSnap(from);
            }
            return c;
        }).orElse(false);
        if (this.destination.isGone(this.world)) {
            this.remove();
        }
        return changed;
    }

    private boolean updateCatenary(final Vec3 from, final Fastener<?> dest, final Vec3 point) {
        if (this.updateCatenary || this.isDynamic()) {
            final Vec3 vec = point.subtract(from);
            if (vec.length() > 1e-6) {
                final Direction facing = this.fastener.getFacing();
                if (this.fastener instanceof FenceFastener && dest instanceof FenceFastener && vec.horizontalDistance() < 1e-2) {
                    this.catenary = this.verticalHelix(vec);
                } else {
                    this.catenary = Catenary.from(vec, facing.getAxis() == Direction.Axis.Y ? 0.0F : (float) Math.toRadians(90.0F + facing.toYRot()), SLACK_CURVE, this.slack);
                }
                this.onCalculateCatenary(!this.destination.equals(this.prevDestination));
                final CollidableList.Builder bob = new CollidableList.Builder();
                this.addCollision(bob, from);
                this.collision = bob.build();
            }
            this.updateCatenary = false;
            this.prevDestination = this.destination;
            return true;
        }
        return false;
    }

    private Curve verticalHelix(final Vec3 vec) {
        final float length = (float) vec.length();
        final float height = (float) vec.y;
        final float stepSize = 0.25F;
        final float loopsPerBlock = 1.0F;
        final float radius = 0.33F;
        final int steps = (int) (Mth.TWO_PI * radius * loopsPerBlock * length / stepSize);
        final float rad = -Mth.TWO_PI * (loopsPerBlock * length);
        final float[] x = new float[steps];
        final float[] y = new float[steps];
        final float[] z = new float[steps];
        float helixLength = 0.0F;
        for (int i = 0; i < steps; i++) {
            float t = (float) i / (steps - 1);
            x[i] = radius * Mth.cos(t * rad);
            y[i] = t * height;
            z[i] = radius * Mth.sin(t * rad);
            if (i > 0) {
                helixLength += Mth.sqrt(
                    Mth.square(x[i] - x[i - 1]) +
                    Mth.square(y[i] - y[i - 1]) +
                    Mth.square(z[i] - z[i - 1]));
            }
        }
        return new Curve3d(steps, x, y, z, helixLength);
    }

    public void addCollision(final CollidableList.Builder collision, final Vec3 origin) {
        if (this.catenary == null) {
            return;
        }
        final int count = this.catenary.getCount();
        if (count <= 2) {
            return;
        }
        final float r = this.getRadius();
        final Catenary.SegmentIterator it = this.catenary.iterator();
        final AABB[] bounds = new AABB[count - 1];
        int index = 0;
        while (it.next()) {
            final float x0 = it.getX(0.0F);
            final float y0 = it.getY(0.0F);
            final float z0 = it.getZ(0.0F);
            final float x1 = it.getX(1.0F);
            final float y1 = it.getY(1.0F);
            final float z1 = it.getZ(1.0F);
            bounds[index++] = new AABB(
                origin.x + x0, origin.y + y0, origin.z + z0,
                origin.x + x1, origin.y + y1, origin.z + z1
            ).inflate(r);
        }
        collision.add(FeatureCollisionTree.build(CORD_FEATURE, i -> Segment.INSTANCE, i -> bounds[i], 1, bounds.length - 2));
    }

    public void deserialize(final Fastener<?> destination, final CompoundTag compound, final boolean drop) {
        this.destination = destination.createAccessor();
        this.drop = drop;
        this.deserializeLogic(compound);
    }

    @Override
    public CompoundTag serialize() {
        final CompoundTag compound = new CompoundTag();
        compound.put("destination", FastenerType.serialize(this.destination));
        compound.put("logic", this.serializeLogic());
        compound.putFloat("slack", this.slack);
        if (!this.drop) compound.putBoolean("drop", false);
        return compound;
    }

    @Override
    public void deserialize(final CompoundTag compound) {
        this.destination = FastenerType.deserialize(compound.getCompound("destination"));
        this.deserializeLogic(compound.getCompound("logic"));
        this.slack = compound.contains("slack", Tag.TAG_ANY_NUMERIC) ? compound.getFloat("slack") : 1;
        this.drop = !compound.contains("drop", Tag.TAG_ANY_NUMERIC) || compound.getBoolean("drop");
        this.updateCatenary = true;
    }

    public CompoundTag serializeLogic() {
        return new CompoundTag();
    }

    public void deserializeLogic(final CompoundTag compound) {}

    static class Segment implements Feature {
        static final Segment INSTANCE = new Segment();

        @Override
        public int getId() {
            return 0;
        }
    }
}
