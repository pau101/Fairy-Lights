package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.item.LightItem;
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

public final class PennantBuntingConnection extends HangingFeatureConnection<Pennant> implements Lettered {
    private List<DyeColor> pattern;

    private StyledString text;

    public PennantBuntingConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
    }

    public PennantBuntingConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
        super(world, fastener, uuid);
        this.pattern = new ArrayList<>();
        this.text = new StyledString();
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
    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        if (this.openTextGui(player, action, intersection)) {
            super.processClientAction(player, action, intersection);
        }
    }

    @Override
    public boolean interact(final PlayerEntity player, final Vec3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
            final int index = feature % this.pattern.size();
            final DyeColor patternColor = this.pattern.get(index);
            final DyeColor color = DyeColor.getColor(heldStack);
            if (patternColor != color) {
                this.pattern.set(index, color);
                this.dataUpdateState = true;
                heldStack.shrink(1);
                this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
                return true;
            }
        }
        return super.interact(player, hit, featureType, feature, heldStack, hand);
    }

    @Override
    protected Pennant[] createFeatures(final int length) {
        return new Pennant[length];
    }

    @Override
    protected Pennant createFeature(final int index, final Vec3d point, final Vec3d rotation) {
        final Pennant pennant = new Pennant(index, point, rotation);
        if (this.pattern.size() > 0) {
            pennant.setColor(LightItem.getColorValue(this.pattern.get(index % this.pattern.size())));
        }
        return pennant;
    }

    @Override
    protected float getFeatureSpacing() {
        return 11;
    }

    @Override
    public boolean isSuppportedText(final StyledString text) {
        return text.length() <= this.features.length && Lettered.super.isSuppportedText(text);
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
        for (final DyeColor color : this.pattern) {
            final CompoundNBT colorCompound = new CompoundNBT();
            colorCompound.putByte("color", (byte) color.getId());
            patternList.add(colorCompound);
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
            final CompoundNBT colorCompound = patternList.getCompound(i);
            this.pattern.add(DyeColor.byId(colorCompound.getByte("color")));
        }
        this.text = StyledString.deserialize(compound.getCompound("text"));
    }
}
