package com.pau101.fairylights.util.crafting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.crafting.ingredient.Ingredient;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import com.pau101.fairylights.util.crafting.ingredient.behavior.factory.BehaviorNBTSetFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientAuxiliaryDyeFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientAuxiliaryItemFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientAuxiliaryListFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientAuxiliaryOreFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientRegularDyeFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientRegularItemFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientRegularListFactory;
import com.pau101.fairylights.util.crafting.ingredient.factory.IngredientRegularOreFactory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = FairyLights.ID)
public final class GenericRecipeFactory implements IRecipeFactory {
	private static IForgeRegistry<IngredientAuxiliaryFactory> auxiliaryIngredients;

	private static IForgeRegistry<IngredientRegularFactory> regularIngredients;

	private static IForgeRegistry<BehaviorFactory> behaviors;

	private static IForgeRegistry<BehaviorAuxiliaryFactory> auxiliaryBehaviors;

	private static IForgeRegistry<BehaviorRegularFactory> regularBehaviors;

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		ItemStack result = parseItemStack(context, json, "result");
		GenericRecipeBuilder builder = new GenericRecipeBuilder(result);
		String[] shape;
		if (json.has("pattern")) {
			JsonArray array = getJsonArray(json, "pattern");
			if (array.size() == 0) {
				throw new JsonSyntaxException("Invalid pattern, must not be empty");
			}
			shape = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				String row = JsonUtils.getString(array.get(i), "pattern[" + i + "]");
				if (i > 0 && row.length() != shape[0].length()) {
					throw new JsonSyntaxException("Invalid pattern, each row must be the same width");
				}
				shape[i] = row;
			}
		} else {
			throw new JsonSyntaxException("Missing pattern, expected string or string array");
		}
		builder.withShape(shape);
		for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
			String token = entry.getKey();
			if (token.length() != 1) {
				throw new JsonSyntaxException("Invalid pattern key, expected a single character: " + token);
			}
			builder.withIngredient(token.charAt(0), parseRegularIngredient(context, entry.getValue(), "key:" + token));
		}
		if (json.has("auxiliary")) {
			for (JsonElement e : getJsonArray(json, "auxiliary")) {
				builder.withAuxiliaryIngredient(parseAuxiliaryIngredient(context, e, "auxiliary"));
			}
		}
		try {
			return builder.build();
		} catch (RuntimeException e) {
			throw new JsonSyntaxException(e);
		}
	}

	public static IngredientRegular parseRegularIngredient(JsonContext context, JsonElement json, String errorName) {
		return parseIngredient(context, json, regularIngredients, errorName);
	}

	public static IngredientAuxiliary parseAuxiliaryIngredient(JsonContext context, JsonElement json, String errorName) {
		return parseIngredient(context, json, auxiliaryIngredients, errorName);
	}

	private static <I extends Ingredient<I, ?>, F extends IngredientFactory<I, F, ?>> I parseIngredient(JsonContext context, JsonElement element, IForgeRegistry<F> ingredientRegistry, String errorName) {
		F factory = null;
		JsonObject obj = null;
		if (element.isJsonObject()) {
			obj = element.getAsJsonObject();
			String type = JsonUtils.getString(obj, "type", "fairylights:item");
			factory = getEntry(context, ingredientRegistry, type, "ingredient");
		} else if (element.isJsonArray()) {
			obj = new JsonObject();
			obj.add("ingredients", element);
			factory = ingredientRegistry.getValue(new ResourceLocation("fairylights:list"));
		} else if (element.isJsonPrimitive()) {
			obj = new JsonObject();
			obj.add("item", element);
			factory = ingredientRegistry.getValue(new ResourceLocation("fairylights:item"));
		}
		if (factory == null) {
			throw new JsonSyntaxException("Unknown ingredient for " + errorName);
		}
		return factory.parse(context, obj);
	}

	@SubscribeEvent
	public static void init(RegistryEvent.NewRegistry event) {
		auxiliaryIngredients = newReg(IngredientAuxiliaryFactory.class, "auxiliary_ingredients");
		regularIngredients = newReg(IngredientRegularFactory.class, "regular_ingredients");
		behaviors = newReg(BehaviorFactory.class, "behaviors");
		auxiliaryBehaviors = newReg(BehaviorAuxiliaryFactory.class, "auxiliary_behaviors");
		regularBehaviors = newReg(BehaviorRegularFactory.class, "regular_behaviors");
	}

	private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> newReg(Class<T> type, String name) {
		return new RegistryBuilder<T>()
			.setName(new ResourceLocation(FairyLights.ID, name))
			.setType(type)
			.disableSaving()
			.create();
	}

	@SubscribeEvent
	public static void registerRegularFactories(RegistryEvent.Register<IngredientRegularFactory> event) {
		event.getRegistry().registerAll(
			create(IngredientRegularDyeFactory::new, "dye"),
			create(IngredientRegularItemFactory::new, "item"),
			create(IngredientRegularListFactory::new, "list"),
			create(IngredientRegularOreFactory::new, "ore_dict")
		);
	}

	@SubscribeEvent
	public static void registerAuxiliaryFactories(RegistryEvent.Register<IngredientAuxiliaryFactory> event) {
		event.getRegistry().registerAll(
			create(IngredientAuxiliaryDyeFactory::new, "dye"),
			create(IngredientAuxiliaryItemFactory::new, "item"),
			create(IngredientAuxiliaryListFactory::new, "list"),
			create(IngredientAuxiliaryOreFactory::new, "ore_dict")
		);
	}

	@SubscribeEvent
	public static void registerBehaviors(RegistryEvent.Register<BehaviorFactory> event) {
		event.getRegistry().registerAll(
			create(BehaviorNBTSetFactory::new, "set_nbt")
		);
	}

	private static <T extends IForgeRegistryEntry.Impl<T>> T create(Supplier<T> factory, String name) {
		return factory.get().setRegistryName(name);
	}

	public static JsonArray getJsonArray(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			JsonElement elem = json.get(memberName);
			if (elem.isJsonArray()) {
				return elem.getAsJsonArray();
			}
			JsonArray array = new JsonArray();
			array.add(elem);
			return array;
		}
		throw new JsonSyntaxException("Missing " + memberName);
	}

	public static ItemStack parseItemStack(JsonContext context, JsonObject json, String memberName) {
		if (!json.has(memberName)) {
			throw new JsonSyntaxException("Missing " + memberName + ", expected to find an item stack");
		}
		JsonElement elem = json.get(memberName);
		if (elem.isJsonPrimitive()) {
			String itemName = context.appendModId(elem.getAsString());
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
			if (item == null) {
				throw new JsonSyntaxException("Unknown item '" + itemName + "'");
			}
			if (item.getHasSubtypes()) {
				throw new JsonParseException("Missing data for item '" + itemName + "'");
			}
			return new ItemStack(item);
		}
		if (elem.isJsonObject()) {
			return parseItemStack(context, elem.getAsJsonObject());
		}
		throw new JsonSyntaxException("Invalid item stack, must be string or object");
	}

	public static ItemStack parseItemStack(JsonContext context, JsonObject json) {
		return CraftingHelper.getItemStack(json, context);
	}

	private static <T extends IForgeRegistryEntry<T>> T getEntry(JsonContext context, IForgeRegistry<T> registry, String type, String registryKind) {
		return getOptionalEntry(context, registry, type)
			.orElseThrow(() -> new JsonSyntaxException("Unknown " + registryKind + " type: " + type));
	}

	private static <T extends IForgeRegistryEntry<T>> Optional<T> getOptionalEntry(JsonContext context, IForgeRegistry<T> registry, String type) {
		return getOptionalEntry(context, registry, new ResourceLocation(context.appendModId(type)));
	}

	private static <T extends IForgeRegistryEntry<T>> Optional<T> getOptionalEntry(JsonContext context, IForgeRegistry<T> registry, ResourceLocation id) {
		return Optional.ofNullable(registry.getValue(id));
	}

	public static abstract class BehaviorFactoryEntry<B, S extends BehaviorFactoryEntry<B, S>> extends IForgeRegistryEntry.Impl<S> {
		public abstract B parse(JsonContext context, JsonObject json);
	}

	public static abstract class BehaviorFactory extends BehaviorFactoryEntry<Behavior, BehaviorFactory> {}

	public static abstract class BehaviorAuxiliaryFactory extends BehaviorFactoryEntry<BehaviorAuxiliary<?>, BehaviorAuxiliaryFactory> {}

	public static abstract class BehaviorRegularFactory extends BehaviorFactoryEntry<BehaviorRegular, BehaviorRegularFactory> {}

	public static abstract class IngredientFactory<I extends Ingredient<I, ?>, S extends IngredientFactory<I, S, BB>, BB extends BehaviorBuilder> extends IForgeRegistryEntry.Impl<S> {
		public final I parse(JsonContext context, JsonObject json) {
			return parse(context, json, parseBehaviors(context, json), parseTooltip(json));
		}

		public abstract I parse(JsonContext context, JsonObject json, BB behaviors, Consumer<List<String>> tooltip);

		private BB parseBehaviors(JsonContext context, JsonObject json) {
			BB behaviors = newBehaviorBuilder();
			if (!json.has("behavior")) {
				return behaviors;
			}
			JsonArray arr = getJsonArray(json, "behavior");
			for (int i = 0; i < arr.size(); i++) {
				JsonElement behaviorElem = arr.get(i);
				if (behaviorElem.isJsonObject()) {
					behaviors.parse(context, json);
				} else {
					throw new JsonSyntaxException("Invalid behavior element, must be object");
				}
			}
			return behaviors;
		}

		protected abstract BB newBehaviorBuilder();

		private Consumer<List<String>> parseTooltip(JsonObject json) {
			if (!json.has("tooltip")) {
				return Ingredient.EMPTY_TOOLTIP;
			}
			JsonArray arr = getJsonArray(json, "tooltip");
			ImmutableList.Builder<String> tooltipBldr = ImmutableList.builder();
			for (int i = 0; i < arr.size(); i++) {
				tooltipBldr.add(JsonUtils.getString(arr.get(i), "tooltip[" + i + "]"));
			}
			ImmutableList<String> tooltip = tooltipBldr.build();
			return lines -> tooltip.stream().map(I18n::format).forEach(lines::add);
		}
	}

	public static abstract class IngredientAuxiliaryFactory extends IngredientFactory<IngredientAuxiliary, IngredientAuxiliaryFactory, AuxiliaryBehaviorBuilder> {
		@Override
		protected final AuxiliaryBehaviorBuilder newBehaviorBuilder() {
			return new AuxiliaryBehaviorBuilder();
		}

		@Override
		public final IngredientAuxiliary parse(JsonContext context, JsonObject json, AuxiliaryBehaviorBuilder behaviors, Consumer<List<String>> tooltip) {
			boolean isRequired;
			if (json.has("required")) {
				isRequired = JsonUtils.getBoolean(json, "required");
			} else {
				isRequired = false;
			}
			int limit;
			if (json.has("limit")) {
				limit = JsonUtils.getInt(json, "limit");
			} else {
				limit = IngredientAuxiliary.MAX_LIMIT;
			}
			return parse(context, json, behaviors, tooltip, isRequired, limit);
		}

		protected abstract IngredientAuxiliary parse(JsonContext context, JsonObject json, AuxiliaryBehaviorBuilder behaviors, Consumer<List<String>> tooltip, boolean isRequired, int limit);
	}

	public static final class AuxiliaryBehaviorBuilder extends BehaviorBuilder {
		private final ImmutableList.Builder<BehaviorAuxiliary<?>> auxiliaryBehaviors = ImmutableList.builder();

		public ImmutableList<BehaviorAuxiliary<?>> getAuxiliaryBehaviors() {
			return auxiliaryBehaviors.build();
		}

		@Override
		protected ImmutableList<Type<?, ?>> createTypes() {
			return ImmutableList.of(
				new Type<>(behaviors, GenericRecipeFactory.behaviors),
				new Type<>(auxiliaryBehaviors, GenericRecipeFactory.auxiliaryBehaviors)
			);
		}
	}

	public static abstract class BehaviorBuilder {
		private final ImmutableList<Type<?, ?>> types;

		protected final ImmutableList.Builder<Behavior> behaviors = ImmutableList.builder();

		private BehaviorBuilder() {
			this.types = createTypes();
		}

		public ImmutableList<Behavior> getBehaviors() {
			return behaviors.build();
		}

		protected abstract ImmutableList<Type<?, ?>> createTypes();

		public final void parse(JsonContext context, JsonObject json) {
			String name = context.appendModId(JsonUtils.getString(json, "type"));
			ResourceLocation id = new ResourceLocation(name);
			for (Type<?, ?> type : types) {
				if (type.addIfPresent(context, json, id)) {
					return;
				}
			}
			throw new JsonSyntaxException("Unknown behavior type: " + name);
		}

		protected final class Type<B, BF extends BehaviorFactoryEntry<B, BF>> {
			private final ImmutableList.Builder<B> builder;

			private final IForgeRegistry<BF> registry;

			public Type(ImmutableList.Builder<B> builder, IForgeRegistry<BF> registry) {
				this.builder = builder;
				this.registry = registry;
			}

			private boolean addIfPresent(JsonContext context, JsonObject json, ResourceLocation id) {
				BF factory = registry.getValue(id);
				if (factory == null) {
					return false;
				}
				builder.add(factory.parse(context, json));
				return true;
			}
		}
	}

	public static abstract class IngredientRegularFactory extends IngredientFactory<IngredientRegular, IngredientRegularFactory, RegularBehaviorBuilder> {
		@Override
		protected final RegularBehaviorBuilder newBehaviorBuilder() {
			return new RegularBehaviorBuilder();
		}
	}

	public static final class RegularBehaviorBuilder extends BehaviorBuilder {
		private final ImmutableList.Builder<BehaviorRegular> regularBehaviors = ImmutableList.builder();

		public ImmutableList<BehaviorRegular> getRegularBehaviors() {
			return regularBehaviors.build();
		}

		@Override
		protected ImmutableList<Type<?, ?>> createTypes() {
			return ImmutableList.of(
				new Type<>(behaviors, GenericRecipeFactory.behaviors),
				new Type<>(regularBehaviors, GenericRecipeFactory.regularBehaviors)
			);
		}
	}
}
