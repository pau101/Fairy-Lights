package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.collision.Collidable;
import me.paulf.fairylights.server.collision.CollidableList;
import me.paulf.fairylights.server.collision.FeatureCollisionTree;
import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.feature.Feature;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.CubicBezier;
import me.paulf.fairylights.util.NBTSerializable;
import me.paulf.fairylights.util.Utils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants.NBT;

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

    protected World world;

    @Nullable
    private Catenary catenary;

    @Nullable
    protected Catenary prevCatenary;

    protected float slack = 1;

    private Collidable collision = Collidable.empty();

    private boolean updateCatenary;

    private int prevStretchStage;

    private boolean removed;

    private boolean drop;

    public Connection(final ConnectionType<?> type, final World world, final Fastener<?> fastener, final UUID uuid) {
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
    public final Catenary getCatenary() {
        return this.catenary;
    }

    @Nullable
    public final Catenary getPrevCatenary() {
        return this.prevCatenary == null ? this.catenary : this.prevCatenary;
    }

    public void setWorld(final World world) {
        this.world = world;
    }

    public final World getWorld() {
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
        final CompoundNBT tagCompound = this.serializeLogic();
        if (!tagCompound.isEmpty()) {
            stack.func_77982_d(tagCompound);
        }
        return stack;
    }

    public float getRadius() {
        return 0.0625F;
    }

    public final boolean isDynamic() {
        return this.fastener.isMoving() || this.destination.get(this.world, false).filter(Fastener::isMoving).isPresent();
    }

    public final boolean isModifiable(final PlayerEntity player) {
        return this.world.func_175660_a(player, this.fastener.getPos());
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

    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        FairyLights.NETWORK.sendToServer(new InteractionConnectionMessage(this, action, intersection));
    }

    public void disconnect(final PlayerEntity player, final Vector3d hit) {
        this.destination.get(this.world).ifPresent(f -> this.disconnect(f, hit));
    }

    private void disconnect(final Fastener<?> destinationFastener, final Vector3d hit) {
        this.fastener.removeConnection(this);
        destinationFastener.removeConnection(this.uuid);
        if (this.shouldDrop()) {
            final ItemStack stack = this.getItemStack();
            final ItemEntity item = new ItemEntity(this.world, hit.field_72450_a, hit.field_72448_b, hit.field_72449_c, stack);
            final float scale = 0.05F;
            item.func_213293_j(
                this.world.field_73012_v.nextGaussian() * scale,
                this.world.field_73012_v.nextGaussian() * scale + 0.2F,
                this.world.field_73012_v.nextGaussian() * scale
            );
            this.world.func_217376_c(item);
        }
        this.world.func_184148_a(null, hit.field_72450_a, hit.field_72448_b, hit.field_72449_c, FLSounds.CORD_DISCONNECT.get(), SoundCategory.BLOCKS, 1, 1);
    }

    public boolean reconnect(final Fastener<?> destination) {
        return this.fastener.reconnect(this.world, this, destination);
    }

    public boolean interact(final PlayerEntity player, final Vector3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        final Item item = heldStack.func_77973_b();
        if (item instanceof ConnectionItem && !this.matches(heldStack)) {
            return this.replace(player, hit, heldStack);
        } else if (heldStack.func_77973_b().func_206844_a(Tags.Items.STRING)) {
            return this.slacken(hit, heldStack, 0.2F);
        } else if (heldStack.func_77973_b() == Items.field_151055_y) {
            return this.slacken(hit, heldStack, -0.2F);
        }
        return false;
    }

    public boolean matches(final ItemStack stack) {
        if (this.getType().getItem().equals(stack.func_77973_b())) {
            final CompoundNBT tag = stack.func_77978_p();
            return tag == null || Utils.impliesNbt(this.serializeLogic(), tag);
        }
        return false;
    }

    private boolean replace(final PlayerEntity player, final Vector3d hit, final ItemStack heldStack) {
        return this.destination.get(this.world).map(dest -> {
            this.fastener.removeConnection(this);
            dest.removeConnection(this.uuid);
            if (this.shouldDrop()) {
                player.field_71071_by.func_70441_a(this.getItemStack());
            }
            final CompoundNBT data = heldStack.func_77978_p();
            final ConnectionType<? extends Connection> type = ((ConnectionItem) heldStack.func_77973_b()).getConnectionType();
            final Connection conn = this.fastener.connect(this.world, dest, type, data == null ? new CompoundNBT() : data, true);
            conn.slack = this.slack;
            conn.onConnect(player.field_70170_p, player, heldStack);
            heldStack.func_190918_g(1);
            this.world.func_184148_a(null, hit.field_72450_a, hit.field_72448_b, hit.field_72449_c, FLSounds.CORD_CONNECT.get(), SoundCategory.BLOCKS, 1, 1);
            return true;
        }).orElse(false);
    }

    private boolean slacken(final Vector3d hit, final ItemStack heldStack, final float amount) {
        if (this.slack <= 0 && amount < 0 || this.slack >= MAX_SLACK && amount > 0) {
            return true;
        }
        this.slack = MathHelper.func_76131_a(this.slack + amount, 0, MAX_SLACK);
        if (this.slack < 1e-2F) {
            this.slack = 0;
        }
        this.computeCatenary();
        this.world.func_184148_a(null, hit.field_72450_a, hit.field_72448_b, hit.field_72449_c, FLSounds.CORD_STRETCH.get(), SoundCategory.BLOCKS, 1, 0.8F + (MAX_SLACK - this.slack) * 0.4F);
        return true;
    }

    public void onConnect(final World world, final PlayerEntity user, final ItemStack heldStack) {}

    protected void onRemove() {}

    protected void onUpdate() {}

    protected void onCalculateCatenary(final boolean relocated) {}

    public final boolean update(final Vector3d from) {
        this.prevCatenary = this.catenary;
        final boolean changed = this.destination.get(this.world, false).map(dest -> {
            final Vector3d point = dest.getConnectionPoint();
            final boolean c = this.updateCatenary(from, dest, point);
            this.onUpdate();
            final double dist = point.func_72438_d(from);
            final double pull = dist - MAX_LENGTH + PULL_RANGE;
            if (pull > 0) {
                final int stage = (int) (pull + 0.1F);
                if (stage > this.prevStretchStage) {
                    this.world.func_184148_a(null, point.field_72450_a, point.field_72448_b, point.field_72449_c, FLSounds.CORD_STRETCH.get(), SoundCategory.BLOCKS, 0.25F, 0.5F + stage / 8F);
                }
                this.prevStretchStage = stage;
            }
            if (dist > MAX_LENGTH + PULL_RANGE) {
                this.world.func_184148_a(null, point.field_72450_a, point.field_72448_b, point.field_72449_c, FLSounds.CORD_SNAP.get(), SoundCategory.BLOCKS, 0.75F, 0.8F + this.world.field_73012_v.nextFloat() * 0.3F);
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

    private boolean updateCatenary(final Vector3d from, final Fastener<?> dest, final Vector3d point) {
        if (this.updateCatenary || this.isDynamic()) {
            final Vector3d vec = point.func_178788_d(from);
            if (vec.func_72433_c() > 1e-6) {
                final Direction facing = this.fastener.getFacing();
                this.catenary = Catenary.from(vec, facing.func_176740_k() != Direction.Axis.Y ? (float) Math.toRadians(90.0F + facing.func_185119_l()) : 0.0F, SLACK_CURVE, this.slack);
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

    public void addCollision(final CollidableList.Builder collision, final Vector3d origin) {
        if (this.catenary == null) {
            return;
        }
        final int count = this.catenary.getCount();
        if (count <= 2) {
            return;
        }
        final float r = this.getRadius();
        final Catenary.SegmentIterator it = this.catenary.iterator();
        final AxisAlignedBB[] bounds = new AxisAlignedBB[count - 1];
        int index = 0;
        while (it.next()) {
            final float x0 = it.getX(0.0F);
            final float y0 = it.getY(0.0F);
            final float z0 = it.getZ(0.0F);
            final float x1 = it.getX(1.0F);
            final float y1 = it.getY(1.0F);
            final float z1 = it.getZ(1.0F);
            bounds[index++] = new AxisAlignedBB(
                origin.field_72450_a + x0, origin.field_72448_b + y0, origin.field_72449_c + z0,
                origin.field_72450_a + x1, origin.field_72448_b + y1, origin.field_72449_c + z1
            ).func_186662_g(r);
        }
        collision.add(FeatureCollisionTree.build(CORD_FEATURE, i -> Segment.INSTANCE, i -> bounds[i], 1, bounds.length - 2));
    }

    public void deserialize(final Fastener<?> destination, final CompoundNBT compound, final boolean drop) {
        this.destination = destination.createAccessor();
        this.drop = drop;
        this.deserializeLogic(compound);
    }

    @Override
    public CompoundNBT serialize() {
        final CompoundNBT compound = new CompoundNBT();
        compound.func_218657_a("destination", FastenerType.serialize(this.destination));
        compound.func_218657_a("logic", this.serializeLogic());
        compound.func_74776_a("slack", this.slack);
        if (!this.drop) compound.func_74757_a("drop", false);
        return compound;
    }

    @Override
    public void deserialize(final CompoundNBT compound) {
        this.destination = FastenerType.deserialize(compound.func_74775_l("destination"));
        this.deserializeLogic(compound.func_74775_l("logic"));
        this.slack = compound.func_150297_b("slack", NBT.TAG_ANY_NUMERIC) ? compound.func_74760_g("slack") : 1;
        this.drop = !compound.func_150297_b("drop", NBT.TAG_ANY_NUMERIC) || compound.func_74767_n("drop");
        this.updateCatenary = true;
    }

    public CompoundNBT serializeLogic() {
        return new CompoundNBT();
    }

    public void deserializeLogic(final CompoundNBT compound) {}

    static class Segment implements Feature {
        static final Segment INSTANCE = new Segment();

        @Override
        public int getId() {
            return 0;
        }
    }
}
