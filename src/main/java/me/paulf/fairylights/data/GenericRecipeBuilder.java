package me.paulf.fairylights.data;

import com.google.gson.JsonObject;
import me.paulf.fairylights.FairyLights;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericRecipeBuilder {
    private final IRecipeSerializer<?> serializer;

    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    public GenericRecipeBuilder(final IRecipeSerializer<?> serializer) {
        this.serializer = Objects.requireNonNull(serializer, "serializer");
    }

    public GenericRecipeBuilder addCriterion(final String name, final ICriterionInstance criterion) {
        this.advancementBuilder.withCriterion(name, criterion);
        return this;
    }

    public void build(final Consumer<IFinishedRecipe> consumer, final ResourceLocation id) {
        final Supplier<JsonObject> advancementBuilder;
        final ResourceLocation advancementId;
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            advancementBuilder = () -> null;
            advancementId = new ResourceLocation("");
        } else {
            advancementBuilder = this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id))
                .withRewards(AdvancementRewards.Builder.recipe(id))
                .withRequirementsStrategy(IRequirementsStrategy.OR)
                ::serialize;
            advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + FairyLights.ID + "/" + id.getPath());
        }
        consumer.accept(new Result(this.serializer, id, advancementBuilder, advancementId));
    }

    public static GenericRecipeBuilder customRecipe(final IRecipeSerializer<?> serializer) {
        return new GenericRecipeBuilder(serializer);
    }

    static class Result implements IFinishedRecipe {
        final IRecipeSerializer<?> serializer;

        final ResourceLocation id;

        final Supplier<JsonObject> advancementJson;

        final ResourceLocation advancementId;

        public Result(final IRecipeSerializer<?> serializer, final ResourceLocation id, final Supplier<JsonObject> advancementJson, final ResourceLocation advancementId) {
            this.serializer = serializer;
            this.id = id;
            this.advancementJson = advancementJson;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(final JsonObject json) {
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public JsonObject getAdvancementJson() {
            return this.advancementJson.get();
        }

        @Override
        public ResourceLocation getAdvancementID() {
            return this.advancementId;
        }
    }
}
