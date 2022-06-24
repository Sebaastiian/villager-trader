package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import net.minecraft.nbt.FloatTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModSetup {

    public static final String TAB_NAME = VillagerTrader.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            ItemStack stack = new ItemStack(ModItems.VILLAGER_ORB.get());
            // Make the item look as if it contained a villager
            // Hopefully not using authentic data won't cause issues
            stack.getOrCreateTag().put(VillagerOrbItem.COMPOUND_DATA, FloatTag.ZERO);
            return stack;
        }
    };
}
