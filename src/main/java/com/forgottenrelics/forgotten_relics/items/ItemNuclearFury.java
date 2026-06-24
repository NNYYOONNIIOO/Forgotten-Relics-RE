package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityRageousMissile;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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

import java.util.List;

public class ItemNuclearFury extends Item implements IRechargable {

    public ItemNuclearFury() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("nuclear_fury");
        this.setRegistryName("nuclear_fury");
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
        return RelicsConfigHandler.nuclearFuryMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.NuclearFury1.lore"));
            tooltip.add(I18n.format("item.NuclearFury2.lore"));
            tooltip.add(I18n.format("item.NuclearFury3.lore", (int) RelicsConfigHandler.nuclearFuryDamageMIN, (int) RelicsConfigHandler.nuclearFuryDamageMAX));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.NuclearFuryVisCost.lore", RelicsConfigHandler.nuclearFuryVisCostPerSecond));
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
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        clearMissiles(player);
        return true;
    }

    public static void clearMissiles(EntityPlayer player) {
        World world = player.world;
        if (world.isRemote) return;

        double range = RelicsConfigHandler.nuclearFuryClearRange;
        List<EntityRageousMissile> missiles = world.getEntitiesWithinAABB(EntityRageousMissile.class,
            new AxisAlignedBB(player.posX - range, player.posY - range, player.posZ - range,
                player.posX + range, player.posY + range, player.posZ + range));

        int cleared = 0;
        for (EntityRageousMissile missile : missiles) {
            if (missile.getThrower() == player) {
                missile.setDead();
                cleared++;
            }
        }

        if (cleared > 0) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 1.0F, 1.5F);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        // Check if player is actively using this item
        ItemStack activeItem = player.getActiveItemStack();
        if (activeItem.isEmpty() || activeItem.getItem() != this) return;

        int count = player.getItemInUseCount();
        int maxDuration = getMaxItemUseDuration(stack);
        int ticksUsed = maxDuration - count;

        // Spawn missile every 2 ticks
        if (ticksUsed > 0 && ticksUsed % 2 == 0) {
            // Always check Vis before spawning
            int charge = RechargeHelper.getCharge(stack);
            if (charge < 1) {
                player.stopActiveHand();
                return;
            }

            // Consume Vis: every 4 ticks consume 1 charge (5 per second = 1 per 4 ticks)
            if (ticksUsed % 4 == 0) {
                RechargeHelper.consumeCharge(stack, player, 1);
            }

            double x = player.posX + ((Math.random() - 0.5) * 3.1);
            double y = player.posY + 3.8 + (Math.random() - 0.5 * 3.1);
            double z = player.posZ + ((Math.random() - 0.5) * 3.1);

            EntityRageousMissile missile = new EntityRageousMissile(player, false);
            missile.setPosition(x, y, z);
            world.spawnEntity(missile);

            world.playSound(null, x, y, z, SoundEvents.ENTITY_FIREWORK_LAUNCH,
                SoundCategory.PLAYERS, 0.6F, 0.8F + (float) Math.random() * 0.2F);

            SuperpositionHandler.imposeBurst(world, player.dimension, x, y, z, 0.25F);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
