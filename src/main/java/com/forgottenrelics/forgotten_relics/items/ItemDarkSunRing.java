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
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;

public class ItemDarkSunRing
extends ItemBaubleBase
implements IBauble,
IRechargable {
    public void registerRenderers() {
    }

    public ItemDarkSunRing() {
        super("dark_sun_ring");
        this.maxStackSize = 1;
        this.setUnlocalizedName("dark_sun_ring");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemDarkSunRing1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format("item.ItemDarkSunRing2_1.lore", (int)RelicsConfigHandler.darkSunRingDamageCap));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format("item.ItemDarkSunRing4.lore", (int)(RelicsConfigHandler.darkSunRingDeflectChance * 100.0f)));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing6.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing7.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing8.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDarkSunRing9.lore", (Object[])new Object[0]));
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
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        super.onWornTick(itemstack, entity);
        if (entity.isBurning()) {
            entity.extinguish();
        }
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.darkSunRingMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

