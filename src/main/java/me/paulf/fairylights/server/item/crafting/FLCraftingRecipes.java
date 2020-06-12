package me.paulf.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.ColorLightItem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.Blender;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.GenericRecipeBuilder;
import me.paulf.fairylights.util.crafting.ingredient.BasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.BasicRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertBasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@EventBusSubscriber(modid = FairyLights.ID)
public final class FLCraftingRecipes {
    private FLCraftingRecipes() {}

    public static final DeferredRegister<IRecipeSerializer<?>> REG = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, FairyLights.ID);

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> HANGING_LIGHTS = REG.register("crafting_special_hanging_lights", makeSerializer(FLCraftingRecipes::createHangingLights));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> HANGING_LIGHTS_AUGMENTATION = REG.register("crafting_special_hanging_lights_augmentation", makeSerializer(FLCraftingRecipes::createHangingLightsAugmentation));

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

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> CANDLE_LANTERN_LIGHT = REG.register("crafting_special_candle_lantern_light", makeSerializer(FLCraftingRecipes::createCandleLanternLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> OIL_LANTERN_LIGHT = REG.register("crafting_special_oil_lantern_light", makeSerializer(FLCraftingRecipes::createOilLanternLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> JACK_O_LANTERN = REG.register("crafting_special_jack_o_lantern", makeSerializer(FLCraftingRecipes::createJackOLantern));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SKULL_LIGHT = REG.register("crafting_special_skull_light", makeSerializer(FLCraftingRecipes::createSkullLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> GHOST_LIGHT = REG.register("crafting_special_ghost_light", makeSerializer(FLCraftingRecipes::createGhostLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SPIDER_LIGHT = REG.register("crafting_special_spider_light", makeSerializer(FLCraftingRecipes::createSpiderLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> WITCH_LIGHT = REG.register("crafting_special_witch_light", makeSerializer(FLCraftingRecipes::createWitchLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> SNOWFLAKE_LIGHT = REG.register("crafting_special_snowflake_light", makeSerializer(FLCraftingRecipes::createSnowflakeLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> ICICLE_LIGHTS = REG.register("crafting_special_icicle_lights", makeSerializer(FLCraftingRecipes::createIcicleLights));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> METEOR_LIGHT = REG.register("crafting_special_meteor_light", makeSerializer(FLCraftingRecipes::createMeteorLight));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> LIGHT_TWINKLE = REG.register("crafting_special_light_twinkle", makeSerializer(FLCraftingRecipes::createLightTwinkle));

    public static final RegistryObject<IRecipeSerializer<GenericRecipe>> EDIT_COLOR = REG.register("crafting_special_edit_color", makeSerializer(FLCraftingRecipes::createDyeColor));

    public static final RegistryObject<IRecipeSerializer<CopyColorRecipe>> COPY_COLOR = REG.register("crafting_special_copy_color", makeSerializer(CopyColorRecipe::new));

    public static final Tag<Item> LIGHTS = new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "lights"));

    public static final Tag<Item> TWINKLING_LIGHTS = new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "twinkling_lights"));

    public static final Tag<Item> PENNANTS = new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "pennants"));

    public static final Tag<Item> DYEABLE = new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "dyeable"));

    public static final RegularIngredient DYE_SUBTYPE_INGREDIENT = new BasicRegularIngredient(Ingredient.fromTag(Tags.Items.DYES)) {
        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            return ColorLightItem.getDyeColor(output).map(dye -> ImmutableList.of(OreDictUtils.getDyes(dye))).orElse(ImmutableList.of());
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public void matched(final ItemStack ingredient, final CompoundNBT nbt) {
            ColorLightItem.setColor(nbt, OreDictUtils.getDyeColor(ingredient));
        }
    };

    private static <T extends ICraftingRecipe> Supplier<IRecipeSerializer<T>> makeSerializer(final Function<ResourceLocation, T> factory) {
        return () -> new SpecialRecipeSerializer<>(factory);
    }

    private static GenericRecipe createDyeColor(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, EDIT_COLOR)
            .withShape("I")
            .withIngredient('I', DYEABLE).withOutput('I')
            .withAuxiliaryIngredient(new BasicAuxiliaryIngredient<Blender>(Ingredient.fromTag(Tags.Items.DYES), true, 8) {
                @Override
                public Blender accumulator() {
                    return new Blender();
                }

                @Override
                public void consume(final Blender data, final ItemStack ingredient) {
                    data.add(ColorLightItem.getColor(OreDictUtils.getDyeColor(ingredient)));
                }

                @Override
                public boolean finish(final Blender data, final CompoundNBT nbt) {
                    ColorLightItem.setColor(nbt, data.blend());
                    return false;
                }
            })
            .build();
    }

    private static GenericRecipe createLightTwinkle(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, LIGHT_TWINKLE)
            .withShape("L")
            .withIngredient('L', TWINKLING_LIGHTS).withOutput('L')
            .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE), true, 1) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return useInputsForTagBool(output, "twinkle", true) ? super.getInput(output) : ImmutableList.of();
                }

                @Override
                public void present(final CompoundNBT nbt) {
                    nbt.putBoolean("twinkle", true);
                }

                @Override
                public void absent(final CompoundNBT nbt) {
                    nbt.putBoolean("twinkle", false);
                }

                @Override
                public void addTooltip(final List<String> tooltip) {
                    super.addTooltip(tooltip);
                    tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.twinkling_lights.glowstone"));
                }
            })
            .build();
    }

    private static GenericRecipe createHangingLights(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, HANGING_LIGHTS, FLItems.HANGING_LIGHTS.get())
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Tags.Items.STRING)
            .withAuxiliaryIngredient(new LightIngredient(true))
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
    private static GenericRecipe createHangingLightsAugmentation(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, HANGING_LIGHTS_AUGMENTATION, FLItems.HANGING_LIGHTS.get())
            .withShape("F")
            .withIngredient('F', new BasicRegularIngredient(Ingredient.fromItems(FLItems.HANGING_LIGHTS.get())) {
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
                    return ImmutableList.of(ImmutableList.of(stack));
                }

                @Override
                public void matched(final ItemStack ingredient, final CompoundNBT nbt) {
                    final CompoundNBT compound = ingredient.getTag();
                    if (compound != null) {
                        nbt.merge(compound);
                    }
                }
            })
            .withAuxiliaryIngredient(new LightIngredient(true) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return ImmutableList.of();
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return ImmutableList.of();
                }
            })
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
            lights.add(ColorLightItem.setColor(new ItemStack(FLItems.FAIRY_LIGHT.get()), color).write(new CompoundNBT()));
        }
        if (compound == null) {
            compound = new CompoundNBT();
            stack.setTag(compound);
        }
        compound.put("pattern", lights);
        return stack;
    }

    private static GenericRecipe createTinselGarland(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, TINSEL_GARLAND, FLItems.TINSEL.get())
            .withShape(" P ", "I-I", " D ")
            .withIngredient('P', Items.PAPER)
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Tags.Items.STRING)
            .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
            .build();
    }

    private static GenericRecipe createPennantBunting(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, PENNANT_BUNTING, FLItems.PENNANT_BUNTING.get())
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Tags.Items.STRING)
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static GenericRecipe createPennantBuntingAugmentation(final ResourceLocation name) {
        return new GenericRecipeBuilder(name, PENNANT_BUNTING_AUGMENTATION, FLItems.PENNANT_BUNTING.get())
            .withShape("B")
            .withIngredient('B', new BasicRegularIngredient(Ingredient.fromItems(FLItems.PENNANT_BUNTING.get())) {
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
                public void matched(final ItemStack ingredient, final CompoundNBT nbt) {
                    final CompoundNBT compound = ingredient.getTag();
                    if (compound != null) {
                        nbt.merge(compound);
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
            ColorLightItem.setColor(pennant, color);
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
        return createPennant(name, TRIANGLE_PENNANT, FLItems.TRIANGLE_PENNANT.get(), " P ");
    }

    private static GenericRecipe createSpearheadPennant(final ResourceLocation name) {
        return createPennant(name, SPEARHEAD_PENNANT, FLItems.SPEARHEAD_PENNANT.get(), " PP");
    }

    private static GenericRecipe createSwallowtailPennant(final ResourceLocation name) {
        return createPennant(name, SWALLOWTAIL_PENNANT, FLItems.SWALLOWTAIL_PENNANT.get(), "P P");
    }

    private static GenericRecipe createSquarePennant(final ResourceLocation name) {
        return createPennant(name, SQUARE_PENNANT, FLItems.SQUARE_PENNANT.get(), "PPP");
    }

    private static GenericRecipe createFairyLight(final ResourceLocation name) {
        return createLight(name, FAIRY_LIGHT, FLItems.FAIRY_LIGHT, b -> b
            .withShape(" I ", "IDI", " G ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createPaperLantern(final ResourceLocation name) {
        return createLight(name, PAPER_LANTERN, FLItems.PAPER_LANTERN, b -> b
            .withShape(" I ", "PDP", "PPP")
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createOrbLantern(final ResourceLocation name) {
        return createLight(name, ORB_LANTERN, FLItems.ORB_LANTERN, b -> b
            .withShape(" I ", "SDS", " W ")
            .withIngredient('S', Tags.Items.STRING)
            .withIngredient('W', Items.WHITE_WOOL)
        );
    }

    private static GenericRecipe createFlowerLight(final ResourceLocation name) {
        return createLight(name, FLOWER_LIGHT, FLItems.FLOWER_LIGHT, b -> b
            .withShape(" I ", "RDB", " Y ")
            .withIngredient('R', Items.POPPY)
            .withIngredient('Y', Items.DANDELION)
            .withIngredient('B', Items.BLUE_ORCHID)
        );
    }

    private static GenericRecipe createCandleLanternLight(final ResourceLocation name) {
        return createLight(name, CANDLE_LANTERN_LIGHT, FLItems.CANDLE_LANTERN_LIGHT, b -> b
            .withShape(" I ", "GDG", "IGI")
            .withIngredient('G', Tags.Items.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createOilLanternLight(final ResourceLocation name) {
        return createLight(name, OIL_LANTERN_LIGHT, FLItems.OIL_LANTERN_LIGHT, b -> b
            .withShape(" I ", "SDS", "IGI")
            .withIngredient('S', Items.STICK)
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createJackOLantern(final ResourceLocation name) {
        return createLight(name, JACK_O_LANTERN, FLItems.JACK_O_LANTERN, b -> b
            .withShape(" I ", "SDS", "GPG")
            .withIngredient('S', ItemTags.WOODEN_SLABS)
            .withIngredient('G', Items.TORCH)
            .withIngredient('P', Items.JACK_O_LANTERN)
        );
    }

    private static GenericRecipe createSkullLight(final ResourceLocation name) {
        return createLight(name, SKULL_LIGHT, FLItems.SKULL_LIGHT, b -> b
            .withShape(" I ", "IDI", " B ")
            .withIngredient('B', Tags.Items.BONES)
        );
    }

    private static GenericRecipe createGhostLight(final ResourceLocation name) {
        return createLight(name, GHOST_LIGHT, FLItems.GHOST_LIGHT, b -> b
            .withShape(" I ", "PDP", "IGI")
            .withIngredient('P', Items.PAPER)
            .withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
        );
    }

    private static GenericRecipe createSpiderLight(final ResourceLocation name) {
        return createLight(name, SPIDER_LIGHT, FLItems.SPIDER_LIGHT, b -> b
            .withShape(" I ", "WDW", "SES")
            .withIngredient('W', Items.COBWEB)
            .withIngredient('S', Tags.Items.STRING)
            .withIngredient('E', Items.SPIDER_EYE)
        );
    }

    private static GenericRecipe createWitchLight(final ResourceLocation name) {
        return createLight(name, WITCH_LIGHT, FLItems.WITCH_LIGHT, b -> b
            .withShape(" I ", "BDW", " S ")
            .withIngredient('B', Items.GLASS_BOTTLE)
            .withIngredient('W', Items.WHEAT)
            .withIngredient('S', Items.STICK)
        );
    }

    private static GenericRecipe createSnowflakeLight(final ResourceLocation name) {
        return createLight(name, SNOWFLAKE_LIGHT, FLItems.SNOWFLAKE_LIGHT, b -> b
            .withShape(" I ", "SDS", " G ")
            .withIngredient('S', Items.SNOWBALL)
            .withIngredient('G', Tags.Items.GLASS_PANES_WHITE)
        );
    }

    private static GenericRecipe createIcicleLights(final ResourceLocation name) {
        return createLight(name, ICICLE_LIGHTS, FLItems.ICICLE_LIGHTS, b -> b
            .withShape(" I ", "GDG", " B ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
            .withIngredient('B', Items.WATER_BUCKET)
        );
    }

    private static GenericRecipe createMeteorLight(final ResourceLocation name) {
        return createLight(name, METEOR_LIGHT, FLItems.METEOR_LIGHT, b -> b
            .withShape(" I ", "GDG", "IPI")
            .withIngredient('G', Tags.Items.DUSTS_GLOWSTONE)
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createLight(final ResourceLocation name, final Supplier<? extends IRecipeSerializer<GenericRecipe>> serializer, final Supplier<? extends Item> variant, final UnaryOperator<GenericRecipeBuilder> recipe) {
        return recipe.apply(new GenericRecipeBuilder(name, serializer))
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('D', FLCraftingRecipes.DYE_SUBTYPE_INGREDIENT)
            .withOutput(variant.get(), 4)
            .build();
    }

    private static class LightIngredient extends BasicAuxiliaryIngredient<ListNBT> {
        private LightIngredient(final boolean isRequired) {
            super(Ingredient.fromTag(LIGHTS), isRequired, 8);
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
                lights.add(ImmutableList.of(ItemStack.read(pattern.getCompound(i))));
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
            patternList.add(ingredient.write(new CompoundNBT()));
        }

        @Override
        public boolean finish(final ListNBT pattern, final CompoundNBT nbt) {
            if (pattern.size() > 0) {
                nbt.put("pattern", pattern);
            }
            return false;
        }

        @Override
        public void addTooltip(final List<String> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.hangingLights.light"));
        }
    }

    private static class PennantIngredient extends BasicAuxiliaryIngredient<ListNBT> {
        private PennantIngredient() {
            super(Ingredient.fromTag(PENNANTS), true, 8);
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
                ColorLightItem.setColor(stack, DyeColor.byId(pennant.getByte("color")));
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
            ColorLightItem.setColor(pennant, ColorLightItem.getColor(ingredient));
            pennant.putString("item", ingredient.getItem().getRegistryName().toString());
            patternList.add(pennant);
        }

        @Override
        public boolean finish(final ListNBT pattern, final CompoundNBT nbt) {
            if (pattern.size() > 0) {
                nbt.put("pattern", pattern);
                nbt.put("text", StyledString.serialize(new StyledString()));
            }
            return false;
        }

        @Override
        public void addTooltip(final List<String> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.pennantBunting.pennant"));
        }
    }
}
