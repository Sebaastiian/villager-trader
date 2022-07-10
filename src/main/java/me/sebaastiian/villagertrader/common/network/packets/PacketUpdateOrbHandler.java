package me.sebaastiian.villagertrader.common.network.packets;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.setup.ModBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketUpdateOrbHandler {

    private final CompoundTag tag;
    private final BlockPos pos;

    public PacketUpdateOrbHandler(CompoundTag tag, BlockPos pos) {
        this.tag = tag;
        this.pos = pos;
    }

    public static PacketUpdateOrbHandler decode(FriendlyByteBuf buffer) {
        return new PacketUpdateOrbHandler(buffer.readNbt(), buffer.readBlockPos());
    }

    public static void encode(PacketUpdateOrbHandler msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.tag);
        buffer.writeBlockPos(msg.pos);
    }

    public static class Handler {
        public static void handle(PacketUpdateOrbHandler msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get()
                    .enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientPacketHandler(msg)));
            ctx.get().setPacketHandled(true);
        }
    }

    private static void clientPacketHandler(PacketUpdateOrbHandler msg) {

        ClientLevel level = Minecraft.getInstance().level;
        Optional<VillagerTradingStationBlockEntity> blockEntity = level.getBlockEntity(msg.pos,
                ModBlockEntities.VILLAGER_TRADING_STATION_BE.get());

        if (blockEntity.isEmpty()) return;

        VillagerTradingStationBlockEntity be = blockEntity.get();
        be.getOrbHandler().deserializeNBT(msg.tag);
    }
}
