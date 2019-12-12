package me.paulf.fairylights.util.crafting;

import com.google.common.base.Strings;
import me.paulf.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import me.paulf.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasicInert;
import me.paulf.fairylights.util.crafting.ingredient.IngredientAuxiliaryOreInert;
import me.paulf.fairylights.util.crafting.ingredient.IngredientRegular;
import me.paulf.fairylights.util.crafting.ingredient.IngredientRegularBasic;
import me.paulf.fairylights.util.crafting.ingredient.IngredientRegularList;
import me.paulf.fairylights.util.crafting.ingredient.IngredientRegularOre;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GenericRecipeBuilder {
	private static final char EMPTY_SPACE = ' ';

	private ResourceLocation name;

	private IRecipeSerializer<GenericRecipe> serializer;

	@Nullable
	private ItemStack output;

	private char[] shape = new char[0];

	private int width;

	private int height;

	private final Map<Character, IngredientRegular> ingredients = new HashMap<>();

	private final List<IngredientAuxiliary> auxiliaryIngredients = new ArrayList<>();

	public GenericRecipeBuilder(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer, Item item) {
		this(name, serializer, new ItemStack(item));
	}

	public GenericRecipeBuilder(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer, Block block) {
		this(name, serializer, new ItemStack(block));
	}

	public GenericRecipeBuilder(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer, ItemStack output) {
		this(name, serializer);
		this.output = Objects.requireNonNull(output, "output");
	}

	public GenericRecipeBuilder(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		this.name = name;
		this.serializer = serializer;
	}

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
		return withOutput(Objects.requireNonNull(item, "item"), 1);
	}

	public GenericRecipeBuilder withOutput(Item item, int size) {
		return withOutput(new ItemStack(Objects.requireNonNull(item, "item"), size));
	}

	public GenericRecipeBuilder withOutput(Block block) {
		return withOutput(Objects.requireNonNull(block, "block"), 1);
	}

	public GenericRecipeBuilder withOutput(Block block, int size) {
		return withOutput(new ItemStack(Objects.requireNonNull(block, "block"), size));
	}

	public GenericRecipeBuilder withOutput(ItemStack output) {
		this.output = Objects.requireNonNull(output, "output");
		return this;
	}

	public GenericRecipeBuilder withIngredient(char key, Item item) {
		return withIngredient(key, new ItemStack(Objects.requireNonNull(item, "item"), 1));
	}

	public GenericRecipeBuilder withIngredient(char key, Block block) {
		return withIngredient(key, new ItemStack(Objects.requireNonNull(block, "block"), 1));
	}

	public GenericRecipeBuilder withIngredient(char key, ItemStack stack) {
		return withIngredient(key, new IngredientRegularBasic(Objects.requireNonNull(stack, "stack")));
	}

	public GenericRecipeBuilder withIngredient(char key, Tag<Item> tag) {
		return withIngredient(key, new IngredientRegularOre(tag));
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
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(item, "item")));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Item item, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(item, "item")), isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(block, "block")));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Block block, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new ItemStack(Objects.requireNonNull(block, "block")), isRequired, limit);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(ItemStack stack) {
		return withAuxiliaryIngredient(Objects.requireNonNull(stack, "stack"), false, 1);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(ItemStack stack, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new IngredientAuxiliaryBasicInert(Objects.requireNonNull(stack, "stack"), isRequired, limit));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Tag<Item> tag) {
		return withAuxiliaryIngredient(tag, false, 1);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Tag<Item> tag, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new IngredientAuxiliaryOreInert(tag, isRequired, limit));
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
		return new GenericRecipe(name, serializer, output, ingredients, auxiliaryIngredients, width, height);
	}

	private static IngredientRegular asIngredient(Object object) {
		if (object instanceof Item) {
			return new IngredientRegularBasic(new ItemStack((Item) object));
		}
		if (object instanceof Block) {
			return new IngredientRegularBasic(new ItemStack((Block) object));
		}
		if (object instanceof ItemStack) {
			return new IngredientRegularBasic((ItemStack) object);
		}
		if (object instanceof Tag) {
			return new IngredientRegularOre((Tag<Item>) object);
		}
		if (object instanceof IngredientRegular) {
			return (IngredientRegular) object;
		}
		throw new IllegalArgumentException("Unknown ingredient object: " + object);
	}
}
