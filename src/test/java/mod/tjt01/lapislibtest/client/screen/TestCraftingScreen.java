package mod.tjt01.lapislibtest.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.menu.TestCraftingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TestCraftingScreen extends AbstractContainerScreen<TestCraftingMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LapisLibTest.MODID, "textures/gui/container/test_crafting.png");

    public TestCraftingScreen(TestCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth)/2;
        int y = (this.height - this.imageHeight)/2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
