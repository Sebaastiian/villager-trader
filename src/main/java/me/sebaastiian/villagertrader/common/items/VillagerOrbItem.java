package me.sebaastiian.villagertrader.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class VillagerOrbItem extends Item {

    public static final String COMPOUND_DATA = "villager_data";

    public VillagerOrbItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        Player player = context.getPlayer();
        ItemStack itemInHand = context.getItemInHand();
        Level level = context.getLevel();
        Direction clickedFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();

        if (!containsVillager(itemInHand))
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        CompoundTag villagerData = itemInHand.getTag().getCompound(COMPOUND_DATA);
        villagerData.remove("Pos"); // Don't spawn at the old location

        itemInHand.getTag().remove(COMPOUND_DATA);

        BlockPos spawnPos = clickedPos.immutable();
        if (!level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).isEmpty())
            spawnPos = spawnPos.relative(clickedFace);

        Villager villager = EntityType.VILLAGER.create(level);
        villager.load(villagerData);
        villager.absMoveTo(spawnPos.getX() + .5, spawnPos.getY(), spawnPos.getZ() + .5, 0, 0);
        level.addFreshEntity(villager);

        player.inventoryMenu.broadcastChanges();

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget,
                                                  InteractionHand usedHand) {
        if (usedHand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        if (!interactionTarget.isAlive())
            return InteractionResult.PASS;

        if (interactionTarget.level.isClientSide)
            return InteractionResult.PASS;

        if (!(interactionTarget instanceof Villager))
            return InteractionResult.FAIL;

        if (containsVillager(stack))
            return InteractionResult.FAIL;

        stack.getTag().put(COMPOUND_DATA, interactionTarget.serializeNBT());
        player.setItemInHand(usedHand, stack);

        interactionTarget.remove(Entity.RemovalReason.DISCARDED);
        player.inventoryMenu.broadcastChanges();

        return InteractionResult.SUCCESS;
    }

    public static boolean containsVillager(ItemStack stack) {
        return stack.getOrCreateTag().contains(COMPOUND_DATA);
    }
}
