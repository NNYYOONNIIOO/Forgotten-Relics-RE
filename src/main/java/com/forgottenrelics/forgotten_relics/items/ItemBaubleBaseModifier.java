/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.item.ItemStack
 */
package com.forgottenrelics.forgotten_relics.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public abstract class ItemBaubleBaseModifier
extends ItemBaubleBase {
    public ItemBaubleBaseModifier(String name) {
        super(name);
    }

    @Override
    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            HashMultimap attributes = HashMultimap.create();
            this.fillModifiers((Multimap<String, AttributeModifier>)attributes, stack);
            player.getAttributeMap().applyAttributeModifiers((Multimap)attributes);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            HashMultimap attributes = HashMultimap.create();
            this.fillModifiers((Multimap<String, AttributeModifier>)attributes, stack);
            player.getAttributeMap().removeAttributeModifiers((Multimap)attributes);
        }
    }

    abstract void fillModifiers(Multimap<String, AttributeModifier> var1, ItemStack var2);
}

