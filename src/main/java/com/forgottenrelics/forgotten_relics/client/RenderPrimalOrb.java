package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityPrimalOrb;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Renders the Primal Orb as a Wisp-like entity with eldritch flash effect.
 * Uses Thaumcraft's particle texture (same as TC Wisp entities).
 * If Thaumic Augmentation is loaded, also renders a Primordial Wisp overlay
 * using the aura node texture.
 * Adds a spiky flash effect with color matching the current primal aspect.
 */
public class RenderPrimalOrb extends Render<EntityPrimalOrb> {

    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private static final ResourceLocation NODE_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/auranodes.png");

    private static final boolean TA_LOADED = Loader.isModLoaded("thaumicaugmentation");

    // Primal aspect colors: Aer, Terra, Ignis, Aqua, Ordo, Perditio
    private static final float[][] ASPECT_COLORS = {
        {0.2F, 0.6F, 1.0F},   // Aer - blue
        {0.6F, 1.0F, 0.2F},   // Terra - green
        {1.0F, 0.4F, 0.1F},   // Ignis - orange
        {0.2F, 0.4F, 1.0F},   // Aqua - deep blue
        {0.9F, 0.9F, 0.9F},   // Ordo - white
        {0.6F, 0.1F, 0.8F}    // Perditio - purple
    };

    private Random flashRandom = new Random();

    public RenderPrimalOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityPrimalOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // === Billboard layers (existing rendering) ===
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        float bob = MathHelper.sin(entity.ticksExisted / 5.0F) * 0.15F + 0.15F;
        float scale = 0.35F * (1.0F + bob);

        int colorIdx = entity.getColorIndex() % ASPECT_COLORS.length;
        float r = ASPECT_COLORS[colorIdx][0];
        float g = ASPECT_COLORS[colorIdx][1];
        float b = ASPECT_COLORS[colorIdx][2];

        // Layer 1: Core particle texture
        GlStateManager.color(r, g, b, 0.9F);
        GlStateManager.scale(scale, scale, scale);
        this.bindTexture(PARTICLE_TEXTURE);

        int frame = entity.ticksExisted % 16;
        float uStart = (float)(512 + frame) / 1024.0F;
        float uEnd = uStart + 16.0F / 1024.0F;
        float vStart = 0.0F;
        float vEnd = 16.0F / 1024.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5D, -0.5D, 0.0D).tex(uStart, vEnd).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(uEnd, vEnd).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(uEnd, vStart).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(uStart, vStart).endVertex();
        tessellator.draw();

        // Layer 2: Outer glow
        float glowScale = 1.5F;
        GlStateManager.scale(glowScale, glowScale, glowScale);
        GlStateManager.color(r, g, b, 0.25F);

        float uStart2 = (float)(320 + frame) / 1024.0F;
        float uEnd2 = uStart2 + 16.0F / 1024.0F;

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5D, -0.5D, 0.0D).tex(uStart2, vEnd).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(uEnd2, vEnd).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(uEnd2, vStart).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(uStart2, vStart).endVertex();
        tessellator.draw();

        // Layer 3 (TA only): Primordial Wisp overlay
        if (TA_LOADED) {
            this.bindTexture(NODE_TEXTURE);
            float primordialScale = 0.8F;
            GlStateManager.scale(1.0F / glowScale, 1.0F / glowScale, 1.0F / glowScale);
            GlStateManager.scale(primordialScale, primordialScale, primordialScale);
            GlStateManager.color(r, g, b, 0.5F);

            int nodeFrame = entity.ticksExisted % 16;
            float uStart3 = (float)(800 + nodeFrame) / 1024.0F;
            float uEnd3 = uStart3 + 16.0F / 1024.0F;

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-0.5D, -0.5D, 0.0D).tex(uStart3, vEnd).endVertex();
            buffer.pos(0.5D, -0.5D, 0.0D).tex(uEnd3, vEnd).endVertex();
            buffer.pos(0.5D, 0.5D, 0.0D).tex(uEnd3, vStart).endVertex();
            buffer.pos(-0.5D, 0.5D, 0.0D).tex(uStart3, vStart).endVertex();
            tessellator.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        // === Spiky flash effect (additive blending) ===
        float f1 = (float) entity.ticksExisted / 80.0F;
        float f2 = 0.0F;
        this.flashRandom.setSeed(187L);

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

        // Flash colors: white center + dark aspect-colored outer
        float outerR = r * 0.3F;
        float outerG = g * 0.3F;
        float outerB = b * 0.3F;

        for (int i = 0; i < 12; ++i) {
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.flashRandom.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);

            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            float fa = this.flashRandom.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = this.flashRandom.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            float spikeScale = 30.0F / ((float) Math.min(entity.ticksExisted, 10) / 10.0F);
            fa /= spikeScale;
            f4 /= spikeScale;

            buffer.pos(0.0, 0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(outerR, outerG, outerB, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(outerR, outerG, outerB, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(outerR, outerG, outerB, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(outerR, outerG, outerB, 1.0F - f2).endVertex();

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
    protected ResourceLocation getEntityTexture(EntityPrimalOrb entity) {
        return PARTICLE_TEXTURE;
    }
}
