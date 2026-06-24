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
 *  net.minecraft.item.EnumAction
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
 *  thaumcraft.api.items.IRechargable
 *  thaumcraft.api.items.IRechargable$EnumChargeDisplay
 *  thaumcraft.api.items.RechargeHelper
 *  vazkii.botania.common.Botania
 *  vazkii.botania.common.core.handler.ModSounds
 *  vazkii.botania.common.core.helper.Vector3
 */
package com.forgottenrelics.forgotten_relics.items;

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
import net.minecraft.item.EnumAction;
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
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.Vector3;

public class ItemWeatherStone
extends Item
implements IRechargable {
    public int getVisCost() { return (int)((float)RelicsConfigHandler.weatherStoneVisCost * RelicsConfigHandler.weatherStoneVisMult); }

    public ItemWeatherStone() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("weather_stone");
        this.setRegistryName("weather_stone");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemWeatherStone1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format("item.ItemWeatherStone2.lore", this.getVisCost()));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRaining() & !SuperpositionHandler.isOnCoodown(player)) {
            player.setActiveHand(hand);
        }
        return super.onItemRightClick(world, player, hand);
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.BOW;
    }

    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return RelicsConfigHandler.weatherStoneChannelDuration;
    }

    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        Vector3 vec = Vector3.fromEntityCenter((Entity)player);
        if (player instanceof EntityPlayer && count == 1 & player.world.getWorldInfo().isRaining() && RechargeHelper.consumeCharge((ItemStack)stack, (EntityLivingBase)player, (int)this.getVisCost())) {
            for (int i = 0; i <= 24; ++i) {
                float r = 0.0f;
                float g = 0.3f + (float)Math.random() * 0.5f;
                float b = 0.8f + (float)Math.random() * 0.2f;
                float s = 0.2f + (float)Math.random() * 0.2f;
                float m = 0.15f;
                float xm = ((float)Math.random() - 0.5f) * m;
                float ym = ((float)Math.random() - 0.5f) * m;
                float zm = ((float)Math.random() - 0.5f) * m;
                Botania.proxy.wispFX(vec.x, vec.y, vec.z, r, g, b, s, xm, ym, zm);
            }
            player.world.playSound(vec.x, vec.y, vec.z, ModSounds.altarCraft, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * (double)0.2f), false);
            player.world.getWorldInfo().setRaining(false);
            player.world.getWorldInfo().setRainTime(24000 + (int)(Math.random() * 976000.0));
            SuperpositionHandler.setCasted((EntityPlayer)player, RelicsConfigHandler.weatherStoneCooldown, false);
        }
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.weatherStoneMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}

