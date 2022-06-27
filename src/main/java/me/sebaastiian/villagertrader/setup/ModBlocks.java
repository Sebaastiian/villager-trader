package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.blocks.VillagerTradingStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final RegistryObject<Block> VILLAGER_TRADING_STATION = Registration.BLOCKS.register(
            "villager_trading_station", () -> new VillagerTradingStationBlock(BlockBehaviour.Properties.of(
                    Material.STONE).strength(5.0F)));

    static void register() {
    }
}
