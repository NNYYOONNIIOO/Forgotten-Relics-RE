/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  vazkii.botania.client.render.IModelRegister
 *  vazkii.botania.common.core.BotaniaCreativeTab
 */
package com.forgottenrelics.forgotten_relics.items;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.render.IModelRegister;
import vazkii.botania.common.core.BotaniaCreativeTab;

public abstract class ItemModBase
extends Item
implements IModelRegister {
    public ItemModBase(String name) {
        this.setCreativeTab((CreativeTabs)BotaniaCreativeTab.INSTANCE);
        this.setRegistryName(new ResourceLocation("forgotten_relics", name));
        this.setUnlocalizedName(name);
    }

    @Nonnull
    public String getUnlocalizedNameInefficiently(@Nonnull ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack).replaceAll("item\\.", "item.forgotten_relics:");
    }

    @SideOnly(value=Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

