package me.sebaastiian.villagertrader.common.items;

import me.sebaastiian.villagertrader.client.util.TradesTooltip;
import me.sebaastiian.villagertrader.common.util.VillagerNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VillagerOrbItem extends Item {

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

        if (!VillagerNbt.containsVillager(itemInHand))
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        CompoundTag villagerData = itemInHand.getTag().getCompound(VillagerNbt.COMPOUND_DATA);
        villagerData.remove("Pos"); // Don't spawn at the old location

        itemInHand.getTag().remove(VillagerNbt.COMPOUND_DATA);

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

        if (VillagerNbt.containsVillager(stack))
            return InteractionResult.FAIL;

        stack.getTag().put(VillagerNbt.COMPOUND_DATA, interactionTarget.serializeNBT());
        player.setItemInHand(usedHand, stack);

        interactionTarget.remove(Entity.RemovalReason.DISCARDED);
        player.inventoryMenu.broadcastChanges();

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (VillagerNbt.containsVillager(stack)) {
            if (hasOffers(stack.getTag().getCompound(VillagerNbt.COMPOUND_DATA)) && !Screen.hasShiftDown()) {
                MutableComponent holdShiftInfo = new TranslatableComponent(
                        this.getDescriptionId() + ".hold_shift").withStyle(ChatFormatting.DARK_GRAY);
                tooltipComponents.add(holdShiftInfo);
            }
            CompoundTag villagerData = stack.getTag().getCompound(VillagerNbt.COMPOUND_DATA);
            Villager villager = EntityType.VILLAGER.create(level);
            villager.load(villagerData);

            ResourceLocation profName = villager.getVillagerData().getProfession().getRegistryName();
            TranslatableComponent villagerInfo = new TranslatableComponent(
                    villager.getType().getDescriptionId() + '.' + (!"minecraft".equals(
                            profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());

            tooltipComponents.add(
                    new TranslatableComponent(this.getDescriptionId() + ".tooltip_filled",
                            villagerInfo.withStyle(ChatFormatting.GREEN)).withStyle(
                            ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(new TranslatableComponent(this.getDescriptionId() + ".tooltip_empty").withStyle(
                    ChatFormatting.GRAY));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!Screen.hasShiftDown()) return super.getTooltipImage(stack);
        if (!VillagerNbt.containsVillager(stack))
            return super.getTooltipImage(stack);

        CompoundTag villagerData = stack.getTag().getCompound(VillagerNbt.COMPOUND_DATA);
        if (hasOffers(villagerData)) {
            return Optional.of(new TradesTooltip(VillagerNbt.getOffers(villagerData)));
        }

        return super.getTooltipImage(stack);
    }

    private boolean hasOffers(CompoundTag villagerData) {
        return villagerData.contains("Offers", 10);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        super.fillItemCategory(category, items);

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Villager villager = EntityType.VILLAGER.create(level);

        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().put(VillagerNbt.COMPOUND_DATA, villager.serializeNBT());
        items.add(stack);
    }
}
