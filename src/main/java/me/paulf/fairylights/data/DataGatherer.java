package me.paulf.fairylights.data;

import com.google.common.collect.*;
import com.google.gson.*;
import com.mojang.datafixers.util.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.server.item.crafting.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.block.*;
import net.minecraft.data.*;
import net.minecraft.data.loot.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.*;
import net.minecraftforge.common.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.registries.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.FAIRY_LIGHTS.get()).build(consumer, new ResourceLocation(FairyLights.ID, "fairy_lights"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.FAIRY_LIGHTS_AUGMENTATION.get()).build(consumer, new ResourceLocation(FairyLights.ID, "fairy_lights_augmentation"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.TINSEL_GARLAND.get()).build(consumer, new ResourceLocation(FairyLights.ID, "tinsel_garland"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING.get()).build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING_AUGMENTATION.get()).build(consumer, new ResourceLocation(FairyLights.ID, "pennant_bunting_augmentation"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "pennant"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.FAIRY_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "fairy_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.PAPER_LANTERN.get()).build(consumer, new ResourceLocation(FairyLights.ID, "paper_lantern"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.ORB_LANTERN.get()).build(consumer, new ResourceLocation(FairyLights.ID, "orb_lantern"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.FLOWER_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "flower_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.ORNATE_LANTERN.get()).build(consumer, new ResourceLocation(FairyLights.ID, "ornate_lantern"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.OIL_LANTERN.get()).build(consumer, new ResourceLocation(FairyLights.ID, "oil_lantern"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.JACK_O_LANTERN.get()).build(consumer, new ResourceLocation(FairyLights.ID, "jack_o_lantern"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.SKULL_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "skull_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.GHOST_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "ghost_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.SPIDER_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "spider_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.WITCH_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "witch_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.SNOWFLAKE_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "snowflake_light"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.ICICLE_LIGHTS.get()).build(consumer, new ResourceLocation(FairyLights.ID, "icicle_lights"));
            CustomRecipeBuilder.customRecipe(FLCraftingRecipes.METEOR_LIGHT.get()).build(consumer, new ResourceLocation(FairyLights.ID, "meteor_light"));
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
