/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IWarpingGear
 */
package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;

public class ItemChaosCore
extends Item
implements IWarpingGear {
    public void registerRenderers() {
    }

    public ItemChaosCore() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("chaos_core");
        this.setRegistryName("chaos_core");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {
        if (!world.isRemote & Math.random() <= 2.08E-4) {
            int randomizedPotionID = 1 + (int)(Math.random() * 21.0);
            if (randomizedPotionID == 6 || randomizedPotionID == 7) {
                randomizedPotionID = 20;
            }
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.getPotionById((int)randomizedPotionID), 100 + (int)(Math.random() * 2400.0), (int)(Math.random() * 3.0), false, false));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemChaosCore1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore6.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemChaosCore7.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public int getWarp(ItemStack arg0, EntityPlayer arg1) {
        return RelicsConfigHandler.chaosCoreWarp;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

