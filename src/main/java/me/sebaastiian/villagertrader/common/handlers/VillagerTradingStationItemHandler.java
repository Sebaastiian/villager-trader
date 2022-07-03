package me.sebaastiian.villagertrader.common.handlers;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class VillagerTradingStationItemHandler extends ItemStackHandler {
    VillagerTradingStationBlockEntity blockEntity;

    public VillagerTradingStationItemHandler(int size) {
        super(size);
    }

    public VillagerTradingStationItemHandler(int size, VillagerTradingStationBlockEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        // To make sure the TE persists when the chunk is saved later we need to
        // mark it dirty every time the item handler changes
        if (blockEntity == null) return;
        blockEntity.setChanged();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot == 0)
            return stack.getItem() instanceof VillagerOrbItem && VillagerOrbItem.containsVillager(stack);
        return true;
    }

}
