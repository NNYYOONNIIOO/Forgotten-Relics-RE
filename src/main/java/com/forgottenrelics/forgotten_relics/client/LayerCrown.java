/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.entity.RenderPlayer
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.ResourceLocation
 */
package com.forgottenrelics.forgotten_relics.client;

import com.forgottenrelics.forgotten_relics.client.ModelCrown;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class LayerCrown
implements LayerRenderer<EntityPlayer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("forgotten_relics", "textures/armor/crown_prs.png");
    private RenderPlayer renderer;

    public LayerCrown(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    public void doRenderLayer(EntityPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ModelCrown model = new ModelCrown(entity);
        if (SuperpositionHandler.hasBauble(entity, (Item)CommonProxy.terrorCrown)) {
            this.renderer.bindTexture(TEXTURE);
            model.setModelAttributes((ModelBase)this.renderer.getMainModel());
            model.setupAngles(this.renderer.getMainModel());
            model.render((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

