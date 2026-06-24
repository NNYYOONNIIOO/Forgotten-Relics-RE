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
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IRechargable
 *  thaumcraft.api.items.IRechargable$EnumChargeDisplay
 *  thaumcraft.api.items.RechargeHelper
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.ArrayList;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemDeificAmulet
extends ItemBaubleBase
implements IBauble,
IRechargable {
    public void registerRenderers() {
    }

    public ItemDeificAmulet() {
        super("deific_amulet");
        this.setMaxStackSize(1);
        this.setUnlocalizedName("deific_amulet");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            if (RelicsConfigHandler.deificAmuletEffectImmunity) {
                if (RelicsConfigHandler.deificAmuletOnlyNegatesDebuffs) {
                    par3List.add(I18n.format((String)"item.ItemDeificAmulet1_alt.lore", (Object[])new Object[0]));
                } else {
                    par3List.add(I18n.format((String)"item.ItemDeificAmulet1.lore", (Object[])new Object[0]));
                }
            }
            par3List.add(I18n.format((String)"item.ItemDeificAmulet2.lore", (Object[])new Object[0]));
            if (RelicsConfigHandler.deificAmuletInvincibility) {
                par3List.add(I18n.format((String)"item.ItemDeificAmulet3.lore", (Object[])new Object[0]));
            }
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDeificAmulet4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDeificAmulet5.lore", (Object[])new Object[0]));
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
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        super.onWornTick(itemstack, entity);
        if (!entity.world.isRemote & entity instanceof EntityPlayer) {
            if (entity.getActivePotionEffects() != null & RelicsConfigHandler.deificAmuletEffectImmunity) {
                if (RelicsConfigHandler.deificAmuletOnlyNegatesDebuffs) {
                    ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>(entity.getActivePotionEffects());
                    for (PotionEffect effect : effects) {
                        Potion id = effect.getPotion();
                        boolean badEffect = id.isBadEffect();
                        if (!badEffect) continue;
                        entity.removePotionEffect(id);
                    }
                } else {
                    entity.clearActivePotions();
                }
            }
            if (entity.isBurning()) {
                entity.extinguish();
            }
            if (entity.getAir() == 0 && RechargeHelper.consumeCharge((ItemStack)itemstack, (EntityLivingBase)entity, (int)((int)(RelicsConfigHandler.deificAmuletFireVisCost * RelicsConfigHandler.deificAmuletVisMult)))) {
                entity.setFire(RelicsConfigHandler.deificAmuletFireDuration);
            }
            if (RelicsConfigHandler.deificAmuletInvincibility) {
                if (ItemNBTHelper.getInt((ItemStack)itemstack, (String)"ICooldown", (int)0) == 0 & entity.hurtResistantTime > 10) {
                    entity.hurtResistantTime = RelicsConfigHandler.deificAmuletInvincibilityExtension;
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"ICooldown", (int)RelicsConfigHandler.deificAmuletInvincibilityCooldown);
                }
                if (ItemNBTHelper.getInt((ItemStack)itemstack, (String)"ICooldown", (int)0) > 0) {
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"ICooldown", (int)(ItemNBTHelper.getInt((ItemStack)itemstack, (String)"ICooldown", (int)0) - 1));
                }
            }
        }
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.deificAmuletMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

