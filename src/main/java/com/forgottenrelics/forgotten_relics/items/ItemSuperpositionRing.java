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
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  vazkii.botania.common.core.helper.Vector3
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.items.ItemBaubleBase;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.core.helper.Vector3;

public class ItemSuperpositionRing
extends ItemBaubleBase
implements IBauble {
    public ItemSuperpositionRing() {
        super("superposition_ring");
        this.maxStackSize = 1;
        this.setUnlocalizedName("superposition_ring");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {
        if (GuiScreen.isShiftKeyDown()) {
            par3List.add(I18n.format((String)"item.ItemSuperpositionRing1.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemSuperpositionRing2.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemSuperpositionRing3.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemSuperpositionRing4.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.ItemSuperpositionRing5.lore", (Object[])new Object[0]));
            par3List.add(I18n.format((String)"item.FREmpty.lore", (Object[])new Object[0]));
            par3List.add(SuperpositionHandler.getBaubleTooltip(this.getBaubleType(par1ItemStack)));
        } else {
            par3List.add(I18n.format((String)"item.FRShiftTooltip.lore", (Object[])new Object[0]));
        }
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.EPIC;
    }

    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        super.onWornTick(itemstack, entity);
        if (entity.ticksExisted % RelicsConfigHandler.superpositionRingCheckInterval == 0 & !entity.world.isRemote & entity instanceof EntityPlayer && Math.random() <= RelicsConfigHandler.superpositionRingSwapChance) {
            List<EntityPlayer> players = SuperpositionHandler.getBaubleOwnersList(entity.world, CommonProxy.superpositionRing);
            players.remove(entity);
            if (players.size() > 0) {
                EntityPlayer randomPlayer = players.get((int)(Math.random() * (double)players.size()));
                Vector3 pos1 = Vector3.fromEntityCenter((Entity)entity);
                Vector3 pos2 = Vector3.fromEntityCenter((Entity)randomPlayer);
                if (randomPlayer.dimension != entity.dimension && entity.getServer() != null && randomPlayer.getServer() != null) {
                    int dim1 = entity.dimension;
                    int dim2 = randomPlayer.dimension;
                    randomPlayer.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP)randomPlayer, dim1, ((WorldServer)randomPlayer.world).getDefaultTeleporter());
                    entity.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP)entity, dim2, ((WorldServer)entity.world).getDefaultTeleporter());
                }
                entity.setPosition(pos2.x, pos2.y, pos2.z);
                randomPlayer.setPosition(pos1.x, pos1.y, pos1.z);
                entity.world.playSound(pos2.x, pos2.y, pos2.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * 0.2), false);
                randomPlayer.world.playSound(pos2.x, pos2.y, pos2.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (float)((double)0.8f + Math.random() * 0.2), false);
            }
        }
    }
}

