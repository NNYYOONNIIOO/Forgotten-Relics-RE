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
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.util.DamageSource
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
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;

public class ItemParadox
extends ItemSword
implements IWarpingGear {
    public ItemParadox(Item.ToolMaterial m) {
        super(m);
        this.setCreativeTab(Main.tabForgottenRelics);
        this.setRegistryName("paradox");
        this.setUnlocalizedName("paradox");
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        double currentDamage = Math.random() * (double)RelicsConfigHandler.paradoxDamageCap;
        entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), (float)currentDamage);
        player.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), (float)((double)RelicsConfigHandler.paradoxDamageCap - currentDamage));
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemParadox1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemParadox2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemParadox3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemParadox4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemParadox5.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
    }

    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean b) {
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % RelicsConfigHandler.paradoxRepairRate == 0) {
            stack.damageItem(-RelicsConfigHandler.paradoxRepairAmount, (EntityLivingBase)entity);
        }
    }

    public int getWarp(ItemStack arg0, EntityPlayer arg1) {
        return RelicsConfigHandler.paradoxWarp;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

