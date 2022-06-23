package me.sebaastiian.villagertrader.common.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(VillagerTrader.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.DIRT);
        }
    };
}
