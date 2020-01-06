package me.paulf.fairylights.util.crafting.ingredient;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class InertListAuxiliaryIngredient extends ListAuxiliaryIngredient<Void> {
    public InertListAuxiliaryIngredient(final boolean isRequired, final int limit, final AuxiliaryIngredient<?>... ingredients) {
        super(isRequired, limit, ingredients);
    }

    public InertListAuxiliaryIngredient(final boolean isRequired, final AuxiliaryIngredient<?>... ingredients) {
        super(isRequired, ingredients);
    }

    public InertListAuxiliaryIngredient(final List<? extends AuxiliaryIngredient<?>> ingredients, final boolean isRequired, final int limit) {
        super(Objects.requireNonNull(ingredients, "ingredients"), isRequired, limit);
    }

    @Nullable
    @Override
    public Void accumulator() {
        return null;
    }

    @Override
    public void consume(final Void v, final ItemStack ingredient) {}

    @Override
    public boolean finish(final Void v, final ItemStack output) {
        return false;
    }
}
