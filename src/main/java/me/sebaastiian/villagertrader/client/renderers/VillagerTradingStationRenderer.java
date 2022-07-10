package me.sebaastiian.villagertrader.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VillagerTradingStationRenderer implements BlockEntityRenderer<VillagerTradingStationBlockEntity> {

    public VillagerTradingStationRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@NotNull VillagerTradingStationBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack orb = blockEntity.getOrbHandler().getStackInSlot(0);
        if (orb.isEmpty()) return;
        if (!VillagerNbt.containsVillager(orb)) return;

        poseStack.pushPose();

        poseStack.translate(.5, 0, .5);
        poseStack.scale(.3F, .3F, .3F);

        CompoundTag villagerData = orb.getTag().getCompound(VillagerNbt.COMPOUND_DATA);
        villagerData.remove("Pos");
        villagerData.remove("Rotation"); // If we don't remove rotation, the villager will show extreme rotating

        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        Level level = blockEntity.getLevel();
        Villager villager = EntityType.VILLAGER.create(level);
        villager.load(villagerData);
        List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> offers = VillagerNbt.getOffers(villagerData);
        Pair<Pair<ItemStack, ItemStack>, ItemStack> offer = offers.get(blockEntity.getSelectedTrade());
        ItemStack result = offer.getSecond();
        villager.setItemInHand(InteractionHand.MAIN_HAND, result);

        entityRenderDispatcher.render(villager, 0, 0, 0, 0F, partialTick,
                poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
