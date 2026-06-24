package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityCrimsonOrb;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Renders the Crimson Orb (Crimson Spell projectile) with two layers:
 *
 * Pass 1: Outer billboard (alpha blending, red color, particle texture)
 *         - Creates the visible crimson spherical shape
 * Pass 2: Inner spiky sphere (additive blending, warm white center + dark red outer)
 *         - Creates the bright crimson flash/glow effect
 *
 * All GL state changes use GlStateManager to prevent state pollution.
 */
public class RenderCrimsonOrb extends Render<EntityCrimsonOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private Random random = new Random();

    public RenderCrimsonOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityCrimsonOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f1 = (float) entity.ticksExisted / 80.0F;
        float f2 = 0.0F;

        // === Pass 1: Outer billboard (alpha blending, crimson color) ===
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();

        this.bindTexture(TEXTURE);

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        float bob = MathHelper.sin(entity.ticksExisted / 5.0F) * 0.2F + 0.2F;
        float billboardScale = 0.4F * (1.0F + bob);
        GlStateManager.scale(billboardScale, billboardScale, billboardScale);

        // 32x32 grid: red variant at row 6
        float uStart = (float) (1 + entity.ticksExisted % 6) / 32.0F;
        float uEnd = uStart + 0.03125F;
        float vStart = 0.1875F;
        float vEnd = vStart + 0.03125F;

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        // Dark crimson color for the billboard
        buffer.pos(-0.5, -0.5, 0.0).tex(uStart, vEnd).color(0.6F, 0.05F, 0.02F, 0.9F).endVertex();
        buffer.pos(0.5, -0.5, 0.0).tex(uEnd, vEnd).color(0.6F, 0.05F, 0.02F, 0.9F).endVertex();
        buffer.pos(0.5, 0.5, 0.0).tex(uEnd, vStart).color(0.6F, 0.05F, 0.02F, 0.9F).endVertex();
        buffer.pos(-0.5, 0.5, 0.0).tex(uStart, vStart).color(0.6F, 0.05F, 0.02F, 0.9F).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();

        // === Pass 2: Inner spiky sphere (additive blending, crimson flash) ===
        this.random.setSeed(187L);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
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

            // Center vertex: warm white flash (slightly orange-tinted for crimson theme)
            buffer.pos(0.0, 0.0, 0.0).color(1.0F, 0.85F, 0.6F, 1.0F - f2).endVertex();
            // Outer vertices: dark red
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.3F, 0.05F, 0.02F, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(0.3F, 0.05F, 0.02F, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(0.3F, 0.05F, 0.02F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.3F, 0.05F, 0.02F, 1.0F - f2).endVertex();

            tessellator.draw();
        }

        GlStateManager.popMatrix();

        // === Restore all state via GlStateManager ===
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
    protected ResourceLocation getEntityTexture(EntityCrimsonOrb entity) {
        return TEXTURE;
    }
}
