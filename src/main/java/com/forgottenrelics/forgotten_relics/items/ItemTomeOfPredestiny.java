package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.packets.LightningMessage;
import com.forgottenrelics.forgotten_relics.packets.PlayerMotionUpdateMessage;
import com.forgottenrelics.forgotten_relics.packets.TelekinesisAttackMessage;
import com.forgottenrelics.forgotten_relics.packets.TelekinesisUseMessage;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemTomeOfPredestiny extends Item implements IRechargable {

    private static final float RANGE = 3F;

    private static HashMap<EntityPlayer, HashMap<String, Object>> globalTomeMap = new HashMap<>();

    private static final String TAG_TICKS_TILL_EXPIRE = "ticksTillExpire";
    private static final String TAG_TICKS_COOLDOWN = "ticksCooldown";
    private static final String TAG_TARGET = "target";
    private static final String TAG_DIST = "dist";
    private static final String TAG_RE_DIST = "reDist";

    private boolean verificationVariable = false;

    public ItemTomeOfPredestiny() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("tome_of_predestiny");
        this.setRegistryName("tome_of_predestiny");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    private HashMap<String, Object> getPlayerTomeData(EntityPlayer player) {
        if (globalTomeMap.containsKey(player)) {
            return globalTomeMap.get(player);
        }
        HashMap<String, Object> stats = new HashMap<>();
        stats.put(TAG_TICKS_TILL_EXPIRE, 0);
        stats.put(TAG_TICKS_COOLDOWN, 0);
        stats.put(TAG_TARGET, -1);
        stats.put(TAG_DIST, -1D);
        stats.put(TAG_RE_DIST, -1D);
        globalTomeMap.put(player, stats);
        return stats;
    }

    private int getIntTag(EntityPlayer player, String tag, int defaultVal) {
        HashMap<String, Object> data = getPlayerTomeData(player);
        if (data.containsKey(tag)) {
            return (Integer) data.get(tag);
        }
        return defaultVal;
    }

    private double getDoubleTag(EntityPlayer player, String tag, double defaultVal) {
        HashMap<String, Object> data = getPlayerTomeData(player);
        if (data.containsKey(tag)) {
            return (Double) data.get(tag);
        }
        return defaultVal;
    }

    private void setTag(EntityPlayer player, String tag, Object value) {
        HashMap<String, Object> data = getPlayerTomeData(player);
        data.put(tag, value);
        globalTomeMap.put(player, data);
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
    public int getMaxCharge(ItemStack itemStack, net.minecraft.entity.EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.predestinyMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, net.minecraft.entity.EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.PredestinyTome1.lore"));
            tooltip.add(I18n.format("item.PredestinyTome2.lore"));
            tooltip.add(I18n.format("item.PredestinyTome3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.PredestinyTome4.lore"));
            tooltip.add(I18n.format("item.PredestinyTome5.lore"));
            tooltip.add(I18n.format("item.PredestinyTome6.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.PredestinyTome7.lore"));
            tooltip.add(I18n.format("item.PredestinyTome8.lore", (int) RelicsConfigHandler.telekinesisTomeDamageMIN, (int) RelicsConfigHandler.telekinesisTomeDamageMAX));
            tooltip.add(I18n.format("item.PredestinyTome9.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.PredestinyTome10.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.PredestinyTomeVisCost.lore", RelicsConfigHandler.telekinesisVisCostPerTick));
            tooltip.add(I18n.format("item.PredestinyTomeLightningCost.lore", RelicsConfigHandler.telekinesisLightningVisCost));
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

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        onUsingTickServer(stack, player);
    }

    public void onUsingTickServer(ItemStack stack, EntityPlayer player) {
        World world = player.world;
        if (world.isRemote) return;

        int targetID = getIntTag(player, TAG_TARGET, -1);
        int ticksCooldown = getIntTag(player, TAG_TICKS_COOLDOWN, 0);

        if (ticksCooldown > 0) return;

        double length = RelicsConfigHandler.telekinesisReach;
        double re_dist = getDoubleTag(player, TAG_RE_DIST, -1D);

        EntityLivingBase target = null;

        if (targetID != -1 && world.getEntityByID(targetID) != null) {
            target = getExistingTarget(player, world, targetID, RANGE + 3);
        }

        if (target == null) {
            target = searchForTarget(player, world, RANGE);
        }

        if (target != null && re_dist == -1D) {
            re_dist = player.getDistance(target);
        }

        if (target != null) {
            if (SuperpositionHandler.isEntityBlacklistedFromTelekinesis(target)) return;

            // Consume tick Vis cost
            if (RechargeHelper.getCharge(stack) < RelicsConfigHandler.telekinesisVisCostPerTick) return;
            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.telekinesisVisCostPerTick);

            target.fallDistance = 0.0F;
            if (target.getActivePotionEffect(MobEffects.SLOWNESS) == null) {
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2, 3, true, false));
            }

            Vector3 target3 = Vector3.fromEntityCenter(player);
            if (player.isSneaking()) {
                target3 = target3.add(new Vector3(player.getLookVec()).multiply(re_dist));
            } else {
                target3 = target3.add(new Vector3(player.getLookVec()).multiply(length));
                re_dist = player.getDistance(target);
            }
            target3 = target3.add(new Vector3(0, 0.5, 0));

            double multiplier = target.getDistance(target3.x, target3.y, target3.z);
            float vectorPower = RelicsConfigHandler.telekinesisMoveForce;

            if (multiplier < RelicsConfigHandler.telekinesisCloseThreshold) {
                vectorPower = 0.333333F;
            } else if (multiplier >= RelicsConfigHandler.telekinesisFarThreshold) {
                vectorPower *= (float) (multiplier / RelicsConfigHandler.telekinesisFarThreshold);
            }

            setEntityMotionFromVector(target, target3, vectorPower);

            setTag(player, TAG_TARGET, target.getEntityId());
            setTag(player, TAG_DIST, length);
            setTag(player, TAG_RE_DIST, re_dist);
            setTag(player, TAG_TICKS_TILL_EXPIRE, RelicsConfigHandler.telekinesisTargetExpireTicks);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        int ticksTillExpire = getIntTag(player, TAG_TICKS_TILL_EXPIRE, 0);
        int ticksCooldown = getIntTag(player, TAG_TICKS_COOLDOWN, 0);

        if (ticksTillExpire <= 0) {
            setTag(player, TAG_TARGET, -1);
            setTag(player, TAG_DIST, -1D);
            setTag(player, TAG_RE_DIST, -1D);
        }

        ticksTillExpire--;
        if (ticksCooldown > 0) ticksCooldown--;

        setTag(player, TAG_TICKS_TILL_EXPIRE, ticksTillExpire);
        setTag(player, TAG_TICKS_COOLDOWN, ticksCooldown);

        // Client-side: detect right-click and left-click, send packets to server
        if (world.isRemote) {
            boolean usingItem = false;

            if (Minecraft.getMinecraft().currentScreen == null) {
                if (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()
                    && player.getHeldItemMainhand() == stack) {
                    Main.packetInstance.sendToServer(new TelekinesisUseMessage());
                    usingItem = true;
                }

                if (usingItem && player.getHeldItemMainhand() == stack && hasPressedAttackKey()) {
                    Main.packetInstance.sendToServer(new TelekinesisAttackMessage());
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean hasPressedAttackKey() {
        if (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() && !verificationVariable) {
            verificationVariable = true;
            return true;
        } else if (!Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() && verificationVariable) {
            verificationVariable = false;
            return false;
        }
        return false;
    }

    public void leftClick(EntityPlayer player) {
        World world = player.world;
        if (world.isRemote) return;

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || stack.getItem() != this) return;

        int targetID = getIntTag(player, TAG_TARGET, -1);
        if (targetID == -1) return;

        Entity target = world.getEntityByID(targetID);
        if (target == null || !(target instanceof EntityLivingBase)) return;

        EntityLivingBase targetEntity = (EntityLivingBase) target;

        if (player.getDistance(targetEntity) > RelicsConfigHandler.telekinesisLeftClickRange) return;

        if (player.isSneaking()) {
            // Sneak + Left-click: throw the entity away
            if (RechargeHelper.getCharge(stack) >= RelicsConfigHandler.telekinesisThrowVisCost) {
                RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.telekinesisThrowVisCost);

                Vec3d lookVec = player.getLookVec().normalize();
                targetEntity.motionX = lookVec.x * 3.0;
                targetEntity.motionY = lookVec.y * 1.5 + 0.5;
                targetEntity.motionZ = lookVec.z * 3.0;

                // Clear control state
                setTag(player, TAG_TARGET, -1);
                setTag(player, TAG_DIST, -1D);
                setTag(player, TAG_RE_DIST, -1D);
                setTag(player, TAG_TICKS_TILL_EXPIRE, 0);
                setTag(player, TAG_TICKS_COOLDOWN, RelicsConfigHandler.telekinesisThrowCooldown);
            }
        } else {
            // Left-click: lightning attack
            if (RechargeHelper.getCharge(stack) >= RelicsConfigHandler.telekinesisLightningVisCost) {
                RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.telekinesisLightningVisCost);

                // Spawn lightning bolts
                for (int i = 0; i < RelicsConfigHandler.telekinesisLightningCount; i++) {
                    double offsetX = (Math.random() - 0.5) * 2;
                    double offsetZ = (Math.random() - 0.5) * 2;
                    EntityLightningBolt lightning = new EntityLightningBolt(world,
                        targetEntity.posX + offsetX, targetEntity.posY, targetEntity.posZ + offsetZ, false);
                    world.spawnEntity(lightning);
                }

                world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 1.0F, 0.8F);

                // Deal 16-40 damage
                float damage = 16.0F + (float)(Math.random() * 24.0F);
                targetEntity.attackEntityFrom(new DamageRegistryHandler.DamageSourceTLightning(player), damage);

                // Lightning chain visual: thickness based on damage (16-40 -> width 0.03-0.1)
                float mainWidth = 0.03F + (damage - 16.0F) / 24.0F * 0.07F;

                // Send lightning arc from player to target
                Main.packetInstance.sendToAllAround(
                    new LightningMessage(player.posX, player.posY + player.height / 2.0D, player.posZ,
                        targetEntity.posX, targetEntity.posY + targetEntity.height / 2.0D, targetEntity.posZ, true, mainWidth),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(player.dimension,
                        player.posX, player.posY, player.posZ, 64.0D));

                // Chain to nearby entities (visual only, damage already handled by EntityLightningBolt)
                AxisAlignedBB chainBox = new AxisAlignedBB(
                    targetEntity.posX - 4, targetEntity.posY - 4, targetEntity.posZ - 4,
                    targetEntity.posX + 4, targetEntity.posY + 4, targetEntity.posZ + 4);
                List<EntityLivingBase> chainTargets = world.getEntitiesWithinAABB(EntityLivingBase.class, chainBox);
                chainTargets.remove(player);
                chainTargets.remove(targetEntity);

                // Limit to 3 chain targets
                while (chainTargets.size() > RelicsConfigHandler.telekinesisLightningChainTargets) {
                    chainTargets.remove((int)(Math.random() * chainTargets.size()));
                }

                float chainWidth = mainWidth * 0.6F;
                for (Entity chainTarget : chainTargets) {
                    Main.packetInstance.sendToAllAround(
                        new LightningMessage(targetEntity.posX, targetEntity.posY + targetEntity.height / 2.0D, targetEntity.posZ,
                            chainTarget.posX, chainTarget.posY + chainTarget.height / 2.0D, chainTarget.posZ, false, chainWidth),
                        new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(player.dimension,
                            targetEntity.posX, targetEntity.posY, targetEntity.posZ, 64.0D));
                }

                SuperpositionHandler.setCasted(player, RelicsConfigHandler.telekinesisLightningCooldown, true);
            }
        }
    }

    private void setEntityMotionFromVector(Entity entity, Vector3 originalPosVector, float modifier) {
        Vector3 entityVector = Vector3.fromEntityCenter(entity);
        Vector3 finalVector = new Vector3(originalPosVector.x, originalPosVector.y, originalPosVector.z).subtract(entityVector);

        if (finalVector.mag() > 1) {
            finalVector.normalize();
        }

        entity.motionX = finalVector.x * modifier;
        entity.motionY = finalVector.y * modifier;
        entity.motionZ = finalVector.z * modifier;

        // Sync motion for player entities
        if (entity instanceof EntityPlayerMP) {
            Main.packetInstance.sendTo(new PlayerMotionUpdateMessage(
                finalVector.x * modifier, finalVector.y * modifier, finalVector.z * modifier),
                (EntityPlayerMP) entity);
        }
    }

    private EntityLivingBase searchForTarget(EntityPlayer player, World world, float range) {
        EntityLivingBase newTarget = null;
        Vec3d lookVec = player.getLookVec();
        List<EntityLivingBase> entities = new ArrayList<>();
        int distance = 1;

        while (entities.size() == 0 && distance < 32) {
            Vec3d target = player.getPositionEyes(1.0F).add(lookVec.scale(distance));
            target = target.addVector(0, 0.5, 0);

            entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(target.x - range, target.y - range, target.z - range,
                                  target.x + range, target.y + range, target.z + range));

            entities.remove(player);
            distance++;
        }

        if (entities.size() > 0) {
            newTarget = entities.get(0);
        }

        return newTarget;
    }

    private EntityLivingBase getExistingTarget(EntityPlayer player, World world, int targetID, float range) {
        Entity target = world.getEntityByID(targetID);
        if (!(target instanceof EntityLivingBase)) return null;

        EntityLivingBase taritem = (EntityLivingBase) target;
        Vec3d lookVec = player.getLookVec();
        int distance = 1;

        while (distance < 32) {
            Vec3d targetVec = player.getPositionEyes(1.0F).add(lookVec.scale(distance));
            targetVec = targetVec.addVector(0, 0.5, 0);

            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(targetVec.x - range, targetVec.y - range, targetVec.z - range,
                                  targetVec.x + range, targetVec.y + range, targetVec.z + range));

            if (entities.contains(taritem)) {
                return taritem;
            }

            distance++;
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
