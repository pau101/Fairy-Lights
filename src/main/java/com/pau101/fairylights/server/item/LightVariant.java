package com.pau101.fairylights.server.item;

import com.google.common.base.CaseFormat;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.item.crafting.Recipes;
import com.pau101.fairylights.util.Utils;
import com.pau101.fairylights.util.crafting.GenericRecipeBuilder;

import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public enum LightVariant {
	FAIRY("fairy", true, 5, 5, new GenericRecipeBuilder()
		.withShape(" I ", "IDI", " G ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('G', "paneGlassColorless")
	),
	PAPER("paper", false, 9, 16.5F, new GenericRecipeBuilder()
		.withShape(" I ", "PDP", "PPP")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('P', Items.PAPER)
	),
	ORB("orb", false, 10, 11.5F, new GenericRecipeBuilder()
		.withShape( " I ", "SDS", " W ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('S', Items.STRING)
		.withIngredient('W', Blocks.WOOL)
	),
	FLOWER("flower", true, 10, 6, new GenericRecipeBuilder()
		.withShape(" I ", "RDB", " Y ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('R', Blocks.RED_FLOWER, EnumFlowerType.POPPY.getMeta())
		.withIngredient('Y', Blocks.YELLOW_FLOWER)
		.withIngredient('B', Blocks.RED_FLOWER, EnumFlowerType.BLUE_ORCHID.getMeta())
	),
	ORNATE("ornate", false, 24, 8, 12, new GenericRecipeBuilder()
		.withShape(" I ", "GDG", "IGI")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('G', "nuggetGold")
	),
	OIL("oil", false, 32, 8, 13.5F, new GenericRecipeBuilder()
		.withShape(" I ", "SDS", "IGI")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('S', "stickWood")
		.withIngredient('G', "paneGlassColorless")
	),
	LUXO_BALL("luxo_ball", true, 5, 5, new GenericRecipeBuilder()
		.withShape(" I ", "BBB", "YRY")
		.withIngredient('I', "ingotIron")
		.withIngredient('B', "dyeBlue")
		.withIngredient('Y', "dyeYellow")
		.withIngredient('R', "dyeRed")
	),
	JACK_O_LANTERN("jack_o_lantern", true, 7, 9.5F, new GenericRecipeBuilder()
		.withShape(" I ", "SDS", "GPG")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('S', "slabWood")
		.withIngredient('G', Blocks.TORCH)
		.withIngredient('P', Blocks.PUMPKIN)
	),
	SKULL("skull", true, 6, 9, new GenericRecipeBuilder()
		.withShape(" I ", "IDI", " B ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withAnyIngredient('B', Items.BONE, new ItemStack(Items.SKULL, 1, 0))
	),
	GHOST("ghost", true, 6, 8, new GenericRecipeBuilder()
		.withShape(" I ", "PDP", "IGI")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('P', Items.PAPER)
		.withIngredient('G', "paneGlassWhite")
	),
	SPIDER("spider", true, 12, 14, new GenericRecipeBuilder()
		.withShape(" I ", "WDW", "SES")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('W', Blocks.WEB)
		.withIngredient('S', Items.STRING)
		.withIngredient('E', Items.SPIDER_EYE)
	),
	WITCH("witch", true, 8, 10, new GenericRecipeBuilder()
		.withShape(" I ", "BDW", " S ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('B', Items.GLASS_BOTTLE)
		.withIngredient('W', Items.WHEAT)
		.withIngredient('S', "stickWood")
	),
	SNOWFLAKE("snowflake", true, 8, 12.5F, new GenericRecipeBuilder()
		.withShape(" I ", "SDS", " G ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('S', Items.SNOWBALL)
		.withIngredient('G', "paneGlassWhite")
	),
	ICICLE("icicle", false, 10, 7, 20, new GenericRecipeBuilder()
		.withShape(" I ", "GDG", " B ")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('G', "paneGlassColorless")
		.withAnyIngredient('B', Items.WATER_BUCKET, Blocks.ICE, Blocks.PACKED_ICE)
	),
	METEOR("meteor", false, 24, 3, 28.5F, new GenericRecipeBuilder()
		.withShape(" I ", "GDG", "IPI")
		.withIngredient('I', "ingotIron")
		.withIngredient('D', Recipes.LIGHT_DYE)
		.withIngredient('G', Items.GLOWSTONE_DUST)
		.withIngredient('P', Items.PAPER)
	, 0.02F, 100);

	private final String name;

	private final String unlocalizedName;

	private final boolean parallelsCord;

	private final float spacing;

	private final float width;

	private final float height;

	private final IRecipe recipe;

	private final float twinkleChance;

	private final int tickCycle;

	private final boolean alwaysTwinkle;

	private LightVariant(String name, boolean parallelsCord, float width, float height, GenericRecipeBuilder recipe) {
		this(name, parallelsCord, 16, width, height, recipe);
	}

	private LightVariant(String name, boolean parallelsCord, float spacing, float width, float height, GenericRecipeBuilder recipe) {
		this(name, parallelsCord, spacing, width, height, recipe, 0.05F, 40, false);
	}

	private LightVariant(String name, boolean parallelsCord, float spacing, float width, float height, GenericRecipeBuilder recipe, float twinkleChance, int tickCycle) {
		this(name, parallelsCord, spacing, width, height, recipe, twinkleChance, tickCycle, true);
	}

	private LightVariant(String name, boolean parallelsCord, float spacing, float width, float height, GenericRecipeBuilder recipe, float twinkleChance, int tickCycle, boolean alwaysTwinkle) {
		this.name = name;
		this.parallelsCord = parallelsCord;
		this.spacing = spacing;
		this.width = width / 16;
		this.height = height / 16;
		this.recipe = recipe.withOutput(FairyLights.light, 4, getFirstMeta()).build();
		this.twinkleChance = twinkleChance;
		this.tickCycle = tickCycle;
		this.alwaysTwinkle = alwaysTwinkle;
		unlocalizedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
	}

	public String getName() {
		return name;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public boolean parallelsCord() {
		return parallelsCord;
	}

	public float getSpacing() {
		return spacing;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public IRecipe getRecipe() {
		return recipe;
	}

	public float getTwinkleChance() {
		return twinkleChance;
	}

	public int getTickCycle() {
		return tickCycle;
	}

	public boolean alwaysDoTwinkleLogic() {
		return alwaysTwinkle;
	}

	public int getFirstMeta() {
		return ordinal() * ItemLight.COLOR_COUNT; 
	}

	public static LightVariant getLightVariant(int index) {
		return Utils.getEnumValue(LightVariant.class, index);
	}
}
