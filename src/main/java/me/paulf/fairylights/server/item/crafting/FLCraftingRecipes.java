package me.paulf.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.LightItem;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.GenericRecipeBuilder;
import me.paulf.fairylights.util.crafting.ingredient.BasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.BasicRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertBasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertListAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@EventBusSubscriber(modid = FairyLights.ID)
public final class FLCraftingRecipes {
    private FLCraftingRecipes() {}

    public static final DeferredRegister<IRecipeSerializer<?>> REG = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, FairyLights.ID);

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS = REG.register("crafting_special_fairy_lights", makeSerializer(FLCraftingRecipes::createFairyLights));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS_AUGMENTATION = REG.register("crafting_special_fairy_lights_augmentation", makeSerializer(FLCraftingRecipes::createFairyLightsAugmentation));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> TINSEL_GARLAND = REG.register("crafting_special_tinsel_garland", makeSerializer(FLCraftingRecipes::createTinselGarland));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING = REG.register("crafting_special_pennant_bunting", makeSerializer(FLCraftingRecipes::createPennantBunting));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING_AUGMENTATION = REG.register("crafting_special_pennant_bunting_augmentation", makeSerializer(FLCraftingRecipes::createPennantBuntingAugmentation));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> TRIANGLE_PENNANT = REG.register("crafting_special_triangle_pennant", makeSerializer(FLCraftingRecipes::createTrianglePennant));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SPEARHEAD_PENNANT = REG.register("crafting_special_spearhead_pennant", makeSerializer(FLCraftingRecipes::createSpearheadPennant));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SWALLOWTAIL_PENNANT = REG.register("crafting_special_swallowtail_pennant", makeSerializer(FLCraftingRecipes::createSwallowtailPennant));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SQUARE_PENNANT = REG.register("crafting_special_square_pennant", makeSerializer(FLCraftingRecipes::createSquarePennant));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHT = REG.register("crafting_special_fairy_light", makeSerializer(FLCraftingRecipes::createFairyLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> PAPER_LANTERN = REG.register("crafting_special_paper_lantern", makeSerializer(FLCraftingRecipes::createPaperLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> ORB_LANTERN = REG.register("crafting_special_orb_lantern", makeSerializer(FLCraftingRecipes::createOrbLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> FLOWER_LIGHT = REG.register("crafting_special_flower_light", makeSerializer(FLCraftingRecipes::createFlowerLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> ORNATE_LANTERN = REG.register("crafting_special_ornate_lantern", makeSerializer(FLCraftingRecipes::createOrnateLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> OIL_LANTERN = REG.register("crafting_special_oil_lantern", makeSerializer(FLCraftingRecipes::createOilLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> JACK_O_LANTERN = REG.register("crafting_special_jack_o_lantern", makeSerializer(FLCraftingRecipes::createJackOLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SKULL_LIGHT = REG.register("crafting_special_skull_light", makeSerializer(FLCraftingRecipes::createSkullLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> GHOST_LIGHT = REG.register("crafting_special_ghost_light", makeSerializer(FLCraftingRecipes::createGhostLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SPIDER_LIGHT = REG.register("crafting_special_spider_light", makeSerializer(FLCraftingRecipes::createSpiderLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> WITCH_LIGHT = REG.register("crafting_special_witch_light", makeSerializer(FLCraftingRecipes::createWitchLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SNOWFLAKE_LIGHT = REG.register("crafting_special_snowflake_light", makeSerializer(FLCraftingRecipes::createSnowflakeLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> ICICLE_LIGHTS = REG.register("crafting_special_icicle_lights", makeSerializer(FLCraftingRecipes::createIcicleLights));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> METEOR_LIGHT = REG.register("crafting_special_meteor_light", makeSerializer(FLCraftingRecipes::createMeteorLight));

    private static Ingredient dyeIngredient() {
        return Ingredient.fromTag(Tags.Items.DYES);
    }

    public static final RegularIngredient DYE_SUBTYPE_INGREDIENT = new BasicRegularIngredient(dyeIngredient()) {
        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            return ImmutableList.of(OreDictUtils.getDyes(LightItem.getLightColor(output)));
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public void matched(final ItemStack ingredient, final ItemStack output) {
            LightItem.setLightColor(output, OreDictUtils.getDyeMetadata(ingredient));
        }
    };

    private static Supplier<IRecipeSerializer<GenericRecipe>> makeSerializer(final Function<ResourceLocation, GenericRecipe> factory) {
        return () -> new SpecialRecipeSerializer<>(factory);
    }

    private static GenericRecipe createFairyLights(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, FAIRY_LIGHTS, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withAnyIngredient('-',
                new BasicRegularIngredient(Ingredient.fromTag(Tags.Items.STRING)) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return useInputsForTagBool(output, "tight", false) ? super.getInput(output) : ImmutableList.of();
                    }
                },
                new BasicRegularIngredient(Ingredient.fromItems(Items.STICK)) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return useInputsForTagBool(output, "tight", true) ? super.getInput(output) : ImmutableList.of();
                    }

                    @Override
                    public void present(final ItemStack output) {
                        output.getOrCreateTag().putBoolean("tight", true);
                    }

                    @Override
                    public void absent(final ItemStack output) {
                        output.getOrCreateTag().putBoolean("tight", false);
                    }
                }
            )
            .withAuxiliaryIngredient(new LightIngredient(true))
            .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE), false, 1) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return useInputsForTagBool(output, "twinkle", true) ? super.getInput(output) : ImmutableList.of();
                }

                @Override
                public void present(final ItemStack output) {
                    output.getOrCreateTag().putBoolean("twinkle", true);
                }

                @Override
                public void absent(final ItemStack output) {
                    output.getOrCreateTag().putBoolean("twinkle", false);
                }

                @Override
                public void addTooltip(final List<String> tooltip) {
                    super.addTooltip(tooltip);
                    tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.glowstone"));
                }
            })
            .build();
    }

    private static boolean useInputsForTagBool(final ItemStack output, final String key, final boolean value) {
        final CompoundNBT compound = output.getTag();
        return compound != null && compound.getBoolean(key) == value;
    }

    /*
     *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
     *  different recipe layouts the the input ingredients can be generated for so I could show applying a
     *  new light pattern as well.
     */
    private static GenericRecipe createFairyLightsAugmentation(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, FAIRY_LIGHTS_AUGMENTATION, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
            .withShape("F")
            .withIngredient('F', new BasicRegularIngredient(Ingredient.fromItems(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return Arrays.stream(this.ingredient.getMatchingStacks())
                        .map(ItemStack::copy)
                        .flatMap(stack -> {
                            stack.setTag(new CompoundNBT());
                            return makeHangingLightsExamples(stack).stream();
                        }).collect(ImmutableList.toImmutableList());
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    final ItemStack stack = output.copy();
                    final CompoundNBT compound = stack.getTag();
                    if (compound == null) {
                        return ImmutableList.of();
                    }
                    stack.setCount(1);
                    compound.putBoolean("twinkle", false);
                    return ImmutableList.of(ImmutableList.of(stack));
                }

                @Override
                public void matched(final ItemStack ingredient, final ItemStack output) {
                    final CompoundNBT compound = ingredient.getTag();
                    if (compound != null) {
                        output.setTag(compound.copy());
                    }
                }
            })
            .withAuxiliaryIngredient(new InertListAuxiliaryIngredient(true,
                new LightIngredient(false) {
                    @Override
                    public ImmutableList<ItemStack> getInputs() {
                        return ImmutableList.of();
                    }

                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return ImmutableList.of();
                    }
                },
                new BasicAuxiliaryIngredient<MutableInt>(Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE), false, 1) {
                    @Override
                    public MutableInt accumulator() {
                        return new MutableInt();
                    }

                    @Override
                    public void consume(final MutableInt count, final ItemStack ingredient) {
                        count.increment();
                    }

                    @Override
                    public boolean finish(final MutableInt count, final ItemStack output) {
                        if (count.intValue() > 0) {
                            if (output.getOrCreateTag().getBoolean("twinkle")) {
                                return true;
                            }
                            output.getOrCreateTag().putBoolean("twinkle", true);
                        }
                        return false;
                    }
                }
            ))
            .build();
    }

    private static ImmutableList<ItemStack> makeHangingLightsExamples(final ItemStack stack) {
        return ImmutableList.of(
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.MAGENTA, DyeColor.CYAN, DyeColor.WHITE),
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.LIGHT_BLUE),
            makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PINK, DyeColor.CYAN, DyeColor.GREEN),
            makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PURPLE, DyeColor.LIGHT_GRAY, DyeColor.GREEN),
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.YELLOW, DyeColor.CYAN, DyeColor.PURPLE)
        );
    }

    public static ItemStack makeHangingLights(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        CompoundNBT compound = stack.getTag();
        final ListNBT lights = new ListNBT();
        for (final DyeColor color : colors) {
            final CompoundNBT light = new CompoundNBT();
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

    private static GenericRecipe createTinselGarland(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, TINSEL_GARLAND, FLItems.TINSEL.orElseThrow(IllegalStateException::new))
            .withShape(" P ", "I-I", " D ")
            .withIngredient('P', Items.PAPER)
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Tags.Items.STRING)
            .withIngredient('D', new BasicRegularIngredient(dyeIngredient()) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    final CompoundNBT compound = output.getTag();
                    if (compound == null) {
                        return ImmutableList.of();
                    }
                    return ImmutableList.of(OreDictUtils.getDyes(LightItem.getLightColor(output)));
                }

                @Override
                public boolean dictatesOutputType() {
                    return true;
                }

                @Override
                public void matched(final ItemStack ingredient, final ItemStack output) {
                    LightItem.setLightColor(output, OreDictUtils.getDyeMetadata(ingredient));
                }
            })
            .build();
    }

    private static GenericRecipe createPennantBunting(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, PENNANT_BUNTING, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Tags.Items.STRING)
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static GenericRecipe createPennantBuntingAugmentation(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, PENNANT_BUNTING_AUGMENTATION, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
            .withShape("B")
            .withIngredient('B', new BasicRegularIngredient(Ingredient.fromItems(FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return Arrays.stream(this.ingredient.getMatchingStacks())
                        .map(ItemStack::copy)
                        .flatMap(stack -> {
                            stack.setTag(new CompoundNBT());
                            return makePennantExamples(stack).stream();
                        }).collect(ImmutableList.toImmutableList());
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    final CompoundNBT compound = output.getTag();
                    if (compound == null) {
                        return ImmutableList.of();
                    }
                    return ImmutableList.of(makePennantExamples(output));
                }

                @Override
                public void matched(final ItemStack ingredient, final ItemStack output) {
                    final CompoundNBT compound = ingredient.getTag();
                    if (compound != null) {
                        output.setTag(compound.copy());
                    }
                }
            })
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static ImmutableList<ItemStack> makePennantExamples(final ItemStack stack) {
        return ImmutableList.of(
            makePennant(stack, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.RED),
            makePennant(stack, DyeColor.PINK, DyeColor.LIGHT_BLUE),
            makePennant(stack, DyeColor.ORANGE, DyeColor.WHITE),
            makePennant(stack, DyeColor.LIME, DyeColor.YELLOW)
        );
    }

    public static ItemStack makePennant(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        CompoundNBT compound = stack.getTag();
        final ListNBT pennants = new ListNBT();
        for (final DyeColor color : colors) {
            final CompoundNBT pennant = new CompoundNBT();
            pennant.putByte("color", (byte) color.getId());
            pennant.putString("item", FLItems.TRIANGLE_PENNANT.get().getRegistryName().toString());
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

    private static GenericRecipe createPennant(final ResourceLocation name, final Supplier<IRecipeSerializer<GenericRecipe>> serializer, final Item item, final String pattern) {
        return new GenericRecipeBuilder(name, serializer, item)
            .withShape("- -", "PDP", pattern)
            .withIngredient('P', Items.PAPER)
            .withIngredient('-', Tags.Items.STRING)
            .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
            .build();
    }

    private static GenericRecipe createTrianglePennant(final ResourceLocation name) {
        return createPennant(name, TRIANGLE_PENNANT, FLItems.TRIANGLE_PENNANT.orElseThrow(IllegalStateException::new), " P ");
    }

    private static GenericRecipe createSpearheadPennant(final ResourceLocation name) {
        return createPennant(name, SPEARHEAD_PENNANT, FLItems.SPEARHEAD_PENNANT.orElseThrow(IllegalStateException::new), " PP");
    }

    private static GenericRecipe createSwallowtailPennant(final ResourceLocation name) {
        return createPennant(name, SWALLOWTAIL_PENNANT, FLItems.SWALLOWTAIL_PENNANT.orElseThrow(IllegalStateException::new), "P P");
    }

    private static GenericRecipe createSquarePennant(final ResourceLocation name) {
        return createPennant(name, SQUARE_PENNANT, FLItems.SQUARE_PENNANT.orElseThrow(IllegalStateException::new), "PPP");
    }

    private static GenericRecipe createFairyLight(final ResourceLocation name) {
        return createLight(name, FAIRY_LIGHT, LightVariant.FAIRY, b -> b
            .withShape(" I ", "IDI", " G ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createPaperLantern(final ResourceLocation name) {
        return createLight(name, PAPER_LANTERN, LightVariant.PAPER, b -> b
            .withShape(" I ", "PDP", "PPP")
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createOrbLantern(final ResourceLocation name) {
        return createLight(name, ORB_LANTERN, LightVariant.ORB, b -> b
            .withShape(" I ", "SDS", " W ")
            .withIngredient('S', Tags.Items.STRING)
            .withIngredient('W', Items.WHITE_WOOL)
        );
    }

    private static GenericRecipe createFlowerLight(final ResourceLocation name) {
        return createLight(name, FLOWER_LIGHT, LightVariant.FLOWER, b -> b
            .withShape(" I ", "RDB", " Y ")
            .withIngredient('R', Items.POPPY)
            .withIngredient('Y', Items.DANDELION)
            .withIngredient('B', Items.BLUE_ORCHID)
        );
    }

    private static GenericRecipe createOrnateLantern(final ResourceLocation name) {
        return createLight(name, ORNATE_LANTERN, LightVariant.ORNATE, b -> b
            .withShape(" I ", "GDG", "IGI")
            .withIngredient('G', Tags.Items.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createOilLantern(final ResourceLocation name) {
        return createLight(name, OIL_LANTERN, LightVariant.OIL, b -> b
            .withShape(" I ", "SDS", "IGI")
            .withIngredient('S', Items.STICK)
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createJackOLantern(final ResourceLocation name) {
        return createLight(name, JACK_O_LANTERN, LightVariant.JACK_O_LANTERN, b -> b
            .withShape(" I ", "SDS", "GPG")
            .withIngredient('S', ItemTags.WOODEN_SLABS)
            .withIngredient('G', Items.TORCH)
            .withIngredient('P', Items.JACK_O_LANTERN)
        );
    }

    private static GenericRecipe createSkullLight(final ResourceLocation name) {
        return createLight(name, SKULL_LIGHT, LightVariant.SKULL, b -> b
            .withShape(" I ", "IDI", " B ")
            .withIngredient('B', Tags.Items.BONES)
        );
    }

    private static GenericRecipe createGhostLight(final ResourceLocation name) {
        return createLight(name, GHOST_LIGHT, LightVariant.GHOST, b -> b
            .withShape(" I ", "PDP", "IGI")
            .withIngredient('P', Items.PAPER)
            .withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
        );
    }

    private static GenericRecipe createSpiderLight(final ResourceLocation name) {
        return createLight(name, SPIDER_LIGHT, LightVariant.SPIDER, b -> b
            .withShape(" I ", "WDW", "SES")
            .withIngredient('W', Items.COBWEB)
            .withIngredient('S', Tags.Items.STRING)
            .withIngredient('E', Items.SPIDER_EYE)
        );
    }

    private static GenericRecipe createWitchLight(final ResourceLocation name) {
        return createLight(name, WITCH_LIGHT, LightVariant.WITCH, b -> b
            .withShape(" I ", "BDW", " S ")
            .withIngredient('B', Items.GLASS_BOTTLE)
            .withIngredient('W', Items.WHEAT)
            .withIngredient('S', Items.STICK)
        );
    }

    private static GenericRecipe createSnowflakeLight(final ResourceLocation name) {
        return createLight(name, SNOWFLAKE_LIGHT, LightVariant.SNOWFLAKE, b -> b
            .withShape(" I ", "SDS", " G ")
            .withIngredient('S', Items.SNOWBALL)
            .withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
        );
    }

    private static GenericRecipe createIcicleLights(final ResourceLocation name) {
        return createLight(name, ICICLE_LIGHTS, LightVariant.ICICLE, b -> b
            .withShape(" I ", "GDG", " B ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
            .withIngredient('B', Items.WATER_BUCKET)
        );
    }

    private static GenericRecipe createMeteorLight(final ResourceLocation name) {
        return createLight(name, METEOR_LIGHT, LightVariant.METEOR, b -> b
            .withShape(" I ", "GDG", "IPI")
            .withIngredient('G', Tags.Items.DUSTS_GLOWSTONE)
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createLight(final ResourceLocation name, final Supplier<? extends IRecipeSerializer<GenericRecipe>> serializer, final LightVariant variant, final UnaryOperator<GenericRecipeBuilder> recipe) {
        return recipe.apply(new GenericRecipeBuilder(name, serializer))
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('D', FLCraftingRecipes.DYE_SUBTYPE_INGREDIENT)
            .withOutput(variant.getItem(), 4)
            .build();
    }

    private static class LightIngredient extends BasicAuxiliaryIngredient<ListNBT> {
        private LightIngredient(final boolean isRequired) {
            super(Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "lights"))), isRequired, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final CompoundNBT compound = output.getTag();
            if (compound == null) {
                return ImmutableList.of();
            }
            final ListNBT pattern = compound.getList("pattern", Constants.NBT.TAG_COMPOUND);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                final CompoundNBT light = pattern.getCompound(i);
                final ItemStack stack = new ItemStack(LightVariant.getLightVariant(light.getInt("light")).getItem());
                LightItem.setLightColor(stack, DyeColor.byId(light.getByte("color")));
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
        public void consume(final ListNBT patternList, final ItemStack ingredient) {
            final CompoundNBT light = new CompoundNBT();
            light.putInt("light", Arrays.stream(LightVariant.values())
                .filter(v -> ingredient.getItem().equals(v.getItem()))
                .mapToInt(Enum::ordinal)
                .findFirst().orElse(0)
            );
            light.putByte("color", (byte) LightItem.getLightColor(ingredient).getId());
            patternList.add(light);
        }

        @Override
        public boolean finish(final ListNBT pattern, final ItemStack output) {
            if (pattern.size() > 0) {
                output.setTagInfo("pattern", pattern);
            }
            return false;
        }

        @Override
        public void addTooltip(final List<String> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.light"));
        }
    }

    private static class PennantIngredient extends BasicAuxiliaryIngredient<ListNBT> {
        private PennantIngredient() {
            super(Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "pennants"))), true, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final CompoundNBT compound = output.getTag();
            if (compound == null) {
                return ImmutableList.of();
            }
            final ListNBT pattern = compound.getList("pattern", Constants.NBT.TAG_COMPOUND);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                final CompoundNBT pennant = pattern.getCompound(i);
                final ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(pennant.getString("item"))));
                LightItem.setLightColor(stack, DyeColor.byId(pennant.getByte("color")));
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
        public void consume(final ListNBT patternList, final ItemStack ingredient) {
            final CompoundNBT pennant = new CompoundNBT();
            pennant.putByte("color", (byte) LightItem.getLightColor(ingredient).getId());
            pennant.putString("item", ingredient.getItem().getRegistryName().toString());
            patternList.add(pennant);
        }

        @Override
        public boolean finish(final ListNBT pattern, final ItemStack output) {
            if (pattern.size() > 0) {
                output.setTagInfo("pattern", pattern);
                output.setTagInfo("text", StyledString.serialize(new StyledString()));
            }
            return false;
        }

        @Override
        public void addTooltip(final List<String> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.pennantBunting.pennant"));
        }
    }
}
