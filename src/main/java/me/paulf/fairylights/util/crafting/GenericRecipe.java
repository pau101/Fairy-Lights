package me.paulf.fairylights.util.crafting;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;
import me.paulf.fairylights.util.crafting.ingredient.Ingredient;
import me.paulf.fairylights.util.crafting.ingredient.AuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.EmptyRegularIngredient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;

public final class GenericRecipe extends SpecialRecipe {
	public static final EmptyRegularIngredient EMPTY = new EmptyRegularIngredient();

	private final IRecipeSerializer<GenericRecipe> serializer;

	private final ItemStack output;

	private final RegularIngredient[] ingredients;

	private final AuxiliaryIngredient<?>[] auxiliaryIngredients;

	private final int width;

	private final int height;

	private final ThreadLocal<ItemStack> result = ThreadLocal.withInitial(() -> ItemStack.EMPTY);

	private final ImmutableList<IntUnaryOperator> xFunctions = ImmutableList.of(IntUnaryOperator.identity(), i -> getWidth() - 1 - i);

	private final NonNullList<net.minecraft.item.crafting.Ingredient> displayIngredients;

	GenericRecipe(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer, ItemStack output, RegularIngredient[] ingredients, AuxiliaryIngredient<?>[] auxiliaryIngredients, int width, int height) {
		super(name);
		Objects.requireNonNull(serializer, "serializer");
		Objects.requireNonNull(output, "output");
		Objects.requireNonNull(ingredients, "ingredients");
		Objects.requireNonNull(auxiliaryIngredients, "auxiliaryIngredients");
		checkIngredients(ingredients, auxiliaryIngredients);
		Preconditions.checkArgument(width >= 0, "width must be greater than or equal to zero");
		Preconditions.checkArgument(height >= 0, "height must be greater than or equal to zero");
		this.serializer = serializer;
		this.output = output;
		this.ingredients = ingredients;
		this.auxiliaryIngredients = auxiliaryIngredients;
		this.width = width;
		this.height = height;
		displayIngredients = createDisplayIngredients();
	}

	private NonNullList<net.minecraft.item.crafting.Ingredient> createDisplayIngredients() {
		NonNullList<net.minecraft.item.crafting.Ingredient> ingredients = NonNullList.withSize(9, net.minecraft.item.crafting.Ingredient.EMPTY);
		for (int i = 0 ; i < this.ingredients.length; i++) {
			int x = i % width, y = i / width;
			ItemStack[] stacks = this.ingredients[i].getInputs().toArray(new ItemStack[0]);
			ingredients.set(x + y * 3, net.minecraft.item.crafting.Ingredient.fromStacks(stacks));
		}
		for (int i = 0, slot = 0; i < auxiliaryIngredients.length && slot < ingredients.size(); slot++) {
			net.minecraft.item.crafting.Ingredient ing = ingredients.get(slot);
			if (ing == net.minecraft.item.crafting.Ingredient.EMPTY) {
				AuxiliaryIngredient<?> aux = auxiliaryIngredients[i++];
				if (aux.isRequired()) {
					ItemStack[] stacks = aux.getInputs().toArray(new ItemStack[0]);
					ingredients.set(slot, net.minecraft.item.crafting.Ingredient.fromStacks(stacks));
				}
			}
		}
		return ingredients;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return serializer;
	}

	public ItemStack getOutput() {
		return output.copy();
	}

	public RegularIngredient[] getGenericIngredients() {
		return ingredients.clone();
	}

