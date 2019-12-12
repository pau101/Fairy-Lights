package com.pau101.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.Utils;
import com.pau101.fairylights.util.crafting.GenericRecipe;
import com.pau101.fairylights.util.crafting.GenericRecipeBuilder;
import com.pau101.fairylights.util.crafting.ingredient.Ingredient;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasicInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryListInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryOre;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularDye;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@EventBusSubscriber(modid = FairyLights.ID)
public final class Recipes {
	private Recipes() {}

	public static final DeferredRegister<IRecipeSerializer<?>> REG = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, FairyLights.ID);

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS = REG.register("crafting_special_fairy_lights", makeSerializer(Recipes::createFairyLights));

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS_AUGMENTATION = REG.register("crafting_special_fairy_lights_augmentation", makeSerializer(Recipes::createFairyLightsAugmentation));

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> TINSEL_GARLAND = REG.register("crafting_special_tinsel_garland", makeSerializer(Recipes::createTinselGarland));

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING = REG.register("crafting_special_pennant_bunting", makeSerializer(Recipes::createPennantBunting));

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING_AUGMENTATION = REG.register("crafting_special_pennant_bunting_augmentation", makeSerializer(Recipes::createPennantBuntingAugmentation));

	private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT = REG.register("crafting_special_pennant", makeSerializer(Recipes::createPennant));

	static {
		for (LightVariant variant : LightVariant.values()) {
			REG.register("crafting_special_" + variant.getName(), makeSerializer(variant::getRecipe));
		}
	}

	public static final IngredientRegular LIGHT_DYE = new IngredientRegularDye() {
		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			return ImmutableList.of(OreDictUtils.getDyes(ItemLight.getLightColor(output)));
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public void matched(ItemStack ingredient, ItemStack output) {
			output.getTag().putByte("color", (byte) OreDictUtils.getDyeMetadata(ingredient));
		}
	};

	private static Supplier<IRecipeSerializer<GenericRecipe>> makeSerializer(BiFunction<ResourceLocation, IRecipeSerializer<GenericRecipe>, GenericRecipe> factory) {
		class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GenericRecipe> {
			@Override
			public GenericRecipe read(final ResourceLocation recipeId, final JsonObject json) {
				return factory.apply(recipeId, this);
			}

			@Override
			public GenericRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
				return factory.apply(recipeId, this);
			}

			@Override
			public void write(final PacketBuffer buffer, final GenericRecipe recipe) {}
		}
		return Serializer::new;
	}

	private static GenericRecipe createFairyLights(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
			.withShape("I-I")
			.withIngredient('I', Tags.Items.INGOTS_IRON)
			.withAnyIngredient('-',
				new IngredientRegularBasic(Items.STRING) {
					@Override
					public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
						return useInputsForTagBool(this, output, "tight", false) ? super.getInput(output) : ImmutableList.of();
					}
				},
				new IngredientRegularBasic(Items.STICK) {
					@Override
					public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
						return useInputsForTagBool(this, output, "tight", true) ? super.getInput(output) : ImmutableList.of();
					}

					@Override
					public void present(ItemStack output) {
						output.getTag().putBoolean("tight", true);
					}

					@Override
					public void absent(ItemStack output) {
						output.getTag().putBoolean("tight", false);
					}
				}
			)
			.withAuxiliaryIngredient(new LightIngredient(true))
			.withAuxiliaryIngredient(new IngredientAuxiliaryBasicInert(Items.GLOWSTONE_DUST, false, 1) {
				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					return useInputsForTagBool(this, output, "twinkle", true) ? super.getInput(output) : ImmutableList.of();
				}

				@Override
				public void present(ItemStack output) {
					output.getTag().putBoolean("twinkle", true);
				}

				@Override
				public void absent(ItemStack output) {
					output.getTag().putBoolean("twinkle", false);
				}

				@Override
				public void addTooltip(List<String> tooltip) {
					super.addTooltip(tooltip);
					tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.glowstone"));
				}
			})
			.build();
	}

	private static boolean useInputsForTagBool(Ingredient ingredient, ItemStack output, String key, boolean value) {
		CompoundNBT compound = output.getTag();
		return compound != null && compound.getBoolean(key) == value;
	}

	/*
	 *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
	 *  different recipe layouts the the input ingredients can be generated for so I could show applying a
	 *  new light pattern as well.
	 */
	private static GenericRecipe createFairyLightsAugmentation(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
			.withShape("F")
			.withIngredient('F', new IngredientRegularBasic(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new)) {
				@Override
				public ImmutableList<ItemStack> getInputs() {
					ItemStack stack = ingredient.copy();
					stack.setTag(new CompoundNBT());
					return makeHangingLightsExamples(stack);
				}

				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					ItemStack stack = output.copy();
					CompoundNBT compound = stack.getTag();
					if (compound == null) {
						return ImmutableList.of();
					}
					stack.setCount(1);
					compound.putBoolean("twinkle", false);
					return ImmutableList.of(ImmutableList.of(stack));
				}

				@Override
				public void matched(ItemStack ingredient, ItemStack output) {
					CompoundNBT compound = ingredient.getTag();
					if (compound != null) {
						output.setTag(compound.copy());
					}
				}
			})
			.withAuxiliaryIngredient(new IngredientAuxiliaryListInert(true,
				new LightIngredient(false) {
					@Override
					public ImmutableList<ItemStack> getInputs() {
						return ImmutableList.of();
					}

					@Override
					public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
						return ImmutableList.of();
					}
				},
				new IngredientAuxiliaryBasic<MutableInt>(Items.GLOWSTONE_DUST, false, 1) {
					@Override
					public MutableInt accumulator() {
						return new MutableInt();
					}

					@Override
					public void consume(MutableInt count, ItemStack ingredient) {
						count.increment();
					}

					@Override
					public boolean finish(MutableInt count, ItemStack output) {
						if (count.intValue() > 0) {
							if (output.getTag().getBoolean("twinkle")) {
								return true;
							}
							output.getTag().putBoolean("twinkle", true);
						}
						return false;
					}
				}
			))
			.build();
	}

	private static ImmutableList<ItemStack> makeHangingLightsExamples(ItemStack stack) {
		return ImmutableList.of(
			makeHangingLights(stack, DyeColor.CYAN, DyeColor.MAGENTA, DyeColor.CYAN, DyeColor.WHITE),
			makeHangingLights(stack, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.LIGHT_BLUE),
			makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PINK, DyeColor.CYAN, DyeColor.GREEN),
			makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PURPLE, DyeColor.LIGHT_GRAY, DyeColor.GREEN),
			makeHangingLights(stack, DyeColor.CYAN, DyeColor.YELLOW, DyeColor.CYAN, DyeColor.PURPLE)
		);
	}

	public static ItemStack makeHangingLights(ItemStack base, DyeColor... colors) {
		ItemStack stack = base.copy();
		CompoundNBT compound = stack.getTag();
		ListNBT lights = new ListNBT();
		for (DyeColor color : colors) {
			CompoundNBT light = new CompoundNBT();
			light.putByte("color", (byte) color.getId());
			light.putInt("light", LightVariant.FAIRY.ordinal());
			lights.add(light);
		}
		if (compound == null) {
			compound = new CompoundNBT();
			stack.setTag(compound);
		}
		compound.put("pattern", lights);
		compound.putBoolean("twinkle", false);
		compound.putBoolean("tight", false);
		return stack;
	}

	private static GenericRecipe createTinselGarland(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.TINSEL.orElseThrow(IllegalStateException::new))
			.withShape(" P ", "I-I", " D ")
			.withIngredient('P', Items.PAPER)
			.withIngredient('I', Tags.Items.INGOTS_IRON)
			.withIngredient('-', Items.STRING)
			.withIngredient('D', new IngredientRegularDye() {
				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					CompoundNBT compound = output.getTag();
					if (compound == null) {
						return ImmutableList.of();
					}
					return ImmutableList.of(OreDictUtils.getDyes(DyeColor.byId(compound.getByte("color"))));
				}

				@Override
				public boolean dictatesOutputType() {
					return true;
				}

				@Override
				public void matched(ItemStack ingredient, ItemStack output) {
					output.getTag().putByte("color", (byte) OreDictUtils.getDyeMetadata(ingredient));
				}
			})
			.build();
	}

	private static GenericRecipe createPennantBunting(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
			.withShape("I-I")
			.withIngredient('I', Tags.Items.INGOTS_IRON)
			.withIngredient('-', Items.STRING)
			.withAuxiliaryIngredient(new PennantIngredient())
			.build();
	}

	private static GenericRecipe createPennantBuntingAugmentation(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
			.withShape("B")
			.withIngredient('B', new IngredientRegularBasic(FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new)) {
				@Override
				public ImmutableList<ItemStack> getInputs() {
					ItemStack stack = ingredient.copy();
					stack.setTag(new CompoundNBT());
					return makePennantExamples(stack);
				}

				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					CompoundNBT compound = output.getTag();
					if (compound == null) {
						return ImmutableList.of();
					}
					return ImmutableList.of(makePennantExamples(output));
				}

				@Override
				public void matched(ItemStack ingredient, ItemStack output) {
					CompoundNBT compound = ingredient.getTag();
					if (compound != null) {
						output.setTag(compound.copy());
					}
				}
			})
			.withAuxiliaryIngredient(new PennantIngredient())
			.build();
	}

	private static ImmutableList<ItemStack> makePennantExamples(ItemStack stack) {
		return ImmutableList.of(
			makePennant(stack, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.RED),
			makePennant(stack, DyeColor.PINK, DyeColor.LIGHT_BLUE),
			makePennant(stack, DyeColor.ORANGE, DyeColor.WHITE),
			makePennant(stack, DyeColor.LIME, DyeColor.YELLOW)
		);
	}

	public static ItemStack makePennant(ItemStack base, DyeColor... colors) {
		ItemStack stack = base.copy();
		CompoundNBT compound = stack.getTag();
		ListNBT pennants = new ListNBT();
		for (DyeColor color : colors) {
			CompoundNBT pennant = new CompoundNBT();
			pennant.putByte("color", (byte) color.getId());
			pennants.add(pennant);
		}
		if (compound == null) {
			compound = new CompoundNBT();
			stack.setTag(compound);
		}
		compound.put("pattern", pennants);
		compound.put("text", StyledString.serialize(new StyledString()));
		return stack;
	}

	private static GenericRecipe createPennant(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT.orElseThrow(IllegalStateException::new))
			.withShape("- -", "PDP", " P ")
			.withIngredient('P', Items.PAPER)
			.withIngredient('-', Items.STRING)
			.withIngredient('D', new IngredientRegularDye() {
				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					return ImmutableList.of(OreDictUtils.getDyes(ItemLight.getLightColor(output)));
				}

				@Override
				public boolean dictatesOutputType() {
					return true;
				}

				@Override
				public void matched(ItemStack ingredient, ItemStack output) {
					output.getOrCreateTag().putByte("color", (byte) OreDictUtils.getDyeMetadata(ingredient));
				}
			})
			.build();
	}

	private static class LightIngredient extends IngredientAuxiliaryOre<ListNBT> {
		private LightIngredient(boolean isRequired) {
			super(new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "lights")), isRequired, 8);
		}

		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			CompoundNBT compound = output.getTag();
			if (compound == null) {
				return ImmutableList.of();
			}
			ListNBT pattern = compound.getList("pattern", Constants.NBT.TAG_COMPOUND);
			if (pattern.isEmpty()) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
			for (int i = 0; i < pattern.size(); i++) {
				CompoundNBT light = pattern.getCompound(i);
				ItemStack stack = new ItemStack(LightVariant.getLightVariant(light.getInt("light")).getItem());
				stack.getOrCreateTag().putByte("color", light.getByte("color"));
				lights.add(ImmutableList.of(stack));
			}
			return lights.build();
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public ListNBT accumulator() {
			return new ListNBT();
		}

		@Override
		public void consume(ListNBT patternList, ItemStack ingredient) {
			CompoundNBT light = new CompoundNBT();
			light.putInt("light", Arrays.stream(LightVariant.values())
				.filter(v -> ingredient.getItem().equals(v.getItem()))
				.mapToInt(Enum::ordinal)
				.findFirst().orElse(0)
			);
			light.putByte("color", (byte) ItemLight.getLightColor(ingredient).getId());
			patternList.add(light);
		}

		@Override
		public boolean finish(ListNBT pattern, ItemStack output) {
			if (pattern.size() > 0) {
				output.setTagInfo("pattern", pattern);
			}
			return false;
		}

		@Override
		public void addTooltip(List<String> tooltip) {
			tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.light"));
		}
	}

	private static class PennantIngredient extends IngredientAuxiliaryBasic<ListNBT> {
		private PennantIngredient() {
			super(new ItemStack(FLItems.PENNANT.orElseThrow(IllegalStateException::new)), true, 8);
		}

		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			CompoundNBT compound = output.getTag();
			if (compound == null) {
				return ImmutableList.of();
			}
			ListNBT pattern = compound.getList("pattern", Constants.NBT.TAG_COMPOUND);
			if (pattern.isEmpty()) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
			for (int i = 0; i < pattern.size(); i++) {
				CompoundNBT pennant = pattern.getCompound(i);
				ItemStack stack = new ItemStack(FLItems.PENNANT.orElseThrow(IllegalStateException::new));
				stack.getOrCreateTag().putByte("color", pennant.getByte("color"));
				pennants.add(ImmutableList.of(stack));
			}
			return pennants.build();
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public ListNBT accumulator() {
			return new ListNBT();
		}

		@Override
		public void consume(ListNBT patternList, ItemStack ingredient) {
			CompoundNBT pennant = new CompoundNBT();
			pennant.putByte("color", (byte) ItemLight.getLightColor(ingredient).getId());
			patternList.add(pennant);
		}

		@Override
		public boolean finish(ListNBT pattern, ItemStack output) {
			if (pattern.size() > 0) {
				output.setTagInfo("pattern", pattern);
				output.setTagInfo("text", StyledString.serialize(new StyledString()));
			}
			return false;
		}

		@Override
		public void addTooltip(List<String> tooltip) {
			tooltip.add(Utils.formatRecipeTooltip("recipe.pennantBunting.pennant"));
		}
	}
}
