package me.paulf.fairylights.data;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.JsonObject;

import me.paulf.fairylights.FairyLights;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GenericRecipeBuilder {
    private final RecipeSerializer<?> serializer;

    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public GenericRecipeBuilder(final RecipeSerializer<?> serializer) {
        this.serializer = Objects.requireNonNull(serializer, "serializer");
    }

    public GenericRecipeBuilder addCriterion(final String name, final CriterionTriggerInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public void build(final Consumer<FinishedRecipe> consumer, final ResourceLocation id) {
        final Supplier<JsonObject> advancementBuilder;
        final ResourceLocation advancementId;
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            advancementBuilder = () -> null;
            advancementId = new ResourceLocation("");
        } else {
            advancementBuilder = this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", new RecipeUnlockedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR)
                ::serializeToJson;
            advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + FairyLights.ID + "/" + id.getPath());
        }
        consumer.accept(new Result(this.serializer, id, advancementBuilder, advancementId));
    }

    public static GenericRecipeBuilder customRecipe(final RecipeSerializer<?> serializer) {
        return new GenericRecipeBuilder(serializer);
    }

    static class Result implements FinishedRecipe {
        final RecipeSerializer<?> serializer;

        final ResourceLocation id;

        final Supplier<JsonObject> advancementJson;

        final ResourceLocation advancementId;

        public Result(final RecipeSerializer<?> serializer, final ResourceLocation id, final Supplier<JsonObject> advancementJson, final ResourceLocation advancementId) {
            this.serializer = serializer;
            this.id = id;
            this.advancementJson = advancementJson;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(final JsonObject json) {
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementJson.get();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
