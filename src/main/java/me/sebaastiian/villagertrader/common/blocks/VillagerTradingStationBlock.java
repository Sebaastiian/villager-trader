package me.sebaastiian.villagertrader.common.blocks;

import me.sebaastiian.villagertrader.common.blockentities.VillagerTradingStationBlockEntity;
import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import me.sebaastiian.villagertrader.common.handlers.VillagerTradingStationItemHandler;
import me.sebaastiian.villagertrader.setup.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class VillagerTradingStationBlock extends Block implements EntityBlock {
    public static final String SCREEN_TRANSLATION_KEY = "screen.villagertrader.villager_trading_station";

    public VillagerTradingStationBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof VillagerTradingStationBlockEntity)) return InteractionResult.PASS;

        LazyOptional<IItemHandler> cap = blockEntity.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

        cap.ifPresent(h -> {
            VillagerTradingStationBlockEntity be = (VillagerTradingStationBlockEntity) blockEntity;
            MenuProvider menuProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return new TranslatableComponent(SCREEN_TRANSLATION_KEY);
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                    return new VillagerTradingStationContainer(be,
                            containerId, playerInventory, player, (VillagerTradingStationItemHandler) h);
                }
            };

            NetworkHooks.openGui((ServerPlayer) player, menuProvider, buf -> {
                buf.writeBlockPos(pos);
            });
        });
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VillagerTradingStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
                                                                  BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide || pBlockEntityType != ModBlockEntities.VILLAGER_TRADING_STATION_BE.get()) return null;

        return (level, blockPos, blockState, blockEntity) -> VillagerTradingStationBlockEntity.serverTick(level,
                blockPos, blockState, (VillagerTradingStationBlockEntity) blockEntity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity != null) {
                LazyOptional<IItemHandler> cap = tileEntity.getCapability(
                        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

                cap.ifPresent(handler -> {
                    for (int i = 0; i < handler.getSlots(); ++i) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                                handler.getStackInSlot(i));
                    }
                });
            }

        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
