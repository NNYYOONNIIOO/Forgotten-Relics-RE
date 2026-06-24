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
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IVisDiscountGear
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
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemArcanum
extends ItemBaubleBase
implements IBauble,
IVisDiscountGear,
IRechargable {
    public void registerRenderers() {
    }

    public ItemArcanum() {
        super("arcanum");
        this.maxStackSize = 1;
        this.setUnlocalizedName("arcanum");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemArcanum2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum6.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum7.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum8.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemArcanum9.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(SuperpositionHandler.getBaubleTooltip(this.getBaubleType(par1ItemStack)));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
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
        block10: {
            super.onWornTick(itemstack, entity);
            if (entity instanceof EntityPlayer & !entity.world.isRemote & Math.random() <= 0.025 * (double)RelicsConfigHandler.arcanumGenRate) {
                NonNullList inv = ((EntityPlayer)entity).inventory.mainInventory;
                int a = 0;
                while (true) {
                    if (a >= InventoryPlayer.getHotbarSize()) {
                        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)((EntityPlayer)entity));
                        for (a = 0; a < baubles.getSlots(); ++a) {
                            if (!(RechargeHelper.rechargeItem((World)entity.world, (ItemStack)baubles.getStackInSlot(a), (BlockPos)entity.getPosition(), (EntityPlayer)((EntityPlayer)entity), (int)1) > 0.0f)) continue;
                            return;
                        }
                        inv = ((EntityPlayer)entity).inventory.armorInventory;
                        for (a = 0; a < inv.size(); ++a) {
                            if (!(RechargeHelper.rechargeItem((World)entity.world, (ItemStack)((ItemStack)inv.get(a)), (BlockPos)entity.getPosition(), (EntityPlayer)((EntityPlayer)entity), (int)1) > 0.0f)) continue;
                            return;
                        }
                        break block10;
                    }
                    if (RechargeHelper.rechargeItem((World)entity.world, (ItemStack)((ItemStack)inv.get(a)), (BlockPos)entity.getPosition(), (EntityPlayer)((EntityPlayer)entity), (int)1) > 0.0f) {
                        return;
                    }
                    ++a;
                }
            }
            if (entity instanceof EntityPlayer & Math.random() <= RelicsConfigHandler.arcanumTeleportChance & !entity.world.isRemote) {
                for (int counter = 0; counter <= RelicsConfigHandler.arcanumTeleportRange && !SuperpositionHandler.validTeleportRandomly((Entity)entity, entity.world, RelicsConfigHandler.arcanumTeleportRange); ++counter) {
                }
            } else if (Math.random() <= RelicsConfigHandler.arcanumDormantTransformChance & !entity.world.isRemote & entity instanceof EntityPlayer) {
                ItemStack replacedStack = new ItemStack((Item)CommonProxy.dormantArcanum);
                ItemNBTHelper.setInt((ItemStack)replacedStack, (String)"ILifetime", (int)((int)((RelicsConfigHandler.arcanumDormantLifeMin + Math.random() * (RelicsConfigHandler.arcanumDormantLifeMax - RelicsConfigHandler.arcanumDormantLifeMin)) * (double)RelicsConfigHandler.dormantArcanumVisMult)));
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)((EntityPlayer)entity));
                baubles.setStackInSlot(6, replacedStack);
            }
        }
    }

    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return (int)RelicsConfigHandler.arcanumVisDiscount;
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.arcanumMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

