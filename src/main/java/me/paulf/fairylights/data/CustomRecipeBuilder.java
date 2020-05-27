package me.paulf.fairylights.data;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CustomRecipeBuilder {
    private final IRecipeSerializer<?> serializer;

    public CustomRecipeBuilder(final IRecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static CustomRecipeBuilder customRecipe(final IRecipeSerializer<?> serializer) {
        return new CustomRecipeBuilder(serializer);
    }

    public void build(final Consumer<IFinishedRecipe> consumer, final ResourceLocation id) {
        consumer.accept(new IFinishedRecipe() {
            @Override
            public void serialize(final JsonObject json) {
            }

            @Override
            public IRecipeSerializer<?> getSerializer() {
                return CustomRecipeBuilder.this.serializer;
            }

            @Override
            public ResourceLocation getID() {
                return id;
            }

            @Override
            @Nullable
            public JsonObject getAdvancementJson() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementID() {
                return new ResourceLocation("");
            }
        });
    }
}
