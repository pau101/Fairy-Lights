package com.pau101.fairylights.server.item.crafting;

import org.apache.commons.lang3.mutable.MutableInt;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.util.DyeOreDictUtils;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.crafting.GenericRecipeBuilder;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryBasicInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryListInert;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularBasic;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularDye;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularOre;
import com.pau101.fairylights.util.styledstring.StyledString;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

public final class Recipes {
	private Recipes() {}

	public static final IngredientRegular LIGHT_DYE = new IngredientRegularDye() {
		@Override
		public boolean dictatesOutputType() {
			return true;
		}

		@Override
		public void matched(ItemStack ingredient, ItemStack output) {
			output.setItemDamage(Mth.floorInterval(output.getMetadata(), ItemLight.COLOR_COUNT) + DyeOreDictUtils.getDyeMetadata(ingredient));
		}
	};

	private static class LightIngredient extends IngredientAuxiliaryBasic<NBTTagList> {
		public LightIngredient(boolean isRequired) {
			super(FairyLights.light, OreDictionary.WILDCARD_VALUE, isRequired, 8);
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
		public boolean finish(NBTTagList patternList, ItemStack output) {
			if (patternList.tagCount() > 0) {
				output.setTagInfo("pattern", patternList);
			}
			return false;
		}
	}

	private static class PennantIngredient extends IngredientAuxiliaryBasic<NBTTagList> {
		public PennantIngredient() {
			super(new ItemStack(FairyLights.pennant, 1, OreDictionary.WILDCARD_VALUE), true, 8);
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
		public boolean finish(NBTTagList patternList, ItemStack output) {
			if (patternList.tagCount() > 0) {
				output.setTagInfo("pattern", patternList);
				output.setTagInfo("text", StyledString.serialize(new StyledString()));
			}
			return false;
		}
	}

	public static final IRecipe FAIRY_LIGHTS = new GenericRecipeBuilder(FairyLights.hangingLights)
		.withShape("I-I")
		.withIngredient('I', "ingotIron")
		.withAnyIngredient('-',
			Items.STRING,
			new IngredientRegularOre("stickWood") {
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
			public void present(ItemStack output) {
				output.getTagCompound().setBoolean("twinkle", true);
			}
	
			@Override
			public void absent(ItemStack output) {
				output.getTagCompound().setBoolean("twinkle", false);
			}
		})
		.build();

	public static final IRecipe FAIRY_LIGHTS_AUGMENTATION = new GenericRecipeBuilder(FairyLights.hangingLights)
		.withShape("F")
		.withIngredient('F', new IngredientRegularBasic(FairyLights.hangingLights) {
			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				NBTTagCompound compound = ingredient.getTagCompound();
				if (compound != null) {
					output.setTagCompound(compound.copy());
				}
			}
		})
		.withAuxiliaryIngredient(new IngredientAuxiliaryListInert(true,
			new LightIngredient(false),
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
		.build();

	public static final IRecipe TINSEL_GARLAND = new GenericRecipeBuilder(FairyLights.tinsel)
		.withShape(" P ", "I-I", " D ")
		.withIngredient('P', Items.PAPER)
		.withIngredient('I', "ingotIron")
		.withIngredient('-', Items.STRING)
		.withIngredient('D', new IngredientRegularDye() {
			@Override
			public boolean dictatesOutputType() {
				return true;
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.getTagCompound().setByte("color", (byte) DyeOreDictUtils.getDyeMetadata(ingredient));
			}
		})
		.build();

	public static final IRecipe PENNANT_BUNTING = new GenericRecipeBuilder(FairyLights.pennantBunting)
		.withShape("I-I")
		.withIngredient('I', "ingotIron")
		.withIngredient('-', Items.STRING)
		.withAuxiliaryIngredient(new PennantIngredient())
		.build();

	public static final IRecipe PENNANT_BUNTING_AUGMENTATION = new GenericRecipeBuilder(FairyLights.pennantBunting)
		.withShape("B")
		.withIngredient('B', new IngredientRegularBasic(FairyLights.pennantBunting) {
			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				NBTTagCompound compound = ingredient.getTagCompound();
				if (compound != null) {
					output.setTagCompound(compound.copy());
				}
			}
		})
		.withAuxiliaryIngredient(new PennantIngredient())
		.build();

	public static final IRecipe PENNANT = new GenericRecipeBuilder(FairyLights.pennant)
		.withShape("- -", "PDP", " P ")
		.withIngredient('P', Items.PAPER)
		.withIngredient('-', Items.STRING)
		.withIngredient('D', new IngredientRegularDye() {
			@Override
			public boolean dictatesOutputType() {
				return true;
			}

			@Override
			public void matched(ItemStack ingredient, ItemStack output) {
				output.setItemDamage(DyeOreDictUtils.getDyeMetadata(ingredient));
			}
		})
		.build();

	public static final IRecipe LETTER_BUNTING = new GenericRecipeBuilder(FairyLights.letterBunting)
		.withShape("I-I", "PBF")
		.withIngredient('I', "ingotIron")
		.withIngredient('-', Items.STRING)
		.withIngredient('P', Items.PAPER)
		.withIngredient('B', "dyeBlack")
		.withIngredient('F', new IngredientRegularBasic(Items.FEATHER) {
			@Override
			public void present(ItemStack output) {
				output.setTagInfo("text", StyledString.serialize(new StyledString()));
			}
		})
		.build();
}
