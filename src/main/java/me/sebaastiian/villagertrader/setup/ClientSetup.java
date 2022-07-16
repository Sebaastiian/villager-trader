package me.sebaastiian.villagertrader.setup;

import me.sebaastiian.villagertrader.client.renderers.VillagerTradingStationRenderer;
import me.sebaastiian.villagertrader.client.screens.VillagerTradingStationScreen;
import me.sebaastiian.villagertrader.client.util.ClientTradesTooltip;
import me.sebaastiian.villagertrader.client.util.TradesTooltip;
import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
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
            MenuScreens.register(ModContainers.VILLAGER_TRADING_STATION_CONTAINER.get(),
                    VillagerTradingStationScreen::new);
        });

        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.VILLAGER_ORB.get(), PREDICATE_VILLAGER_ORB,
                    (stack, level, entity, seed) -> VillagerNbt.containsVillager(stack) ? 1 : 0);
        });
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TradesTooltip.class, ClientTradesTooltip::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VILLAGER_TRADING_STATION_BE.get(),
                VillagerTradingStationRenderer::new);
    }

}
