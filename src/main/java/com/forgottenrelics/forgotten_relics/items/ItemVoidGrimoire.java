package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.packets.VoidGrimoireParticleMessage;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.HashMap;
import java.util.List;

/**
 * Grimoire of The Abyss - Obliterates targeted entities by banishing them to the Void.
 * Charge: 100 Vis, 0.2 Vis per tick while channeling.
 * Target is immobilized and levitates during channeling.
 */
public class ItemVoidGrimoire extends Item implements IRechargable {

    private static final float VIS_PER_TICK = 0.2F;
    private static final int CHANNEL_DURATION = 100; // 5 seconds
    private static final int TARGET_RANGE = 64;

    private static HashMap<EntityPlayer, EntityLivingBase> targetList = new HashMap<>();

    public ItemVoidGrimoire() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("void_grimoire");
        this.setRegistryName("void_grimoire");
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
        return RelicsConfigHandler.voidGrimoireMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemVoidGrimoire1.lore"));
            tooltip.add(I18n.format("item.ItemVoidGrimoire2.lore"));
            tooltip.add(I18n.format("item.ItemVoidGrimoire3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemVoidGrimoire4.lore"));
            tooltip.add(I18n.format("item.ItemVoidGrimoire5.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemVoidGrimoire6.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.NuclearFuryVisCost.lore", (int)(VIS_PER_TICK * 20)));
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
        return RelicsConfigHandler.voidGrimoireChannelDuration;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        // Check if enabled in config
        if (!RelicsConfigHandler.voidGrimoireEnabled) {
            return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }

        // Raytrace to find target entity
        Entity pointedEntity = getPointedEntity(world, player, RelicsConfigHandler.voidGrimoireSearchRange, 3.0F);

