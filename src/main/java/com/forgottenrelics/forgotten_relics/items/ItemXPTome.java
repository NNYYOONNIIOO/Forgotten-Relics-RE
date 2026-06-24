/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.common.lib.SoundsTC
 *  vazkii.botania.common.core.helper.ExperienceHelper
 *  vazkii.botania.common.core.helper.ItemNBTHelper
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;
import vazkii.botania.common.core.helper.ExperienceHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemXPTome
extends Item {
    public final String TAG_ABSORPTION = "AbsorptionMode";

    public ItemXPTome() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("xp_tome");
        this.setRegistryName("xp_tome");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        String cMode = !ItemNBTHelper.getBoolean((ItemStack)par1ItemStack, (String)"IsActive", (boolean)false) ? I18n.format((String)"item.ItemXPTomeDeactivated.lore", (Object[])new Object[0]) : (ItemNBTHelper.getBoolean((ItemStack)par1ItemStack, (String)this.TAG_ABSORPTION, (boolean)true) ? I18n.format((String)"item.ItemXPTomeAbsorption.lore", (Object[])new Object[0]) : I18n.format((String)"item.ItemXPTomeExtraction.lore", (Object[])new Object[0]));
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemXPTome1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome6.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome7.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome8.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemXPTome9.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
        par3List.add(I18n.format((String)"item.ItemXPTomeMode.lore", (Object[])new Object[0]) + " " + cMode);
        par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
        par3List.add(I18n.format((String)"item.ItemXPTomeExp.lore", (Object[])new Object[0]));
        par3List.add(I18n.format((String)"item.FRCode6.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"XPStored", (int)0) + " " + I18n.format((String)"item.ItemXPTomeUnits.lore", (Object[])new Object[0]) + " " + ExperienceHelper.getLevelForExperience((int)ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"XPStored", (int)0)) + " " + I18n.format((String)"item.ItemXPTomeLevels.lore", (Object[])new Object[0]));
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {
        if (!(entity instanceof EntityPlayer) || world.isRemote || !ItemNBTHelper.getBoolean((ItemStack)itemstack, (String)"IsActive", (boolean)false)) {
            return;
        }
        boolean action = false;
        EntityPlayer player = (EntityPlayer)entity;
        if (ItemNBTHelper.getBoolean((ItemStack)itemstack, (String)this.TAG_ABSORPTION, (boolean)true)) {
            if (ExperienceHelper.getPlayerXP((EntityPlayer)player) >= RelicsConfigHandler.xpTomeTransferRate) {
                ExperienceHelper.drainPlayerXP((EntityPlayer)player, (int)RelicsConfigHandler.xpTomeTransferRate);
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"XPStored", (int)(ItemNBTHelper.getInt((ItemStack)itemstack, (String)"XPStored", (int)0) + RelicsConfigHandler.xpTomeTransferRate));
                action = true;
            } else if (ExperienceHelper.getPlayerXP((EntityPlayer)player) > 0 & ExperienceHelper.getPlayerXP((EntityPlayer)player) < RelicsConfigHandler.xpTomeTransferRate) {
                int exp = ExperienceHelper.getPlayerXP((EntityPlayer)player);
                ExperienceHelper.drainPlayerXP((EntityPlayer)player, (int)exp);
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"XPStored", (int)(ItemNBTHelper.getInt((ItemStack)itemstack, (String)"XPStored", (int)0) + exp));
                action = true;
            }
        } else {
            int xp = ItemNBTHelper.getInt((ItemStack)itemstack, (String)"XPStored", (int)0);
            if (xp >= RelicsConfigHandler.xpTomeTransferRate) {
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"XPStored", (int)(xp - RelicsConfigHandler.xpTomeTransferRate));
                ExperienceHelper.addPlayerXP((EntityPlayer)player, (int)RelicsConfigHandler.xpTomeTransferRate);
                action = true;
            } else if (xp > 0 & xp < RelicsConfigHandler.xpTomeTransferRate) {
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"XPStored", (int)0);
                ExperienceHelper.addPlayerXP((EntityPlayer)player, (int)xp);
                action = true;
            }
        }
        if (action) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            if (ItemNBTHelper.getBoolean((ItemStack)stack, (String)this.TAG_ABSORPTION, (boolean)true)) {
                ItemNBTHelper.setBoolean((ItemStack)stack, (String)this.TAG_ABSORPTION, (boolean)false);
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, (float)((double)0.4f + Math.random() * (double)0.1f));
            } else {
                ItemNBTHelper.setBoolean((ItemStack)stack, (String)this.TAG_ABSORPTION, (boolean)true);
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, (float)((double)0.4f + Math.random() * (double)0.1f));
            }
        } else if (ItemNBTHelper.getBoolean((ItemStack)stack, (String)"IsActive", (boolean)false)) {
            ItemNBTHelper.setBoolean((ItemStack)stack, (String)"IsActive", (boolean)false);
            world.playSound(null, player.getPosition(), SoundsTC.fly, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * (double)0.2f));
        } else {
            ItemNBTHelper.setBoolean((ItemStack)stack, (String)"IsActive", (boolean)true);
            world.playSound(null, player.getPosition(), SoundsTC.fly, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * (double)0.2f));
        }
        player.setActiveHand(hand);
        return super.onItemRightClick(world, player, hand);
    }

    public boolean isFull3D() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return ItemNBTHelper.getBoolean((ItemStack)stack, (String)"IsActive", (boolean)false);
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }
}