	public AuxiliaryIngredient<?>[] getAuxiliaryIngredients() {
		return auxiliaryIngredients.clone();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public NonNullList<net.minecraft.item.crafting.Ingredient> getIngredients() {
		return displayIngredients;
	}

	@Override
	public boolean canFit(int width, int height) {
		return this.width <= width && this.height <= height;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (!canFit(inventory.getWidth(), inventory.getHeight())) {
			return false;
		}
		int scanWidth = inventory.getWidth() + 1 - width;
		int scanHeight = inventory.getHeight() + 1 - height;
		for (int i = 0, end = scanWidth * scanHeight; i < end; i++) {
			int x = i % scanWidth, y = i / scanWidth;
			for (IntUnaryOperator func : xFunctions) {
				ItemStack result = getResult(inventory, prepareOutput(), x, y, func);
				this.result.set(result);
				if (!result.isEmpty()) {
					prepareResult();
					return true;
				}
			}
		}
		result.set(ItemStack.EMPTY);
		return false;
	}

	private ItemStack prepareOutput() {
		ItemStack result = output.copy();
		if (!result.hasTag()) {
			result.setTag(new CompoundNBT());
		}
		return result;
	}

	private void prepareResult() {
		ItemStack result = this.result.get();
		if (result.hasTag() && result.getTag().isEmpty()) {
			result.setTag(null);
		}
	}

	private ItemStack getResult(CraftingInventory inventory, ItemStack output, int originX, int originY, IntUnaryOperator funcX) {
		MatchResultRegular[] match = new MatchResultRegular[ingredients.length];
		Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> auxMatchResults = LinkedListMultimap.create();
		Map<AuxiliaryIngredient<?>, Integer> auxMatchTotals = new HashMap<>();
		Set<Ingredient<?, ?>> presentCalled = new HashSet<>();
		List<MatchResultAuxiliary> auxResults = new ArrayList<>();
		for (int i = 0, w = inventory.getWidth(), size = w * inventory.getHeight(), auxCount = auxiliaryIngredients.length; i < size; i++) {
			int x = i % w, y = i / w;
			int ingX = x - originX;
			int ingY = y - originY;
			ItemStack input = inventory.getStackInSlot(i);
			if (contains(ingX, ingY)) {
				int index = funcX.applyAsInt(ingX) + ingY * width;
				RegularIngredient ingredient = ingredients[index];
				MatchResultRegular result = ingredient.matches(input, output);
				if (!result.doesMatch()) {
					return ItemStack.EMPTY;
				}
				match[index] = result;
				result.forMatch(presentCalled, output);
			} else if (!EMPTY.matches(input, output).doesMatch()) {
				boolean nonAuxiliary = true;
				for (int n = 0; n < auxCount; n++) {
					MatchResultAuxiliary result = auxiliaryIngredients[n].matches(input, output);
					if (result.doesMatch()) {
						if (result.isAtLimit(auxMatchTotals.getOrDefault(result.ingredient, 0))) {
							return ItemStack.EMPTY;
						}
						result.forMatch(presentCalled, output);
						auxMatchTotals.merge(result.ingredient, 1, IntMath::checkedAdd);
						nonAuxiliary = false;
						result.propagate(auxMatchResults);
					}
					auxResults.add(result);
				}
				if (nonAuxiliary) {
					return ItemStack.EMPTY;
				}
			}
		}
		Set<Ingredient<?, ?>> absentCalled = new HashSet<>();
		for (MatchResultRegular result : match) {
			result.notifyAbsence(presentCalled, absentCalled, output);
		}
		for (MatchResultAuxiliary result : auxResults) {
			result.notifyAbsence(presentCalled, absentCalled, output);
		}
		for (AuxiliaryIngredient<?> ingredient : auxiliaryIngredients) {
			if (ingredient.process(auxMatchResults, output)) {
				return ItemStack.EMPTY;
			}
		}
		return output;
	}

	private boolean contains(int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inventory) {
		ItemStack result = this.result.get();
		return result.isEmpty() ? result : result.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	public interface MatchResult<I extends Ingredient, M extends MatchResult<I, M>> {
		I getIngredient();

		ItemStack getInput();

		boolean doesMatch();

		void forMatch(Set<Ingredient<?, ?>> called, ItemStack output);

		void notifyAbsence(Set<Ingredient<?, ?>> presentCalled, Set<Ingredient<?, ?>> absentCalled, ItemStack output);

		M withParent(M parent);
	}

	public static class MatchResultRegular implements MatchResult<RegularIngredient, MatchResultRegular> {
		protected final RegularIngredient ingredient;

		protected final ItemStack input;

		protected final boolean doesMatch;

		protected final ImmutableList<MatchResultRegular> supplementaryResults;

		public MatchResultRegular(RegularIngredient ingredient, ItemStack input, boolean doesMatch, List<MatchResultRegular> supplementaryResults) {
			this.ingredient = Objects.requireNonNull(ingredient, "ingredient");
			this.input = input;
			this.doesMatch = doesMatch;
			this.supplementaryResults = ImmutableList.copyOf(supplementaryResults);
		}

		@Override
		public final RegularIngredient getIngredient() {
			return ingredient;
		}

		@Override
		public final ItemStack getInput() {
			return input;
		}

		@Override
		public final boolean doesMatch() {
			return doesMatch;
		}

		@Override
		public void forMatch(Set<Ingredient<?, ?>> called, ItemStack output) {
			ingredient.matched(input, output);
			if (!called.contains(ingredient)) {
				ingredient.present(output);
				called.add(ingredient);
			}
		}

		@Override
		public void notifyAbsence(Set<Ingredient<?, ?>> presentCalled, Set<Ingredient<?, ?>> absentCalled, ItemStack output) {
			if (!presentCalled.contains(ingredient) && !absentCalled.contains(ingredient)) {
				ingredient.absent(output);
				absentCalled.add(ingredient);
			}
			for (MatchResultRegular result : supplementaryResults) {
				result.notifyAbsence(presentCalled, absentCalled, output);
			}
		}

		@Override
		public MatchResultRegular withParent(MatchResultRegular parent) {
			return new MatchResultParentedRegular(ingredient, input, doesMatch, supplementaryResults, parent);
		}
	}

	public static class MatchResultParentedRegular extends MatchResultRegular {
		protected final MatchResultRegular parent;

		public MatchResultParentedRegular(RegularIngredient ingredient, ItemStack input, boolean doesMatch, List<MatchResultRegular> supplementaryResults, MatchResultRegular parent) {
			super(ingredient, input, doesMatch, supplementaryResults);
			this.parent = Objects.requireNonNull(parent, "parent");
		}

		@Override
		public void forMatch(Set<Ingredient<?, ?>> called, ItemStack output) {
			super.forMatch(called, output);
			parent.forMatch(called, output);
		}

		@Override
		public void notifyAbsence(Set<Ingredient<?, ?>> presentCalled, Set<Ingredient<?, ?>> absentCalled, ItemStack output) {
			super.notifyAbsence(presentCalled, absentCalled, output);
			parent.notifyAbsence(presentCalled, absentCalled, output);
		}

		@Override
		public MatchResultRegular withParent(MatchResultRegular parent) {
			return this.parent.withParent(new MatchResultParentedRegular(ingredient, input, doesMatch, supplementaryResults, parent));
		}
	}

	public static class MatchResultAuxiliary implements MatchResult<AuxiliaryIngredient<?>, MatchResultAuxiliary> {
		protected final AuxiliaryIngredient ingredient;

		protected final ItemStack input;

		protected final boolean doesMatch;

		protected final ImmutableList<MatchResultAuxiliary> supplementaryResults;

		public MatchResultAuxiliary(AuxiliaryIngredient ingredient, ItemStack input, boolean doesMatch, List<MatchResultAuxiliary> supplementaryResults) {
			this.ingredient = Objects.requireNonNull(ingredient, "ingredient");
			this.input = input;
			this.doesMatch = doesMatch;
			this.supplementaryResults = ImmutableList.copyOf(supplementaryResults);
		}

		@Override
		public final AuxiliaryIngredient getIngredient() {
			return ingredient;
		}

		@Override
		public final ItemStack getInput() {
			return input;
		}

		@Override
		public final boolean doesMatch() {
			return doesMatch;
		}

		@Override
		public void forMatch(Set<Ingredient<?, ?>> called, ItemStack output) {
			if (!called.contains(ingredient)) {
				ingredient.present(output);
				called.add(ingredient);
			}
		}

		@Override
		public void notifyAbsence(Set<Ingredient<?, ?>> presentCalled, Set<Ingredient<?, ?>> absentCalled, ItemStack output) {
			if (!presentCalled.contains(ingredient) && !absentCalled.contains(ingredient)) {
				ingredient.absent(output);
				absentCalled.add(ingredient);
			}
			for (MatchResultAuxiliary result : supplementaryResults) {
				result.notifyAbsence(presentCalled, absentCalled, output);
			}
		}

		@Override
		public MatchResultAuxiliary withParent(MatchResultAuxiliary parent) {
			return new MatchResultParentedAuxiliary(ingredient, input, doesMatch, supplementaryResults, parent);
		}

		public boolean isAtLimit(int count) {
			return count >= ingredient.getLimit();
		}

		public void propagate(Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> map) {
			map.put(ingredient, this);
		}
	}

	public static class MatchResultParentedAuxiliary extends MatchResultAuxiliary {
		protected final MatchResultAuxiliary parent;

		public MatchResultParentedAuxiliary(AuxiliaryIngredient ingredient, ItemStack input, boolean doesMatch, List<MatchResultAuxiliary> supplementaryResults, MatchResultAuxiliary parent) {
			super(ingredient, input, doesMatch, supplementaryResults);
			this.parent = Objects.requireNonNull(parent, "parent");
		}

		@Override
		public void forMatch(Set<Ingredient<?, ?>> called, ItemStack output) {
			super.forMatch(called, output);
			parent.forMatch(called, output);
		}

		@Override
		public void notifyAbsence(Set<Ingredient<?, ?>> presentCalled, Set<Ingredient<?, ?>> absentCalled, ItemStack output) {
			super.notifyAbsence(presentCalled, absentCalled, output);
			parent.notifyAbsence(presentCalled, absentCalled, output);
		}

		@Override
		public MatchResultAuxiliary withParent(MatchResultAuxiliary parent) {
			return this.parent.withParent(new MatchResultParentedAuxiliary(ingredient, input, doesMatch, supplementaryResults, parent));
		}

		@Override
		public boolean isAtLimit(int count) {
			return super.isAtLimit(count) || parent.isAtLimit(count);
		}

		@Override
		public void propagate(Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> map) {
			super.propagate(map);
			parent.propagate(map);
		}
	}

	private static void checkIngredients(RegularIngredient[] ingredients, AuxiliaryIngredient<?>[] auxiliaryIngredients) {
		checkForNulls(ingredients);
		checkForNulls(auxiliaryIngredients);
		boolean ingredientDictator = checkDictatorship(false, ingredients);
		checkDictatorship(ingredientDictator, auxiliaryIngredients);
	}

	private static void checkForNulls(Ingredient<?, ?>[] ingredients) {
		for (int i = 0; i < ingredients.length; i++) {
			if (ingredients[i] == null) {
				throw new NullPointerException("Must not have null ingredients, found at index " + i);
			}
		}
	}

	private static boolean checkDictatorship(boolean foundDictator, Ingredient<?, ?>[] ingredients) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.dictatesOutputType()) {
				if (foundDictator) {
					throw new IllegalRecipeException("Only one ingredient can dictate output type");
				}
				foundDictator = true;
			}
		}
		return foundDictator;
	}
}
