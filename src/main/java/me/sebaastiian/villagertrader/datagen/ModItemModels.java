package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.setup.ClientSetup;
import me.sebaastiian.villagertrader.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModels extends ItemModelProvider {
    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, VillagerTrader.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemModelBuilder villagerOrbModel = simpleItem(ModItems.VILLAGER_ORB);

        ItemModelBuilder villagerOrbFullModel = singleTexture(
                ModItems.VILLAGER_ORB.get().getRegistryName().getPath() + "_full",
                modLoc("item/villager_orb"),
                "layer0", modLoc("item/villager_orb_full"));

        villagerOrbModel.override()
                .predicate(ClientSetup.PREDICATE_VILLAGER_ORB, 1F)
                .model(villagerOrbFullModel).end();

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        String path = item.get().getRegistryName().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0",
                modLoc("item/" + path));
    }
}
