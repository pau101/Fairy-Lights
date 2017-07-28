package com.pau101.fairylights.server.item.crafting;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.Utils;
import com.pau101.fairylights.util.crafting.GenericRecipeBuilder;
import com.pau101.fairylights.util.crafting.ingredient.Ingredient;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasicInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryListInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularDye;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularOre;
import com.pau101.fairylights.util.styledstring.StyledString;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;

public final class Recipes {
	private Recipes() {}

	public static final IngredientRegular LIGHT_DYE = new IngredientRegularDye() {
		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			return ImmutableList.of(OreDictUtils.getDyes(ItemLight.getLightColor(output.getItemDamage())));
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public void matched(ItemStack ingredient, ItemStack output) {
			output.setItemDamage(Mth.floorInterval(output.getMetadata(), ItemLight.COLOR_COUNT) + OreDictUtils.getDyeMetadata(ingredient));
		}
	};

	private static class LightIngredient extends IngredientAuxiliaryBasic<NBTTagList> {
		public LightIngredient(boolean isRequired) {
			super(FairyLights.light, OreDictionary.WILDCARD_VALUE, isRequired, 8);
		}

		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			NBTTagCompound compound = output.getTagCompound();
			if (compound == null) {
				return ImmutableList.of();
			}
			NBTTagList pattern = compound.getTagList("pattern", NBT.TAG_COMPOUND);
			if (pattern.hasNoTags()) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
			for (int i = 0; i < pattern.tagCount(); i++) {
				NBTTagCompound light = pattern.getCompoundTagAt(i);
				int meta = light.getInteger("light") * ItemLight.COLOR_COUNT + light.getByte("color");
				lights.add(ImmutableList.of(new ItemStack(FairyLights.light, 1, meta)));
			}
			return lights.build();
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public NBTTagList accumulator() {
			return new NBTTagList();
		}

		@Override
		public void consume(NBTTagList patternList, ItemStack ingredient) {
			int variant = ingredient.getMetadata();
			NBTTagCompound light = new NBTTagCompound();
			light.setInteger("light", ItemLight.getLightVariantOrdinal(variant));
			light.setByte("color", ItemLight.getLightColorOrdinal(variant));
			patternList.appendTag(light);
		}

		@Override
		public boolean finish(NBTTagList pattern, ItemStack output) {
			if (pattern.tagCount() > 0) {
				output.setTagInfo("pattern", pattern);
			}
			return false;
		}

		@Override
		public void addTooltip(List<String> tooltip) {
			tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.light"));
		}
	}

	private static class PennantIngredient extends IngredientAuxiliaryBasic<NBTTagList> {
		public PennantIngredient() {
			super(new ItemStack(FairyLights.pennant, 1, OreDictionary.WILDCARD_VALUE), true, 8);
		}

		@Override
		public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
			NBTTagCompound compound = output.getTagCompound();
			if (compound == null) {
				return ImmutableList.of();
			}
			NBTTagList pattern = compound.getTagList("pattern", NBT.TAG_COMPOUND);
			if (pattern.hasNoTags()) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
			for (int i = 0; i < pattern.tagCount(); i++) {
				NBTTagCompound pennant = pattern.getCompoundTagAt(i);
				pennants.add(ImmutableList.of(new ItemStack(FairyLights.pennant, 1, pennant.getByte("color"))));
			}
			return pennants.build();
		}

		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public NBTTagList accumulator() {
			return new NBTTagList();
		}

		@Override
		public void consume(NBTTagList patternList, ItemStack ingredient) {
			NBTTagCompound pennant = new NBTTagCompound();
			pennant.setByte("color", (byte) ingredient.getMetadata());
			patternList.appendTag(pennant);
		}

