/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  com.google.common.collect.Multimap
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import com.google.common.collect.Multimap;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBaseModifier;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAdvancedMiningCharm
extends ItemBaubleBaseModifier {
    public void registerRenderers() {
    }

    public ItemAdvancedMiningCharm() {
        super("advanced_mining_charm");
        this.maxStackSize = 1;
        this.setUnlocalizedName("advanced_mining_charm");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format("item.ItemAdvancedMiningCharm1.lore", (int)(RelicsConfigHandler.advancedMiningCharmBoost * 100.0f)));
            par3List.add(I18n.format("item.ItemAdvancedMiningCharm2.lore", RelicsConfigHandler.advancedMiningCharmReach));
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
    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(ItemAdvancedMiningCharm.getBaubleUUID(stack), "Advanced Mining Charm", (double)RelicsConfigHandler.advancedMiningCharmReach, 0).setSaved(false));
    }
}

