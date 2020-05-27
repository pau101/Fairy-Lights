package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.Collections;
import java.util.Objects;

public abstract class OreAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
    protected final Tag<Item> tag;

    protected final boolean isRequired;

    protected final int limit;

    public OreAuxiliaryIngredient(final Tag<Item> tag, final boolean isRequired, final int limit) {
        Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
        this.tag = Objects.requireNonNull(tag, "tag");
        this.isRequired = isRequired;
        this.limit = limit;
    }

    @Override
    public final GenericRecipe.MatchResultAuxiliary matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultAuxiliary(this, input, input.getItem().isIn(this.tag), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return this.tag.getAllElements().stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    public boolean isRequired() {
        return this.isRequired;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }
}