		@Override
		public boolean finish(NBTTagList pattern, ItemStack output) {
			if (pattern.tagCount() > 0) {
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

	public static final IRecipe FAIRY_LIGHTS = new GenericRecipeBuilder(FairyLights.hangingLights)
		.withShape("I-I")
		.withIngredient('I', "ingotIron")
		.withAnyIngredient('-',
			new IngredientRegularBasic(Items.STRING) {
				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					return useInputsForTagBool(this, output, "tight", false) ? super.getInput(output) : ImmutableList.of();
				}
			},
			new IngredientRegularOre("stickWood") {
				@Override
				public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
					return useInputsForTagBool(this, output, "tight", true) ? super.getInput(output) : ImmutableList.of();
				}

				@Override
				public void present(ItemStack output) {
					output.getTagCompound().setBoolean("tight", true);
				}
	
				@Override
				public void absent(ItemStack output) {
					output.getTagCompound().setBoolean("tight", false);
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
				output.getTagCompound().setBoolean("twinkle", true);
			}
	
			@Override
			public void absent(ItemStack output) {
				output.getTagCompound().setBoolean("twinkle", false);
			}

			@Override
			public void addTooltip(List<String> tooltip) {
				super.addTooltip(tooltip);
				tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.glowstone"));
			}
		})
		.build()
		.setRegistryName("fairy_lights");

	private static boolean useInputsForTagBool(Ingredient ingredient, ItemStack output, String key, boolean value) {
		NBTTagCompound compound = output.getTagCompound();
		return compound != null && compound.getBoolean(key) == value;
	}

