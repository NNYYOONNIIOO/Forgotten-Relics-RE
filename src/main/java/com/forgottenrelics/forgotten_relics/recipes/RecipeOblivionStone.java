/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.registries.IForgeRegistryEntry$Impl
 */
package com.forgottenrelics.forgotten_relics.recipes;

import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeOblivionStone
extends IForgeRegistryEntry.Impl<IRecipe>
implements IRecipe {
    public RecipeOblivionStone() {
        this.setRegistryName("forge:oblivion_stone");
    }

    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
        ItemStack repairedStack = ItemStack.EMPTY;
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
        ItemStack voidStone = ItemStack.EMPTY;
        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack checkedItemStack = par1InventoryCrafting.getStackInSlot(i);
            if (checkedItemStack == ItemStack.EMPTY) continue;
            if (checkedItemStack.getItem() == CommonProxy.oblivionStone) {
                if (voidStone == ItemStack.EMPTY) {
                    voidStone = checkedItemStack;
                    continue;
                }
                return ItemStack.EMPTY;
            }
            stackList.add(checkedItemStack);
        }
        if (voidStone != ItemStack.EMPTY & stackList.size() == 1) {
            ItemStack savedStack = ((ItemStack)stackList.get(0)).copy();
            NBTTagCompound nbt = voidStone.hasTagCompound() ? voidStone.getTagCompound().copy() : new NBTTagCompound();
            int[] arr = nbt.getIntArray("SupersolidID");
            int[] meta = nbt.getIntArray("SupersolidMetaID");
            int counter = 0;
            if (arr.length >= RelicsConfigHandler.oblivionStoneHardCap) {
                return ItemStack.EMPTY;
            }
            for (int s : arr) {
                int metaD = meta[counter];
                ++counter;
                if (s == Item.getIdFromItem((Item)savedStack.getItem()) & metaD != -1 & metaD == savedStack.getItemDamage()) {
                    return ItemStack.EMPTY;
                }
                if (!(s == Item.getIdFromItem((Item)savedStack.getItem()) & metaD == -1)) continue;
                return ItemStack.EMPTY;
            }
            arr = SuperpositionHandler.addInt(arr, Item.getIdFromItem((Item)savedStack.getItem()));
            meta = !savedStack.isItemStackDamageable() ? SuperpositionHandler.addInt(meta, savedStack.getItemDamage()) : SuperpositionHandler.addInt(meta, -1);
            nbt.setIntArray("SupersolidID", arr);
            nbt.setIntArray("SupersolidMetaID", meta);
            ItemStack returnedStack = voidStone.copy();
            returnedStack.setTagCompound(nbt);
            return returnedStack;
        }
        if (voidStone != ItemStack.EMPTY & stackList.size() == 0) {
            return new ItemStack((Item)CommonProxy.oblivionStone, 1, voidStone.getItemDamage());
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public int getRecipeSize() {
        return 10;
    }

    public boolean matches(InventoryCrafting par1InventoryCrafting, World arg1) {
        ItemStack repairedStack = ItemStack.EMPTY;
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
        ItemStack voidStone = ItemStack.EMPTY;
        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack checkedItemStack = par1InventoryCrafting.getStackInSlot(i);
            if (checkedItemStack == ItemStack.EMPTY) continue;
            if (checkedItemStack.getItem() == CommonProxy.oblivionStone) {
                if (voidStone == ItemStack.EMPTY) {
                    voidStone = checkedItemStack;
                    continue;
                }
                return false;
            }
            stackList.add(checkedItemStack);
        }
        if (voidStone != ItemStack.EMPTY & stackList.size() == 1) {
            ItemStack savedStack = ((ItemStack)stackList.get(0)).copy();
            NBTTagCompound nbt = voidStone.hasTagCompound() ? voidStone.getTagCompound().copy() : new NBTTagCompound();
            int[] arr = nbt.getIntArray("SupersolidID");
            int[] meta = nbt.getIntArray("SupersolidMetaID");
            int counter = 0;
            if (arr.length >= RelicsConfigHandler.oblivionStoneHardCap) {
                return false;
            }
            for (int s : arr) {
                int metaD = meta[counter];
                ++counter;
                if (s == Item.getIdFromItem((Item)savedStack.getItem()) & metaD != -1 & metaD == savedStack.getItemDamage()) {
                    return false;
                }
                if (!(s == Item.getIdFromItem((Item)savedStack.getItem()) & metaD == -1)) continue;
                return false;
            }
            return true;
        }
        return voidStone != ItemStack.EMPTY & stackList.size() == 0;
    }

    public boolean canFit(int width, int height) {
        return true;
    }
}

