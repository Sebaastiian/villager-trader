package me.sebaastiian.villagertrader.common.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class CustomItemHandler<T extends BlockEntity> extends ItemStackHandler {
    private T blockEntity;
    BiPredicate<ItemStack, Integer> insertPredicate;
    BiPredicate<ItemStack, Integer> extractPredicate;

    public CustomItemHandler(int size) {
        super(size);
        this.insertPredicate = (stack, integer) -> true;
        this.extractPredicate = (stack, integer) -> true;
    }

    public CustomItemHandler(int size, T blockEntity) {
        this(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        // To make sure the TE persists when the chunk is saved later we need to
        // mark it dirty every time the item handler changes
        if (blockEntity == null) return;
        blockEntity.setChanged();
    }


    public CustomItemHandler<T> setInputFilter(BiPredicate<ItemStack, Integer> predicate) {
        this.insertPredicate = predicate;
        return this;
    }

    public CustomItemHandler<T> setOutputFilter(BiPredicate<ItemStack, Integer> predicate) {
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
        if (extractPredicate.test(getStackInSlot(slot), slot)) {
            return super.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }
}
