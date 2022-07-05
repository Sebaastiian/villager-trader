package me.sebaastiian.villagertrader.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.client.util.ItemRenderHelper;
import me.sebaastiian.villagertrader.common.VillagerTrader;
import me.sebaastiian.villagertrader.common.config.VillagerTraderConfig;
import me.sebaastiian.villagertrader.common.containers.VillagerTradingStationContainer;
import me.sebaastiian.villagertrader.common.network.PacketHandler;
import me.sebaastiian.villagertrader.common.network.packets.PacketSetSelectedTrade;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class VillagerTradingStationScreen extends AbstractContainerScreen<VillagerTradingStationContainer> {

    private final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(VillagerTrader.MODID,
            "textures/gui/container/villager_trading_station.png");

    private final VillagerTradingStationScreen.TradeOfferButton[] tradeOfferButtons = new VillagerTradingStationScreen.TradeOfferButton[9];

    public VillagerTradingStationScreen(VillagerTradingStationContainer pMenu,
                                        Inventory pPlayerInventory,
                                        Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 298;
        this.imageHeight = 190;
        this.inventoryLabelX = 107;
        this.inventoryLabelY = this.imageHeight - 117;
        this.titleLabelX = 100;
        this.titleLabelY = 8;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 5;

        for (int l = 0; l < 9; ++l) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(
                    new VillagerTradingStationScreen.TradeOfferButton(i + 5, k, l, (button) -> {
                        if (button instanceof VillagerTradingStationScreen.TradeOfferButton) {
                            menu.setSelectedTrade(((TradeOfferButton) button).getIndex());
                            PacketHandler.INSTANCE.sendToServer(new PacketSetSelectedTrade(menu.getSelectedTrade()));
                        }
                    }));
            k += 20;
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (!this.menu.offers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 3 + 1;
            int l = i + 5 + 5;

            for (Pair<Pair<ItemStack, ItemStack>, ItemStack> offer : this.menu.offers) {
                this.itemRenderer.blitOffset = 100.0F;
                int j1 = k + 2;
                Pair<ItemStack, ItemStack> inputs = offer.getFirst();
                ItemStack result = offer.getSecond();
                this.itemRenderer.renderAndDecorateFakeItem(inputs.getFirst(), l, j1);
                this.itemRenderer.renderGuiItemDecorations(this.font, inputs.getFirst(), l, j1);

                if (!inputs.getSecond().isEmpty()) {
                    this.itemRenderer.renderAndDecorateFakeItem(inputs.getSecond(), i + 5 + 35, j1);
                    this.itemRenderer.renderGuiItemDecorations(this.font, inputs.getSecond(), i + 5 + 35, j1);
                }

                RenderSystem.enableBlend();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
                blit(matrixStack, i + 5 + 35 + 20, j1 + 3, this.getBlitOffset(), 15.0F, 194.0F, 10, 9, 512, 256);

                this.itemRenderer.renderAndDecorateFakeItem(result, i + 5 + 68, j1);
                this.itemRenderer.renderGuiItemDecorations(this.font, result, i + 5 + 68, j1);
                this.itemRenderer.blitOffset = 0.0F;
                k += 20;
            }

            if (menu.getSelectedTrade() >= 0 && menu.getSelectedTrade() < menu.offers.size()) {
                Pair<Pair<ItemStack, ItemStack>, ItemStack> trade = this.menu.offers.get(menu.getSelectedTrade());
                Pair<ItemStack, ItemStack> inputs = trade.getFirst();
                ItemRenderHelper.renderFakeItemTransparent(inputs.getFirst(), leftPos + menu.slots.get(1).x,
                        topPos + menu.slots.get(1).y, 0.4F);
                ItemRenderHelper.renderFakeItemTransparent(inputs.getSecond(), leftPos + menu.slots.get(2).x,
                        topPos + menu.slots.get(2).y, 0.4F);
            }
        }

        for (TradeOfferButton button : this.tradeOfferButtons) {
            if (button.isHoveredOrFocused()) {
                button.renderToolTip(matrixStack, mouseX, mouseY);
            }
            button.visible = button.index < this.menu.offers.size();
        }

        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        this.blit(poseStack, relX, relY, this.getBlitOffset(), 0, 0, this.imageWidth, this.imageHeight, 512, 256);

        int progress = menu.getProgress();
        int interpolated = (int) ((float) progress / VillagerTraderConfig.server.tradingStationTradeTime.get() * 24F);
        this.blit(poseStack, leftPos + 185, topPos + 37, 317, 104, interpolated + 1, 16, 512, 256);
    }

    class TradeOfferButton extends Button {

        final int index;

        public TradeOfferButton(int pX, int pY, int index, OnPress pOnPress) {
            super(pX, pY, 89, 20, TextComponent.EMPTY, pOnPress);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }


        @Override
        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            Pair<ItemStack, ItemStack> inputs = VillagerTradingStationScreen.this.menu.offers
                    .get(this.index)
                    .getFirst();
            if (pMouseX < this.x + 20) {
                VillagerTradingStationScreen.this.renderTooltip(pPoseStack, inputs.getFirst(), pMouseX, pMouseY);
            } else if (pMouseX < this.x + 50 && pMouseX > this.x + 30) {
                if (!inputs.getSecond().isEmpty()) {
                    VillagerTradingStationScreen.this.renderTooltip(pPoseStack, inputs.getSecond(), pMouseX, pMouseY);
                }
            } else if (pMouseX > this.x + 65) {
                ItemStack result = VillagerTradingStationScreen.this.menu.offers
                        .get(this.index)
                        .getSecond();
                VillagerTradingStationScreen.this.renderTooltip(pPoseStack, result, pMouseX, pMouseY);
            }
        }
    }
}
