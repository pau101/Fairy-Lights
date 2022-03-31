package me.paulf.fairylights.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Function;

public final class LazyItemStack {
    private final RegistryObject<? extends Item> object;

    private final Function<? super Item, ItemStack> factory;

    private ItemStack stack;

    public LazyItemStack(final RegistryObject<? extends Item> object, final Function<? super Item, ItemStack> factory) {
        this.object = object;
        this.factory = factory;
        this.stack = ItemStack.field_190927_a;
    }

    public ItemStack get() {
        if (this.stack.func_190926_b()) {
            this.object.map(this.factory).ifPresent(stack -> this.stack = stack);
        }
        return this.stack;
    }
}
