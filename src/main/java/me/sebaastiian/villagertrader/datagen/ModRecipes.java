package me.sebaastiian.villagertrader.datagen;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.setup.ModBlocks;
import me.sebaastiian.villagertrader.setup.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipes extends RecipeProvider {

    public ModRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModItems.VILLAGER_ORB.get())
                .pattern(" x ")
                .pattern("xex")
                .pattern(" x ")
                .define('x', Tags.Items.LEATHER)
                .define('e', Tags.Items.GEMS_EMERALD)
                .group(VillagerTrader.MODID)
                .unlockedBy("has_emerald", has(Tags.Items.GEMS_EMERALD))
                .save(consumer);

        ShapedRecipeBuilder.shaped((ModBlocks.VILLAGER_TRADING_STATION.get()))
                .pattern("ebe")
                .pattern("ene")
                .pattern("ebe")
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('b', Tags.Items.STORAGE_BLOCKS_EMERALD)
                .define('n', Tags.Items.NETHER_STARS)
                .group(VillagerTrader.MODID)
                .unlockedBy("has_villager_orb", has(ModItems.VILLAGER_ORB.get()))
                .save(consumer);
    }
}
