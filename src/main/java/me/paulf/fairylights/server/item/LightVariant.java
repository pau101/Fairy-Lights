package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.GenericRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.UnaryOperator;

public enum LightVariant {
	FAIRY("fairy_light", FLItems.FAIRY_LIGHT, true, 5, 5, b -> b
		.withShape(" I ", "IDI", " G ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
	),
	PAPER("paper_lantern", FLItems.PAPER_LANTERN, false, 9, 16.5F, b -> b
		.withShape(" I ", "PDP", "PPP")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('P', Items.PAPER)
	),
	ORB("orb_lantern", FLItems.ORB_LANTERN, false, 10, 11.5F, b -> b
		.withShape(" I ", "SDS", " W ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('S', Items.STRING)
		.withIngredient('W', Blocks.WHITE_WOOL)
	),
	FLOWER("flower_light", FLItems.FLOWER_LIGHT, true, 10, 6, b -> b
		.withShape(" I ", "RDB", " Y ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('R', Blocks.POPPY)
		.withIngredient('Y', Blocks.DANDELION)
		.withIngredient('B', Blocks.BLUE_ORCHID)
	),
	ORNATE("ornate_lantern", FLItems.ORNATE_LANTERN, false, 24, 8, 12, b -> b
		.withShape(" I ", "GDG", "IGI")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('G', Tags.Items.NUGGETS_GOLD)
	),
	OIL("oil_lantern", FLItems.OIL_LANTERN, false, 32, 8, 13.5F, b -> b
		.withShape(" I ", "SDS", "IGI")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('S', Items.STICK)
		.withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
	),
	JACK_O_LANTERN("jack_o_lantern", FLItems.JACK_O_LANTERN, true, 7, 9.5F, b -> b
		.withShape(" I ", "SDS", "GPG")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('S', ItemTags.WOODEN_SLABS)
		.withIngredient('G', Blocks.TORCH)
		.withIngredient('P', Blocks.PUMPKIN)
	),
	SKULL("skull_light", FLItems.SKULL_LIGHT, true, 6, 9, b -> b
		.withShape(" I ", "IDI", " B ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withAnyIngredient('B', Items.BONE, new ItemStack(Items.SKELETON_SKULL))
	),
	GHOST("ghost_light", FLItems.GHOST_LIGHT, true, 6, 8, b -> b
		.withShape(" I ", "PDP", "IGI")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('P', Items.PAPER)
		.withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
	),
	SPIDER("spider_light", FLItems.SPIDER_LIGHT, true, 12, 14, b -> b
		.withShape(" I ", "WDW", "SES")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('W', Blocks.COBWEB)
		.withIngredient('S', Items.STRING)
		.withIngredient('E', Items.SPIDER_EYE)
	),
	WITCH("witch_light", FLItems.WITCH_LIGHT, true, 8, 10, b -> b
		.withShape(" I ", "BDW", " S ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('B', Items.GLASS_BOTTLE)
		.withIngredient('W', Items.WHEAT)
		.withIngredient('S', Items.STICK)
	),
	SNOWFLAKE("snowflake_light", FLItems.SNOWFLAKE_LIGHT, true, 8, 12.5F, b -> b
		.withShape(" I ", "SDS", " G ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('S', Items.SNOWBALL)
		.withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
	),
	ICICLE("icicle_lights", FLItems.ICICLE_LIGHTS, false, 10, 7, 20, b -> b
		.withShape(" I ", "GDG", " B ")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
		.withAnyIngredient('B', Items.WATER_BUCKET, Blocks.ICE, Blocks.PACKED_ICE)
	),
	METEOR("meteor_light", FLItems.METEOR_LIGHT, false, 24, 3, 28.5F, b -> b
		.withShape(" I ", "GDG", "IPI")
		.withIngredient('I', Tags.Items.INGOTS_IRON)
		.withIngredient('D', FLCraftingRecipes.LIGHT_DYE)
		.withIngredient('G', Items.GLOWSTONE_DUST)
		.withIngredient('P', Items.PAPER)
	, 0.02F, 100);

	private final String name;

	private RegistryObject<? extends Item> item;

	private final boolean parallelsCord;

	private final float spacing;

	private final float width;

	private final float height;

	private final UnaryOperator<GenericRecipeBuilder> recipe;

	private final float twinkleChance;

	private final int tickCycle;

	private final boolean alwaysTwinkle;

	private LightVariant(String name, RegistryObject<? extends Item> item, boolean parallelsCord, float width, float height, UnaryOperator<GenericRecipeBuilder> recipe) {
		this(name, item, parallelsCord, 16, width, height, recipe);
	}

	private LightVariant(String name, RegistryObject<? extends Item> item, boolean parallelsCord, float spacing, float width, float height, UnaryOperator<GenericRecipeBuilder> recipe) {
		this(name, item, parallelsCord, spacing, width, height, recipe, 0.05F, 40, false);
	}

	private LightVariant(String name, RegistryObject<? extends Item> item, boolean parallelsCord, float spacing, float width, float height, UnaryOperator<GenericRecipeBuilder> recipe, float twinkleChance, int tickCycle) {
		this(name, item, parallelsCord, spacing, width, height, recipe, twinkleChance, tickCycle, true);
	}

	private LightVariant(String name, RegistryObject<? extends Item> item, boolean parallelsCord, float spacing, float width, float height, UnaryOperator<GenericRecipeBuilder> recipe, float twinkleChance, int tickCycle, boolean alwaysTwinkle) {
		this.name = name;
		this.item = item;
		this.parallelsCord = parallelsCord;
		this.spacing = spacing;
		this.width = width / 16;
		this.height = height / 16;
		this.recipe = recipe;
		this.twinkleChance = twinkleChance;
		this.tickCycle = tickCycle;
		this.alwaysTwinkle = alwaysTwinkle;
	}

	public String getName() {
		return name;
	}

	public Item getItem() {
		return item.orElseThrow(IllegalStateException::new);
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

	public GenericRecipe getRecipe(ResourceLocation name, IRecipeSerializer<GenericRecipe> serializer) {
		return recipe.apply(new GenericRecipeBuilder(name, serializer))
			.withOutput(getItem(), 4)
			.build();
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

	public static LightVariant getLightVariant(int index) {
		return Utils.getEnumValue(LightVariant.class, index);
	}
}
