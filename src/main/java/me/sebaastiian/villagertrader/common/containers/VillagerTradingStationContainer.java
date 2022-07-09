package me.sebaastiian.villagertrader.common.containers;

import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.handlers.CustomEnergyStorage;
import me.sebaastiian.villagertrader.common.handlers.CustomItemHandler;
import me.sebaastiian.villagertrader.common.handlers.VillagerResultHandlerSlot;
import me.sebaastiian.villagertrader.common.items.VillagerOrbItem;
import me.sebaastiian.villagertrader.common.network.PacketHandler;
import me.sebaastiian.villagertrader.common.network.packets.PacketUpdateMerchantOffers;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import me.sebaastiian.villagertrader.setup.ModBlocks;
import me.sebaastiian.villagertrader.setup.ModContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity.*;

public class VillagerTradingStationContainer extends AbstractContainerMenu {

    public static int SLOTS = ORB_SLOTS + INPUT_SLOTS + OUTPUT_SLOTS;
    public Player player;
    private IItemHandler playerInventory;

    public VillagerTradingStationBlockEntity blockEntity;
    public List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = List.of();

    private ItemStack previousItemInFirstSlot = ItemStack.EMPTY;

    private final CustomEnergyStorage energyStorage;

    public VillagerTradingStationContainer(
            int containerId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this((VillagerTradingStationBlockEntity) player.level.getBlockEntity(extraData.readBlockPos()), containerId,
                playerInventory, player, new CustomItemHandler<>(SLOTS),
                new CustomEnergyStorage(200_000, 500));
    }

    public VillagerTradingStationContainer(VillagerTradingStationBlockEntity blockEntity, int containerId,
                                           Inventory playerInventory, Player player,
                                           IItemHandler handler,
                                           CustomEnergyStorage energyStorage) {
        super(ModContainers.VILLAGER_TRADING_STATION_CONTAINER.get(), containerId);
        this.player = player;
        this.blockEntity = blockEntity;
        LazyOptional<IItemHandler> playerInv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (playerInv.isPresent()) {
            this.playerInventory = playerInv.orElse(new InvWrapper(playerInventory));
        }
        this.energyStorage = energyStorage;

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

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                int energyStored = getEnergy() & 0xffff0000;
                energyStorage.setEnergy(energyStored + (value & 0xffff));
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int energyStored = getEnergy() & 0x0000ffff;
                energyStorage.setEnergy(energyStored | (value << 16));
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getSelectedTrade();
            }

            @Override
            public void set(int value) {
                setSelectedTrade(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getProgress();
            }

            @Override
            public void set(int value) {
                setProgress(value);
            }
        });
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public void setSelectedTrade(int selectedTrade) {
        blockEntity.setSelectedTrade(selectedTrade);
    }

    public int getSelectedTrade() {
        return blockEntity.getSelectedTrade();
    }

    public void setProgress(int progress) {
        blockEntity.setProgress(progress);
    }

    public int getProgress() {
        return blockEntity.getProgress();
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(player.level, blockEntity.getBlockPos()), player,
                ModBlocks.VILLAGER_TRADING_STATION.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
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

            if (VillagerNbt.containsVillager(currentItemInFirstSlot)) {
                List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = VillagerNbt.tryGetOffers(
                        currentItemInFirstSlot);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PacketUpdateMerchantOffers(offers, blockEntity.getBlockPos()));
                blockEntity.setOffers(offers);
            } else {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PacketUpdateMerchantOffers(List.of(), blockEntity.getBlockPos()));
                blockEntity.setOffers(List.of());
            }
        }
    }
}

