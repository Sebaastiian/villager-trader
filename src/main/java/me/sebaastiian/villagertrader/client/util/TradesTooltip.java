package me.sebaastiian.villagertrader.client.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TradesTooltip implements TooltipComponent {

    private final List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> trades;

    public TradesTooltip(
            List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> trades) {
        this.trades = trades;
    }

    public List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> getTrades() {
        return trades;
    }
}
