package com.pau101.fairylights.item.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.pau101.fairylights.FairyLights;

public class RecipeFairyLights implements IRecipe {
	private int recipeWidth;
	private int recipeHeight;
	private Object[][] recipeItems;
	private Item outputItem;

	private ItemStack[] itemContained;

	private ItemStack recipeOutput;

	private List<UsedSlot> usedSlots;

	private class UsedSlot {
		public int x, y;

		private UsedSlot(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UsedSlot other = (UsedSlot) obj;
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			return true;
		}
	}

	public RecipeFairyLights() {
		Object[] recipe = { "I-I", 'I', "ingotIron", '-', new Object[] { Items.string, "stickWood" } };
		outputItem = FairyLights.fairyLights;

		String recipeString = "";
		int slot = 0;

		if (recipe[slot] instanceof String[]) {
			String[] recipeRows = (String[]) recipe[slot++];

			for (int i = 0; i < recipeRows.length; i++) {
				String recipeRow = recipeRows[i];
				++recipeHeight;
				recipeWidth = recipeRow.length();
				recipeString = recipeString + recipeRow;
			}
		} else {
			while (recipe[slot] instanceof String) {
				String recipeRow = (String) recipe[slot++];
				++recipeHeight;
				recipeWidth = recipeRow.length();
				recipeString = recipeString + recipeRow;
			}
		}

		HashMap<Character, List<Object>> ingredientMap;

		for (ingredientMap = new HashMap(); slot < recipe.length; slot += 2) {
			Character character = (Character) recipe[slot];
			List<Object> itemStacks = new ArrayList<Object>();

			if (recipe[slot + 1] instanceof Item) {
				itemStacks.add(new ItemStack((Item) recipe[slot + 1]));
			} else if (recipe[slot + 1] instanceof Block) {
				itemStacks.add(new ItemStack((Block) recipe[slot + 1], 1, OreDictionary.WILDCARD_VALUE));
			} else if (recipe[slot + 1] instanceof ItemStack) {
				itemStacks.add(((ItemStack) recipe[slot + 1]).copy());
			} else if (recipe[slot + 1] instanceof Object[]) {
				Object[] objects = (Object[]) recipe[slot + 1];
				for (int i = 0; i < objects.length; i++) {
					Object object = objects[i];
					if (object instanceof Item) {
						itemStacks.add(new ItemStack((Item) object));
					} else if (object instanceof Block) {
						itemStacks.add(new ItemStack((Block) object, 1, OreDictionary.WILDCARD_VALUE));
					} else if (object instanceof ItemStack) {
						itemStacks.add(((ItemStack) object).copy());
					} else if (object instanceof String) {
						itemStacks.add(OreDictionary.getOres((String) object));
					}
				}
			} else if (recipe[slot + 1] instanceof String) {
				itemStacks.add(OreDictionary.getOres((String) recipe[slot + 1]));
			}

			ingredientMap.put(character, itemStacks);
		}

		recipeItems = new Object[recipeWidth * recipeHeight][];

		for (int i = 0; i < recipeWidth * recipeHeight; i++) {
			char character = recipeString.charAt(i);

			if (ingredientMap.containsKey(Character.valueOf(character))) {
				List<Object> itemStackOptions = ingredientMap.get(Character.valueOf(character));
				recipeItems[i] = new Object[itemStackOptions.size()];
				for (int n = 0; n < recipeItems[i].length; n++) {
					recipeItems[i][n] = itemStackOptions.get(n);
				}
			} else {
				recipeItems[i] = null;
			}
		}
	}

	private boolean checkMatch(InventoryCrafting inventoryCrafting, int offsetX, int offsetY, boolean mirror) {
		itemContained = new ItemStack[recipeWidth * recipeHeight];
		usedSlots = new ArrayList<UsedSlot>();
		for (int x = 0; x < recipeWidth; x++) {
			for (int y = 0; y < recipeHeight; y++) {
				Object[] itemStackinInv;
				int index;
				if (mirror) {
					itemStackinInv = recipeItems[index = recipeWidth - x - 1 + y * recipeWidth];
				} else {
					itemStackinInv = recipeItems[index = x + y * recipeWidth];
				}
				ItemStack itemStack = inventoryCrafting.getStackInRowAndColumn(x + offsetX, y + offsetY);

				if (itemStack == null || !sharesItem(index, itemStack, itemStackinInv)) {
					return false;
				}
				usedSlots.add(new UsedSlot(x + offsetX, y + offsetY));
			}
		}
		return true;
	}

