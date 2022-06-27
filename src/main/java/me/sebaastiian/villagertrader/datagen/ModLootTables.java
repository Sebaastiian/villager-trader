package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.setup.ModBlocks;
import net.minecraft.data.DataGenerator;

public class ModLootTables extends BaseLootTableProvider {
    public ModLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(ModBlocks.VILLAGER_TRADING_STATION.get(),
                createSimpleTable("villager_trading_station", ModBlocks.VILLAGER_TRADING_STATION.get()));
    }

    @Override
    public String getName() {
        return "VillagerTrader Loot Tables";
    }
}
