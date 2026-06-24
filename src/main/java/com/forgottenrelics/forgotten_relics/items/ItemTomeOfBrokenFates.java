package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.math.BigDecimal;
import java.util.List;

public class ItemTomeOfBrokenFates extends Item implements IRechargable {

    public ItemTomeOfBrokenFates() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("tome_of_broken_fates");
        this.setRegistryName("tome_of_broken_fates");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.FateTome1.lore"));
            tooltip.add(I18n.format("item.FateTome2.lore"));
            tooltip.add(I18n.format("item.FateTome3.lore"));
            tooltip.add(I18n.format("item.FateTome4.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTome5.lore", RelicsConfigHandler.fateTomeCooldownMIN, RelicsConfigHandler.fateTomeCooldownMAX));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTome6.lore"));
            tooltip.add(I18n.format("item.FateTome7.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTome8.lore"));
            tooltip.add(I18n.format("item.FateTome9.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.fateTomeVisCost));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }

        if (stack.hasTagCompound() && ItemNBTHelper.verifyExistance(stack, "IFateCooldown")) {
            int cooldown = ItemNBTHelper.getInt(stack, "IFateCooldown", 0);
            if (cooldown > 0) {
                tooltip.add(I18n.format("item.FREmpty.lore"));
                tooltip.add(I18n.format("item.FRCode" + (int)((Math.random() * 15) + 1) + ".lore")
                    + I18n.format("item.FateTomeCooldown.lore") + " "
                    + new BigDecimal(cooldown / 20.0D).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + " "
                    + I18n.format("item.FRSeconds.lore"));
            }
        }
        tooltip.add(I18n.format("item.FREmpty.lore"));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;

        if (!world.isRemote) {
            if (!stack.hasTagCompound()) {
                ItemNBTHelper.setInt(stack, "IFateID", (int)(Math.random() * Integer.MAX_VALUE));
                ItemNBTHelper.setInt(stack, "IFateCooldown", 0);
            } else {
                if (ItemNBTHelper.verifyExistance(stack, "IFateCooldown")) {
                    int cooldown = ItemNBTHelper.getInt(stack, "IFateCooldown", 0);
                    if (cooldown > 0) {
                        ItemNBTHelper.setInt(stack, "IFateCooldown", cooldown - 1);
                        if (cooldown - 1 == 0) {
                            SuperpositionHandler.sendNotification((EntityPlayer) entity, 1);
                        }
                        ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
                    }
                }
            }

            // Disastrous consequences for having multiple tomes
            if (Math.random() <= RelicsConfigHandler.fateTomeMultiHeldChance) {
                EntityPlayer player = (EntityPlayer) entity;
                if (SuperpositionHandler.itemSearch(player, this).size() > 1) {
                    insanelyDisastrousConsequences(player);
                }
            }
        }
    }

    private void insanelyDisastrousConsequences(EntityPlayer player) {
        World world = player.world;

        // Remove all Fate Tomes from player inventory
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if (!player.inventory.mainInventory.get(i).isEmpty()
                && player.inventory.mainInventory.get(i).getItem() instanceof ItemTomeOfBrokenFates) {
                player.inventory.mainInventory.set(i, ItemStack.EMPTY);
            }
        }

        // Deal 40000 fate damage to all entities within 64 blocks
        DamageSource fateDamage = new DamageRegistryHandler.DamageSourceFate();
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
            new AxisAlignedBB(player.posX - 64, player.posY - 64, player.posZ - 64,
                              player.posX + 64, player.posY + 64, player.posZ + 64));
        for (EntityLivingBase ent : entities) {
            ent.attackEntityFrom(fateDamage, RelicsConfigHandler.fateTomeDamage);
            world.newExplosion(player, ent.posX, ent.posY, ent.posZ, RelicsConfigHandler.fateTomeExplosionRadius, true, true);
        }

        // Massive explosion at player position
        world.newExplosion(player, player.posX, player.posY, player.posZ, RelicsConfigHandler.fateTomeBigExplosionRadius, true, true);
    }

    /**
     * Attempt to trigger the fate protection. Returns true if successful.
     */
    public boolean triggerFateProtection(EntityPlayer player, ItemStack tomeStack) {
        if (player.world.isRemote) return false;

        // Check cooldown
        if (ItemNBTHelper.verifyExistance(tomeStack, "IFateCooldown")) {
            int cooldown = ItemNBTHelper.getInt(tomeStack, "IFateCooldown", 0);
            if (cooldown > 0) return false;
        }

        // Try to consume charge from the tome itself
        if (RechargeHelper.getCharge(tomeStack) < RelicsConfigHandler.fateTomeVisCost) {
            return false;
        }
        RechargeHelper.consumeCharge(tomeStack, player, RelicsConfigHandler.fateTomeVisCost);

        // Cancel death, restore health
        player.setHealth(player.getMaxHealth());

        // 75% buffs, 25% debuffs
        if (Math.random() < RelicsConfigHandler.fateTomeBuffChance) {
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 500, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 1000, 0));
        } else {
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 1));
        }

        // Set cooldown: 30-90 seconds (600-1800 ticks)
        int cooldownTicks = (30 + (int)(Math.random() * 61)) * 20;
        ItemNBTHelper.setInt(tomeStack, "IFateCooldown", cooldownTicks);

        return true;
    }

    @Override
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.fateTomeMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