        if (pointedEntity instanceof EntityLivingBase) {
            // Check if target is a player and config disallows it
            if (pointedEntity instanceof EntityPlayerMP && !RelicsConfigHandler.overthrowerAffectsPlayers) {
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }
            targetList.put(player, (EntityLivingBase) pointedEntity);
            player.setActiveHand(hand);
        } else {
            targetList.put(player, null);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (!(player instanceof EntityPlayer) || player.world.isRemote) return;

        EntityPlayer caster = (EntityPlayer) player;
        int ticksUsed = RelicsConfigHandler.voidGrimoireChannelDuration - count;

        // Consume Vis per tick
        int charge = RechargeHelper.getCharge(stack);
        if (charge < 1) {
            caster.stopActiveHand();
            return;
        }
        // Consume 0.2 Vis per tick = 1 Vis every 5 ticks
        if (ticksUsed % 5 == 0 && ticksUsed > 0) {
            RechargeHelper.consumeCharge(stack, caster, 1);
        }

        EntityLivingBase target = targetList.get(caster);
        if (target == null || target.isDead) {
            targetList.put(caster, null);
            caster.stopActiveHand();
            return;
        }

        // Reset fall distance and levitate target (0.03 blocks/tick = ~3 blocks over 100 ticks)
        target.fallDistance = 0.0F;
        target.motionY = RelicsConfigHandler.voidGrimoireLevitateSpeed;
        target.velocityChanged = true;

        // Immobilize target (extreme slowness)
        try {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, 100, true, false));
        } catch (Exception ex) {
            // ignore
        }

        Vector3 targetPos = Vector3.fromEntityCenter(target);

        // Play charging sound at start
        if (count == CHANNEL_DURATION) {
            caster.world.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 4.0F, 0.75F);
        }

        // Play void ambient sound every 10 ticks
        if (count % 10 == 0 && count != RelicsConfigHandler.voidGrimoireChannelDuration) {
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.5F);
        }

        // Send casting particles to nearby clients
        Main.packetInstance.sendToAllAround(
            new VoidGrimoireParticleMessage(targetPos.x, targetPos.y, targetPos.z, false),
            new NetworkRegistry.TargetPoint(target.dimension, target.posX, target.posY, target.posZ, 64.0D));

        // Channeling complete
        if (count == 1) {
            obliterate(target, caster);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
        // When player releases early, stop the levitation immediately
        if (player instanceof EntityPlayer) {
            EntityLivingBase target = targetList.get(player);
            if (target != null && !target.isDead) {
                target.motionY = 0.0D;
                target.velocityChanged = true;
                target.removePotionEffect(MobEffects.SLOWNESS);
            }
            targetList.remove(player);
        }
    }

    /**
     * Obliterate the target entity - banish to the void and destroy.
     * Non-player entities are teleported to a far void coordinate and killed.
     * Players are teleported to a far void coordinate (and will likely die).
     */
    private void obliterate(EntityLivingBase entity, EntityPlayer caster) {
        Vector3 targetPos = Vector3.fromEntityCenter(entity);

        // Impose burst effect (shockwave)
        SuperpositionHandler.imposeBurst(entity.world, entity.dimension, targetPos.x, targetPos.y, targetPos.z, 2.0F);

        // Send final burst particles
        Main.packetInstance.sendToAllAround(
            new VoidGrimoireParticleMessage(targetPos.x, targetPos.y, targetPos.z, true),
            new NetworkRegistry.TargetPoint(caster.dimension, targetPos.x, targetPos.y, targetPos.z, 128.0D));

        // Lightning bolts at target position
        for (int i = 0; i < 3; i++) {
            EntityLightningBolt lightning = new EntityLightningBolt(
                caster.world, targetPos.x, targetPos.y, targetPos.z, false);
            caster.world.spawnEntity(lightning);
        }

        // Thaumcraft craftfail sound (void obliteration sound)
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
            thaumcraft.common.lib.SoundsTC.craftfail, SoundCategory.PLAYERS, 4.0F, 0.8F + itemRand.nextFloat() * 0.2F);

        // Endermen death sound
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
            SoundEvents.ENTITY_ENDERMEN_DEATH, SoundCategory.PLAYERS, 2.0F, 0.5F);

        if (!(entity instanceof EntityPlayerMP)) {
            // Non-player: teleport to void and kill
            entity.setPositionAndUpdate(0, -1000, 0);
            entity.setDead();

            // Pixie death sparkle effect (dark purple sparkles)
            for (int i = 0; i < 12; i++) {
                Botania.proxy.sparkleFX(
                    targetPos.x + (Math.random() - 0.5) * 0.25,
                    targetPos.y + 0.5 + (Math.random() - 0.5) * 0.25,
                    targetPos.z + (Math.random() - 0.5) * 0.25,
                    0.3F, 0.0F, 0.5F, 1.0F + (float) Math.random() * 0.25F, 5);
            }
        } else {
            // Player: teleport to void coordinate
            EntityPlayerMP playerMP = (EntityPlayerMP) entity;
            playerMP.setPositionAndUpdate(0, -1000, 0);

            // Send chat message
            TextComponentTranslation msg2 = new TextComponentTranslation("message.overthrown3");
            playerMP.mcServer.getPlayerList().sendMessage(
                new TextComponentTranslation("").appendSibling(caster.getDisplayName())
                    .appendText(" ").appendSibling(new TextComponentTranslation("message.overthrown1"))
                    .appendText(" ").appendSibling(playerMP.getDisplayName())
                    .appendText(" ").appendSibling(msg2));
        }

        // Remove from target list
        targetList.remove(caster);
    }

    /**
     * Get the entity the player is looking at within range.
     */
    private Entity getPointedEntity(World world, EntityPlayer player, double range, float expand) {
        Vec3d lookVec = player.getLookVec();
        Vec3d eyePos = player.getPositionEyes(1.0F);
        Vec3d endPos = eyePos.addVector(lookVec.x * range, lookVec.y * range, lookVec.z * range);

        RayTraceResult rayTrace = world.rayTraceBlocks(eyePos, endPos, false, true, false);
        double traceDist = range;

        if (rayTrace != null) {
            traceDist = rayTrace.hitVec.distanceTo(eyePos);
        }

        AxisAlignedBB searchBox = player.getEntityBoundingBox()
            .expand(lookVec.x * range, lookVec.y * range, lookVec.z * range)
            .grow(expand, expand, expand);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);
        Entity pointedEntity = null;

        for (EntityLivingBase entity : entities) {
            if (entity == player) continue;

            AxisAlignedBB entityBox = entity.getEntityBoundingBox().grow(expand);
            RayTraceResult entityRay = entityBox.calculateIntercept(eyePos, endPos);

            if (entityBox.contains(eyePos)) {
                if (traceDist >= 0.0D) {
                    pointedEntity = entity;
                    traceDist = 0.0D;
                }
            } else if (entityRay != null) {
                double entityDist = eyePos.distanceTo(entityRay.hitVec);
                if (entityDist < traceDist) {
                    pointedEntity = entity;
                    traceDist = entityDist;
                }
            }
        }

        return pointedEntity;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
