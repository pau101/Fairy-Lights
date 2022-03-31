package me.paulf.fairylights.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.item.FLItems;
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
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = FairyLights.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator();
        gen.func_200390_a(new RecipeGenerator(gen));
        gen.func_200390_a(new LootTableGenerator(gen));
    }

    static class RecipeGenerator extends RecipeProvider {
        RecipeGenerator(final DataGenerator generator) {
            super(generator);
        }

        @Override
        protected void func_200404_a(final Consumer<IFinishedRecipe> consumer) {
            final CompoundNBT nbt = new CompoundNBT();
            nbt.func_218657_a("text", StyledString.serialize(new StyledString()));
            ShapedRecipeBuilder.func_200470_a(FLItems.LETTER_BUNTING.get())
                .func_200472_a("I-I")
                .func_200472_a("PBF")
                .func_200469_a('I', Tags.Items.INGOTS_IRON)
                .func_200469_a('-', Tags.Items.STRING)
                .func_200462_a('P', Items.field_151121_aF)
                .func_200462_a('B', Items.field_196136_br)
                .func_200469_a('F', Tags.Items.FEATHERS)
                .func_200465_a("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .func_200465_a("has_string", func_200409_a(Tags.Items.STRING))
                .func_200464_a(addNbt(consumer, nbt));
            ShapedRecipeBuilder.func_200468_a(FLItems.GARLAND.get(), 2)
                .func_200472_a("I-I")
                .func_200469_a('I', Tags.Items.INGOTS_IRON)
                .func_200462_a('-', Items.field_221796_dh)
                .func_200465_a("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .func_200465_a("has_vine", func_200403_a(Items.field_221796_dh))
                .func_200464_a(consumer);
            ShapedRecipeBuilder.func_200468_a(FLItems.OIL_LANTERN.get(), 4)
                .func_200472_a(" I ")
                .func_200472_a("STS")
                .func_200472_a("IGI")
                .func_200469_a('I', Tags.Items.INGOTS_IRON)
                .func_200462_a('S', Items.field_151055_y)
                .func_200462_a('T', Items.field_221657_bQ)
                .func_200469_a('G', Tags.Items.GLASS_PANES_COLORLESS)
                .func_200465_a("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .func_200465_a("has_torch", func_200403_a(Items.field_221657_bQ))
                .func_200464_a(consumer);
            ShapedRecipeBuilder.func_200468_a(FLItems.CANDLE_LANTERN.get(), 4)
                .func_200472_a(" I ")
                .func_200472_a("GTG")
                .func_200472_a("IGI")
                .func_200469_a('I', Tags.Items.INGOTS_IRON)
                .func_200469_a('G', Tags.Items.NUGGETS_GOLD)
                .func_200462_a('T', Items.field_221657_bQ)
                .func_200465_a("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .func_200465_a("has_torch", func_200403_a(Items.field_221657_bQ))
                .func_200464_a(consumer);
            ShapedRecipeBuilder.func_200468_a(FLItems.INCANDESCENT_LIGHT.get(), 4)
                .func_200472_a(" I ")
                .func_200472_a("ITI")
                .func_200472_a(" G ")
                .func_200469_a('I', Tags.Items.INGOTS_IRON)
                .func_200469_a('G', Tags.Items.GLASS_PANES_COLORLESS)
                .func_200462_a('T', Items.field_221657_bQ)
                .func_200465_a("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .func_200465_a("has_torch", func_200403_a(Items.field_221657_bQ))
                .func_200464_a(consumer);
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

        GenericRecipeBuilder lightRecipe(final IRecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_iron", func_200409_a(Tags.Items.INGOTS_IRON))
                .addCriterion("has_dye", func_200409_a(Tags.Items.DYES));
        }

        GenericRecipeBuilder pennantRecipe(final IRecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .addCriterion("has_paper", func_200403_a(Items.field_151121_aF))
                .addCriterion("has_string", func_200409_a(Tags.Items.STRING));
        }
    }

    static class LootTableGenerator extends LootTableProvider {
        LootTableGenerator(final DataGenerator generator) {
            super(generator);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootParameterSets.field_216267_h));
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
        }
    }

    static Consumer<IFinishedRecipe> addNbt(final Consumer<IFinishedRecipe> consumer, final CompoundNBT nbt) {
        return recipe -> consumer.accept(new ForwardingFinishedRecipe() {
            @Override
            protected IFinishedRecipe delegate() {
                return recipe;
            }

            @Override
            public void func_218610_a(final JsonObject json) {
                super.func_218610_a(json);
                json.getAsJsonObject("result").addProperty("nbt", nbt.toString());
            }
        });
    }

    abstract static class ForwardingFinishedRecipe implements IFinishedRecipe {
        protected abstract IFinishedRecipe delegate();

        @Override
        public void func_218610_a(final JsonObject json) {
            this.delegate().func_218610_a(json);
        }

        @Override
        public ResourceLocation func_200442_b() {
            return this.delegate().func_200442_b();
        }

        @Override
        public IRecipeSerializer<?> func_218609_c() {
            return this.delegate().func_218609_c();
        }

        @Nullable
        @Override
        public JsonObject func_200440_c() {
            return this.delegate().func_200440_c();
        }

        @Nullable
        @Override
        public ResourceLocation func_200443_d() {
            return this.delegate().func_200443_d();
        }
    }
}
