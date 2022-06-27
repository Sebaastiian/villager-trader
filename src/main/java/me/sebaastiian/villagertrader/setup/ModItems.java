package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final RegistryObject<Item> VILLAGER_ORB = Registration.ITEMS.register("villager_orb",
            () -> new VillagerOrbItem(defaultProps()));

    public static final RegistryObject<Item> VILLAGER_TRADING_STATION = Registration.ITEMS.register(
            "villager_trading_station", () -> new BlockItem(ModBlocks.VILLAGER_TRADING_STATION.get(), defaultProps()));

    private static Item.Properties defaultProps() {
        return new Item.Properties().tab(ModSetup.ITEM_GROUP);
    }

    static void register() {
    }
}
