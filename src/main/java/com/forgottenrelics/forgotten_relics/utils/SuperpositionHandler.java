/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.BaublesApi
 *  com.google.common.base.Predicates
 *  net.minecraft.block.Block
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.IMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  thaumcraft.api.aspects.Aspect
 *  thaumcraft.api.casters.ICaster
 *  thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss
 *  vazkii.botania.common.block.subtile.functional.SubTileHeiseiDream
 *  vazkii.botania.common.entity.EntityDoppleganger
 */
package com.forgottenrelics.forgotten_relics.utils;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.base.Predicates;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.packets.BurstMessage;
import com.forgottenrelics.forgotten_relics.packets.ICanSwingMySwordMessage;
import com.forgottenrelics.forgotten_relics.packets.NotificationMessage;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import vazkii.botania.common.block.subtile.functional.SubTileHeiseiDream;
import vazkii.botania.common.entity.EntityDoppleganger;

public class SuperpositionHandler {
    public static boolean validTeleport(Entity entity, double x_init, double y_init, double z_init, World world) {
        int x = (int)x_init;
        int y = (int)y_init;
        int z = (int)z_init;
        Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
        if (block != Blocks.AIR & block.isCollidable()) {
            for (int counter = 0; counter <= 32; ++counter) {
                if (!(!world.isAirBlock(new BlockPos(x, y + counter - 1, z)) & world.getBlockState(new BlockPos(x, y + counter - 1, z)).getBlock().isCollidable() & world.isAirBlock(new BlockPos(x, y + counter, z)) & world.isAirBlock(new BlockPos(x, y + counter + 1, z)))) continue;
                SuperpositionHandler.imposeBurst(entity.world, entity.dimension, entity.posX, entity.posY + 1.0, entity.posZ, 1.25f);
                entity.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                entity.setPositionAndUpdate((double)x + 0.5, (double)(y + counter), (double)z + 0.5);
                entity.world.playSound((double)x, (double)(y + counter), (double)z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                return true;
            }
        } else {
            for (int counter = 0; counter <= 32; ++counter) {
                if (!(!world.isAirBlock(new BlockPos(x, y - counter - 1, z)) & world.getBlockState(new BlockPos(x, y - counter - 1, z)).getBlock().isCollidable() & world.isAirBlock(new BlockPos(x, y - counter, z)) & world.isAirBlock(new BlockPos(x, y - counter + 1, z)))) continue;
                SuperpositionHandler.imposeBurst(entity.world, entity.dimension, entity.posX, entity.posY + 1.0, entity.posZ, 1.25f);
                entity.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                entity.setPositionAndUpdate((double)x + 0.5, (double)(y - counter), (double)z + 0.5);
                entity.world.playSound((double)x, (double)(y - counter), (double)z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                return true;
            }
        }
        return false;
    }

    public static boolean validTeleportRandomly(Entity entity, World world, int radius) {
        int d = radius * 2;
        double x = entity.posX + (Math.random() - 0.5) * (double)d;
        double y = entity.posY + (Math.random() - 0.5) * (double)d;
        double z = entity.posZ + (Math.random() - 0.5) * (double)d;
        return SuperpositionHandler.validTeleport(entity, x, y, z, world);
    }

    public static void setCasted(EntityPlayer player, int cooldown, boolean swing) {
        if (!player.world.isRemote) {
            Main.castingCooldowns.put(player, cooldown);
            if (swing) {
                player.setActiveHand(player.getActiveHand());
                Main.packetInstance.sendTo((IMessage)new ICanSwingMySwordMessage(true), (EntityPlayerMP)player);
            }
        }
    }

    public static boolean isOnCoodown(EntityPlayer player) {
        int cooldown;
        if (player.world.isRemote) {
            return false;
        }
        try {
            cooldown = Main.castingCooldowns.get(player);
        }
        catch (NullPointerException ex) {
            Main.castingCooldowns.put(player, 0);
            cooldown = 0;
        }
        return cooldown != 0;
    }

    public static void cryHavoc(World world, EntityPlayer player, int RANGE) {
        block1: {
            EntityLiving entity1;
            IMob mob;
            List mobs = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(player.posX - (double)RANGE, player.posY - (double)RANGE, player.posZ - (double)RANGE, player.posX + (double)RANGE + 1.0, player.posY + (double)RANGE + 1.0, player.posZ + (double)RANGE + 1.0), Predicates.instanceOf(IMob.class));
            if (mobs.size() <= 1) break block1;
            Iterator iterator = mobs.iterator();
            while (!(!iterator.hasNext() || (mob = (IMob)iterator.next()) instanceof EntityLiving && SubTileHeiseiDream.brainwashEntity((EntityLiving)(entity1 = (EntityLiving)mob), (List)mobs))) {
            }
        }
    }

    public static void imposeBurst(World world, int dimension, double x, double y, double z, float size) {
        if (!world.isRemote) {
            Main.packetInstance.sendToAllAround((IMessage)new BurstMessage(x, y, z, size), new NetworkRegistry.TargetPoint(dimension, x, y, z, 128.0));
        }
    }

    public static void sendNotification(EntityPlayer player, int type) {
        if (!player.world.isRemote) {
            Main.packetInstance.sendTo((IMessage)new NotificationMessage(type), (EntityPlayerMP)player);
        }
    }

    public static boolean isDamageTypeAbsolute(DamageSource source) {
        return source == DamageSource.OUT_OF_WORLD || source == DamageSource.STARVE || source instanceof DamageRegistryHandler.DamageSourceFate || source instanceof DamageRegistryHandler.DamageSourceOblivion || source instanceof DamageRegistryHandler.DamageSourceSoulDrain || source instanceof DamageRegistryHandler.DamageSourceTrueDamage || source instanceof DamageRegistryHandler.DamageSourceTrueDamageUndef;
    }

    public static boolean isEntityBlacklistedFromTelekinesis(EntityLivingBase entity) {
        return entity instanceof EntityThaumcraftBoss || entity instanceof EntityDoppleganger;
    }

    public static String getBaubleTooltip(BaubleType type) {
        String str = "";
        switch (type) {
            case AMULET: {
                str = I18n.format((String)"item.FRAmulet.lore", (Object[])new Object[0]);
                break;
            }
            case BELT: {
                str = I18n.format((String)"item.FRBelt.lore", (Object[])new Object[0]);
                break;
            }
            case RING: {
                str = I18n.format((String)"item.FRRing.lore", (Object[])new Object[0]);
                break;
            }
            default: {
                str = "";
            }
        }
        return str;
    }

    public static RayTraceResult getPointedBlock(EntityPlayer player, World world, float range) {
        double d0 = player.posX;
        double d1 = player.posY + 1.62 - player.posY;
        double d2 = player.posZ;
        Vec3d position = new Vec3d(d0, d1, d2);
        Vec3d look = player.getLook(1.0f);
        Vec3d finalvec = position.addVector(look.x * (double)range, look.y * (double)range, look.z * (double)range);
        RayTraceResult mop = world.rayTraceBlocks(position, finalvec);
        return mop;
    }

    public static List<EntityPlayer> getBaubleOwnersList(World world, Item baubleItem) {
        LinkedList<EntityPlayer> returnList = new LinkedList<EntityPlayer>();
        if (!world.isRemote && world.getMinecraftServer() != null) {
            ArrayList playersList = new ArrayList(world.getMinecraftServer().getPlayerList().getPlayers());
            for (int counter = playersList.size() - 1; counter >= 0; --counter) {
                if (!SuperpositionHandler.hasBauble((EntityPlayer)playersList.get(counter), baubleItem)) continue;
                returnList.add((EntityPlayer)playersList.get(counter));
            }
        }
        return returnList;
    }

    public static EntityPlayer findPlayerWithBauble(World world, int radius, Item baubleItem, EntityLivingBase entity) {
        LinkedList returnList = new LinkedList();
        if (!world.isRemote) {
            List playerList = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.posX - (double)radius, entity.posY - (double)radius, entity.posZ - (double)radius, entity.posX + (double)radius, entity.posY + (double)radius, entity.posZ + (double)radius));
            if (playerList.contains(entity)) {
                playerList.remove(entity);
            }
            for (int counter = playerList.size() - 1; counter >= 0; --counter) {
                if (!SuperpositionHandler.hasBauble((EntityPlayer)playerList.get(counter), baubleItem)) continue;
                returnList.add(playerList.get(counter));
            }
            if (returnList.size() > 0) {
                return (EntityPlayer)returnList.get((int)((double)(returnList.size() - 1) * Math.random()));
            }
            return null;
        }
        return null;
    }

    public static boolean hasBauble(EntityPlayer player, Item theBauble) {
        return BaublesApi.isBaubleEquipped((EntityPlayer)player, (Item)theBauble) != -1;
    }

    public static ItemStack getRandomValidWand(List<ItemStack> list, Aspect aspect) {
        return ItemStack.EMPTY;
    }

    public static List wandSearch(EntityPlayer player) {
        LinkedList<Object> itemStackList = new LinkedList<Object>();
        for (int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
            if (player.inventory.mainInventory.get(slot) == ItemStack.EMPTY || !(((ItemStack)player.inventory.mainInventory.get(slot)).getItem() instanceof ICaster)) continue;
            itemStackList.add(player.inventory.mainInventory.get(slot));
        }
        return itemStackList;
    }

    public static List itemSearch(EntityPlayer player, Item researchItem) {
        LinkedList<Object> itemStackList = new LinkedList<Object>();
        for (int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
            if (player.inventory.mainInventory.get(slot) == ItemStack.EMPTY || ((ItemStack)player.inventory.mainInventory.get(slot)).getItem() != researchItem) continue;
            itemStackList.add(player.inventory.mainInventory.get(slot));
        }
        return itemStackList;
    }

    public static ItemStack findFirst(EntityPlayer player, Item searchItem) {
        for (int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
            if (player.inventory.mainInventory.get(slot) == ItemStack.EMPTY || ((ItemStack)player.inventory.mainInventory.get(slot)).getItem() != searchItem) continue;
            return (ItemStack)player.inventory.mainInventory.get(slot);
        }
        return null;
    }

    public static int[] addInt(int[] series, int newInt) {
        int[] newSeries = new int[series.length + 1];
        for (int i = 0; i < series.length; ++i) {
            newSeries[i] = series[i];
        }
        newSeries[newSeries.length - 1] = newInt;
        return newSeries;
    }
}

