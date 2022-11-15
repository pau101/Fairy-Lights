package me.paulf.fairylights.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class LazyItemStack {
    private final RegistryObject<? extends Item> object;

    private final Function<? super Item, ItemStack> factory;

    private ItemStack stack;

    public LazyItemStack(final RegistryObject<? extends Item> object, final Function<? super Item, ItemStack> factory) {
        this.object = object;
        this.factory = factory;
        this.stack = ItemStack.EMPTY;
    }

    public ItemStack get() {
        if (this.stack.isEmpty()) {
            this.object.map(this.factory).ifPresent(stack -> this.stack = stack);
        }
        return this.stack;
    }
}
