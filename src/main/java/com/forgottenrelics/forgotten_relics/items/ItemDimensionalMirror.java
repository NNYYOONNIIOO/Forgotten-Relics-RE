/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.Teleporter
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.common.lib.SoundsTC
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 *  vazkii.botania.common.core.helper.Vector3
 */
package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.utils.ExtradimensionalTeleporter;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.lib.SoundsTC;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.Vector3;

public class ItemDimensionalMirror
extends Item
implements IRechargable {
    public void registerRenderers() {
    }

    public ItemDimensionalMirror() {
        this.maxStackSize = 1;
        this.setRegistryName("dimensional_mirror");
        this.setUnlocalizedName("dimensional_mirror");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, @Nullable World worldIn, List<String> par3List, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemDimensionalMirror1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDimensionalMirror2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDimensionalMirror3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemDimensionalMirror4.lore", (Object[])new Object[0]));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
        if (par1ItemStack.hasTagCompound()) {
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.MirrorLoc.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.MirrorX.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"IStoredX", (int)0));
            par3List.add(I18n.format((String)"item.MirrorY.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"IStoredY", (int)0));
            par3List.add(I18n.format((String)"item.MirrorZ.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"IStoredZ", (int)0));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.MirrorDimension.lore", (Object[])new Object[0]) + ItemNBTHelper.getInt((ItemStack)par1ItemStack, (String)"IDimensionID", (int)0));
        }
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.BOW;
    }

    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return RelicsConfigHandler.dimensionalMirrorChannelDuration;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        boolean effect = stack.hasTagCompound();
        return effect;
    }

    public boolean isFull3D() {
        return false;
    }

    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        Vector3 vec = Vector3.fromEntityCenter((Entity)player);
        for (int counter = 0; counter <= 3; ++counter) {
            player.world.spawnParticle(EnumParticleTypes.PORTAL, vec.x, vec.y, vec.z, (Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0, new int[0]);
        }
        if (count == 1) {
            int dimension = ItemNBTHelper.getInt((ItemStack)stack, (String)"IDimensionID", (int)0);
            int x = ItemNBTHelper.getInt((ItemStack)stack, (String)"IStoredX", (int)0);
            int y = ItemNBTHelper.getInt((ItemStack)stack, (String)"IStoredY", (int)0);
            int z = ItemNBTHelper.getInt((ItemStack)stack, (String)"IStoredZ", (int)0);
            SuperpositionHandler.imposeBurst(player.world, player.dimension, vec.x, vec.y, vec.z, 1.25f);
            if (!player.world.isRemote & player.dimension != dimension) {
                ((EntityPlayerMP)player).mcServer.getPlayerList().transferPlayerToDimension((EntityPlayerMP)player, dimension, (Teleporter)new ExtradimensionalTeleporter(((EntityPlayerMP)player).mcServer.getWorld(dimension), (double)x + 0.5, (double)y + 0.5, (double)z + 0.5));
            } else {
                player.setPosition((double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
            }
            player.world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * 0.2), false);
            player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * 0.2), false);
            for (int counter = 0; counter <= 128; ++counter) {
                player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY - 1.0, player.posZ, (Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0, new int[0]);
            }
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean written = false;
        written = stack.hasTagCompound();
        if (written & !player.isSneaking()) {
            if (!RelicsConfigHandler.interdimensionalMirror & player.dimension != ItemNBTHelper.getInt((ItemStack)stack, (String)"IDimensionID", (int)0)) {
                return new ActionResult(EnumActionResult.PASS, (Object)stack);
            }
            if (player.dimension == 1 & ItemNBTHelper.getInt((ItemStack)stack, (String)"IDimensionID", (int)0) != 1) {
                return new ActionResult(EnumActionResult.PASS, (Object)stack);
            }
            player.setActiveHand(hand);
        } else if (player.isSneaking()) {
            ItemNBTHelper.setInt((ItemStack)stack, (String)"IStoredX", (int)((int)player.posX));
            ItemNBTHelper.setInt((ItemStack)stack, (String)"IStoredY", (int)((int)player.posY));
            ItemNBTHelper.setInt((ItemStack)stack, (String)"IStoredZ", (int)((int)player.posZ));
            ItemNBTHelper.setInt((ItemStack)stack, (String)"IDimensionID", (int)player.dimension);
            world.playSound(player.posX, player.posY, player.posZ, SoundsTC.jar, SoundCategory.PLAYERS, 1.0f, 2.0f, false);
            player.setActiveHand(hand);
        }
        return new ActionResult(EnumActionResult.PASS, (Object)stack);
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.dimensionalMirrorMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }
}

