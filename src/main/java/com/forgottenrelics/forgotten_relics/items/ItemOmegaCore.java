package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaublesApi;
import com.forgottenrelics.forgotten_relics.Main;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

import java.util.List;

public class ItemOmegaCore extends Item {

    public ItemOmegaCore() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("omega_core");
        this.setRegistryName("omega_core");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.OmegaCore1.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.OmegaCore2.lore"));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        // Fill Vis for all IRechargable items in inventory
        fillAllVis(player);
    }

    private void fillAllVis(EntityPlayer player) {
        // Main inventory
        for (ItemStack invStack : player.inventory.mainInventory) {
            if (!invStack.isEmpty() && invStack.getItem() instanceof IRechargable && invStack.getItem() != this) {
                int current = RechargeHelper.getCharge(invStack);
                if (current >= 0) {
                    RechargeHelper.rechargeItemBlindly(invStack, player, 9999);
                }
            }
        }

        // Armor inventory
        for (ItemStack invStack : player.inventory.armorInventory) {
            if (!invStack.isEmpty() && invStack.getItem() instanceof IRechargable) {
                int current = RechargeHelper.getCharge(invStack);
                if (current >= 0) {
                    RechargeHelper.rechargeItemBlindly(invStack, player, 9999);
                }
            }
        }

        // Offhand inventory
        for (ItemStack invStack : player.inventory.offHandInventory) {
            if (!invStack.isEmpty() && invStack.getItem() instanceof IRechargable) {
                int current = RechargeHelper.getCharge(invStack);
                if (current >= 0) {
                    RechargeHelper.rechargeItemBlindly(invStack, player, 9999);
                }
            }
        }

        // Baubles slots
        try {
            for (int i = 0; i < 7; i++) {
                ItemStack baubleStack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if (!baubleStack.isEmpty() && baubleStack.getItem() instanceof IRechargable) {
                    int current = RechargeHelper.getCharge(baubleStack);
                    if (current >= 0) {
                        RechargeHelper.rechargeItemBlindly(baubleStack, player, 9999);
                    }
                }
            }
        } catch (Exception e) {
            // Baubles not available
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
