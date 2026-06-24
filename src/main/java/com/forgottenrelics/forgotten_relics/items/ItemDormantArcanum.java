/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.BaublesApi
 *  baubles.api.IBauble
 *  baubles.api.cap.IBaublesItemHandler
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IRechargable
 *  thaumcraft.api.items.IRechargable$EnumChargeDisplay
 *  thaumcraft.api.items.RechargeHelper
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemDormantArcanum
extends ItemBaubleBase
implements IBauble,
IRechargable {
    public void registerRenderers() {
    }

    public ItemDormantArcanum() {
        super("dormant_arcanum");
        this.maxStackSize = 1;
        this.setUnlocalizedName("dormant_arcanum");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemDormantArcanum1.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        if (par1ItemStack.hasTagCompound()) {
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FRCode6.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"ILifetime", (int)0) * 2 + I18n.format((String)"item.ItemDormantArcanum2.lore", (Object[])new Object[0]));
        }
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.CHARM;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        super.onWornTick(itemstack, entity);
        if (itemstack.hasTagCompound() & !entity.world.isRemote & entity instanceof EntityPlayer) {
            if (ItemNBTHelper.getInt((ItemStack)itemstack, (String)"ILifetime", (int)0) > 0) {
                if (RechargeHelper.consumeCharge((ItemStack)itemstack, (EntityLivingBase)entity, (int)3)) {
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"ILifetime", (int)(ItemNBTHelper.getInt((ItemStack)itemstack, (String)"ILifetime", (int)0) - 1));
                }
            } else {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)((EntityPlayer)entity));
                baubles.setStackInSlot(6, new ItemStack((Item)CommonProxy.arcanum));
            }
        }
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.dormantArcanumMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

