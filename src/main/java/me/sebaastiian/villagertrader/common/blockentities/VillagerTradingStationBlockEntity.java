package me.sebaastiian.villagertrader.common.blockentities;

import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.config.VillagerTraderConfig;
import me.sebaastiian.villagertrader.common.energy.CustomEnergyStorage;
import me.sebaastiian.villagertrader.common.inventory.CustomItemHandler;
import me.sebaastiian.villagertrader.common.inventory.CustomItemHandlerWrapper;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VillagerTradingStationBlockEntity extends BlockEntity {
    public VillagerTradingStationBlockEntity(BlockPos pWorldPosition,
                                             BlockState pBlockState) {
        super(ModBlockEntities.VILLAGER_TRADING_STATION_BE.get(), pWorldPosition, pBlockState);
    }

    public static final int ORB_SLOTS = 1;
    public static final int INPUT_SLOTS = 2;
    public static final int OUTPUT_SLOTS = 1;


    private final CustomItemHandler<VillagerTradingStationBlockEntity> orbHandler = new CustomItemHandler<>(
            ORB_SLOTS, this).setInputFilter((stack, slot) -> stack.getItem() instanceof VillagerOrbItem);
    private final IItemHandlerModifiable publicOrbHandler = new CustomItemHandlerWrapper(orbHandler).setOutputFilter(
            ((itemStack, slot) -> false));
    private final CustomItemHandler<VillagerTradingStationBlockEntity> inputHandler = new CustomItemHandler<>(
            INPUT_SLOTS, this);
    private final IItemHandlerModifiable publicInputHandler = new CustomItemHandlerWrapper(
            inputHandler).setOutputFilter((stack, slot) -> false);
    private final CustomItemHandler<VillagerTradingStationBlockEntity> outputHandler = new CustomItemHandler<>(
            OUTPUT_SLOTS, this);
    private final IItemHandlerModifiable publicOutputHandler = new CustomItemHandlerWrapper(
            outputHandler).setInputFilter((stack, slot) -> false);

    private final LazyOptional<IItemHandlerModifiable> publicItemHandler = LazyOptional.of(
            () -> new CombinedInvWrapper(publicOrbHandler, publicInputHandler, publicOutputHandler));
    private final LazyOptional<IItemHandlerModifiable> privateItemHandler = LazyOptional.of(
            () -> new CombinedInvWrapper(orbHandler, inputHandler, outputHandler));

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(this, 200_000, 500);
    private final LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    private int selectedTrade;
    private List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = List.of();
    private int progress = -1;

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return privateItemHandler.cast();
            }
            return publicItemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        publicItemHandler.invalidate();
        privateItemHandler.invalidate();
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

        inputHandler.extractItem(0, inputs.getFirst().getCount(), false);
        inputHandler.extractItem(1, inputs.getSecond().getCount(), false);
        outputHandler.insertItem(0, result.copy(), false);
    }

    private boolean hasCorrectItemsForTrade() {
        ItemStack stackFirstSlot = inputHandler.getStackInSlot(0);
        ItemStack stackSecondSlot = inputHandler.getStackInSlot(1);

        Pair<Pair<ItemStack, ItemStack>, ItemStack> trade = offers.get(selectedTrade);
        Pair<ItemStack, ItemStack> inputs = trade.getFirst();
        ItemStack result = trade.getSecond();

        if (stackFirstSlot.getItem() != inputs.getFirst().getItem()) return false;
        if (stackFirstSlot.getCount() < inputs.getFirst().getCount()) return false;
        if (!canInsertResult(result.copy())) return false;

        if (!inputs.getSecond().isEmpty()) {
            if (stackSecondSlot.getItem() != inputs.getSecond().getItem()) return false;
            return stackSecondSlot.getCount() >= inputs.getSecond().getCount();
        }

        return true;
    }

    private boolean canInsertResult(ItemStack result) {
        return (outputHandler.insertItem(0, result, true).getCount() != result.getCount());
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
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory_orb", orbHandler.serializeNBT());
        pTag.put("inventory_input", inputHandler.serializeNBT());
        pTag.put("inventory_output", outputHandler.serializeNBT());
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.putInt("selected_trade", selectedTrade);
        pTag.putInt("progress", progress);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        orbHandler.deserializeNBT(pTag.getCompound("inventory_orb"));
        inputHandler.deserializeNBT(pTag.getCompound("inventory_input"));
        outputHandler.deserializeNBT(pTag.getCompound("inventory_output"));
        energyStorage.deserializeNBT(pTag.get("energy"));
        selectedTrade = pTag.getInt("selected_trade");
        progress = pTag.getInt("progress");

        ItemStack stack = orbHandler.getStackInSlot(0);
        if (!VillagerNbt.containsVillager(stack)) return;
        offers = VillagerNbt.tryGetOffers(stack);
    }
}
