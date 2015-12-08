package com.pau101.fairylights.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.pau101.fairylights.FairyLights;

public enum LightVariant {
	FAIRY("fairy", new Object[] { " I ", "IDI", " G ", 'I', "ingotIron", 'D', "dye", 'G', "paneGlassColorless" }),
	PAPER("paper", new Object[] { " I ", "PDP", "PPP", 'I', "ingotIron", 'D', "dye", 'P', Items.paper }),
	ORB("orb", new Object[] { " I ", "SDS", " W ", 'I', "ingotIron", 'D', "dye", 'S', Items.string, 'W', Blocks.wool }),
	FLOWER("flower", new Object[] { " I ", "RDB", " Y ", 'I', "ingotIron", 'D', "dye", 'R', new ItemStack(Blocks.red_flower, 1, 0), 'Y', new ItemStack(Blocks.yellow_flower, 1, 0), 'B', new ItemStack(Blocks.red_flower, 1, 1) }),
	ORNATE("ornate", 24, new Object[] { " I ", "GDG", "IGI", 'I', "ingotIron", 'D', "dye", 'G', "nuggetGold" }),
	OIL("oil", 32, new Object[] { " I ", "SDS", "IGI", 'I', "ingotIron", 'D', "dye", 'S', "stickWood", 'G', "paneGlassColorless" }),
	LUXO_BALL("luxo_ball", new Object[] { " I ", "BBB", "YRY", 'I', "ingotIron", 'B', "dyeBlue", 'Y', "dyeYellow", 'R', "dyeRed" }),
	JACK_O_LANTERN("jack_o_lantern", new Object[] { " I ", "SDS", "GPG", 'I', "ingotIron", 'D', "dye", 'S', new ItemStack(Blocks.wooden_slab, 1, 4), 'G', Blocks.torch, 'P', Blocks.pumpkin }),
	SKULL("skull", new Object[] { " I ", "IDI", " B ", 'I', "ingotIron", 'D', "dye", 'B', Items.bone }),
	GHOST("ghost", new Object[] { " I ", "PDP", "IGI", 'I', "ingotIron", 'D', "dye", 'P', Items.paper, 'G', "paneGlassWhite" }),
	SPIDER("spider", new Object[] { " I ", "WDW", "SES", 'I', "ingotIron", 'D', "dye", 'W', Blocks.web, 'S', Items.string, 'E', Items.spider_eye }),
	WITCH("witch", new Object[] { " I ", "BDW", " S ", 'I', "ingotIron", 'D', "dye", 'B', Items.glass_bottle, 'W', Items.wheat, 'S', "stickWood" }),
	WEEDWOOD_LANTERN("weedwood_lantern", 32, new Object[] { " I ", "SDS", "IGI", 'I', "ingotIron", 'D', "dye", 'S', new ItemStack(Blocks.wooden_slab, 1, 5), 'G', "stickWood" });

	static {
		LightVariant[] variants = values();
		for (int meta = 0; meta < variants.length; meta++) {
			variants[meta].craftingResult = new ItemStack(FairyLights.light, 4, meta);
		}
	}

	private String name;

	private float spacing;

	private ItemStack craftingResult;

	private Object[] craftingRecipe;

	private LightVariant(String name, Object[] recipeItems) {
		this(name, 16, recipeItems);
	}

	private LightVariant(String name, float spacing, Object[] recipeItems) {
		this.name = name;
		this.spacing = spacing;
		craftingRecipe = recipeItems;
	}

	public String getName() {
		return name;
	}

	public float getSpacing() {
		return spacing;
	}

	public ItemStack getCraftingResult() {
		return craftingResult;
	}

	public Object[] getCraftingRecipe() {
		return craftingRecipe;
	}

	public static LightVariant getLightVariant(int index) {
		LightVariant[] variants = values();
		return variants[index < 0 || index >= variants.length ? 0 : index];
	}
}
