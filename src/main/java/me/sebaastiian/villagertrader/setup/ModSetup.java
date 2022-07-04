package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.network.PacketHandler;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import net.minecraft.nbt.FloatTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = VillagerTrader.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static final String TAB_NAME = VillagerTrader.MODID;
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            ItemStack stack = new ItemStack(ModItems.VILLAGER_ORB.get());
            // Make the item look as if it contained a villager
            // Hopefully not using authentic data won't cause issues
            stack.getOrCreateTag().put(VillagerNbt.COMPOUND_DATA, FloatTag.ZERO);
            return stack;
        }
    };

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

}