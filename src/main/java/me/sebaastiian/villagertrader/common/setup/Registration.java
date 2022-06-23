package me.sebaastiian.villagertrader.common.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            VillagerTrader.MODID);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);

        ModItems.register();
    }

}
