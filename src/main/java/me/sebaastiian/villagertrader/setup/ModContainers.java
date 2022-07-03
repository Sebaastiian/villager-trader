package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers {

    public static final RegistryObject<MenuType<VillagerTradingStationContainer>> VILLAGER_TRADING_STATION_CONTAINER = Registration.CONTAINERS.register(
            "villager_trading_station",
            () -> IForgeMenuType.create(
                    (windowId, inv, data) -> new VillagerTradingStationContainer(windowId, inv, inv.player, data)));

    static void register() {
    }
}
