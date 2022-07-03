package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final RegistryObject<BlockEntityType<VillagerTradingStationBlockEntity>> VILLAGER_TRADING_STATION_BE = Registration.BLOCK_ENTITIES.register(
            "villager_trading_station", () -> BlockEntityType.Builder.of(VillagerTradingStationBlockEntity::new,
                    ModBlocks.VILLAGER_TRADING_STATION.get()).build(null));

    static void register() {
    }

}
