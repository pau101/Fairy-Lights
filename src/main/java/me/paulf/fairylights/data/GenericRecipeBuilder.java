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

    private final Advancement.Builder advancementBuilder = Advancement.Builder.func_200278_a();

    public GenericRecipeBuilder(final IRecipeSerializer<?> serializer) {
        this.serializer = Objects.requireNonNull(serializer, "serializer");
    }

    public GenericRecipeBuilder addCriterion(final String name, final ICriterionInstance criterion) {
        this.advancementBuilder.func_200275_a(name, criterion);
        return this;
    }

    public void build(final Consumer<IFinishedRecipe> consumer, final ResourceLocation id) {
        final Supplier<JsonObject> advancementBuilder;
        final ResourceLocation advancementId;
        if (this.advancementBuilder.func_200277_c().isEmpty()) {
            advancementBuilder = () -> null;
            advancementId = new ResourceLocation("");
        } else {
            advancementBuilder = this.advancementBuilder.func_200272_a(new ResourceLocation("recipes/root"))
                .func_200275_a("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.field_234582_a_, id))
                .func_200271_a(AdvancementRewards.Builder.func_200280_c(id))
                .func_200270_a(IRequirementsStrategy.field_223215_b_)
                ::func_200273_b;
            advancementId = new ResourceLocation(id.func_110624_b(), "recipes/" + FairyLights.ID + "/" + id.func_110623_a());
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
        public void func_218610_a(final JsonObject json) {
        }

        @Override
        public IRecipeSerializer<?> func_218609_c() {
            return this.serializer;
        }

        @Override
        public ResourceLocation func_200442_b() {
            return this.id;
        }

        @Override
        public JsonObject func_200440_c() {
            return this.advancementJson.get();
        }

        @Override
        public ResourceLocation func_200443_d() {
            return this.advancementId;
        }
    }
}
