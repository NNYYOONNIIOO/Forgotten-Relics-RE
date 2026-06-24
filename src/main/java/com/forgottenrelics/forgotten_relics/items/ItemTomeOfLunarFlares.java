package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityLunarFlare;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemTomeOfLunarFlares extends Item implements IRechargable {

    private static final float DIRECT_DAMAGE = 72.0F;
    private static final float IMPACT_DAMAGE = 40.0F;

    public ItemTomeOfLunarFlares() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("tome_of_lunar_flares");
        this.setRegistryName("tome_of_lunar_flares");
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
        return RelicsConfigHandler.lunarFlaresMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.LunarFlares1.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.LunarFlares2.lore"));
            tooltip.add(I18n.format("item.LunarFlares3.lore", (int) IMPACT_DAMAGE));
            tooltip.add(I18n.format("item.LunarFlares35.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.LunarFlares4.lore", (int) DIRECT_DAMAGE));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.lunarFlaresVisCost));
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
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        ItemStack activeItem = player.getActiveItemStack();
        if (activeItem.isEmpty() || activeItem.getItem() != this) return;

        int count = player.getItemInUseCount();
        int maxDuration = getMaxItemUseDuration(stack);
        int ticksUsed = maxDuration - count;

        if (ticksUsed > 0 && ticksUsed % 2 == 0) {
            int charge = RechargeHelper.getCharge(stack);
            if (charge < RelicsConfigHandler.lunarFlaresVisCost) {
                player.stopActiveHand();
                return;
            }

            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.lunarFlaresVisCost);

            // Use player.rayTrace for correct eye-height targeting
            RayTraceResult mop = player.rayTrace(RelicsConfigHandler.lunarFlaresRayTraceRange, 1.0F);
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                spawnLunarFlare(world, player, mop);
            }

            if (ticksUsed % 4 == 0) {
                world.playSound(null, player.posX, player.posY, player.posZ,
                    vazkii.botania.common.core.handler.ModSounds.starcaller, SoundCategory.PLAYERS, 0.4F, 1.4F);
            }
        }
    }

    private void spawnLunarFlare(World world, EntityPlayer player, RayTraceResult mop) {
        int targetX = mop.getBlockPos().getX();
        int targetY = mop.getBlockPos().getY();
        int targetZ = mop.getBlockPos().getZ();

        EntityLunarFlare flare = new EntityLunarFlare(world, player, targetX, targetY, targetZ);

        // Calculate spawn position: high above and offset from target
        Vector3 posVec = new Vector3(targetX + 0.5, targetY, targetZ + 0.5);
        Vector3 motVec = new Vector3((Math.random() - 0.5) * 18, (24 + (Math.random() - 0.5) * 18) * 2.0D, (Math.random() - 0.5) * 18);
        posVec = posVec.add(motVec);
        motVec = motVec.normalize().negate().multiply(4.0);

        flare.setPosition(posVec.x, posVec.y, posVec.z);
        flare.motionX = motVec.x;
        flare.motionY = motVec.y;
        flare.motionZ = motVec.z;

        world.spawnEntity(flare);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
