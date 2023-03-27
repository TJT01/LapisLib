package mod.tjt01.lapislib.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.tjt01.lapislib.util.ColorSequence;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public class ExtraGuiUtils {
    public static void fillHorizontalGradient(
            PoseStack poseStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo, int blitOffset
    ) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.last().pose();
        float a1 = (colorFrom >> 24 & 0xFF) / 255.0F;
        float r1 = (colorFrom >> 16 & 0xFF) / 255.0F;
        float g1 = (colorFrom >> 8 & 0xFF) / 255.0F;
        float b1 = (colorFrom & 0xFF) / 255.0F;

        float a2 = (colorTo >> 24 & 0xFF) / 255.0F;
        float r2 = (colorTo >> 16 & 0xFF) / 255.0F;
        float g2 = (colorTo >> 8 & 0xFF) / 255.0F;
        float b2 = (colorTo & 0xFF) / 255.0F;

        bufferBuilder.vertex(matrix, x2, y1, blitOffset).color(r2, g2, b2, a2).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, blitOffset).color(r1, g1, b1, a1).endVertex();
        bufferBuilder.vertex(matrix, x1, y2, blitOffset).color(r1, g1, b1, a1).endVertex();
        bufferBuilder.vertex(matrix, x2, y2, blitOffset).color(r2, g2, b2, a2).endVertex();

        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sequenceVerticalGradient(
            PoseStack poseStack, int x1, int y1, int x2, int y2, ColorSequence sequence, int blitOffset
    ) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < sequence.getLength() - 1; i++) {
            ColorSequence.Keypoint keypointA = sequence.get(i);
            ColorSequence.Keypoint keypointB = sequence.get(i + 1);

            int colorFrom = keypointA.color();
            int colorTo = keypointB.color();

            float a1 = (colorFrom >> 24 & 0xFF) / 255.0F;
            float r1 = (colorFrom >> 16 & 0xFF) / 255.0F;
            float g1 = (colorFrom >> 8 & 0xFF) / 255.0F;
            float b1 = (colorFrom & 0xFF) / 255.0F;

            float a2 = (colorTo >> 24 & 0xFF) / 255.0F;
            float r2 = (colorTo >> 16 & 0xFF) / 255.0F;
            float g2 = (colorTo >> 8 & 0xFF) / 255.0F;
            float b2 = (colorTo & 0xFF) / 255.0F;

            float start = Mth.lerp((float) keypointA.time(), x1, x2);
            float end = Mth.lerp((float) keypointB.time(), x1, x2);

            bufferBuilder.vertex(matrix, x2, start, blitOffset).color(r1, g1, b1, a1).endVertex();
            bufferBuilder.vertex(matrix, x1, start, blitOffset).color(r1, g1, b1, a1).endVertex();
            bufferBuilder.vertex(matrix, x1, end, blitOffset).color(r2, g2, b2, a2).endVertex();
            bufferBuilder.vertex(matrix, x2, end, blitOffset).color(r2, g2, b2, a2).endVertex();
        }

        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sequenceHorizontalGradient(
            PoseStack poseStack, int x1, int y1, int x2, int y2, ColorSequence sequence, int blitOffset
    ) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < sequence.getLength() - 1; i++) {
            ColorSequence.Keypoint keypointA = sequence.get(i);
            ColorSequence.Keypoint keypointB = sequence.get(i + 1);

            int colorFrom = keypointA.color();
            int colorTo = keypointB.color();

            float a1 = (colorFrom >> 24 & 0xFF) / 255.0F;
            float r1 = (colorFrom >> 16 & 0xFF) / 255.0F;
            float g1 = (colorFrom >> 8 & 0xFF) / 255.0F;
            float b1 = (colorFrom & 0xFF) / 255.0F;

            float a2 = (colorTo >> 24 & 0xFF) / 255.0F;
            float r2 = (colorTo >> 16 & 0xFF) / 255.0F;
            float g2 = (colorTo >> 8 & 0xFF) / 255.0F;
            float b2 = (colorTo & 0xFF) / 255.0F;

            float start = Mth.lerp((float) keypointA.time(), x1, x2);
            float end = Mth.lerp((float) keypointB.time(), x1, x2);

            bufferBuilder.vertex(matrix, end, y1, blitOffset).color(r2, g2, b2, a2).endVertex();
            bufferBuilder.vertex(matrix, start, y1, blitOffset).color(r1, g1, b1, a1).endVertex();
            bufferBuilder.vertex(matrix, start, y2, blitOffset).color(r1, g1, b1, a1).endVertex();
            bufferBuilder.vertex(matrix, end, y2, blitOffset).color(r2, g2, b2, a2).endVertex();
        }

        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