	private boolean sharesItem(int index, ItemStack itemStack, Object... options) {
		Item item = itemStack.getItem();
		int damage = itemStack.getItemDamage();
		for (int i = 0; i < options.length; i++) {
			Object option = options[i];
			if (option instanceof ItemStack) {
				ItemStack optionItem = (ItemStack) option;
				if (OreDictionary.itemMatches(optionItem, itemStack, false)) {
					itemContained[index] = optionItem;
					return true;
				}
			} else if (option instanceof List) {
				Iterator<ItemStack> iter = ((List<ItemStack>) option).iterator();
				while (iter.hasNext()) {
					ItemStack optionItem = iter.next();
					if (OreDictionary.itemMatches(optionItem, itemStack, false)) {
						itemContained[index] = optionItem;
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean containsRecipe(InventoryCrafting inventoryCrafting) {
		for (int x = 0; x <= 3 - recipeWidth; x++) {
			for (int y = 0; y <= 3 - recipeHeight; y++) {
				if (checkMatch(inventoryCrafting, x, y, true) || checkMatch(inventoryCrafting, x, y, false)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		return recipeOutput.copy();
	}

	private void getDetails(InventoryCrafting inventoryCrafting, World world, boolean rewriteLights) {
		recipeOutput = new ItemStack(outputItem, rewriteLights ? 1 : 2);
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagList patternList = new NBTTagList();
		boolean twinkle = false;
		boolean tight = itemContained[1] != null && itemContained[1].getItem() == Items.stick;
		boolean noExtraFairyLights = true;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (!usedSlots.contains(new UsedSlot(x, y))) {
					ItemStack itemStack = inventoryCrafting.getStackInRowAndColumn(x, y);
					if (itemStack != null) {
						Item item = itemStack.getItem();
						if (item == FairyLights.light && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("color", 3)) {
							NBTTagCompound patternCompound = new NBTTagCompound();
							patternCompound.setInteger("light", itemStack.getMetadata());
							patternCompound.setByte("color", (byte) itemStack.getTagCompound().getInteger("color"));
							patternList.appendTag(patternCompound);
						} else if (item == Items.glowstone_dust) {
							if (twinkle) {
								recipeOutput = null;
								return;
							} else {
								twinkle = true;
							}
						} else if (item == outputItem && noExtraFairyLights) {
							if (itemStack.hasTagCompound()) {
								NBTTagCompound existingCompound = itemStack.getTagCompound();
								if (existingCompound.hasKey("tight", 1)) {
									tight = existingCompound.getBoolean("tight");
								}
							}
							noExtraFairyLights = false;
						} else {
							recipeOutput = null;
							return;
						}
					}
				}
			}
		}
		if (patternList.tagCount() == 0) {
			recipeOutput = null;
			return;
		}
		tagCompound.setTag("pattern", patternList);
		tagCompound.setBoolean("twinkle", twinkle);
		tagCompound.setBoolean("tight", tight);
		recipeOutput.setTagCompound(tagCompound);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	private boolean inventoryContains(IInventory inventory, Item item) {
		for (int slot = 0, size = inventory.getSizeInventory(); slot < size; slot++) {
			ItemStack itemStack = inventory.getStackInSlot(slot);
			if (itemStack != null && itemStack.getItem() == item) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		recipeOutput = null;
		if (containsRecipe(inventoryCrafting)) {
			getDetails(inventoryCrafting, world, false);
		} else if (inventoryContains(inventoryCrafting, outputItem)) {
			getDetails(inventoryCrafting, world, true);
		}
		return recipeOutput != null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inventoryCrafting) {
		ItemStack[] itemStack = new ItemStack[inventoryCrafting.getSizeInventory()];

		for (int i = 0; i < itemStack.length; ++i) {
			ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
			itemStack[i] = ForgeHooks.getContainerItem(itemstack);
		}

		return itemStack;
	}
}
