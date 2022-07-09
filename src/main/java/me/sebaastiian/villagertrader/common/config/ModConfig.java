package me.sebaastiian.villagertrader.common.config;

import net.minecraftforge.fml.ModLoadingContext;

public class ModConfig {

    private ModConfig() {
    }

    public static final ServerConfig server = new ServerConfig();

    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, server.configSpec);
    }

}
