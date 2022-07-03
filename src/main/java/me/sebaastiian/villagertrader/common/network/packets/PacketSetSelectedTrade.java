package me.sebaastiian.villagertrader.common.network.packets;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetSelectedTrade {
    private final int selectedTrade;

    public PacketSetSelectedTrade(int selectedTrade) {
        this.selectedTrade = selectedTrade;
    }

    public static PacketSetSelectedTrade decode(FriendlyByteBuf buffer) {
        return new PacketSetSelectedTrade(buffer.readInt());
    }

    public static void encode(PacketSetSelectedTrade msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.selectedTrade);
    }

    public static class Handler {
        public static void handle(PacketSetSelectedTrade msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;

                AbstractContainerMenu container = sender.containerMenu;
                if (!(container instanceof VillagerTradingStationContainer)) return;

                VillagerTradingStationBlockEntity blockEntity = ((VillagerTradingStationContainer) container).blockEntity;
                blockEntity.setSelectedTrade(msg.selectedTrade);
                System.out.println("packet " + msg.selectedTrade);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
