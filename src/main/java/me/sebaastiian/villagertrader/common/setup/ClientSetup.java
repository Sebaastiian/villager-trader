package me.sebaastiian.villagertrader.common.setup;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VillagerTrader.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final ResourceLocation PREDICATE_VILLAGER_ORB = new ResourceLocation(VillagerTrader.MODID,
            "contains_villager");

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.VILLAGER_ORB.get(), PREDICATE_VILLAGER_ORB,
                    (stack, level, entity, seed) -> VillagerOrbItem.containsVillager(stack) ? 1 : 0);
        });
    }
}
