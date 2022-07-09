package me.sebaastiian.villagertrader.common.energy;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    protected VillagerTradingStationBlockEntity blockEntity;

    public CustomEnergyStorage(int capacity) {
        super(capacity);
    }

    public CustomEnergyStorage(VillagerTradingStationBlockEntity be, int capacity) {
        super(capacity);
        blockEntity = be;
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public CustomEnergyStorage(VillagerTradingStationBlockEntity be, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        blockEntity = be;
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public CustomEnergyStorage(VillagerTradingStationBlockEntity be, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        blockEntity = be;
    }

    protected void onEnergyChanged() {
        if (blockEntity == null) return;
        blockEntity.setChanged();
    }

    /**
     * @param amount   Amount of energy to consume
     * @param simulate Should energy actually be reduced
     * @return Amount of energy that is left
     */
    public int consumeEnergy(int amount, boolean simulate) {
        if (!simulate) {
            energy = Math.max(0, energy - amount);
            onEnergyChanged();
            return energy;
        }
        return energy - amount;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onEnergyChanged();
    }
}
