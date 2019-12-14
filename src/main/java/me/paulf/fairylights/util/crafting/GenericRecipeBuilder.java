package me.paulf.fairylights.util.crafting;

import com.google.common.base.Strings;
import me.paulf.fairylights.util.crafting.ingredient.AuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertBasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertOreAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.BasicRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.ListRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.OreRegularIngredient;
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

	private final Map<Character, RegularIngredient> ingredients = new HashMap<>();

	private final List<AuxiliaryIngredient> auxiliaryIngredients = new ArrayList<>();

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
		return withIngredient(key, new BasicRegularIngredient(Objects.requireNonNull(stack, "stack")));
	}

	public GenericRecipeBuilder withIngredient(char key, Tag<Item> tag) {
		return withIngredient(key, new OreRegularIngredient(tag));
	}

	public GenericRecipeBuilder withIngredient(char key, RegularIngredient ingredient) {
		ingredients.put(key, Objects.requireNonNull(ingredient, "ingredient"));
		return this;
	}

	public GenericRecipeBuilder withAnyIngredient(char key, Object... objects) {
		Objects.requireNonNull(objects, "objects");
		List<RegularIngredient> ingredients = new ArrayList<>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			ingredients.add(asIngredient(objects[i]));
		}
		this.ingredients.put(key, new ListRegularIngredient(ingredients));
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
		return withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(Objects.requireNonNull(stack, "stack"), isRequired, limit));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Tag<Item> tag) {
		return withAuxiliaryIngredient(tag, false, 1);
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(Tag<Item> tag, boolean isRequired, int limit) {
		return withAuxiliaryIngredient(new InertOreAuxiliaryIngredient(tag, isRequired, limit));
	}

	public GenericRecipeBuilder withAuxiliaryIngredient(AuxiliaryIngredient<?> ingredient) {
		auxiliaryIngredients.add(Objects.requireNonNull(ingredient, "ingredient"));
		return this;
	}

	public GenericRecipe build() {
		RegularIngredient[] ingredients = new RegularIngredient[width * height];
		for (int i = 0; i < shape.length; i++) {
			char key = shape[i];
			RegularIngredient ingredient = this.ingredients.get(key);
			if (ingredient == null) {
				if (key != EMPTY_SPACE) {
					throw new IllegalArgumentException("An ingredient is missing for the shape, \"" + key + "\"");
				}
				ingredient = GenericRecipe.EMPTY;
			}
			ingredients[i] = ingredient;
		}
		AuxiliaryIngredient<?>[] auxiliaryIngredients = this.auxiliaryIngredients.toArray(
			new AuxiliaryIngredient<?>[this.auxiliaryIngredients.size()]
		);
		return new GenericRecipe(name, serializer, output, ingredients, auxiliaryIngredients, width, height);
	}

	private static RegularIngredient asIngredient(Object object) {
		if (object instanceof Item) {
			return new BasicRegularIngredient(new ItemStack((Item) object));
		}
		if (object instanceof Block) {
			return new BasicRegularIngredient(new ItemStack((Block) object));
		}
		if (object instanceof ItemStack) {
			return new BasicRegularIngredient((ItemStack) object);
		}
		if (object instanceof Tag) {
			return new OreRegularIngredient((Tag<Item>) object);
		}
		if (object instanceof RegularIngredient) {
			return (RegularIngredient) object;
		}
		throw new IllegalArgumentException("Unknown ingredient object: " + object);
	}
}
