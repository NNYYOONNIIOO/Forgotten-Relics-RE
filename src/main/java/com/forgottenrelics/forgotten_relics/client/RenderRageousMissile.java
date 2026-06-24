package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityRageousMissile;
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
 * Renders the Rageous Missile (Nuclear Fury) with ONLY the spiky flash effect.
 * No billboard/sphere rendering - just the eldritch flash glow.
 * All GL state changes use GlStateManager to prevent state pollution.
 */
public class RenderRageousMissile extends Render<EntityRageousMissile> {

    private Random random = new Random();

    public RenderRageousMissile(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityRageousMissile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f1 = (float) entity.ticksExisted / 80.0F;
        float f2 = 0.0F;

        // Spiky flash effect only - no billboard/sphere
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

            buffer.pos(0.0, 0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();

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
    protected ResourceLocation getEntityTexture(EntityRageousMissile entity) {
        return null;
    }
}
