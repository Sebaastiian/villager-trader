package me.sebaastiian.villagertrader.common.containers;

import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.handlers.VillagerResultHandlerSlot;
import me.sebaastiian.villagertrader.common.handlers.VillagerTradingStationItemHandler;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import me.sebaastiian.villagertrader.common.network.PacketHandler;
import me.sebaastiian.villagertrader.common.network.packets.PacketUpdateMerchantOffers;
import me.sebaastiian.villagertrader.setup.ModBlocks;
import me.sebaastiian.villagertrader.setup.ModContainers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class VillagerTradingStationContainer extends AbstractContainerMenu {

    public static int SLOTS = 4;
    public Player player;
    private IItemHandler playerInventory;

    public VillagerTradingStationBlockEntity blockEntity;
    public List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = List.of();

    private ItemStack previousItemInFirstSlot = ItemStack.EMPTY;

    public VillagerTradingStationContainer(
            int containerId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this((VillagerTradingStationBlockEntity) player.level.getBlockEntity(extraData.readBlockPos()), containerId,
                playerInventory, player, new VillagerTradingStationItemHandler(SLOTS));
    }

    public VillagerTradingStationContainer(VillagerTradingStationBlockEntity blockEntity, int containerId,
                                           Inventory playerInventory, Player player,
                                           VillagerTradingStationItemHandler handler) {
        super(ModContainers.VILLAGER_TRADING_STATION_CONTAINER.get(), containerId);
        this.player = player;
        this.blockEntity = blockEntity;
        this.playerInventory = new InvWrapper(playerInventory);

        this.addSlot(new SlotItemHandler(handler, 0, 276, 10));
        this.addSlot(new SlotItemHandler(handler, 1, 136, 37));
        this.addSlot(new SlotItemHandler(handler, 2, 162, 37));
        this.addSlot(new VillagerResultHandlerSlot(handler, 3, 220, 38));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(this.playerInventory, j + i * 9 + 9, 108 + j * 18, 85 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new SlotItemHandler(this.playerInventory, k, 108 + k * 18, 143));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(player.level, blockEntity.getBlockPos()), player,
                ModBlocks.VILLAGER_TRADING_STATION.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < SLOTS) { // Move from my slots to player
                if (!this.moveItemStackTo(stack, SLOTS, 36 + SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() instanceof VillagerOrbItem) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 1, SLOTS, false)) {
                    return ItemStack.EMPTY;
                } else if (index < 28) {
                    if (!this.moveItemStackTo(stack, 27 + SLOTS, 36 + SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 36 + SLOTS && !this.moveItemStackTo(stack, SLOTS, 27 + SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        ItemStack currentItemInFirstSlot = slots.get(0).getItem();
        if (previousItemInFirstSlot != currentItemInFirstSlot) {
            previousItemInFirstSlot = currentItemInFirstSlot;

            if (VillagerOrbItem.containsVillager(currentItemInFirstSlot)) {
                CompoundTag villagerData = currentItemInFirstSlot.getTag().getCompound(VillagerOrbItem.COMPOUND_DATA);
                List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = VillagerOrbItem.getOffers(
                        villagerData);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PacketUpdateMerchantOffers(offers, blockEntity.getBlockPos()));
            } else {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PacketUpdateMerchantOffers(List.of(), blockEntity.getBlockPos()));
            }
        }
    }
}

