package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.setup.ModBlocks;
import me.sebaastiian.villagertrader.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import static me.sebaastiian.villagertrader.setup.ModSetup.TAB_NAME;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(DataGenerator gen, String locale) {
        super(gen, VillagerTrader.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + TAB_NAME, "Villager Trader");
        add("screen.villagertrader.villager_trading_station", "Villager Trading Station");
        add(ModItems.VILLAGER_ORB.get(), "Villager Orb");

        add(ModItems.VILLAGER_ORB.get().getDescriptionId() + ".hold_shift", "Hold Shift for Villager Trades");
        add(ModItems.VILLAGER_ORB.get().getDescriptionId() + ".tooltip_filled", "Contains %s");
        add(ModItems.VILLAGER_ORB.get().getDescriptionId() + ".tooltip_empty", "Empty");


        add(ModBlocks.VILLAGER_TRADING_STATION.get(), "Villager Trading Station");
    }
}
