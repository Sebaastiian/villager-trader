package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.setup.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTags extends BlockTagsProvider {
    public ModBlockTags(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, VillagerTrader.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.VILLAGER_TRADING_STATION.get());
    }

    @Override
    public String getName() {
        return "VillagerTrader BlockTags";
    }
}
