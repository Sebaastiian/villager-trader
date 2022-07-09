package me.sebaastiian.villagertrader.common.handlers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class CustomItemHandlerWrapper extends CombinedInvWrapper {
    private final CustomItemHandler<?> internalSlot;
    private BiPredicate<ItemStack, Integer> insertPredicate;
    private BiPredicate<ItemStack, Integer> extractPredicate;

    public CustomItemHandlerWrapper(CustomItemHandler<?> hidden) {
        super(hidden);
        this.internalSlot = hidden;
        this.insertPredicate = internalSlot.insertPredicate;
        this.extractPredicate = internalSlot.extractPredicate;
    }

    public CustomItemHandlerWrapper setInputFilter(BiPredicate<ItemStack, Integer> predicate) {
        this.insertPredicate = predicate;
        return this;
    }

    public CustomItemHandlerWrapper setOutputFilter(BiPredicate<ItemStack, Integer> predicate) {
        this.extractPredicate = predicate;
        return this;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (insertPredicate.test(stack, slot)) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (extractPredicate.test(
                getStackInSlot(slot), slot)) {
            return super.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }
}
