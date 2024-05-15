package mod.tjt01.lapislibtest.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.menu.TestCraftingMenu;
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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth)/2;
        int y = (this.height - this.imageHeight)/2;
        this.blit(pPoseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
