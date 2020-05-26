package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.*;
import me.paulf.fairylights.util.crafting.*;
import net.minecraft.item.*;
import net.minecraft.tags.*;

import java.util.*;

public class OreRegularIngredient implements RegularIngredient {
    private final Tag<Item> tag;

    public OreRegularIngredient(final Tag<Item> tag) {
        this.tag = Objects.requireNonNull(tag, "tag");
    }

    @Override
    public GenericRecipe.MatchResultRegular matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultRegular(this, input, input.getItem().isIn(this.tag), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return this.tag.getAllElements().stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
    }
}
