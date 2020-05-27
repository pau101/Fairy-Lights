package me.paulf.fairylights.data;

import com.google.common.collect.*;
import com.mojang.datafixers.util.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.item.*;
import net.minecraft.block.*;
import net.minecraft.data.*;
import net.minecraft.data.loot.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.*;
import net.minecraftforge.common.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.registries.*;

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
                .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(FLItems.LADDER.get())
                .patternLine("#/")
                .patternLine("#/")
                .patternLine("#/")
                .key('#', Items.LADDER)
                .key('/', Items.STICK)
                .addCriterion("has_stick", this.hasItem(Items.STICK))
                .build(consumer);
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
}
