package me.sebaastiian.villagertrader.common.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModSetup {

    public static final String TAB_NAME = VillagerTrader.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.VILLAGER_ORB.get());
        }
    };
}
