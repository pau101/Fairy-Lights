package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.Multimap;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface AuxiliaryIngredient<A> extends Ingredient<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> {
	boolean isRequired();

	int getLimit();

	@Nullable
	A accumulator();

	void consume(A accumulator, ItemStack ingredient);

	boolean finish(A accumulator, ItemStack output);

	default boolean process(Multimap<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> map, ItemStack output) {
		Collection<GenericRecipe.MatchResultAuxiliary> results = map.get(this);
		if (results.isEmpty() && isRequired()) {
			return true;
		}
		A ax = accumulator();
		for (GenericRecipe.MatchResultAuxiliary result : results) {
			consume(ax, result.getInput());
		}
		return finish(ax, output);
	}

	@Override
	public default void addTooltip(List<String> tooltip) {
		if (!isRequired()) {
			tooltip.add(Utils.formatRecipeTooltip("recipe.ingredient.auxiliary.optional"));
		}
	}
}
