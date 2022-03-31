package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.feature.Pennant;
import me.paulf.fairylights.server.item.DyeableItem;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PennantBuntingConnection extends HangingFeatureConnection<Pennant> implements Lettered {
    private List<ItemStack> pattern;

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
    public boolean interact(final PlayerEntity player, final Vector3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
            final int index = feature % this.pattern.size();
            final ItemStack pennant = this.pattern.get(index);
            if (!ItemStack.func_77989_b(pennant, heldStack)) {
                final ItemStack placed = heldStack.func_77979_a(1);
                this.pattern.set(index, placed);
                ItemHandlerHelper.giveItemToPlayer(player, pennant);
                this.computeCatenary();
                heldStack.func_190918_g(1);
                this.world.func_184148_a(null, hit.field_72450_a, hit.field_72448_b, hit.field_72449_c, FLSounds.FEATURE_COLOR_CHANGE.get(), SoundCategory.BLOCKS, 1, 1);
                return true;
            }
        }
        return super.interact(player, hit, featureType, feature, heldStack, hand);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        for (final Pennant light : this.features) {
            light.tick(this.world);
        }
    }

    @Override
    protected Pennant[] createFeatures(final int length) {
        return new Pennant[length];
    }

    @Override
    protected Pennant createFeature(final int index, final Vector3d point, final float yaw, final float pitch) {
        final ItemStack data = this.pattern.isEmpty() ? ItemStack.field_190927_a : this.pattern.get(index % this.pattern.size());
        return new Pennant(index, point, yaw, pitch, DyeableItem.getColor(data), data.func_77973_b());
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
        this.computeCatenary();
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
        for (final ItemStack entry : this.pattern) {
            patternList.add(entry.func_77955_b(new CompoundNBT()));
        }
        compound.func_218657_a("pattern", patternList);
        compound.func_218657_a("text", StyledString.serialize(this.text));
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.pattern = new ArrayList<>();
        final ListNBT patternList = compound.func_150295_c("pattern", NBT.TAG_COMPOUND);
        for (int i = 0; i < patternList.size(); i++) {
            this.pattern.add(ItemStack.func_199557_a(patternList.func_150305_b(i)));
        }
        this.text = StyledString.deserialize(compound.func_74775_l("text"));
    }
}
