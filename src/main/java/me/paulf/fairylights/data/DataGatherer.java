package me.paulf.fairylights.data;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = FairyLights.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator();
        gen.addProvider(new RecipeGenerator(gen));
        gen.addProvider(new LootTableGenerator(gen));
    }

    static class RecipeGenerator extends RecipeProvider {
        RecipeGenerator(final DataGenerator generator) {
            super(generator);
        }

        @Override
        protected void buildCraftingRecipes(final Consumer<FinishedRecipe> consumer) {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("text", StyledString.serialize(new StyledString()));
            ShapedRecipeBuilder.shaped(FLItems.LETTER_BUNTING.get())
                .pattern("I-I")
                .pattern("PBF")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('-', Tags.Items.STRING)
                .define('P', Items.PAPER)
                .define('B', Items.INK_SAC)
                .define('F', Tags.Items.FEATHERS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_string", has(Tags.Items.STRING))
                .save(addNbt(consumer, nbt));
            ShapedRecipeBuilder.shaped(FLItems.GARLAND.get(), 2)
                .pattern("I-I")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('-', Items.VINE)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_vine", has(Items.VINE))
                .save(consumer);
            ShapedRecipeBuilder.shaped(FLItems.OIL_LANTERN.get(), 4)
                .pattern(" I ")
                .pattern("STS")
                .pattern("IGI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('S', Items.STICK)
                .define('T', Items.TORCH)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            ShapedRecipeBuilder.shaped(FLItems.CANDLE_LANTERN.get(), 4)
                .pattern(" I ")
                .pattern("GTG")
                .pattern("IGI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .define('T', Items.TORCH)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            ShapedRecipeBuilder.shaped(FLItems.INCANDESCENT_LIGHT.get(), 4)
                .pattern(" I ")
                .pattern("ITI")
                .pattern(" G ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .define('T', Items.TORCH)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.HANGING_LIGHTS.get())
                .addCriterion("has_lights", func_200409_a(FLCraftingRecipes.LIGHTS))
                .build(consumer, new ResourceLocation(FairyLights.ID, "hanging_lights"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.HANGING_LIGHTS_AUGMENTATION.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "hanging_lights_augmentation"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.TINSEL_GARLAND.get())
                .addCriterion("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .addCriterion("has_string", func_200409_a(Tags.Items.STRING))
                .build(consumer, new ResourceLocation(FairyLights.ID, "tinsel_garland"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING.get())
                .addCriterion("has_pennants", func_200409_a(FLCraftingRecipes.PENNANTS))
                .build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING_AUGMENTATION.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting_augmentation"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.EDIT_COLOR.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "edit_color"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.COPY_COLOR.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "copy_color"));
            this.pennantRecipe(FLCraftingRecipes.TRIANGLE_PENNANT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "triangle_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SPEARHEAD_PENNANT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "spearhead_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SWALLOWTAIL_PENNANT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "swallowtail_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SQUARE_PENNANT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "square_pennant"));
            this.lightRecipe(FLCraftingRecipes.FAIRY_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "fairy_light"));
            this.lightRecipe(FLCraftingRecipes.PAPER_LANTERN.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "paper_lantern"));
            this.lightRecipe(FLCraftingRecipes.ORB_LANTERN.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "orb_lantern"));
            this.lightRecipe(FLCraftingRecipes.FLOWER_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "flower_light"));
            this.lightRecipe(FLCraftingRecipes.CANDLE_LANTERN_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "candle_lantern_light"));
            this.lightRecipe(FLCraftingRecipes.OIL_LANTERN_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "oil_lantern_light"));
            this.lightRecipe(FLCraftingRecipes.JACK_O_LANTERN.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "jack_o_lantern"));
            this.lightRecipe(FLCraftingRecipes.SKULL_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "skull_light"));
            this.lightRecipe(FLCraftingRecipes.GHOST_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "ghost_light"));
            this.lightRecipe(FLCraftingRecipes.SPIDER_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "spider_light"));
            this.lightRecipe(FLCraftingRecipes.WITCH_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "witch_light"));
            this.lightRecipe(FLCraftingRecipes.SNOWFLAKE_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "snowflake_light"));
            this.lightRecipe(FLCraftingRecipes.HEART_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "heart_light"));
            this.lightRecipe(FLCraftingRecipes.MOON_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "moon_light"));
            this.lightRecipe(FLCraftingRecipes.STAR_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "star_light"));
            this.lightRecipe(FLCraftingRecipes.ICICLE_LIGHTS.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "icicle_lights"));
            this.lightRecipe(FLCraftingRecipes.METEOR_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "meteor_light"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.LIGHT_TWINKLE.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "light_twinkle"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.COLOR_CHANGING_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "color_changing_light"));
        }

        GenericRecipeBuilder lightRecipe(final RecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_iron", has(Tags.Items.INGOTS_IRON))
                .addCriterion("has_dye", has(Tags.Items.DYES));
        }

        GenericRecipeBuilder pennantRecipe(final RecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_paper", has(Items.PAPER))
                .addCriterion("has_string", has(Tags.Items.STRING));
        }
    }

    static class LootTableGenerator extends LootTableProvider {
        LootTableGenerator(final DataGenerator generator) {
            super(generator);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootContextParamSets.BLOCK));
        }

        @Override
        protected void validate(final Map<ResourceLocation, LootTable> map, final ValidationContext tracker) {
            // For built-in mod loot tables
            /*for (final ResourceLocation name : Sets.difference(MyBuiltInLootTables.getAll(), map.keySet())) {
                tracker.addProblem("Missing built-in table: " + name);
            }*/
            map.forEach((name, table) -> LootTables.validate(tracker, name, table));
        }
    }

    static class BlockLootTableGenerator extends BlockLoot {
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return FLBlocks.REG.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        }

        @Override
        protected void addTables() {
        }
    }

    static Consumer<FinishedRecipe> addNbt(final Consumer<FinishedRecipe> consumer, final CompoundTag nbt) {
        return recipe -> consumer.accept(new ForwardingFinishedRecipe() {
            @Override
            protected FinishedRecipe delegate() {
                return recipe;
            }

            @Override
            public void serializeRecipeData(final JsonObject json) {
                super.serializeRecipeData(json);
                json.getAsJsonObject("result").addProperty("nbt", nbt.toString());
            }
        });
    }

    abstract static class ForwardingFinishedRecipe implements FinishedRecipe {
        protected abstract FinishedRecipe delegate();

        @Override
        public void serializeRecipeData(final JsonObject json) {
            this.delegate().serializeRecipeData(json);
        }

        @Override
        public ResourceLocation getId() {
            return this.delegate().getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.delegate().getType();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.delegate().serializeAdvancement();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.delegate().getAdvancementId();
        }
    }
}
