package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.entities.EntityBabylonWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.lib.LibResources;

import javax.annotation.Nonnull;
import java.util.Random;

public class RenderBabylonWeapon extends Render<EntityBabylonWeapon> {

    private static final ResourceLocation babylon = new ResourceLocation(LibResources.MISC_BABYLON);

    public RenderBabylonWeapon(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(@Nonnull EntityBabylonWeapon weapon, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(weapon.getRotation(), 0F, 1F, 0F);

        int live = weapon.getLiveTicks();
        int delay = weapon.getDelay();
        float charge = Math.min(10F, Math.max(live, weapon.getChargeTicks()) + partialTicks);
        float chargeMul = charge / 10F;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Layer 1: Render weapon icon using Botania's King Key weapon textures (3D item model)
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        float s = 1.5F;
        GlStateManager.scale(s, s, s);
        GlStateManager.rotate(-90F, 0F, 1F, 0F);
        GlStateManager.rotate(45F, 0F, 0F, 1F);

        try {
            TextureAtlasSprite icon = MiscellaneousIcons.INSTANCE.kingKeyWeaponIcons[weapon.getVariety()];
            if (icon != null) {
                GlStateManager.color(1F, 1F, 1F, chargeMul);
                float f = icon.getMinU();
                float f1 = icon.getMaxU();
                float f2 = icon.getMinV();
                float f3 = icon.getMaxV();

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
                GlStateManager.disableLighting();

                // Render the icon as a 3D item model (same as Botania's King Key weapons)
                IconHelper.renderIconIn3D(Tessellator.getInstance(), f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 1F / 16F);
            }
        } catch (Exception e) {
            // Fallback if weapon icons are not available
        }

        GlStateManager.popMatrix();

        // Layer 2: Render halo/glow effect
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(1F, 1F, 1F, chargeMul);

        Minecraft.getMinecraft().renderEngine.bindTexture(babylon);

        Tessellator tes = Tessellator.getInstance();

        try {
            ShaderHelper.useShader(ShaderHelper.halo);
        } catch (Exception e) {
            // Shader may not be available
        }

        Random rand = new Random(weapon.getUniqueID().getMostSignificantBits());
        GlStateManager.rotate(-90F, 1F, 0F, 0F);
        GlStateManager.translate(0F, -0.3F + rand.nextFloat() * 0.1F, 1F);

        s = chargeMul;
        if (live > delay)
            s -= Math.min(1F, (live - delay + partialTicks) * 0.2F);
        s *= 2F;
        GlStateManager.scale(s, s, s);

        GlStateManager.rotate(charge * 9F + (weapon.ticksExisted + partialTicks) * 0.5F + rand.nextFloat() * 360F, 0F, 1F, 0F);

        tes.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tes.getBuffer().pos(-1, 0, -1).tex(0, 0).endVertex();
        tes.getBuffer().pos(-1, 0, 1).tex(0, 1).endVertex();
        tes.getBuffer().pos(1, 0, 1).tex(1, 1).endVertex();
        tes.getBuffer().pos(1, 0, -1).tex(1, 0).endVertex();
        tes.draw();

        try {
            ShaderHelper.releaseShader();
        } catch (Exception e) {
            // Shader may not be available
        }

        GlStateManager.enableLighting();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBabylonWeapon entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityBabylonWeapon> {
        @Override
        public Render<? super EntityBabylonWeapon> createRenderFor(RenderManager manager) {
            return new RenderBabylonWeapon(manager);
        }
    }
}
