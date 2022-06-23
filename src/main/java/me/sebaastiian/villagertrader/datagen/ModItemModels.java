package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModels extends ItemModelProvider {
    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, VillagerTrader.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.VILLAGER_ORB);
    }

    private void simpleItem(RegistryObject<Item> item) {
        String path = item.get().getRegistryName().getPath();
        singleTexture(path, mcLoc("item/generated"), "layer0",
                modLoc("item/" + path));
    }
}
