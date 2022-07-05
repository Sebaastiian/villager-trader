package me.sebaastiian.villagertrader.common.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class VillagerTraderConfig {

    private VillagerTraderConfig() {
    }

    public static final ServerConfig server = new ServerConfig();

    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, server.configSpec);
    }

}
