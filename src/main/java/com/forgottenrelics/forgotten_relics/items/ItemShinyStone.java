/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.IBauble
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 *  vazkii.botania.common.core.helper.Vector3
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityShinyEnergy;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.Vector3;

public class ItemShinyStone
extends ItemBaubleBase
implements IBauble {
    public ItemShinyStone() {
        super("shiny_stone");
        this.maxStackSize = 1;
        this.setUnlocalizedName("shiny_stone");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemShinyStone1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemShinyStone2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(SuperpositionHandler.getBaubleTooltip(this.getBaubleType(par1ItemStack)));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
    }

    public void spawnEnergyParticle(EntityLivingBase entity) {
        EntityShinyEnergy energy = new EntityShinyEnergy(entity.world, entity, entity, entity.posX, entity.posY, entity.posZ);
        Vector3 position = Vector3.fromEntityCenter((Entity)entity);
        Vector3 motVec = new Vector3((Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0, (Math.random() - 0.5) * 3.0);
        position.add(motVec);
        motVec.normalize().negate().multiply(0.1);
        energy.setPosition(position.x, position.y, position.z);
        energy.motionX = motVec.x;
        energy.motionY = motVec.y;
        energy.motionZ = motVec.z;
        entity.world.spawnEntity((Entity)energy);
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.CHARM;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        int healCheckrate;
        int healRate;
        super.onWornTick(itemstack, entity);
        if (!entity.world.isRemote & entity.ticksExisted % RelicsConfigHandler.shinyStoneCheckrate == 0) {
            double posX = ItemNBTHelper.getDouble((ItemStack)itemstack, (String)"LastX", (double)0.0);
            double posY = ItemNBTHelper.getDouble((ItemStack)itemstack, (String)"LastY", (double)0.0);
            double posZ = ItemNBTHelper.getDouble((ItemStack)itemstack, (String)"LastZ", (double)0.0);
            int Static = ItemNBTHelper.getInt((ItemStack)itemstack, (String)"Static", (int)0);
            if (entity.posX == posX & entity.posY == posY & entity.posZ == posZ) {
                int particleNumber = 3;
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"HealRate", (int)1);
                if (Static >= RelicsConfigHandler.shinyStoneStillThreshold2) {
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"HealRate", (int)2);
                    particleNumber = 2;
                }
                if (Static >= RelicsConfigHandler.shinyStoneStillThreshold3) {
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"HealRate", (int)3);
                    particleNumber = 1;
                }
                if (Static >= RelicsConfigHandler.shinyStoneStillThreshold4) {
                    ItemNBTHelper.setInt((ItemStack)itemstack, (String)"HealRate", (int)4);
                    particleNumber = 0;
                }
                for (int counter = particleNumber; counter <= 3; ++counter) {
                    this.spawnEnergyParticle(entity);
                }
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"Static", (int)(Static + RelicsConfigHandler.shinyStoneStillIncrement));
            } else {
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"Static", (int)0);
                ItemNBTHelper.setInt((ItemStack)itemstack, (String)"HealRate", (int)0);
            }
            ItemNBTHelper.setDouble((ItemStack)itemstack, (String)"LastX", (double)entity.posX);
            ItemNBTHelper.setDouble((ItemStack)itemstack, (String)"LastY", (double)entity.posY);
            ItemNBTHelper.setDouble((ItemStack)itemstack, (String)"LastZ", (double)entity.posZ);
        }
        if ((healRate = ItemNBTHelper.getInt((ItemStack)itemstack, (String)"HealRate", (int)0)) == 1 & entity.ticksExisted % (10 * (healCheckrate = (int)((double)RelicsConfigHandler.shinyStoneCheckrate / 4.0))) == 0) {
            entity.heal(RelicsConfigHandler.shinyStoneHealAmount);
        } else if (healRate == 2 & entity.ticksExisted % (5 * healCheckrate) == 0) {
            entity.heal(RelicsConfigHandler.shinyStoneHealAmount);
        } else if (healRate == 3 & entity.ticksExisted % (2 * healCheckrate) == 0) {
            entity.heal(RelicsConfigHandler.shinyStoneHealAmount);
        } else if (healRate == 4 & entity.ticksExisted % (1 * healCheckrate) == 0) {
            entity.heal(RelicsConfigHandler.shinyStoneHealAmount);
        }
    }
}

