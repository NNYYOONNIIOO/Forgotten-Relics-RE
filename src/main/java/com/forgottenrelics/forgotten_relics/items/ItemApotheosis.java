package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityBabylonWeapon;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemApotheosis extends Item implements IRechargable {

    public ItemApotheosis() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("apotheosis");
        this.setRegistryName("apotheosis");
        this.setCreativeTab(Main.tabForgottenRelics);
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
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.apotheosisMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemApotheosis1.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemApotheosis2.lore"));
            tooltip.add(I18n.format("item.ItemApotheosis3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemApotheosis4.lore", (int)RelicsConfigHandler.damageApotheosisDirect));
            tooltip.add(I18n.format("item.ItemApotheosis5.lore", (int)RelicsConfigHandler.damageApotheosisImpact));
            tooltip.add(I18n.format("item.ItemApotheosis6.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.NuclearFuryVisCost.lore", RelicsConfigHandler.apotheosisVisCost));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
        tooltip.add(I18n.format("item.FREmpty.lore"));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        ItemStack activeItem = player.getActiveItemStack();
        if (activeItem.isEmpty() || activeItem.getItem() != this) return;

        int count = player.getItemInUseCount();
        int maxDuration = getMaxItemUseDuration(stack);

        // Every 2 ticks, spawn a weapon (same timing as original)
        if (count != maxDuration && count % 2 == 0 && !world.isRemote) {
            int charge = RechargeHelper.getCharge(stack);
            if (charge >= RelicsConfigHandler.apotheosisVisCost) {
                RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.apotheosisVisCost);
                spawnBabylonWeapon(player);
            } else {
                player.stopActiveHand();
            }
        }
    }

    private void spawnBabylonWeapon(EntityPlayer player) {
        Vector3 originalPosX = Vector3.fromEntityCenter(player);
        EntityBabylonWeapon weapon = new EntityBabylonWeapon(player.world, player);

        double rawYaw = player.rotationYawHead;
        double yaw;

        if (rawYaw < 0)
            yaw = Math.abs(rawYaw);
        else
            yaw = 360 - rawYaw;

        double x = 0;
        double z = 0;

        if (yaw >= 0 && yaw <= 90) {
            double m = ((yaw - 0) / 90);
            z = 1.0 - m;
            x = m;
        } else if (yaw >= 90 && yaw <= 180) {
            double m = ((yaw - 90) / 90);
            x = 1.0 - m;
            z = -m;
        } else if (yaw >= 180 && yaw <= 270) {
            double m = ((yaw - 180) / 90);
            z = -(1.0 - m);
            x = -m;
        } else if (yaw >= 270 && yaw <= 360) {
            double m = ((yaw - 270) / 90);
            x = -(1.0 - m);
            z = m;
        }

        double multV2 = yaw % 90;

        if (multV2 < 45) {
            // DO NOTHING
        } else if (multV2 > 45)
            multV2 = 45 - (multV2 - 45);

        double multV3 = 1.0D + (multV2 / 90);

        double lookVx = x * multV3;
        double lookVy = 0;
        double lookVz = z * multV3;
        double addx = lookVx, addy = lookVy, addz = lookVz;

        double finalX = lookVx, finalY = lookVy, finalZ = lookVz;

        for (int supercounter = 0; supercounter <= 100; supercounter++) {

            double fx = lookVx, fy = lookVy, fz = lookVz;

            double negative;
            if (Math.random() >= 0.5)
                negative = -1.0D;
            else
                negative = 1.0D;

            // Rotate by 80 degrees around Y axis
            double angle = Math.toRadians(80.0D * negative);
            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);
            double rx = fx * cosA + fz * sinA;
            double rz = -fx * sinA + fz * cosA;
            fx = rx;
            fz = rz;

            double mult = 2.0F + (Math.random() * 10.0F);
            fx *= mult;
            fy *= mult;
            fz *= mult;

            double addMul = Math.random() * 1.0;
            fx += addx * addMul;
            fy += addy * addMul;
            fz += addz * addMul;

            fy += -0.5 + (Math.random() * 8);

            fx += originalPosX.x;
            fy += originalPosX.y;
            fz += originalPosX.z;

            double range = 2.0D;
            List<EntityBabylonWeapon> weapons = player.world.getEntitiesWithinAABB(EntityBabylonWeapon.class,
                new AxisAlignedBB(fx - range, fy - range, fz - range,
                    fx + range, fy + range, fz + range));

            if (weapons.size() > 0)
                continue;
            else {
                finalX = fx;
                finalY = fy;
                finalZ = fz;
                break;
            }
        }

        weapon.setPosition(finalX, finalY, finalZ);
        weapon.setVariety(player.getRNG().nextInt(12));
        weapon.setRotation(net.minecraft.util.math.MathHelper.wrapDegrees(-player.rotationYawHead + 180));

        player.world.spawnEntity(weapon);
        player.world.playSound(null, weapon.posX, weapon.posY, weapon.posZ,
            vazkii.botania.common.core.handler.ModSounds.babylonSpawn, SoundCategory.PLAYERS,
            1.0F, 1.0F + player.world.rand.nextFloat() * 3.0F);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
