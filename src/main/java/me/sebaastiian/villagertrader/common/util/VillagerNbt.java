package me.sebaastiian.villagertrader.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.List;
import java.util.stream.Collectors;

public class VillagerNbt {
    public static final String COMPOUND_DATA = "villager_data";

    public static boolean containsVillager(ItemStack stack) {
        return stack.getOrCreateTag().contains(COMPOUND_DATA);
    }

    public static List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> tryGetOffers(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag().getCompound(COMPOUND_DATA);
        if (!tag.contains("Offers")) return List.of();

        MerchantOffers offers = new MerchantOffers(tag.getCompound("Offers"));

        return offers.stream()
                .map(offer -> new Pair<>(new Pair<>(offer.getCostA(), offer.getCostB()), offer.getResult()))
                .collect(Collectors.toList());
    }

    public static List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> getOffers(CompoundTag villagerData) {
        MerchantOffers offers = new MerchantOffers(villagerData.getCompound("Offers"));

        return offers.stream()
                .map(offer -> new Pair<>(new Pair<>(offer.getCostA(), offer.getCostB()), offer.getResult()))
                .collect(Collectors.toList());
    }
}
