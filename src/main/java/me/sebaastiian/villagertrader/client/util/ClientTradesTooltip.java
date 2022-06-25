package me.sebaastiian.villagertrader.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import me.sebaastiian.villagertrader.common.VillagerTrader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientTradesTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(VillagerTrader.MODID,
            "textures/gui/container/villager_orb.png");

    private final List<Pair<Pair<ItemStack, ItemStack>, ItemStack>> trades;

    public ClientTradesTooltip(TradesTooltip tooltip) {
        this.trades = tooltip.getTrades();
    }

    @Override
    public int getHeight() {
        return trades.size() * 18 + 8;
    }

    @Override
    public int getWidth(Font font) {
        return 3 * 18;
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer,
                            int blitOffset) {
        for (int i = 0; i < trades.size(); i++) {
            Pair<ItemStack, ItemStack> inputs = trades.get(i).getFirst();
            ItemStack result = trades.get(i).getSecond();

            itemRenderer.renderAndDecorateItem(inputs.getFirst(), mouseX, mouseY + (i * 18), 0);
            itemRenderer.renderGuiItemDecorations(font, inputs.getFirst(), mouseX, mouseY + (i * 18));

            if (!inputs.getSecond().isEmpty()) {
                itemRenderer.renderAndDecorateItem(inputs.getSecond(), mouseX + 18 + 5, mouseY + (i * 18), 0);
                itemRenderer.renderGuiItemDecorations(font, inputs.getSecond(), mouseX + 18 + 5,
                        mouseY + (i * 18));
            }
            blitArrow(poseStack, mouseX + 36 + 8, mouseY + (i * 18) + 4, blitOffset);

            itemRenderer.renderAndDecorateItem(result, mouseX + 54 + 5, mouseY + (i * 18), 0);
            itemRenderer.renderGuiItemDecorations(font, result, mouseX + 54 + 5, mouseY + (i * 18));
        }
    }

    private void blitArrow(PoseStack poseStack, int x, int y, int blitOffset) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(poseStack, x, y, blitOffset, 0F, 0F, 10, 9, 16, 16);
    }
}
