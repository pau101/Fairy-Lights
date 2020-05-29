package me.paulf.fairylights.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        protected void registerRecipes(final Consumer<IFinishedRecipe> consumer) {
            final CompoundNBT nbt = new CompoundNBT();
            nbt.put("text", StyledString.serialize(new StyledString()));
            ShapedRecipeBuilder.shapedRecipe(FLItems.LETTER_BUNTING.get())
                .patternLine("I-I")
                .patternLine("PBF")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('-', Tags.Items.STRING)
                .key('P', Items.PAPER)
                .key('B', Items.INK_SAC)
                .key('F', Tags.Items.FEATHERS)
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .build(addNbt(consumer, nbt));
            ShapedRecipeBuilder.shapedRecipe(FLItems.LADDER.get())
                .patternLine("#/")
                .patternLine("#/")
                .patternLine("#/")
                .key('#', Items.LADDER)
                .key('/', Items.STICK)
                .addCriterion("has_stick", this.hasItem(Items.STICK))
                .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(FLItems.GARLAND.get(), 2)
                .patternLine("I-I")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('-', Items.VINE)
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_vine", this.hasItem(Items.VINE))
                .build(consumer);
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.FAIRY_LIGHTS.get())
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .build(consumer, new ResourceLocation(FairyLights.ID, "fairy_lights"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.FAIRY_LIGHTS_AUGMENTATION.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "fairy_lights_augmentation"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.TINSEL_GARLAND.get())
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .build(consumer, new ResourceLocation(FairyLights.ID, "tinsel_garland"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING.get())
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING_AUGMENTATION.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting_augmentation"));
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
            this.lightRecipe(FLCraftingRecipes.ORNATE_LANTERN.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "ornate_lantern"));
            this.lightRecipe(FLCraftingRecipes.OIL_LANTERN.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "oil_lantern"));
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
            this.lightRecipe(FLCraftingRecipes.ICICLE_LIGHTS.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "icicle_lights"));
            this.lightRecipe(FLCraftingRecipes.METEOR_LIGHT.get())
                .build(consumer, new ResourceLocation(FairyLights.ID, "meteor_light"));
        }

        GenericRecipeBuilder lightRecipe(final IRecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_dye", this.hasItem(Tags.Items.DYES));
        }

        GenericRecipeBuilder pennantRecipe(final IRecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_paper", this.hasItem(Items.PAPER))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING));
        }
    }

    static class LootTableGenerator extends LootTableProvider {
        LootTableGenerator(final DataGenerator generator) {
            super(generator);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootParameterSets.BLOCK));
        }

        @Override
        protected void validate(final Map<ResourceLocation, LootTable> map, final ValidationTracker tracker) {
            // For built-in mod loot tables
            /*for (final ResourceLocation name : Sets.difference(MyBuiltInLootTables.getAll(), map.keySet())) {
                tracker.addProblem("Missing built-in table: " + name);
            }*/
            map.forEach((name, table) -> LootTableManager.func_227508_a_(tracker, name, table));
        }
    }

    static class BlockLootTableGenerator extends BlockLootTables {
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return FLBlocks.REG.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        }

        @Override
        protected void addTables() {
            for (final LightVariant variant : LightVariant.values()) {
                final Block block = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(variant.getItem().getRegistryName()));
                this.registerLootTable(block, (factory) -> LootTable.builder()
                    .addLootPool(withSurvivesExplosion(factory, LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(factory)
                            .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("color", "color")))
                        )
                    )
                );
            }
        }
    }

    static Consumer<IFinishedRecipe> addNbt(final Consumer<IFinishedRecipe> consumer, final CompoundNBT nbt) {
        return recipe -> consumer.accept(new ForwardingFinishedRecipe() {
            @Override
            protected IFinishedRecipe delegate() {
                return recipe;
            }

            @Override
            public void serialize(final JsonObject json) {
                super.serialize(json);
                json.getAsJsonObject("result").addProperty("nbt", nbt.toString());
            }
        });
    }

    abstract static class ForwardingFinishedRecipe implements IFinishedRecipe {
        protected abstract IFinishedRecipe delegate();

        @Override
        public void serialize(final JsonObject json) {
            this.delegate().serialize(json);
        }

        @Override
        public ResourceLocation getID() {
            return this.delegate().getID();
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return this.delegate().getSerializer();
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return this.delegate().getAdvancementJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return this.delegate().getAdvancementID();
        }
    }
}
