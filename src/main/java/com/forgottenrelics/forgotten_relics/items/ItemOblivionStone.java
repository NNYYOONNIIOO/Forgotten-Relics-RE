/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  thaumcraft.api.items.IWarpingGear
 */
package com.forgottenrelics.forgotten_relics.items;

import com.google.common.collect.ArrayListMultimap;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;

public class ItemOblivionStone
extends Item
implements IWarpingGear {
    public ItemOblivionStone() {
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setRegistryName("oblivion_stone");
        this.setUnlocalizedName("oblivion_stone");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        int damage = itemstack.getItemDamage();
        if (player.isSneaking()) {
            if (damage < 100) {
                damage += 100;
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                damage -= 100;
            }
            itemstack.setItemDamage(damage);
        } else {
            if (damage == 0 || damage == 1 || damage == 100 || damage == 101) {
                ++damage;
            } else if (damage == 2 || damage == 102) {
                damage -= 2;
            }
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, (float)((double)0.8f + Math.random() * (double)0.2f));
            itemstack.setItemDamage(damage);
        }
        player.setActiveHand(hand);
        return super.onItemRightClick(world, player, hand);
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {
        if (!(entity instanceof EntityPlayer) || entity.ticksExisted % 10 != 0) {
            return;
        }
        EntityPlayer player = (EntityPlayer)entity;
        int damage = itemstack.getItemDamage();
        if (damage >= 100 || !itemstack.hasTagCompound()) {
            return;
        }
        NBTTagCompound nbt = itemstack.getTagCompound();
        int[] arr = nbt.getIntArray("SupersolidID");
        int[] meta = nbt.getIntArray("SupersolidMetaID");
        ItemOblivionStone.consumeStuff(player, arr, meta, damage);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        for (int i = 0; i < 105; ++i) {
            ModelLoader.setCustomModelResourceLocation((Item)this, (int)i, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }
    }

    public static void consumeStuff(EntityPlayer player, int[] ID, int[] meta, int mode) {
        block16: {
            int filledStacks;
            int cycleCounter;
            HashMap<Integer, Object> stackMap;
            block17: {
                block15: {
                    stackMap = new HashMap<Integer, Object>();
                    cycleCounter = 0;
                    filledStacks = 0;
                    for (int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
                        if (player.inventory.mainInventory.get(slot) == ItemStack.EMPTY) continue;
                        ++filledStacks;
                        if (((ItemStack)player.inventory.mainInventory.get(slot)).getItem() == CommonProxy.oblivionStone) continue;
                        stackMap.put(slot, player.inventory.mainInventory.get(slot));
                    }
                    if (stackMap.size() == 0) {
                        return;
                    }
                    if (mode != 0) break block15;
                    for (int sID : ID) {
                        Iterator iterator = stackMap.keySet().iterator();
                        while (iterator.hasNext()) {
                            int slot = (Integer)iterator.next();
                            if (meta[cycleCounter] != -1) {
                                if (!(((ItemStack)stackMap.get(slot)).getItem() == Item.getItemById((int)sID) & ((ItemStack)stackMap.get(slot)).getItemDamage() == meta[cycleCounter])) continue;
                                player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                                continue;
                            }
                            if (((ItemStack)stackMap.get(slot)).getItem() != Item.getItemById((int)sID)) continue;
                            player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                        }
                        ++cycleCounter;
                    }
                    break block16;
                }
                if (mode != 1) break block17;
                for (int sID : ID) {
                    int slot;
                    HashMap localStackMap = new HashMap(stackMap);
                    ArrayListMultimap stackSizeMultimap = ArrayListMultimap.create();
                    Iterator iterator = stackMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        slot = (Integer)iterator.next();
                        if (meta[cycleCounter] != -1) {
                            if (((ItemStack)stackMap.get(slot)).getItem() == Item.getItemById((int)sID) && ((ItemStack)stackMap.get(slot)).getItemDamage() == meta[cycleCounter]) continue;
                            localStackMap.remove(slot);
                            continue;
                        }
                        if (((ItemStack)stackMap.get(slot)).getItem() == Item.getItemById((int)sID)) continue;
                        localStackMap.remove(slot);
                    }
                    iterator = localStackMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        slot = (Integer)iterator.next();
                        stackSizeMultimap.put((Object)((ItemStack)localStackMap.get(slot)).getCount(), (Object)slot);
                    }
                    while (localStackMap.size() > 1) {
                        int smallestStackSize = (Integer)Collections.min(stackSizeMultimap.keySet());
                        Collection smallestStacks = stackSizeMultimap.get((Object)smallestStackSize);
                        int slotWithSmallestStack = (Integer)Collections.max(smallestStacks);
                        player.inventory.setInventorySlotContents(slotWithSmallestStack, ItemStack.EMPTY);
                        stackSizeMultimap.remove((Object)smallestStackSize, (Object)slotWithSmallestStack);
                        localStackMap.remove(slotWithSmallestStack);
                    }
                    ++cycleCounter;
                }
                break block16;
            }
            if (mode != 2 || filledStacks < player.inventory.mainInventory.size()) break block16;
            for (int sID : ID) {
                int slot;
                HashMap localStackMap = new HashMap(stackMap);
                ArrayListMultimap stackSizeMultimap = ArrayListMultimap.create();
                Iterator smallestStackSize = stackMap.keySet().iterator();
                while (smallestStackSize.hasNext()) {
                    slot = (Integer)smallestStackSize.next();
                    if (meta[cycleCounter] != -1) {
                        if (((ItemStack)stackMap.get(slot)).getItem() == Item.getItemById((int)sID) && ((ItemStack)stackMap.get(slot)).getItemDamage() == meta[cycleCounter]) continue;
                        localStackMap.remove(slot);
                        continue;
                    }
                    if (((ItemStack)stackMap.get(slot)).getItem() == Item.getItemById((int)sID)) continue;
                    localStackMap.remove(slot);
                }
                smallestStackSize = localStackMap.keySet().iterator();
                while (smallestStackSize.hasNext()) {
                    slot = (Integer)smallestStackSize.next();
                    stackSizeMultimap.put((Object)((ItemStack)localStackMap.get(slot)).getCount(), (Object)slot);
                }
                if (localStackMap.size() > 0) {
                    int smallestStackSize2 = (Integer)Collections.min(stackSizeMultimap.keySet());
                    Collection smallestStacks = stackSizeMultimap.get((Object)smallestStackSize2);
                    int slotWithSmallestStack = (Integer)Collections.max(smallestStacks);
                    player.inventory.setInventorySlotContents(slotWithSmallestStack, ItemStack.EMPTY);
                    return;
                }
                ++cycleCounter;
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format((String)"item.OblivionStone1.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone2.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone2_more.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone3.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone4.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone5.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone6.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone7.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone8.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone9.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone10.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone11.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone12.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone13.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone14.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStone15.lore", (Object[])new Object[0]));
        } else if (GuiScreen.isCtrlKeyDown()) {
            list.add(I18n.format((String)"item.OblivionStoneCtrlList.lore", (Object[])new Object[0]));
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                int[] arr = nbt.getIntArray("SupersolidID");
                int[] meta = nbt.getIntArray("SupersolidMetaID");
                int counter = 0;
                if (arr.length <= RelicsConfigHandler.oblivionStoneSoftCap) {
                    for (int s : arr) {
                        Item something = Item.getItemById((int)s);
                        if (something != null) {
                            ItemStack displayStack = meta[counter] != -1 ? new ItemStack(something, 1, meta[counter]) : new ItemStack(something, 1, 0);
                            list.add(TextFormatting.GOLD + " - " + displayStack.getDisplayName());
                        }
                        ++counter;
                    }
                } else {
                    for (int s = 0; s < RelicsConfigHandler.oblivionStoneSoftCap; ++s) {
                        int randomID = (int)(Math.random() * 30.0);
                        Item something = Item.getItemById((int)arr[randomID]);
                        if (something == null) continue;
                        ItemStack displayStack = meta[randomID] != -1 ? new ItemStack(something, 1, meta[randomID]) : new ItemStack(something, 1, 0);
                        list.add(TextFormatting.GOLD + " - " + displayStack.getDisplayName());
                    }
                }
            }
        } else {
            list.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.OblivionStoneCtrlTooltip.lore", (Object[])new Object[0]));
            list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            int mode = stack.getItemDamage();
            if (mode < 100) {
                list.add(I18n.format((String)"item.OblivionStoneMode.lore", (Object[])new Object[0]) + " " + I18n.format((String)("item.OblivionMode" + mode + ".lore"), (Object[])new Object[0]));
            } else {
                list.add(I18n.format((String)"item.OblivionStoneMode.lore", (Object[])new Object[0]) + " " + I18n.format((String)"item.OblivionStoneDeactivated.lore", (Object[])new Object[0]));
            }
        }
        list.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
    }

    public int getWarp(ItemStack arg0, EntityPlayer arg1) {
        return 2;
    }
}

