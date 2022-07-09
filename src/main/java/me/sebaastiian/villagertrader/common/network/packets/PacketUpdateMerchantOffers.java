package me.sebaastiian.villagertrader.common.network.packets;

import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.inventory.containers.VillagerTradingStationContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateMerchantOffers {

    private final List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers;
    private final BlockPos pos;

    public PacketUpdateMerchantOffers(
            List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers, BlockPos pos) {
        this.offers = offers;
        this.pos = pos;
    }

    public static PacketUpdateMerchantOffers decode(FriendlyByteBuf buffer) {
        return new PacketUpdateMerchantOffers(buffer.readList(buf -> {
            ItemStack firstInput = buf.readItem();
            ItemStack secondInput = buf.readItem();
            ItemStack output = buf.readItem();
            return Pair.of(Pair.of(firstInput, secondInput), output);
        }), buffer.readBlockPos());
    }

    public static void encode(PacketUpdateMerchantOffers msg, FriendlyByteBuf buffer) {
        buffer.writeCollection(msg.offers, (buf, pairInputsOutput) -> {
            Pair<ItemStack, ItemStack> inputs = pairInputsOutput.getFirst();
            buf.writeItemStack(inputs.getFirst(), true);
            buf.writeItemStack(inputs.getSecond(), true);
            buf.writeItemStack(pairInputsOutput.getSecond(), true);
        });
        buffer.writeBlockPos(msg.pos);
    }

    public static class Handler {
        public static void handle(PacketUpdateMerchantOffers msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get()
                    .enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientPacketHandler(msg)));
            ctx.get().setPacketHandled(true);
        }
    }

    private static void clientPacketHandler(PacketUpdateMerchantOffers msg) {

        ClientLevel level = Minecraft.getInstance().level;
        if (!level.hasChunk(msg.pos.getX(), msg.pos.getZ())) return;

        BlockEntity blockEntity = level.getBlockEntity(msg.pos);
        if (!(blockEntity instanceof VillagerTradingStationBlockEntity)) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (!(player.containerMenu instanceof VillagerTradingStationContainer container)) return;

        container.offers = msg.offers;

    }
}
