package me.paulf.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
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
import me.paulf.fairylights.util.crafting.ingredient.DyeRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertBasicAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.InertListAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.Ingredient;
import me.paulf.fairylights.util.crafting.ingredient.OreAuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import me.paulf.fairylights.util.styledstring.StyledString;
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
public final class FLCraftingRecipes {
    private FLCraftingRecipes() {}

    public static final DeferredRegister<IRecipeSerializer<?>> REG = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, FairyLights.ID);

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS = REG.register("crafting_special_fairy_lights", makeSerializer(FLCraftingRecipes::createFairyLights));

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> FAIRY_LIGHTS_AUGMENTATION = REG.register("crafting_special_fairy_lights_augmentation", makeSerializer(FLCraftingRecipes::createFairyLightsAugmentation));

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> TINSEL_GARLAND = REG.register("crafting_special_tinsel_garland", makeSerializer(FLCraftingRecipes::createTinselGarland));

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING = REG.register("crafting_special_pennant_bunting", makeSerializer(FLCraftingRecipes::createPennantBunting));

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT_BUNTING_AUGMENTATION = REG.register("crafting_special_pennant_bunting_augmentation", makeSerializer(FLCraftingRecipes::createPennantBuntingAugmentation));

    private static final RegistryObject<IRecipeSerializer<GenericRecipe>> PENNANT = REG.register("crafting_special_pennant", makeSerializer(FLCraftingRecipes::createPennant));

    static {
        for (final LightVariant variant : LightVariant.values()) {
            REG.register("crafting_special_" + variant.getName(), makeSerializer(variant::getRecipe));
        }
    }

    public static final RegularIngredient LIGHT_DYE = new DyeRegularIngredient() {
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

    private static Supplier<IRecipeSerializer<GenericRecipe>> makeSerializer(final BiFunction<ResourceLocation, IRecipeSerializer<GenericRecipe>, GenericRecipe> factory) {
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

    private static GenericRecipe createFairyLights(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withAnyIngredient('-',
                new BasicRegularIngredient(Items.STRING) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return useInputsForTagBool(this, output, "tight", false) ? super.getInput(output) : ImmutableList.of();
                    }
                },
                new BasicRegularIngredient(Items.STICK) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return useInputsForTagBool(this, output, "tight", true) ? super.getInput(output) : ImmutableList.of();
                    }

                    @Override
                    public void present(final ItemStack output) {
                        output.getTag().putBoolean("tight", true);
                    }

                    @Override
                    public void absent(final ItemStack output) {
                        output.getTag().putBoolean("tight", false);
                    }
                }
            )
            .withAuxiliaryIngredient(new LightIngredient(true))
            .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(Items.GLOWSTONE_DUST, false, 1) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return useInputsForTagBool(this, output, "twinkle", true) ? super.getInput(output) : ImmutableList.of();
                }

                @Override
                public void present(final ItemStack output) {
                    output.getTag().putBoolean("twinkle", true);
                }

                @Override
                public void absent(final ItemStack output) {
                    output.getTag().putBoolean("twinkle", false);
                }

                @Override
                public void addTooltip(final List<String> tooltip) {
                    super.addTooltip(tooltip);
                    tooltip.add(Utils.formatRecipeTooltip("recipe.hangingLights.glowstone"));
                }
            })
            .build();
    }

    private static boolean useInputsForTagBool(final Ingredient ingredient, final ItemStack output, final String key, final boolean value) {
        final CompoundNBT compound = output.getTag();
        return compound != null && compound.getBoolean(key) == value;
    }

    /*
     *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
     *  different recipe layouts the the input ingredients can be generated for so I could show applying a
     *  new light pattern as well.
     */
    private static GenericRecipe createFairyLightsAugmentation(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new))
            .withShape("F")
            .withIngredient('F', new BasicRegularIngredient(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new)) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    final ItemStack stack = this.ingredient.copy();
                    stack.setTag(new CompoundNBT());
                    return makeHangingLightsExamples(stack);
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
                new BasicAuxiliaryIngredient<MutableInt>(Items.GLOWSTONE_DUST, false, 1) {
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

    private static GenericRecipe createTinselGarland(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.TINSEL.orElseThrow(IllegalStateException::new))
            .withShape(" P ", "I-I", " D ")
            .withIngredient('P', Items.PAPER)
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Items.STRING)
            .withIngredient('D', new DyeRegularIngredient() {
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

    private static GenericRecipe createPennantBunting(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
            .withShape("I-I")
            .withIngredient('I', Tags.Items.INGOTS_IRON)
            .withIngredient('-', Items.STRING)
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static GenericRecipe createPennantBuntingAugmentation(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new))
            .withShape("B")
            .withIngredient('B', new BasicRegularIngredient(FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new)) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    final ItemStack stack = this.ingredient.copy();
                    stack.setTag(new CompoundNBT());
                    return makePennantExamples(stack);
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

    private static GenericRecipe createPennant(final ResourceLocation name, final IRecipeSerializer<GenericRecipe> serializer) {
        return new GenericRecipeBuilder(name, serializer, FLItems.PENNANT.orElseThrow(IllegalStateException::new))
            .withShape("- -", "PDP", " P ")
            .withIngredient('P', Items.PAPER)
            .withIngredient('-', Items.STRING)
            .withIngredient('D', new DyeRegularIngredient() {
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
            })
            .build();
    }

    private static class LightIngredient extends OreAuxiliaryIngredient<ListNBT> {
        private LightIngredient(final boolean isRequired) {
            super(new ItemTags.Wrapper(new ResourceLocation(FairyLights.ID, "lights")), isRequired, 8);
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
            super(new ItemStack(FLItems.PENNANT.orElseThrow(IllegalStateException::new)), true, 8);
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
                final ItemStack stack = new ItemStack(FLItems.PENNANT.orElseThrow(IllegalStateException::new));
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
