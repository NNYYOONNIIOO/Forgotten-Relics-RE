package com.forgottenrelics.forgotten_relics.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Ward effect particle - billboard style, always faces the player.
 * Uses Thaumcraft's hemisphere animation textures.
 */
public class FXBlockWard extends Particle {

    protected static final ResourceLocation[] TEXTURES = new ResourceLocation[15];

    static {
        for (int i = 0; i < TEXTURES.length; ++i)
            TEXTURES[i] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (i + 1) + ".png");
    }

    protected int rotation = 0;

    public FXBlockWard(World world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);

        motionX = 0.0;
        motionY = 0.0;
        motionZ = 0.0;
        particleGravity = 0.0F;
        particleMaxAge = 12 + rand.nextInt(5);

        setSize(0.01F, 0.01F);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        particleScale = (float) (5.0 + rand.nextGaussian() * 0.3);
        rotation = rand.nextInt(360);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        Tessellator.getInstance().draw();
        GlStateManager.pushMatrix();
        float fade = (particleAge + partialTicks) / particleMaxAge;
        int frame = (int) Math.min(TEXTURES.length - 1, Math.max(0, (TEXTURES.length - 1) * fade));
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES[frame]);

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

        GlStateManager.color(1.0F, 1.0F, 1.0F, particleAlpha / 2.0F);

        float drawX = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float drawY = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float drawZ = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        // Billboard rendering - always faces the camera using the particle system's rotation vectors
        float scale = particleScale * 0.5F;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        int s = 240 >> 16 & 0xFFFF;
        int b = 240 & 0xFFFF;
        buffer.pos(drawX - rotX * scale - rotXY * scale, drawY - rotZ * scale, drawZ - rotYZ * scale - rotXZ * scale).tex(0.0, 1.0).color(particleRed, particleGreen, particleBlue, particleAlpha / 2.0F).lightmap(s, b).endVertex();
        buffer.pos(drawX - rotX * scale + rotXY * scale, drawY + rotZ * scale, drawZ - rotYZ * scale + rotXZ * scale).tex(1.0, 1.0).color(particleRed, particleGreen, particleBlue, particleAlpha / 2.0F).lightmap(s, b).endVertex();
        buffer.pos(drawX + rotX * scale + rotXY * scale, drawY + rotZ * scale, drawZ + rotYZ * scale + rotXZ * scale).tex(1.0, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha / 2.0F).lightmap(s, b).endVertex();
        buffer.pos(drawX + rotX * scale - rotXY * scale, drawY - rotZ * scale, drawZ + rotYZ * scale - rotXZ * scale).tex(0.0, 0.0).color(particleRed, particleGreen, particleBlue, particleAlpha / 2.0F).lightmap(s, b).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft", "textures/particle/particles.png"));
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        float threshold = particleMaxAge / 5.0F;
        if (particleAge <= threshold)
            particleAlpha = particleAge / threshold;
        else
            particleAlpha = (float) (particleMaxAge - particleAge) / particleMaxAge;

        if (particleAge++ >= particleMaxAge)
            setExpired();

        motionY -= 0.04 * particleGravity;

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }
}
