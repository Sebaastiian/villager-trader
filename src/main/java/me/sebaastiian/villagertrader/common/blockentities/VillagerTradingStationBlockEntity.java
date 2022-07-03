package me.sebaastiian.villagertrader.common.blockentities;

import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import me.sebaastiian.villagertrader.common.handlers.VillagerTradingStationItemHandler;
import me.sebaastiian.villagertrader.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillagerTradingStationBlockEntity extends BlockEntity {
    public VillagerTradingStationBlockEntity(BlockPos pWorldPosition,
                                             BlockState pBlockState) {
        super(ModBlockEntities.VILLAGER_TRADING_STATION_BE.get(), pWorldPosition, pBlockState);
    }

    private final ItemStackHandler itemHandler = new VillagerTradingStationItemHandler(
            VillagerTradingStationContainer.SLOTS, this);
    private final LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState,
                                                          VillagerTradingStationBlockEntity blockEntity) {

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }
}
