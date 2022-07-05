package me.sebaastiian.villagertrader.common;

import com.mojang.logging.LogUtils;
import me.sebaastiian.villagertrader.common.config.VillagerTraderConfig;
import me.sebaastiian.villagertrader.setup.Registration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VillagerTrader.MODID)
public class VillagerTrader {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "villagertrader";

    public VillagerTrader() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        VillagerTraderConfig.registerConfigs(ModLoadingContext.get());
        Registration.register(modEventBus);

    }
}
