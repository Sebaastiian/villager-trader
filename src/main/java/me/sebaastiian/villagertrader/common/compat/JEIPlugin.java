package me.sebaastiian.villagertrader.common.compat;

import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import me.sebaastiian.villagertrader.setup.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(VillagerTrader.MODID, "jei_compat");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.VILLAGER_ORB.get(),
                (stack, context) -> {
                    if (VillagerNbt.containsVillager(stack)) {
                        return "has_villager";
                    }
                    return IIngredientSubtypeInterpreter.NONE;
                });
    }
}