	/*
	 *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
	 *  different recipe layouts the the input ingredients can be generated for so I could show applying a
	 *  new light pattern as well.
	 */
	public static final IRecipe FAIRY_LIGHTS_AUGMENTATION = new GenericRecipeBuilder(FairyLights.hangingLights)
		.withShape("F")
		.withIngredient('F', new IngredientRegularBasic(FairyLights.hangingLights) {
			@Override
			public ImmutableList<ItemStack> getInputs() {
				ItemStack stack = ingredient.copy();
				stack.setTagCompound(new NBTTagCompound());
				return makeHangingLightsExamples(stack);
			}

			@Override
			public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
				ItemStack stack = output.copy();
				NBTTagCompound compound = stack.getTagCompound();
				if (compound == null) {
					return ImmutableList.of();
				}
				stack.setCount(1);
				compound.setBoolean("twinkle", false);
				return ImmutableList.of(ImmutableList.of(stack));
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				NBTTagCompound compound = ingredient.getTagCompound();
				if (compound != null) {
					output.setTagCompound(compound.copy());
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
						if (output.getTagCompound().getBoolean("twinkle")) {
							return true;
						}
						output.getTagCompound().setBoolean("twinkle", true);
					}
					return false;
				}
			}
		))
		.build()
		.setRegistryName("fairy_lights_augmentation");

	private static ImmutableList<ItemStack> makeHangingLightsExamples(ItemStack stack) {
		return ImmutableList.of(
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.MAGENTA, EnumDyeColor.CYAN, EnumDyeColor.WHITE),
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.CYAN, EnumDyeColor.LIGHT_BLUE),
			makeHangingLights(stack, EnumDyeColor.SILVER, EnumDyeColor.PINK, EnumDyeColor.CYAN, EnumDyeColor.GREEN),
			makeHangingLights(stack, EnumDyeColor.SILVER, EnumDyeColor.PURPLE, EnumDyeColor.SILVER, EnumDyeColor.GREEN),
			makeHangingLights(stack, EnumDyeColor.CYAN, EnumDyeColor.YELLOW, EnumDyeColor.CYAN, EnumDyeColor.PURPLE)
		);
	}

	public static ItemStack makeHangingLights(ItemStack base, EnumDyeColor... colors) {
		ItemStack stack = base.copy();
		NBTTagCompound compound = stack.getTagCompound();
		NBTTagList lights = new NBTTagList();
		for (EnumDyeColor color : colors) {
			NBTTagCompound pennant = new NBTTagCompound();
			pennant.setByte("color", (byte) color.getDyeDamage());
			pennant.setInteger("light", LightVariant.FAIRY.ordinal());
			lights.appendTag(pennant);
		}
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		compound.setTag("pattern", lights);
		compound.setBoolean("twinkle", false);
		compound.setBoolean("tight", false);
		return stack;
	}

	public static final IRecipe TINSEL_GARLAND = new GenericRecipeBuilder(FairyLights.tinsel)
		.withShape(" P ", "I-I", " D ")
		.withIngredient('P', Items.PAPER)
		.withIngredient('I', "ingotIron")
		.withIngredient('-', Items.STRING)
		.withIngredient('D', new IngredientRegularDye() {
			@Override
			public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
				NBTTagCompound compound = output.getTagCompound();
				if (compound == null) {
					return ImmutableList.of();
				}
				return ImmutableList.of(OreDictUtils.getDyes(EnumDyeColor.byDyeDamage(compound.getByte("color"))));
			}

			@Override
			public boolean dictatesOutputType() {
				return true;
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.getTagCompound().setByte("color", (byte) OreDictUtils.getDyeMetadata(ingredient));
			}
		})
		.build()
		.setRegistryName("tinsel_garland");

	public static final IRecipe PENNANT_BUNTING = new GenericRecipeBuilder(FairyLights.pennantBunting)
		.withShape("I-I")
		.withIngredient('I', "ingotIron")
		.withIngredient('-', Items.STRING)
		.withAuxiliaryIngredient(new PennantIngredient())
		.build()
		.setRegistryName("pennant_bunting");

	public static final IRecipe PENNANT_BUNTING_AUGMENTATION = new GenericRecipeBuilder(FairyLights.pennantBunting)
		.withShape("B")
		.withIngredient('B', new IngredientRegularBasic(FairyLights.pennantBunting) {
			@Override
			public ImmutableList<ItemStack> getInputs() {
				ItemStack stack = ingredient.copy();
				stack.setTagCompound(new NBTTagCompound());
				return makePennantExamples(stack);
			}

			@Override
			public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
				NBTTagCompound compound = output.getTagCompound();
				if (compound == null) {
					return ImmutableList.of();
				}
				return ImmutableList.of(makePennantExamples(output));
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				NBTTagCompound compound = ingredient.getTagCompound();
				if (compound != null) {
					output.setTagCompound(compound.copy());
				}
			}
		})
		.withAuxiliaryIngredient(new PennantIngredient())
		.build()
		.setRegistryName("pennant_bunting_augmentation");

	private static ImmutableList<ItemStack> makePennantExamples(ItemStack stack) {
		return ImmutableList.of(
			makePennant(stack, EnumDyeColor.BLUE, EnumDyeColor.YELLOW, EnumDyeColor.RED),
			makePennant(stack, EnumDyeColor.PINK, EnumDyeColor.LIGHT_BLUE),
			makePennant(stack, EnumDyeColor.ORANGE, EnumDyeColor.WHITE),
			makePennant(stack, EnumDyeColor.LIME, EnumDyeColor.YELLOW)
		);
	}

	public static ItemStack makePennant(ItemStack base, EnumDyeColor... colors) {
		ItemStack stack = base.copy();
		NBTTagCompound compound = stack.getTagCompound();
		NBTTagList pennants = new NBTTagList();
		for (EnumDyeColor color : colors) {
			NBTTagCompound pennant = new NBTTagCompound();
			pennant.setByte("color", (byte) color.getDyeDamage());
			pennants.appendTag(pennant);
		}
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		compound.setTag("pattern", pennants);
		compound.setTag("text", StyledString.serialize(new StyledString()));
		return stack;
	}

	public static final IRecipe PENNANT = new GenericRecipeBuilder(FairyLights.pennant)
		.withShape("- -", "PDP", " P ")
		.withIngredient('P', Items.PAPER)
		.withIngredient('-', Items.STRING)
		.withIngredient('D', new IngredientRegularDye() {
			@Override
			public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
				return ImmutableList.of(OreDictUtils.getDyes(EnumDyeColor.byDyeDamage(output.getItemDamage())));
			}

			@Override
			public boolean dictatesOutputType() {
				return true;
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.setItemDamage(OreDictUtils.getDyeMetadata(ingredient));
			}
		})
		.build()
		.setRegistryName("pennant");
}
