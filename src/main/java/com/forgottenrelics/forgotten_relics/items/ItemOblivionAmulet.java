/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.IBauble
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IRechargable
 *  thaumcraft.api.items.IRechargable$EnumChargeDisplay
 *  thaumcraft.api.items.IWarpingGear
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IWarpingGear;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemOblivionAmulet
extends ItemBaubleBase
implements IBauble,
IWarpingGear,
IRechargable {
    public void registerRenderers() {
    }

    public ItemOblivionAmulet() {
        super("oblivion_amulet");
        this.maxStackSize = 1;
        this.setUnlocalizedName("oblivion_amulet");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemOblivionAmulet1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemOblivionAmulet2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemOblivionAmulet3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemOblivionAmulet4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemOblivionAmulet5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(SuperpositionHandler.getBaubleTooltip(this.getBaubleType(par1ItemStack)));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
        if (par1ItemStack.hasTagCompound()) {
            par3List.add(I18n.format((String)"item.ItemOblivionAmuletDamage.lore", (Object[])new Object[0]) + " " + (double)Math.round((double)par1ItemStack.getTagCompound().getFloat("IDamageStored") * 100.0) / 100.0);
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
        }
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        super.onWornTick(itemstack, entity);
        if (!entity.world.isRemote & itemstack.hasTagCompound() & Math.random() <= RelicsConfigHandler.oblivionAmuletDamageReleaseChance) {
            if (ItemNBTHelper.getInt((ItemStack)itemstack, (String)"IDamageStored", (int)0) > 0) {
                float getDamage = (float)((double)ItemNBTHelper.getFloat((ItemStack)itemstack, (String)"IDamageStored", (float)0.0f) * Math.random());
                if (getDamage > RelicsConfigHandler.oblivionAmuletDamageCap & Math.random() <= RelicsConfigHandler.oblivionAmuletHighDamageReductionChance) {
                    getDamage = (float)(RelicsConfigHandler.oblivionAmuletDamageCap * Math.random());
                }
                ItemNBTHelper.setFloat((ItemStack)itemstack, (String)"IDamageStored", (float)(ItemNBTHelper.getFloat((ItemStack)itemstack, (String)"IDamageStored", (float)0.0f) - getDamage));
                entity.attackEntityFrom((DamageSource)new DamageRegistryHandler.DamageSourceOblivion(), getDamage);
            }
        } else if (!entity.world.isRemote & Math.random() <= RelicsConfigHandler.oblivionAmuletPotionChance) {
            double omega = Math.random();
            int PotionID = 0;
            PotionID = omega <= 0.25 ? 4 : (omega <= 0.5 ? 15 : (omega <= 0.75 ? 18 : 20));
            entity.addPotionEffect(new PotionEffect(Potion.getPotionById((int)PotionID), RelicsConfigHandler.oblivionAmuletPotionDurationMin + (int)(Math.random() * (RelicsConfigHandler.oblivionAmuletPotionDurationMax - RelicsConfigHandler.oblivionAmuletPotionDurationMin)), RelicsConfigHandler.oblivionAmuletPotionLevelMin + (int)(Math.random() * (RelicsConfigHandler.oblivionAmuletPotionLevelMax - RelicsConfigHandler.oblivionAmuletPotionLevelMin)), true, false));
        }
    }

    public int getWarp(ItemStack arg0, EntityPlayer arg1) {
        return RelicsConfigHandler.oblivionAmuletWarp;
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.oblivionAmuletMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

