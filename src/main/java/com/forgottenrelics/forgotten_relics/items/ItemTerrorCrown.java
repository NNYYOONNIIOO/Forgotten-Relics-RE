/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.IBauble
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IGoggles
 *  thaumcraft.api.items.IRevealer
 *  thaumcraft.api.items.IWarpingGear
 *  thaumcraft.common.lib.utils.EntityUtils
 *  vazkii.botania.api.mana.ManaItemHandler
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.lib.utils.EntityUtils;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemTerrorCrown
extends ItemArmor
implements IWarpingGear,
IGoggles,
IRevealer,
IBauble {
    public ItemTerrorCrown(EntityEquipmentSlot type, ItemArmor.ArmorMaterial mat) {
        super(mat, 0, type);
        this.setCreativeTab(Main.tabForgottenRelics);
        this.setUnlocalizedName("terror_crown");
        this.setRegistryName("terror_crown");
        this.setMaxDamage(1000);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != this;
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        this.onArmorTick(player.world, (EntityPlayer)player, itemstack);
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        Entity scannedEntity;
        SuperpositionHandler.cryHavoc(world, player, RelicsConfigHandler.terrorCrownHavocRange);
        this.onUpdate(itemStack, world, (Entity)player, 0, false);
        if (!world.isRemote && (scannedEntity = EntityUtils.getPointedEntity((World)world, (Entity)player, (double)0.0, (double)RelicsConfigHandler.terrorCrownScanRange, (float)3.0f, (boolean)false)) instanceof EntityLivingBase) {
            EntityLivingBase targetEntity = (EntityLivingBase)scannedEntity;
            try {
                targetEntity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, RelicsConfigHandler.terrorCrownBlindnessDuration, 2, true, false));
                if (!targetEntity.isPotionActive(MobEffects.WITHER)) {
                    targetEntity.addPotionEffect(new PotionEffect(MobEffects.WITHER, RelicsConfigHandler.terrorCrownWitherDuration, RelicsConfigHandler.terrorCrownWitherLevel, false, false));
                }
                targetEntity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, RelicsConfigHandler.terrorCrownNauseaDuration, RelicsConfigHandler.terrorCrownNauseaLevel, true, false));
                targetEntity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, RelicsConfigHandler.terrorCrownSlownessDuration, RelicsConfigHandler.terrorCrownSlownessLevel, true, false));
                targetEntity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, RelicsConfigHandler.terrorCrownWeaknessDuration, RelicsConfigHandler.terrorCrownWeaknessLevel, true, false));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {
        if (itemstack.isItemEnchanted()) {
            NBTTagCompound nbt = ItemNBTHelper.getNBT((ItemStack)itemstack);
            nbt.removeTag("ench");
            itemstack.setTagCompound(nbt);
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!world.isRemote && itemstack.getItemDamage() > 0 && ManaItemHandler.requestManaExact((ItemStack)itemstack, (EntityPlayer)player, (int)RelicsConfigHandler.terrorCrownManaCost, (boolean)true)) {
                itemstack.setItemDamage(itemstack.getItemDamage() - 1);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format("item.ItemTerrorCrown1.lore", RelicsConfigHandler.terrorCrownHavocRange));
            par3List.add(I18n.format((String)"item.ItemTerrorCrown2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemTerrorCrown3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemTerrorCrown4.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public int getWarp(ItemStack arg0, EntityPlayer arg1) {
        return RelicsConfigHandler.terrorCrownWarp;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "forgotten_relics:textures/armor/crown_prs.png";
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return par2ItemStack.isItemEqual(new ItemStack(Items.GOLD_INGOT)) || super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    public boolean showIngamePopups(ItemStack arg0, EntityLivingBase arg1) {
        return true;
    }

    public boolean showNodes(ItemStack arg0, EntityLivingBase arg1) {
        return true;
    }

    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.HEAD;
    }
}

