package me.sebaastiian.villagertrader.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public final ForgeConfigSpec configSpec;

    public final ForgeConfigSpec.IntValue tradingStationTradeTime;
    public final ForgeConfigSpec.IntValue tradingStationEnergyCapacity;
    public final ForgeConfigSpec.IntValue tradingStationEnergyPerTick;
    public final ForgeConfigSpec.IntValue tradingStationMaxTransfer;


    ServerConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Villager Trader Server Config");
        builder.push("villager_trading_station");

        tradingStationTradeTime = builder.comment("Amount of time one trade takes in ticks. (20 Ticks = 1 second)")
                .defineInRange("trading_station_trade_time", 200, 20, 2000);
        tradingStationEnergyCapacity = builder.comment("Energy capacity")
                .defineInRange("trading_station_energy_capacity", 200_000, 20_000, 20_000_000);
        tradingStationEnergyPerTick = builder.comment("Amount of energy used per tick")
                .defineInRange("trading_station_energy_per_tick", 100, 1, 500_000);
        tradingStationMaxTransfer = builder.comment("Max amount of energy to be inserted per tick")
                .defineInRange("trading_station_max_transfer", 500, 1, 500_000);

        builder.pop();
        configSpec = builder.build();
    }
}
