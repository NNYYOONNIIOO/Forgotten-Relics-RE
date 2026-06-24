package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityThunderpealOrb;
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

public class RenderThunderpealOrb extends Render<EntityThunderpealOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private Random flashRandom = new Random();

    public RenderThunderpealOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityThunderpealOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // === Billboard layer (existing rendering) ===
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        float bob = MathHelper.sin(entity.ticksExisted / 5.0F) * 0.2F + 0.2F;
        float scale = 0.4F * (1.0F + bob);
        GlStateManager.scale(scale, scale, scale);

        // Blue tint
        GlStateManager.color(0.4F, 0.6F, 1.0F, 1.0F);

        this.bindTexture(TEXTURE);

        float uStart = (float)(1 + entity.ticksExisted % 6) / 32.0F;
        float uEnd = uStart + 0.03125F;
        float vStart = 0.1875F;
        float vEnd = vStart + 0.03125F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(-0.5D, -0.5D, 0.0D).tex(uStart, vEnd).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(uEnd, vEnd).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(uEnd, vStart).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(uStart, vStart).endVertex();

        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        // === Spiky flash effect (additive blending, blue/electric theme) ===
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

            // Center vertex: white-blue flash
            buffer.pos(0.0, 0.0, 0.0).color(0.9F, 0.95F, 1.0F, 1.0F - f2).endVertex();
            // Outer vertices: dark blue
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.15F, 0.3F, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.15F, 0.3F, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(0.1F, 0.15F, 0.3F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.1F, 0.15F, 0.3F, 1.0F - f2).endVertex();

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
    protected ResourceLocation getEntityTexture(EntityThunderpealOrb entity) {
        return TEXTURE;
    }
}
