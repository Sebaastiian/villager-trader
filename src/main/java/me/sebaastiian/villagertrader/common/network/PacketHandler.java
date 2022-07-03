package me.sebaastiian.villagertrader.common.network;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.network.packets.PacketUpdateMerchantOffers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VillagerTrader.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        INSTANCE.registerMessage(id++, PacketUpdateMerchantOffers.class, PacketUpdateMerchantOffers::encode,
                PacketUpdateMerchantOffers::decode,
                PacketUpdateMerchantOffers.Handler::handle);

    }
}
