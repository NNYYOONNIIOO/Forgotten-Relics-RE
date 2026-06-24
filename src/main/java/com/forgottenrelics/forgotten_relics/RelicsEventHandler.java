/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaublesApi
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.registries.IForgeRegistry
 *  thaumcraft.api.items.RechargeHelper
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 */
package com.forgottenrelics.forgotten_relics;

import baubles.api.BaublesApi;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemParadox;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfBrokenFates;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import com.forgottenrelics.forgotten_relics.recipes.FRRecipes;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class RelicsEventHandler {
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        FRRecipes.initRecipes((IForgeRegistry<IRecipe>)event.getRegistry());
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP & !event.getEntity().world.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
            if (Main.castingCooldowns.containsKey(player)) {
                int cooldown = Main.castingCooldowns.get(player);
                if (cooldown > 0) {
                    Main.castingCooldowns.put((EntityPlayer)player, --cooldown);
                    return;
                }
                return;
            }
            Main.castingCooldowns.put((EntityPlayer)player, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void miningStuff(PlayerEvent.BreakSpeed event) {
        float miningBoost = 1.0f;
        if (SuperpositionHandler.hasBauble(event.getEntityPlayer(), CommonProxy.AdvancedMiningCharm)) {
            miningBoost += RelicsConfigHandler.advancedMiningCharmBoost;
        }
        if (SuperpositionHandler.hasBauble(event.getEntityPlayer(), CommonProxy.MiningCharm)) {
            miningBoost += RelicsConfigHandler.miningCharmBoost;
        }
        event.setNewSpeed(event.getNewSpeed() * miningBoost);
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ItemParadox) {
            for (int x = 0; x < event.getToolTip().size(); ++x) {
                if (!((String)event.getToolTip().get(x)).contains(I18n.format((String)"attribute.name.generic.attackDamage", (Object[])new Object[0])) && !((String)event.getToolTip().get(x)).contains(I18n.format((String)"Attack Damage", (Object[])new Object[0]))) continue;
                event.getToolTip().set(x, " " + I18n.format((String)"item.ItemParadoxDamage_1.lore", (Object[])new Object[0]) + (int)RelicsConfigHandler.paradoxDamageCap + I18n.format((String)"item.ItemParadoxDamage_2.lore", (Object[])new Object[0]));
                return;
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onEntityAttacked(LivingAttackEvent event) {
        float redirectedAmount;
        Entity randomEntity;
        List entityList;
        if (event.getSource().getTrueSource() instanceof EntityPlayer & !event.isCanceled()) {
            EntityPlayer attackerPlayer = (EntityPlayer)event.getSource().getTrueSource();
            if (attackerPlayer.inventory.hasItemStack(new ItemStack((Item)CommonProxy.chaosCore)) & Math.random() < 0.45 && (entityList = event.getEntity().world.getEntitiesWithinAABBExcludingEntity(event.getEntity(), new AxisAlignedBB(event.getEntity().posX - 16.0, event.getEntity().posY - 16.0, event.getEntity().posZ - 16.0, event.getEntity().posX + 16.0, event.getEntity().posY + 16.0, event.getEntity().posZ + 16.0))) != null & entityList.size() > 0) {
                randomEntity = (Entity)entityList.get((int)(Math.random() * (double)(entityList.size() - 1)));
                redirectedAmount = event.getAmount() * (float)(Math.random() * 2.0);
                if (Math.random() < 0.15) {
                    attackerPlayer.attackEntityFrom(event.getSource(), redirectedAmount);
                } else {
                    randomEntity.attackEntityFrom(event.getSource(), redirectedAmount);
                }
                event.setCanceled(true);
            }
        }
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            if (!event.isCanceled() & player.inventory.hasItemStack(new ItemStack((Item)CommonProxy.chaosCore)) & Math.random() < 0.42 && (entityList = event.getEntity().world.getEntitiesWithinAABBExcludingEntity(event.getEntity(), new AxisAlignedBB(event.getEntity().posX - 16.0, event.getEntity().posY - 16.0, event.getEntity().posZ - 16.0, event.getEntity().posX + 16.0, event.getEntity().posY + 16.0, event.getEntity().posZ + 16.0))) != null & entityList.size() > 0) {
                randomEntity = (Entity)entityList.get((int)(Math.random() * (double)(entityList.size() - 1)));
                redirectedAmount = event.getAmount() * (float)(Math.random() * 2.0);
                randomEntity.attackEntityFrom(event.getSource(), redirectedAmount);
                event.setCanceled(true);
            }
            if (!event.isCanceled() & SuperpositionHandler.hasBauble(player, CommonProxy.arcanum) & Math.random() < (double)RelicsConfigHandler.nebulousCoreDodgeChance & !SuperpositionHandler.isDamageTypeAbsolute(event.getSource())) {
                for (int counter = 0; counter <= 32; ++counter) {
                    if (!SuperpositionHandler.validTeleportRandomly(event.getEntity(), event.getEntity().world, 16)) continue;
                    event.getEntity().hurtResistantTime = 20;
                    event.setCanceled(true);
                    break;
                }
            }
            if (!event.isCanceled() & SuperpositionHandler.hasBauble(player, CommonProxy.darkSunRing) & Main.darkRingDamageNegations.contains(event.getSource().damageType)) {
                if (RelicsConfigHandler.darkSunRingHealLimit) {
                    if (event.getEntity().hurtResistantTime == 0) {
                        player.heal(event.getAmount());
                        event.getEntity().hurtResistantTime = 20;
                    }
                } else {
                    player.heal(event.getAmount());
                }
                event.setCanceled(true);
            } else if (!event.isCanceled() & SuperpositionHandler.hasBauble(player, CommonProxy.darkSunRing) & event.getSource().getTrueSource() != null & Math.random() <= (double)RelicsConfigHandler.darkSunRingDeflectChance && player.hurtResistantTime == 0) {
                player.hurtResistantTime = 20;
                event.getSource().getTrueSource().attackEntityFrom(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onFalseJusticeHurt(LivingHurtEvent event) {
        // When a player with False Justice takes damage
        if (event.getEntity() instanceof EntityPlayer && !event.isCanceled()) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.inventory.hasItemStack(new ItemStack(CommonProxy.falseJustice))) {
                DamageSource source = event.getSource();
                // Skip if already true damage to prevent infinite loop
                if (source instanceof DamageRegistryHandler.DamageSourceTrueDamage
                    || source instanceof DamageRegistryHandler.DamageSourceTrueDamageUndef) {
                    return;
                }
                float amount = event.getAmount();
                event.setCanceled(true);
                DamageSource trueSource = source.getTrueSource() != null
                    ? new DamageRegistryHandler.DamageSourceTrueDamage(source.getTrueSource())
                    : new DamageRegistryHandler.DamageSourceTrueDamageUndef();
                player.hurtResistantTime = 0;
                player.attackEntityFrom(trueSource, amount);
            }
        }
        // When a player with False Justice deals damage
        if (event.getSource().getTrueSource() instanceof EntityPlayer && !event.isCanceled()) {
            EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
            if (attacker.inventory.hasItemStack(new ItemStack(CommonProxy.falseJustice))) {
                DamageSource source = event.getSource();
                if (source instanceof DamageRegistryHandler.DamageSourceTrueDamage
                    || source instanceof DamageRegistryHandler.DamageSourceTrueDamageUndef) {
                    return;
                }
                float amount = event.getAmount();
                event.setCanceled(true);
                DamageRegistryHandler.DamageSourceTrueDamage trueSource = new DamageRegistryHandler.DamageSourceTrueDamage(attacker);
                event.getEntity().hurtResistantTime = 0;
                event.getEntity().attackEntityFrom(trueSource, amount);
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (event.getAmount() > 0.0f && event.getEntity() instanceof EntityPlayer & !event.isCanceled()) {
            EntityPlayer aegisOwner;
            EntityPlayer player = (EntityPlayer)event.getEntity();
            if (!event.isCanceled() & player.inventory.hasItemStack(new ItemStack((Item)CommonProxy.chaosCore))) {
                event.setAmount(event.getAmount() * (float)(Math.random() * 2.0));
            }
            if (!event.isCanceled() & event.getAmount() > 100.0f & !SuperpositionHandler.isDamageTypeAbsolute(event.getSource()) & SuperpositionHandler.hasBauble(player, CommonProxy.darkSunRing)) {
                SuperpositionHandler.sendNotification(player, 2);
                event.setCanceled(true);
            }
            if (SuperpositionHandler.hasBauble(player, CommonProxy.darkSunRing) & !event.isCanceled() & Math.random() <= 0.25 & !SuperpositionHandler.isDamageTypeAbsolute(event.getSource())) {
                event.setAmount(event.getAmount() + event.getAmount() * (float)Math.random());
            }
            if (!event.getEntity().world.isRemote & !SuperpositionHandler.hasBauble(player, CommonProxy.AncientAegis) & !event.isCanceled() && (aegisOwner = SuperpositionHandler.findPlayerWithBauble(event.getEntity().world, 32, CommonProxy.AncientAegis, (EntityLivingBase)player)) != null) {
                aegisOwner.attackEntityFrom(event.getSource(), event.getAmount() * 0.4f);
                event.setAmount(event.getAmount() * 0.6f);
            }
            if (SuperpositionHandler.hasBauble(player, CommonProxy.AncientAegis) & !event.isCanceled() & !SuperpositionHandler.isDamageTypeAbsolute(event.getSource())) {
                event.setAmount(event.getAmount() * (1.0f - RelicsConfigHandler.ancientAegisDamageReduction));
            }
            if (!(event.getSource() instanceof DamageRegistryHandler.DamageSourceSuperposition) & !(event.getSource() instanceof DamageRegistryHandler.DamageSourceSuperpositionDefined) && SuperpositionHandler.hasBauble(player, CommonProxy.superpositionRing) & !event.isCanceled()) {
                DamageSource altSource = event.getSource().getTrueSource() != null ? new DamageRegistryHandler.DamageSourceSuperpositionDefined(event.getSource().getTrueSource()) : new DamageRegistryHandler.DamageSourceSuperposition();
                if (event.getSource().isUnblockable()) {
                    altSource.setDamageBypassesArmor();
                }
                if (event.getSource().isDamageAbsolute()) {
                    altSource.setDamageIsAbsolute();
                }
                altSource.damageType = event.getSource().getDamageType();
                List<EntityPlayer> superpositioned = SuperpositionHandler.getBaubleOwnersList(player.world, CommonProxy.superpositionRing);
                if (superpositioned.contains(player)) {
                    superpositioned.remove(player);
                }
                if (superpositioned.size() > 0) {
                    double percent = 0.12 + Math.random() * 0.62;
                    float splitAmount = (float)((double)event.getAmount() * percent);
                    for (int counter = superpositioned.size() - 1; counter >= 0; --counter) {
                        EntityPlayer cPlayer = superpositioned.get(counter);
                        cPlayer.attackEntityFrom(altSource, splitAmount / (float)superpositioned.size());
                    }
                    event.setAmount(event.getAmount() - splitAmount);
                }
            }
            if (RechargeHelper.consumeCharge((ItemStack)BaublesApi.getBaublesHandler((EntityPlayer)player).getStackInSlot(0), (EntityLivingBase)player, (int)((int)(event.getAmount() * 8.0f * RelicsConfigHandler.oblivionAmuletVisMult))) && !event.isCanceled() & SuperpositionHandler.hasBauble(player, CommonProxy.oblivionAmulet) & !SuperpositionHandler.isDamageTypeAbsolute(event.getSource())) {
                ItemStack oblivionAmulet = BaublesApi.getBaublesHandler((EntityPlayer)player).getStackInSlot(0);
                ItemNBTHelper.setFloat((ItemStack)oblivionAmulet, (String)"IDamageStored", (float)(ItemNBTHelper.getFloat((ItemStack)oblivionAmulet, (String)"IDamageStored", (float)0.0f) + event.getAmount()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // Omega Core check
            for (ItemStack stack : player.inventory.mainInventory) {
                if (!stack.isEmpty() && stack.getItem() == CommonProxy.omegaCore) {
                    event.setCanceled(true);
                    player.setHealth(1.0f);
                    return;
                }
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                if (!stack.isEmpty() && stack.getItem() == CommonProxy.omegaCore) {
                    event.setCanceled(true);
                    player.setHealth(1.0f);
                    return;
                }
            }

            // Tome of Broken Fates check
            for (ItemStack stack : player.inventory.mainInventory) {
                if (!stack.isEmpty() && stack.getItem() instanceof ItemTomeOfBrokenFates) {
                    ItemTomeOfBrokenFates tome = (ItemTomeOfBrokenFates) stack.getItem();
                    if (tome.triggerFateProtection(player, stack)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
            for (ItemStack stack : player.inventory.offHandInventory) {
                if (!stack.isEmpty() && stack.getItem() instanceof ItemTomeOfBrokenFates) {
                    ItemTomeOfBrokenFates tome = (ItemTomeOfBrokenFates) stack.getItem();
                    if (tome.triggerFateProtection(player, stack)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    // Research trigger: being hit by Crimson Cultist's orb
    @SubscribeEvent
    public void onCrimsonOrbHit(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote && !event.isCanceled()) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (event.getSource() != null && event.getSource().getImmediateSource() instanceof EntityGolemOrb) {
                EntityGolemOrb orb = (EntityGolemOrb) event.getSource().getImmediateSource();
                EntityLivingBase thrower = orb.getThrower();
                if (thrower instanceof EntityCultist) {
                    // Simulate scanning a Crimson Cultist to trigger the !CrimsonCultist scan trigger
                    ScanningManager.scanTheThing(player, thrower);
                }
            }
        }
    }

    // Research trigger: killing a Gaia Guardian
    @SubscribeEvent
    public void onGaiaGuardianDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityDoppleganger && !event.getEntity().world.isRemote) {
            if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
                // Simulate scanning a Gaia Guardian to trigger the !GaiaGuardian scan trigger
                ScanningManager.scanTheThing(player, event.getEntity());
            }
        }
    }
}

