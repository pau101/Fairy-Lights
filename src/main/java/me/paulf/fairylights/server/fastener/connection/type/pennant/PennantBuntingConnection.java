package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.item.ColorLightItem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.NBTSerializable;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PennantBuntingConnection extends HangingFeatureConnection<Pennant> implements Lettered {
    private List<Entry> pattern;

    private StyledString text;

    public PennantBuntingConnection(final ConnectionType<? extends PennantBuntingConnection> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
        this.pattern = new ArrayList<>();
        this.text = new StyledString();
    }

    @Override
    public float getRadius() {
        return 0.045F;
    }

    @Override
    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        if (this.openTextGui(player, action, intersection)) {
            super.processClientAction(player, action, intersection);
        }
    }

    @Override
    public boolean interact(final PlayerEntity player, final Vec3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
            final int index = feature % this.pattern.size();
            final Entry patternEntry = this.pattern.get(index);
            final int color = ColorLightItem.getColor(OreDictUtils.getDyeColor(heldStack));
            if (patternEntry.getColor() != color) {
                patternEntry.color = color;
                this.dataUpdateState = true;
                heldStack.shrink(1);
                this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.get(), SoundCategory.BLOCKS, 1, 1);
                return true;
            }
        }
        return super.interact(player, hit, featureType, feature, heldStack, hand);
    }

    @Override
    protected void onUpdateLate() {
        super.onUpdateLate();
        for (final Pennant light : this.features) {
            light.tick(this.world);
        }
    }

    @Override
    protected Pennant[] createFeatures(final int length) {
        return new Pennant[length];
    }

    @Override
    protected Pennant createFeature(final int index, final Vec3d point, final float yaw, final float pitch) {
        final Pennant pennant = new Pennant(index, point, yaw, pitch);
        if (this.pattern.size() > 0) {
            final Entry e = this.pattern.get(index % this.pattern.size());
            pennant.setColor(e.getColor());
            pennant.setItem(e.item);
        }
        return pennant;
    }

    @Override
    protected float getFeatureSpacing() {
        return 0.6875F;
    }

    @Override
    public boolean isSupportedText(final StyledString text) {
        return text.length() <= this.features.length && Lettered.super.isSupportedText(text);
    }

    @Override
    public void setText(final StyledString text) {
        this.text = text;
        this.dataUpdateState = true;
    }

    @Override
    public StyledString getText() {
        return this.text;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createTextGUI() {
        return new EditLetteredConnectionScreen<>(this);
    }

    @Override
    public CompoundNBT serializeLogic() {
        final CompoundNBT compound = super.serializeLogic();
        final ListNBT patternList = new ListNBT();
        for (final Entry entry : this.pattern) {
            patternList.add(entry.serialize());
        }
        compound.put("pattern", patternList);
        compound.put("text", StyledString.serialize(this.text));
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.pattern = new ArrayList<>();
        final ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
        for (int i = 0; i < patternList.size(); i++) {
            this.pattern.add(Entry.from(patternList.getCompound(i)));
        }
        this.text = StyledString.deserialize(compound.getCompound("text"));
    }

    static class Entry implements NBTSerializable {
        Item item;

        int color;

        private Entry() {}

        Entry(final Item item, final int color) {
            this.item = item;
            this.color = color;
        }

        public Item getItem() {
            return this.item;
        }

        public int getColor() {
            return this.color;
        }

        @Override
        public CompoundNBT serialize() {
            final CompoundNBT compound = new CompoundNBT();
            compound.putString("item", this.item.getRegistryName().toString());
            ColorLightItem.setColor(compound, this.color);
            return compound;
        }

        @Override
        public void deserialize(final CompoundNBT compound) {
            final ResourceLocation key = ResourceLocation.tryCreate(compound.getString("item"));
            if (ForgeRegistries.ITEMS.containsKey(key)) {
                this.item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(key), "item");
            } else {
                this.item = FLItems.TRIANGLE_PENNANT.get();
            }
            this.color = ColorLightItem.getColor(compound);
        }

        static Entry from(final CompoundNBT compound) {
            final Entry entry = new Entry();
            entry.deserialize(compound);
            return entry;
        }
    }
}
