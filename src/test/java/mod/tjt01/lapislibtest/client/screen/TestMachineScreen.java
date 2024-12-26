package mod.tjt01.lapislibtest.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.menu.TestMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

public class TestMachineScreen extends AbstractContainerScreen<TestMachineMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LapisLibTest.MODID, "textures/gui/container/test_machine.png");

    public TestMachineScreen(TestMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected static void fluidTile(GuiGraphics guiGraphics, int x, int y, int blitOffset, int w, int h, TextureAtlasSprite sprite) {
        int offset = 16 - h;
        int x2 = x + w;
        int y2 = y + h;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(x, y2, blitOffset).uv(sprite.getU0(), sprite.getV1()).endVertex();
        bufferBuilder.vertex(x2, y2, blitOffset).uv(sprite.getU1(), sprite.getV1()).endVertex();
        bufferBuilder.vertex(x2, y, blitOffset).uv(sprite.getU1(), sprite.getV(offset)).endVertex();
        bufferBuilder.vertex(x, y, blitOffset).uv(sprite.getU0(), sprite.getV(offset)).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int totalProgress = this.menu.blockEntity.totalProgress;

        if (totalProgress >= 0) {
            int progress = Mth.floor((float) this.menu.blockEntity.progress / (float) totalProgress * 24.0F);
            guiGraphics.blit(TEXTURE, +79, y + 34, 176, 0, progress, 17);
        }

        FluidStack fluidStack = this.menu.fluid;

        if (mouseX >= this.leftPos + 34 && mouseX <= this.leftPos + 49 && mouseY >= this.topPos + 17 && mouseY <= this.topPos + 68) {
            this.setTooltipForNextRenderPass(
                    fluidStack.isEmpty()
                            ? Component.translatable("lapislib_test.gui.fluid.empty", 4000)
                            : Component.translatable(
                            "lapislib_test.gui.fluid",
                            fluidStack.getAmount(),
                            4000,
                            fluidStack.getDisplayName()
                    )
            );
        }

        if (!fluidStack.isEmpty()) {
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Fluid fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid);
            int color = renderProps.getTintColor();
            float red = (color >> 16 & 0xFF) / 255.0F;
            float green = (color >> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            float alpha = (color >> 24 & 0xFF) / 255.0F;
            RenderSystem.setShaderColor(red, green, blue, alpha);
            Function<ResourceLocation, TextureAtlasSprite> textureAtlas = this.getMinecraft().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite still = textureAtlas.apply(renderProps.getStillTexture(fluidStack));

            int height = Mth.floor(52.0F * (fluidStack.getAmount() / 4000.0F));

            for (int i = 69 - 16; i > 69 - height; i -= 16) {
                fluidTile(guiGraphics, this.leftPos + 34, this.topPos + i, 0, 16, 16, still);
            }
            int topTile = height % 16;
            if (topTile > 0) {
                fluidTile(guiGraphics, this.leftPos + 34, this.topPos + 69 - height, 0, 16, topTile, still);
            }
        }
    }
}
