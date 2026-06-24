package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityDarkMatterOrb;
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
 * Renders the Dark Matter Orb (Eldritch Spell projectile) using the same
 * two-layer approach as Thaumcraft's RenderEldritchOrb:
 *
 * Pass 1: Outer billboard (alpha blending, dark color, particle texture)
 *         - Creates the visible dark spherical shape
 * Pass 2: Inner spiky sphere (additive blending, white center + dark outer)
 *         - Creates the bright eldritch flash/glow effect
 *
 * IMPORTANT: All GL state changes use GlStateManager (not direct GL11 calls)
 * to keep the state tracker in sync and prevent GL state pollution that
 * causes other mods' models to turn white.
 * Direct GL11 calls are only used for GL_SHADE_MODEL and matrix rotations
 * inside the spike loop (which GlStateManager doesn't wrap in 1.12.2).
 */
public class RenderDarkMatterOrb extends Render<EntityDarkMatterOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private Random random = new Random();

    public RenderDarkMatterOrb(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityDarkMatterOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float f1 = (float) entity.ticksExisted / 80.0F;
        float f2 = 0.0F;

        // === Pass 1: Outer billboard (alpha blending, dark color) ===
        // Rendered first so the inner sphere's additive blending adds on top,
        // creating a dark sphere with bright center flash ("black + flash").
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();

        this.bindTexture(TEXTURE);

        // Billboard orientation - face the camera
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        float billboardScale = 0.75F;
        GlStateManager.scale(billboardScale, billboardScale, billboardScale);

        // UV coordinates - animated sprite from particle texture (same as RenderEldritchOrb)
        float uStart = (float) (entity.ticksExisted % 13) / 64.0F;
        float uEnd = uStart + 0.015625F;
        float vStart = 0.046875F;
        float vEnd = vStart + 0.015625F;

        // Dark blue-black color for the billboard
        float cr = 0.05F, cg = 0.05F, cb = 0.1F, ca = 0.85F;

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.5, -0.5, 0.0).tex(uStart, vEnd).color(cr, cg, cb, ca).endVertex();
        buffer.pos(0.5, -0.5, 0.0).tex(uEnd, vEnd).color(cr, cg, cb, ca).endVertex();
        buffer.pos(0.5, 0.5, 0.0).tex(uEnd, vStart).color(cr, cg, cb, ca).endVertex();
        buffer.pos(-0.5, 0.5, 0.0).tex(uStart, vStart).color(cr, cg, cb, ca).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();

        // === Pass 2: Inner spiky sphere (additive blending) ===
        // Matches RenderEldritchOrb's inner sphere: 12 random-rotated triangular cones
        // with white center (flash) and dark outer (eldritch look).
        this.random.setSeed(187L);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        // State setup via GlStateManager only
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE); // additive
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

            // Center vertex: bright white flash
            buffer.pos(0.0, 0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F - f2).endVertex();
            // Outer vertices: dark gray-blue (darker than original's 0.25 for eldritch look)
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(0.0, fa, 1.0F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();
            buffer.pos(-0.866 * f4, fa, -0.5F * f4).color(0.15F, 0.15F, 0.2F, 1.0F - f2).endVertex();

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
    protected ResourceLocation getEntityTexture(EntityDarkMatterOrb entity) {
        return TEXTURE;
    }
}
