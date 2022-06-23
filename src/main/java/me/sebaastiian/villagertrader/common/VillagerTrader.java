package me.sebaastiian.villagertrader.common;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VillagerTrader.MODID)
public class VillagerTrader {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "villagertrader";

    public VillagerTrader() {
    }
}
