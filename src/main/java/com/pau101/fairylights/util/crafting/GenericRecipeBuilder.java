package com.pau101.fairylights.util.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasicInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryOreInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularList;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularOre;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class GenericRecipeBuilder {
	private static final char EMPTY_SPACE = ' ';

	@Nullable
	private ItemStack output;

	private char[] shape = new char[0];

	private int width;

	private int height;

	private final Map<Character, IngredientRegular> ingredients = new HashMap<>();

	private final List<IngredientAuxiliary> auxiliaryIngredients = new ArrayList<>();

	public GenericRecipeBuilder(Item item) {
		this(item, 0);
	}

	public GenericRecipeBuilder(Item item, int metadata) {
		this(item, 1, metadata);
	}

	public GenericRecipeBuilder(Item item, int size, int metadata) {
		this(new ItemStack(item, size, metadata));
	}

	public GenericRecipeBuilder(Block block) {
		this(block, OreDictionary.WILDCARD_VALUE);
	}

	public GenericRecipeBuilder(Block block, int metadata) {
		this(block, 1, metadata);
	}

	public GenericRecipeBuilder(Block block, int size, int metadata) {
		this(new ItemStack(block, size, metadata));
	}

	public GenericRecipeBuilder(ItemStack output) {
		this.output = Objects.requireNonNull(output, "output");
	}

	public GenericRecipeBuilder() {}

	public GenericRecipeBuilder withShape(String... shape) {
		Objects.requireNonNull(shape, "shape");
		width = 0;
		height = 0;
		for (String row : shape) {
			if (row != null && row.length() > width) {
				width = row.length();
			}
			height++;
		}
		StringBuilder bob = new StringBuilder();
		for (String row : shape) {
			bob.append(Strings.nullToEmpty(row));
			int trail = width - (row == null ? 0 : row.length());
			while (trail --> 0) {
				bob.append(' ');
			}
		}
		this.shape = bob.toString().toCharArray();
		return this;
	}

	public GenericRecipeBuilder withOutput(Item item) {
		return withOutput(Objects.requireNonNull(item, "item"), 0);
	}

	public GenericRecipeBuilder withOutput(Item item, int metadata) {
		return withOutput(Objects.requireNonNull(item, "item"), 1, metadata);
	}

	public GenericRecipeBuilder withOutput(Item item, int size, int metadata) {
		return withOutput(new ItemStack(Objects.requireNonNull(item, "item"), size, metadata));
	}

	public GenericRecipeBuilder withOutput(Block block) {
		return withOutput(Objects.requireNonNull(block, "block"), OreDictionary.WILDCARD_VALUE);
	}

	public GenericRecipeBuilder withOutput(Block block, int metadata) {
		return withOutput(Objects.requireNonNull(block, "block"), 1, metadata);
	}

	public GenericRecipeBuilder withOutput(Block block, int size, int metadata) {
		return withOutput(new ItemStack(Objects.requireNonNull(block, "block"), size, metadata));
	}

	public GenericRecipeBuilder withOutput(ItemStack output) {
		this.output = Objects.requireNonNull(output, "output");
		return this;
	}

	public GenericRecipeBuilder withIngredient(char key, Item item) {
		return withIngredient(key, item, 0);
	}

	public GenericRecipeBuilder withIngredient(char key, Block block) {
		return withIngredient(key, block, OreDictionary.WILDCARD_VALUE);
	}

	public GenericRecipeBuilder withIngredient(char key, Item item, int metadata) {
		return withIngredient(key, new ItemStack(Objects.requireNonNull(item, "item"), 1, metadata));
	}

	public GenericRecipeBuilder withIngredient(char key, Block block, int metadata) {
		return withIngredient(key, new ItemStack(Objects.requireNonNull(block, "block"), 1, metadata));
	}

	public GenericRecipeBuilder withIngredient(char key, ItemStack stack) {
		return withIngredient(key, new IngredientRegularBasic(Objects.requireNonNull(stack, "stack")));
	}

	public GenericRecipeBuilder withIngredient(char key, String name) {
		return withIngredient(key, new IngredientRegularOre(name));
	}

	public GenericRecipeBuilder withIngredient(char key, IngredientRegular ingredient) {
		ingredients.put(key, Objects.requireNonNull(ingredient, "ingredient"));
		return this;
	}

	public GenericRecipeBuilder withAnyIngredient(char key, Object... objects) {
		Objects.requireNonNull(objects, "objects");
		List<IngredientRegular> ingredients = new ArrayList<>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			ingredients.add(asIngredient(objects[i]));
		}
		this.ingredients.put(key, new IngredientRegularList(ingredients));
		return this;
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Item item) {
		return withAuxiliaryIngredient(item, 0);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Item item, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(item, 0, isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block) {
		return withAuxiliaryIngredient(block, OreDictionary.WILDCARD_VALUE);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(block, OreDictionary.WILDCARD_VALUE, isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Item item, int metadata) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(item, "item"), 1, metadata));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Item item, int metadata, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(item, "item"), 1, metadata), isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block, int metadata) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(block, "block"), 1, metadata));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block, int metadata, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(block, "block"), 1, metadata), isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(ItemStack stack) {
		return withAuxiliaryIngredient(Objects.requireNonNull(stack, "stack"), false, 1);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(ItemStack stack, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new IngredientAuxiliaryBasicInert(Objects.requireNonNull(stack, "stack"), isRequired, limit));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(String name) {
		return withAuxiliaryIngredient(name, false, 1);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(String name, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new IngredientAuxiliaryOreInert(name, isRequired, limit));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(IngredientAuxiliary<?> ingredient) {
		auxiliaryIngredients.add(Objects.requireNonNull(ingredient, "ingredient"));
		return this;
	}

	public GenericRecipe build() {
		IngredientRegular[] ingredients = new IngredientRegular[width * height];
		for (int i = 0; i < shape.length; i++) {
			char key = shape[i];
			IngredientRegular ingredient = this.ingredients.get(key);
			if (ingredient == null) {
				if (key != EMPTY_SPACE) {
					throw new IllegalArgumentException("An ingredient is missing for the shape, \"" + key + "\"");
				}
				ingredient = GenericRecipe.EMPTY;
			}
			ingredients[i] = ingredient;
		}
		IngredientAuxiliary<?>[] auxiliaryIngredients = this.auxiliaryIngredients.toArray(
			new IngredientAuxiliary<?>[this.auxiliaryIngredients.size()]
		);
		return new GenericRecipe(output, ingredients, auxiliaryIngredients, width, height);
	}

	private static IngredientRegular asIngredient(Object object) {
		if (object instanceof Item) {
			return new IngredientRegularBasic(new ItemStack((Item) object));
		}
		if (object instanceof Block) {
			return new IngredientRegularBasic(new ItemStack((Block) object, 1, OreDictionary.WILDCARD_VALUE));
		}
		if (object instanceof ItemStack) {
			return new IngredientRegularBasic((ItemStack) object);
		}
		if (object instanceof String) {
			return new IngredientRegularOre((String) object);
		}
		if (object instanceof IngredientRegular) {
			return (IngredientRegular) object;
		}
		throw new IllegalArgumentException("Unknown ingredient object: " + object);
	}
}
