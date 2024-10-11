package mod.tjt01.lapislibtest.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.menu.TestMachineMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

    protected static void fluidTile(PoseStack poseStack, int x, int y, int blitOffset, int w, int h, TextureAtlasSprite sprite) {
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth)/2;
        int y = (this.height - this.imageHeight)/2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        FluidStack fluidStack = this.menu.fluid;
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
                fluidTile(poseStack,  this.leftPos + 34, this.topPos + i, this.getBlitOffset(), 16, 16, still);
            }
            int topTile = height % 16;
            if (topTile > 0) {
                fluidTile(poseStack, this.leftPos + 34, this.topPos + 69 - height, this.getBlitOffset(), 16, topTile, still);
            }
        }
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int x, int y) {
        if (x >= this.leftPos + 34 && x <= this.leftPos + 49 && y >= this.topPos + 17 && y <= this.topPos + 68) {
            FluidStack fluid = this.menu.fluid;
            this.renderTooltip(
                    poseStack,
                    fluid.isEmpty()
                            ? Component.translatable("lapislib_test.gui.fluid.empty", 4000)
                            : Component.translatable(
                                    "lapislib_test.gui.fluid",
                            fluid.getAmount(),
                                    4000,
                                    fluid.getDisplayName()
                            ),
                    x,
                    y
            );
        } else {
            super.renderTooltip(poseStack, x, y);
        }
    }
}
