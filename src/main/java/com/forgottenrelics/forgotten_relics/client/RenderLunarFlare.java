package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityLunarFlare;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Renders the Lunar Flare with ONLY the spiky flash effect.
 * Green/cyan theme matching the lunar sparkle particles.
 * No billboard/sphere - just the eldritch flash glow.
 */
public class RenderLunarFlare extends Render<EntityLunarFlare> {

    private Random random = new Random();

    public RenderLunarFlare(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityLunarFlare entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f1 = (float) entity.ticksExisted / 80.0F;
        float f2 = 0.0F;

        this.random.setSeed(187L);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GlStateManager.pushMatrix();

        for (int i = 0; i < 12; ++i) {
            GL11.glRotatef(this.random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(this.random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(this.random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(this.random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.random.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);

            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            float fa = this.random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = this.random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            float scale = 30.0F / ((float) Math.min(entity.ticksExisted, 10) / 10.0F);
            fa /= scale;
            f4 /= scale;

            // Center: bright white-green flash
            buffer.pos(0.0, 0.0, 0.0).color(0.9F, 1.0F, 0.85F, 1.0F - f2).endVertex();
            // Outer: dark green
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.25F, 0.12F, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.25F, 0.12F, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(0.1F, 0.25F, 0.12F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.25F, 0.12F, 1.0F - f2).endVertex();

            tessellator.draw();
        }

        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.disableColorMaterial();

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLunarFlare entity) {
        return null;
    }
}
