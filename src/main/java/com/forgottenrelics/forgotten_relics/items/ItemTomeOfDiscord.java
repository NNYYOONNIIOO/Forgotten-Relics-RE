package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemTomeOfDiscord extends Item implements IRechargable {

    public ItemTomeOfDiscord() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("tome_of_discord");
        this.setRegistryName("tome_of_discord");
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
        return RelicsConfigHandler.discordTomeMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemTeleportationTome1.lore"));
            tooltip.add(I18n.format("item.ItemTeleportationTome2.lore", RelicsConfigHandler.discordTomeTeleportRange));
            tooltip.add(I18n.format("item.ItemTeleportationTome3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemTeleportationTome4.lore"));
            tooltip.add(I18n.format("item.ItemTeleportationTome5.lore", RelicsConfigHandler.discordTomeShiftRange));
            tooltip.add(I18n.format("item.ItemTeleportationTome6.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.discordTomeVisCost));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
        tooltip.add(I18n.format("item.FREmpty.lore"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if (SuperpositionHandler.isOnCoodown(player)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        int charge = RechargeHelper.getCharge(stack);
        if (charge < RelicsConfigHandler.discordTomeVisCost) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        // Check if Ring of Discord is active (extends range)
        boolean discordActive = ItemRingOfDiscord.isDiscordActive(player);
        int effectiveRange = discordActive ? RelicsConfigHandler.discordTomeDiscordRange : RelicsConfigHandler.discordTomeTeleportRange;

        // Sneak + Right Click: teleport in look direction, ignore obstacles
        if (player.isSneaking()) {
            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.discordTomeVisCost);

            Vector3 primalVec = Vector3.fromEntityCenter(player);
            primalVec = new Vector3(primalVec.x, primalVec.y - 0.5, primalVec.z);

            Vec3d lookVec = player.getLookVec();
            int shiftRange = discordActive ? RelicsConfigHandler.discordTomeDiscordRange : RelicsConfigHandler.discordTomeShiftRange;
            Vector3 targetVec = new Vector3(
                primalVec.x + lookVec.x * shiftRange,
                primalVec.y + lookVec.y * shiftRange,
                primalVec.z + lookVec.z * shiftRange
            );

            SuperpositionHandler.imposeBurst(world, player.dimension, player.posX, player.posY + 1, player.posZ, 1.25F);
            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            player.setPositionAndUpdate(targetVec.x, targetVec.y, targetVec.z);

            world.playSound(null, targetVec.x, targetVec.y, targetVec.z,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            SuperpositionHandler.setCasted(player, RelicsConfigHandler.discordTomeCooldown, false);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        // Check for pointed entity
        Entity pointedEntity = getPointedEntity(world, player, effectiveRange, 4.0F);

        if (pointedEntity != null && pointedEntity instanceof EntityLivingBase) {
            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.discordTomeVisCost);

            // Swap positions
            Vector3 playerPos = Vector3.fromEntityCenter(player);
            Vector3 entityPos = Vector3.fromEntityCenter(pointedEntity);

            SuperpositionHandler.imposeBurst(world, player.dimension, player.posX, player.posY + 1, player.posZ, 1.25F);

            player.setPositionAndUpdate(entityPos.x, entityPos.y, entityPos.z);
            ((EntityLivingBase) pointedEntity).setPositionAndUpdate(playerPos.x, playerPos.y, playerPos.z);

            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            world.playSound(null, pointedEntity.posX, pointedEntity.posY, pointedEntity.posZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            SuperpositionHandler.setCasted(player, RelicsConfigHandler.discordTomeCooldown, false);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        // Pointed at block: teleport to block position
        RayTraceResult mop = player.rayTrace(effectiveRange, 1.0F);
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.discordTomeVisCost);

            BlockPos pos = mop.getBlockPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // Find valid teleport position above the target block
            for (int counter = 0; counter <= 32; counter++) {
                BlockPos checkPos = new BlockPos(x, y + counter, z);
                BlockPos abovePos = new BlockPos(x, y + counter + 1, z);
                BlockPos belowPos = new BlockPos(x, y + counter - 1, z);

                if (!world.isAirBlock(belowPos) && world.getBlockState(belowPos).getBlock().isCollidable()
                    && world.isAirBlock(checkPos) && world.isAirBlock(abovePos)) {

                    SuperpositionHandler.imposeBurst(world, player.dimension, player.posX, player.posY + 1, player.posZ, 1.25F);
                    world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    player.setPositionAndUpdate(x + 0.5, y + counter, z + 0.5);

                    world.playSound(null, x + 0.5, y + counter, z + 0.5,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    SuperpositionHandler.setCasted(player, RelicsConfigHandler.discordTomeCooldown, false);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    private Entity getPointedEntity(World world, EntityPlayer player, double range, float expand) {
        Vec3d startPos = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLook(1.0F);
        Vec3d endPos = startPos.addVector(lookVec.x * range, lookVec.y * range, lookVec.z * range);

        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player,
            player.getEntityBoundingBox().expand(lookVec.x * range, lookVec.y * range, lookVec.z * range)
                .grow(expand, expand, expand));

        Entity closestEntity = null;
        double closestDist = range;

        for (Entity entity : entities) {
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                double dist = startPos.distanceTo(entity.getPositionVector());
                if (dist < closestDist) {
                    closestEntity = entity;
                    closestDist = dist;
                }
            }
        }

        return closestEntity;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
