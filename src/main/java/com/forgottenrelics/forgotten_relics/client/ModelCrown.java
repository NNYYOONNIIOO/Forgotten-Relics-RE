/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelPlayer
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 */
package com.forgottenrelics.forgotten_relics.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelCrown
extends ModelBiped {
    private final ModelRenderer crown;

    public ModelCrown(EntityPlayer hat) {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.crown = new ModelRenderer((ModelBase)this);
        this.crown.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.crown.addBox(-4.0f, !hat.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() ? -12.0f : -11.0f, -4.0f, 8, 3, 8, 0.0f);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entityIn.isSneaking()) {
            GlStateManager.translate((float)0.0f, (float)0.2f, (float)0.0f);
        }
        this.crown.render(scale);
    }

    public void setupAngles(ModelPlayer model) {
        ModelCrown.copyProperties(model.bipedHead, this.crown);
    }

    private static void copyProperties(ModelRenderer source, ModelRenderer target) {
        target.rotateAngleX = source.rotateAngleX;
        target.rotateAngleY = source.rotateAngleY;
        target.rotateAngleZ = source.rotateAngleZ;
        target.offsetX = source.offsetX;
        target.offsetY = source.offsetY;
        target.offsetZ = source.offsetZ;
        target.rotationPointX = source.rotationPointX;
        target.rotationPointY = source.rotationPointY;
        target.rotationPointZ = source.rotationPointZ;
    }
}

