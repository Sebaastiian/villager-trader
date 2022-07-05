package me.sebaastiian.villagertrader.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public final ForgeConfigSpec configSpec;

    public final ForgeConfigSpec.IntValue tradingStationTradeTime;

    ServerConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Villager Trader Server Config");
        builder.push("villager_trading_station");

        tradingStationTradeTime = builder.comment("Amount of time one trade takes in ticks. (20 Ticks = 1 second)")
                .defineInRange("trading_station_trade_time", 200, 20, 2000);

        builder.pop();
        configSpec = builder.build();
    }
}
