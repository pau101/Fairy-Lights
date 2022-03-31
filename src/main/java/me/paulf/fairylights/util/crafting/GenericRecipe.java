package me.paulf.fairylights.util.crafting;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;
import me.paulf.fairylights.util.crafting.ingredient.AuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.EmptyRegularIngredient;
import me.paulf.fairylights.util.crafting.ingredient.GenericIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public final class GenericRecipe implements ICraftingRecipe {
    private final ResourceLocation id;

    public static final EmptyRegularIngredient EMPTY = new EmptyRegularIngredient();

    private final Supplier<? extends IRecipeSerializer<GenericRecipe>> serializer;

    private final ItemStack output;

    private final RegularIngredient[] ingredients;

    private final AuxiliaryIngredient<?>[] auxiliaryIngredients;

    private final int width;

    private final int height;

    private final int outputIngredient;

    private final ThreadLocal<ItemStack> result = ThreadLocal.withInitial(() -> ItemStack.field_190927_a);

    private final ImmutableList<IntUnaryOperator> xFunctions = ImmutableList.of(IntUnaryOperator.identity(), i -> this.getWidth() - 1 - i);

    private int room;

    GenericRecipe(final ResourceLocation id, final Supplier<? extends IRecipeSerializer<GenericRecipe>> serializer, final ItemStack output, final RegularIngredient[] ingredients, final AuxiliaryIngredient<?>[] auxiliaryIngredients, final int width, final int height, final int outputIngredient) {
        Preconditions.checkArgument(width > 0, "width must be greater than zero");
        Preconditions.checkArgument(height > 0, "height must be greater than zero");
        this.id = Objects.requireNonNull(id, "name");
        this.serializer = Objects.requireNonNull(serializer, "serializer");
        this.output = Objects.requireNonNull(output, "output");
        this.ingredients = Objects.requireNonNull(ingredients, "ingredients");
        this.auxiliaryIngredients = checkIngredients(ingredients, Objects.requireNonNull(auxiliaryIngredients, "auxiliaryIngredients"));
        this.width = width;
        this.height = height;
        this.outputIngredient = outputIngredient;
        this.room = -1;
    }

    private int getRoom() {
        if (this.room < 0) {
            int room = 0;
            for (final RegularIngredient ing : this.ingredients) {
                if (ing.getInputs().isEmpty()) {
                    room++;
                }
            }
            for (final AuxiliaryIngredient<?> aux : this.auxiliaryIngredients) {
                if (aux.isRequired()) {
                    room--;
                }
            }
            this.room = room;
        }
        return this.room;
    }

    private NonNullList<Ingredient> getDisplayIngredients() {
        final NonNullList<Ingredient> ingredients = NonNullList.func_191197_a(9, Ingredient.field_193370_a);
        for (int i = 0; i < this.ingredients.length; i++) {
            final int x = i % this.width;
            final int y = i / this.width;
            final ItemStack[] stacks = this.ingredients[i].getInputs().toArray(new ItemStack[0]);
            ingredients.set(x + y * 3, Ingredient.func_193369_a(stacks));
        }
        for (int i = 0, slot = 0; slot < ingredients.size(); slot++) {
            final Ingredient ing = ingredients.get(slot);
            if (ing.func_203189_d()) {
                while (i < this.auxiliaryIngredients.length) {
                    final AuxiliaryIngredient<?> aux = this.auxiliaryIngredients[i++];
                    if (aux.isRequired()) {
                        final ItemStack[] stacks = aux.getInputs().toArray(new ItemStack[0]);
                        ingredients.set(slot, Ingredient.func_193369_a(stacks));
                        break;
                    }
                }
            }
        }
        return ingredients;
    }

    @Override
    public ResourceLocation func_199560_c() {
        return this.id;
    }

    @Override
    public boolean func_192399_d() {
        return this.output.func_190926_b();
    }

    @Override
    public IRecipeSerializer<?> func_199559_b() {
        return this.serializer.get();
    }

    public ItemStack getOutput() {
        return this.output.func_77946_l();
    }

    public RegularIngredient[] getGenericIngredients() {
        return this.ingredients.clone();
    }

    public AuxiliaryIngredient<?>[] getAuxiliaryIngredients() {
        return this.auxiliaryIngredients.clone();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public NonNullList<Ingredient> func_192400_c() {
        return this.getDisplayIngredients();
    }

    @Override
    public boolean func_194133_a(final int width, final int height) {
        return this.width <= width && this.height <= height && (this.getRoom() >= 0 || width * height - this.width * this.height + this.getRoom() >= 0);
    }

    @Override
    public boolean func_77569_a(final CraftingInventory inventory, @Nullable final World world) {
        if (!this.func_194133_a(inventory.func_174922_i(), inventory.func_174923_h())) {
            return false;
        }
        final int scanWidth = inventory.func_174922_i() + 1 - this.width;
        final int scanHeight = inventory.func_174923_h() + 1 - this.height;
        for (int i = 0, end = scanWidth * scanHeight; i < end; i++) {
            final int x = i % scanWidth;
            final int y = i / scanWidth;
            for (final IntUnaryOperator func : this.xFunctions) {
                final ItemStack result = this.getResult(inventory, x, y, func);
                if (!result.func_190926_b()) {
                    this.result.set(result);
                    return true;
                }
            }
        }
        this.result.set(ItemStack.field_190927_a);
        return false;
    }

    private ItemStack getResult(final CraftingInventory inventory, final int originX, final int originY, final IntUnaryOperator funcX) {
        final MatchResultRegular[] match = new MatchResultRegular[this.ingredients.length];
        final Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> auxMatchResults = LinkedListMultimap.create();
        final Map<AuxiliaryIngredient<?>, Integer> auxMatchTotals = new HashMap<>();
        final Set<GenericIngredient<?, ?>> presentCalled = new HashSet<>();
        final List<MatchResultAuxiliary> auxResults = new ArrayList<>();
        Item item = this.output.func_77973_b();
        final CompoundNBT tag = new CompoundNBT();
        for (int i = 0, w = inventory.func_174922_i(), size = w * inventory.func_174923_h(); i < size; i++) {
            final int x = i % w;
            final int y = i / w;
            final int ingX = x - originX;
            final int ingY = y - originY;
            final ItemStack input = inventory.func_70301_a(i);
            if (this.contains(ingX, ingY)) {
                final int index = funcX.applyAsInt(ingX) + ingY * this.width;
                final RegularIngredient ingredient = this.ingredients[index];
                final MatchResultRegular result = ingredient.matches(input);
                if (!result.doesMatch()) {
                    return ItemStack.field_190927_a;
                }
                match[index] = result;
                result.forMatch(presentCalled, tag);
                if (index == this.outputIngredient) {
                    final CompoundNBT inputTag = input.func_77978_p();
                    if (inputTag != null) {
                        if (tag.isEmpty()) {
                            tag.func_197643_a(inputTag);
                        } else {
                            final CompoundNBT temp = inputTag.func_74737_b();
                            temp.func_197643_a(tag);
                            tag.func_197643_a(temp);
                        }
                    }
                    item = input.func_77973_b();
                }
            } else if (!EMPTY.matches(input).doesMatch()) {
                boolean nonAuxiliary = true;
                for (final AuxiliaryIngredient<?> auxiliaryIngredient : this.auxiliaryIngredients) {
                    final MatchResultAuxiliary result = auxiliaryIngredient.matches(input);
                    if (result.doesMatch()) {
                        if (result.isAtLimit(auxMatchTotals.getOrDefault(result.ingredient, 0))) {
                            return ItemStack.field_190927_a;
                        }
                        result.forMatch(presentCalled, tag);
                        auxMatchTotals.merge(result.ingredient, 1, IntMath::checkedAdd);
                        nonAuxiliary = false;
                        result.propagate(auxMatchResults);
                    }
                    auxResults.add(result);
                }
                if (nonAuxiliary) {
                    return ItemStack.field_190927_a;
                }
            }
        }
        final Set<GenericIngredient<?, ?>> absentCalled = new HashSet<>();
        for (final MatchResultRegular result : match) {
            result.notifyAbsence(presentCalled, absentCalled, tag);
        }
        for (final MatchResultAuxiliary result : auxResults) {
            result.notifyAbsence(presentCalled, absentCalled, tag);
        }
        for (final AuxiliaryIngredient<?> ingredient : this.auxiliaryIngredients) {
            if (ingredient.process(auxMatchResults, tag)) {
                return ItemStack.field_190927_a;
            }
        }
        final ItemStack output = this.output.func_190926_b() ? new ItemStack(item) : this.output.func_77946_l();
        if (!tag.isEmpty()) {
            output.func_77982_d(tag);
        }
        return output;
    }

    private boolean contains(final int x, final int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    @Override
    public ItemStack func_77572_b(final CraftingInventory inventory) {
        final ItemStack result = this.result.get();
        return result.func_190926_b() ? result : result.func_77946_l();
    }

    @Override
    public ItemStack func_77571_b() {
        return this.output;
    }

    public interface MatchResult<I extends GenericIngredient<I, M>, M extends MatchResult<I, M>> {
        I getIngredient();

        ItemStack getInput();

        boolean doesMatch();

        void forMatch(final Set<GenericIngredient<?, ?>> called, final CompoundNBT nbt);

        void notifyAbsence(final Set<GenericIngredient<?, ?>> presentCalled, final Set<GenericIngredient<?, ?>> absentCalled, final CompoundNBT nbt);

        M withParent(final M parent);
    }

    public static class MatchResultRegular implements MatchResult<RegularIngredient, MatchResultRegular> {
        protected final RegularIngredient ingredient;

        protected final ItemStack input;

        protected final boolean doesMatch;

        protected final ImmutableList<MatchResultRegular> supplementaryResults;

        public MatchResultRegular(final RegularIngredient ingredient, final ItemStack input, final boolean doesMatch, final List<MatchResultRegular> supplementaryResults) {
            this.ingredient = Objects.requireNonNull(ingredient, "ingredient");
            this.input = input;
            this.doesMatch = doesMatch;
            this.supplementaryResults = ImmutableList.copyOf(supplementaryResults);
        }

        @Override
        public final RegularIngredient getIngredient() {
            return this.ingredient;
        }

        @Override
        public final ItemStack getInput() {
            return this.input;
        }

        @Override
        public final boolean doesMatch() {
            return this.doesMatch;
        }

        @Override
        public void forMatch(final Set<GenericIngredient<?, ?>> called, final CompoundNBT nbt) {
            this.ingredient.matched(this.input, nbt);
            if (called.add(this.ingredient)) {
                this.ingredient.present(nbt);
            }
        }

        @Override
        public void notifyAbsence(final Set<GenericIngredient<?, ?>> presentCalled, final Set<GenericIngredient<?, ?>> absentCalled, final CompoundNBT nbt) {
            if (!presentCalled.contains(this.ingredient) && !absentCalled.contains(this.ingredient)) {
                this.ingredient.absent(nbt);
                absentCalled.add(this.ingredient);
            }
            for (final MatchResultRegular result : this.supplementaryResults) {
                result.notifyAbsence(presentCalled, absentCalled, nbt);
            }
        }

        @Override
        public MatchResultRegular withParent(final MatchResultRegular parent) {
            return new MatchResultParentedRegular(this.ingredient, this.input, this.doesMatch, this.supplementaryResults, parent);
        }
    }

    public static class MatchResultParentedRegular extends MatchResultRegular {
        protected final MatchResultRegular parent;

        public MatchResultParentedRegular(final RegularIngredient ingredient, final ItemStack input, final boolean doesMatch, final List<MatchResultRegular> supplementaryResults, final MatchResultRegular parent) {
            super(ingredient, input, doesMatch, supplementaryResults);
            this.parent = Objects.requireNonNull(parent, "parent");
        }

        @Override
        public void forMatch(final Set<GenericIngredient<?, ?>> called, final CompoundNBT nbt) {
            super.forMatch(called, nbt);
            this.parent.forMatch(called, nbt);
        }

        @Override
        public void notifyAbsence(final Set<GenericIngredient<?, ?>> presentCalled, final Set<GenericIngredient<?, ?>> absentCalled, final CompoundNBT nbt) {
            super.notifyAbsence(presentCalled, absentCalled, nbt);
            this.parent.notifyAbsence(presentCalled, absentCalled, nbt);
        }

        @Override
        public MatchResultRegular withParent(final MatchResultRegular parent) {
            return this.parent.withParent(new MatchResultParentedRegular(this.ingredient, this.input, this.doesMatch, this.supplementaryResults, parent));
        }
    }

    public static class MatchResultAuxiliary implements MatchResult<AuxiliaryIngredient<?>, MatchResultAuxiliary> {
        protected final AuxiliaryIngredient<?> ingredient;

        protected final ItemStack input;

        protected final boolean doesMatch;

        protected final ImmutableList<MatchResultAuxiliary> supplementaryResults;

        public MatchResultAuxiliary(final AuxiliaryIngredient<?> ingredient, final ItemStack input, final boolean doesMatch, final List<MatchResultAuxiliary> supplementaryResults) {
            this.ingredient = Objects.requireNonNull(ingredient, "ingredient");
            this.input = input;
            this.doesMatch = doesMatch;
            this.supplementaryResults = ImmutableList.copyOf(supplementaryResults);
        }

        @Override
        public final AuxiliaryIngredient<?> getIngredient() {
            return this.ingredient;
        }

        @Override
        public final ItemStack getInput() {
            return this.input;
        }

        @Override
        public final boolean doesMatch() {
            return this.doesMatch;
        }

        @Override
        public void forMatch(final Set<GenericIngredient<?, ?>> called, final CompoundNBT nbt) {
            if (!called.contains(this.ingredient)) {
                this.ingredient.present(nbt);
                called.add(this.ingredient);
            }
        }

        @Override
        public void notifyAbsence(final Set<GenericIngredient<?, ?>> presentCalled, final Set<GenericIngredient<?, ?>> absentCalled, final CompoundNBT nbt) {
            if (!presentCalled.contains(this.ingredient) && !absentCalled.contains(this.ingredient)) {
                this.ingredient.absent(nbt);
                absentCalled.add(this.ingredient);
            }
            for (final MatchResultAuxiliary result : this.supplementaryResults) {
                result.notifyAbsence(presentCalled, absentCalled, nbt);
            }
        }

        @Override
        public MatchResultAuxiliary withParent(final MatchResultAuxiliary parent) {
            return new MatchResultParentedAuxiliary(this.ingredient, this.input, this.doesMatch, this.supplementaryResults, parent);
        }

        public boolean isAtLimit(final int count) {
            return count >= this.ingredient.getLimit();
        }

        public void propagate(final Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> map) {
            map.put(this.ingredient, this);
        }
    }

    public static class MatchResultParentedAuxiliary extends MatchResultAuxiliary {
        protected final MatchResultAuxiliary parent;

        public MatchResultParentedAuxiliary(final AuxiliaryIngredient<?> ingredient, final ItemStack input, final boolean doesMatch, final List<MatchResultAuxiliary> supplementaryResults, final MatchResultAuxiliary parent) {
            super(ingredient, input, doesMatch, supplementaryResults);
            this.parent = Objects.requireNonNull(parent, "parent");
        }

        @Override
        public void forMatch(final Set<GenericIngredient<?, ?>> called, final CompoundNBT nbt) {
            super.forMatch(called, nbt);
            this.parent.forMatch(called, nbt);
        }

        @Override
        public void notifyAbsence(final Set<GenericIngredient<?, ?>> presentCalled, final Set<GenericIngredient<?, ?>> absentCalled, final CompoundNBT nbt) {
            super.notifyAbsence(presentCalled, absentCalled, nbt);
            this.parent.notifyAbsence(presentCalled, absentCalled, nbt);
        }

        @Override
        public MatchResultAuxiliary withParent(final MatchResultAuxiliary parent) {
            return this.parent.withParent(new MatchResultParentedAuxiliary(this.ingredient, this.input, this.doesMatch, this.supplementaryResults, parent));
        }

        @Override
        public boolean isAtLimit(final int count) {
            return super.isAtLimit(count) || this.parent.isAtLimit(count);
        }

        @Override
        public void propagate(final Multimap<AuxiliaryIngredient<?>, MatchResultAuxiliary> map) {
            super.propagate(map);
            this.parent.propagate(map);
        }
    }

    private static AuxiliaryIngredient<?>[] checkIngredients(final RegularIngredient[] ingredients, final AuxiliaryIngredient<?>[] auxiliaryIngredients) {
        checkForNulls(ingredients);
        checkForNulls(auxiliaryIngredients);
        final boolean ingredientDictator = checkDictatorship(false, ingredients);
        checkDictatorship(ingredientDictator, auxiliaryIngredients);
        return auxiliaryIngredients;
    }

    private static void checkForNulls(final GenericIngredient<?, ?>[] ingredients) {
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i] == null) {
                throw new NullPointerException("Must not have null ingredients, found at index " + i);
            }
        }
    }

    private static boolean checkDictatorship(boolean foundDictator, final GenericIngredient<?, ?>[] ingredients) {
        for (final GenericIngredient<?, ?> ingredient : ingredients) {
            if (ingredient.dictatesOutputType()) {
                if (foundDictator) {
                    throw new IllegalRecipeException("Only one ingredient can dictate output type");
                }
                foundDictator = true;
            }
        }
        return foundDictator;
    }
}
