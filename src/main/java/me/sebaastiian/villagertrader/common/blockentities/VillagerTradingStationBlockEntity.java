package me.sebaastiian.villagertrader.common.blockentities;

import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.config.VillagerTraderConfig;
import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import me.sebaastiian.villagertrader.common.handlers.CustomEnergyStorage;
import me.sebaastiian.villagertrader.common.handlers.VillagerTradingStationItemHandler;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import me.sebaastiian.villagertrader.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VillagerTradingStationBlockEntity extends BlockEntity {
    public VillagerTradingStationBlockEntity(BlockPos pWorldPosition,
                                             BlockState pBlockState) {
        super(ModBlockEntities.VILLAGER_TRADING_STATION_BE.get(), pWorldPosition, pBlockState);
    }

    private final ItemStackHandler itemHandler = new VillagerTradingStationItemHandler(
            VillagerTradingStationContainer.SLOTS, this);
    private final LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(this, 200_000, 500);
    private final LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    private int selectedTrade;
    private List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = List.of();
    private int progress = -1;

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState,
                                                          VillagerTradingStationBlockEntity blockEntity) {
        if (blockEntity.offers.isEmpty()) {
            blockEntity.progress = -1;
            return;
        }

        if (blockEntity.offers.size() <= blockEntity.selectedTrade) {
            blockEntity.selectedTrade = 0;
        }

        if (blockEntity.progress >= VillagerTraderConfig.server.tradingStationTradeTime.get() && blockEntity.hasCorrectItemsForTrade()) {
            blockEntity.progress = 0;
            blockEntity.makeTrade();
        }
        if (blockEntity.hasCorrectItemsForTrade() && blockEntity.energyStorage.consumeEnergy(100, true) >= 0) {
            blockEntity.progress++;
            blockEntity.energyStorage.consumeEnergy(100, false);
        } else {
            blockEntity.progress = Math.max(-1, blockEntity.progress - 5);
        }
    }

    private void makeTrade() {
        Pair<Pair<ItemStack, ItemStack>, ItemStack> trade = offers.get(selectedTrade);
        Pair<ItemStack, ItemStack> inputs = trade.getFirst();
        ItemStack result = trade.getSecond();

        itemHandler.extractItem(1, inputs.getFirst().getCount(), false);
        itemHandler.extractItem(2, inputs.getSecond().getCount(), false);
        itemHandler.insertItem(3, result.copy(), false);
    }

    private boolean hasCorrectItemsForTrade() {
        ItemStack stackFirstSlot = itemHandler.getStackInSlot(1);
        ItemStack stackSecondSlot = itemHandler.getStackInSlot(2);

        Pair<Pair<ItemStack, ItemStack>, ItemStack> trade = offers.get(selectedTrade);
        Pair<ItemStack, ItemStack> inputs = trade.getFirst();
        ItemStack result = trade.getSecond();

        if (stackFirstSlot.getItem() != inputs.getFirst().getItem()) return false;
        if (stackFirstSlot.getCount() < inputs.getFirst().getCount()) return false;
        if (!canInsertResult(result.copy())) return false;

        if (!inputs.getSecond().isEmpty()) {
            if (stackSecondSlot.getItem() != inputs.getSecond().getItem()) return false;
            if (stackSecondSlot.getCount() < inputs.getSecond().getCount()) return false;
        }

        return true;
    }

    private boolean canInsertResult(ItemStack result) {
        return (itemHandler.insertItem(3, result, true).getCount() != result.getCount());
    }

    public void setSelectedTrade(int selectedTrade) {
        this.selectedTrade = selectedTrade;
    }

    public int getSelectedTrade() {
        return selectedTrade;
    }

    public void setOffers(List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers) {
        this.offers = offers;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.putInt("selected_trade", selectedTrade);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        energyStorage.deserializeNBT(pTag.get("energy"));
        selectedTrade = pTag.getInt("selected_trade");

        ItemStack stack = itemHandler.getStackInSlot(0);
        if (!VillagerNbt.containsVillager(stack)) return;
        offers = VillagerNbt.tryGetOffers(stack);
    }
}
