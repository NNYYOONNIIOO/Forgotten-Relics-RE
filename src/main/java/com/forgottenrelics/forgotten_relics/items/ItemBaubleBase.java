/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaublesApi
 *  baubles.api.IBauble
 *  baubles.api.cap.BaublesCapabilities
 *  baubles.api.cap.IBaublesItemHandler
 *  javax.annotation.Nonnull
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.items.ItemHandlerHelper
 *  vazkii.botania.api.item.ICosmeticAttachable
 *  vazkii.botania.api.item.IPhantomInkable
 *  vazkii.botania.client.core.helper.RenderHelper
 *  vazkii.botania.common.core.handler.ModSounds
 *  vazkii.botania.common.core.helper.ItemNBTHelper
 *  vazkii.botania.common.core.helper.PlayerHelper
 *  vazkii.botania.common.entity.EntityDoppleganger
 */
package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import com.forgottenrelics.forgotten_relics.items.ItemModBase;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.entity.EntityDoppleganger;

@Mod.EventBusSubscriber(modid="botania")
public abstract class ItemBaubleBase
extends ItemModBase
implements IBauble,
ICosmeticAttachable,
IPhantomInkable {
    private static final String TAG_HASHCODE = "playerHashcode";
    private static final String TAG_BAUBLE_UUID_MOST = "baubleUUIDMost";
    private static final String TAG_BAUBLE_UUID_LEAST = "baubleUUIDLeast";
    private static final String TAG_COSMETIC_ITEM = "cosmeticItem";
    private static final String TAG_PHANTOM_INK = "phantomInk";

    public ItemBaubleBase(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent evt) {
        if (!evt.getEntityLiving().world.isRemote && evt.getEntityLiving() instanceof EntityPlayer && !evt.getEntityLiving().world.getGameRules().getBoolean("keepInventory") && !((EntityPlayer)evt.getEntityLiving()).isSpectator()) {
            IBaublesItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer)((EntityPlayer)evt.getEntityLiving()));
            for (int i = 0; i < inv.getSlots(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.isEmpty() || !stack.getItem().getRegistryName().getResourceDomain().equals("botania")) continue;
                ((ItemBaubleBase)stack.getItem()).onUnequipped(stack, evt.getEntityLiving());
            }
        }
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!EntityDoppleganger.isTruePlayer((Entity)player)) {
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        }
        ItemStack toEquip = stack.copy();
        toEquip.setCount(1);
        if (this.canEquip(toEquip, (EntityLivingBase)player)) {
            if (world.isRemote) {
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)player);
            for (int i = 0; i < baubles.getSlots(); ++i) {
                if (!baubles.isItemValidForSlot(i, toEquip, (EntityLivingBase)player)) continue;
                ItemStack stackInSlot = baubles.getStackInSlot(i);
                IBauble baubleInSlot = (IBauble)stackInSlot.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                if (!stackInSlot.isEmpty() && baubleInSlot != null && !baubleInSlot.canUnequip(stackInSlot, (EntityLivingBase)player)) continue;
                baubles.setStackInSlot(i, ItemStack.EMPTY);
                baubles.setStackInSlot(i, toEquip);
                ((IBauble)toEquip.getItem()).onEquipped(toEquip, (EntityLivingBase)player);
                stack.shrink(1);
                PlayerHelper.grantCriterion((EntityPlayerMP)((EntityPlayerMP)player), (ResourceLocation)new ResourceLocation("botania", "main/bauble_wear"), (String)"code_triggered");
                if (!stackInSlot.isEmpty()) {
                    if (baubleInSlot != null) {
                        baubleInSlot.onUnequipped(stackInSlot, (EntityLivingBase)player);
                    }
                    if (stack.isEmpty()) {
                        return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot);
                    }
                    ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, (ItemStack)stackInSlot);
                }
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if (GuiScreen.isShiftKeyDown()) {
            this.addHiddenTooltip(par1ItemStack, world, stacks, flags);
        } else {
            this.addStringToTooltip(I18n.format((String)"botaniamisc.shiftinfo", (Object[])new Object[0]), stacks);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addHiddenTooltip(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        ItemStack cosmetic;
        String key = RenderHelper.getKeyDisplayString((String)"Baubles Inventory");
        if (key != null) {
            this.addStringToTooltip(I18n.format((String)"botania.baubletooltip", (Object[])new Object[]{key}), stacks);
        }
        if (!(cosmetic = this.getCosmeticItem(par1ItemStack)).isEmpty()) {
            this.addStringToTooltip(I18n.format((String)"botaniamisc.hasCosmetic", (Object[])new Object[]{cosmetic.getDisplayName()}), stacks);
        }
        if (this.hasPhantomInk(par1ItemStack)) {
            this.addStringToTooltip(I18n.format((String)"botaniamisc.hasPhantomInk", (Object[])new Object[0]), stacks);
        }
    }

    void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (ItemBaubleBase.getLastPlayerHashcode(stack) != player.hashCode()) {
            this.onEquippedOrLoadedIntoWorld(stack, player);
            ItemBaubleBase.setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.world.isRemote) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.equipBauble, SoundCategory.PLAYERS, 0.1f, 1.3f);
                PlayerHelper.grantCriterion((EntityPlayerMP)((EntityPlayerMP)player), (ResourceLocation)new ResourceLocation("botania", "main/bauble_wear"), (String)"code_triggered");
            }
            this.onEquippedOrLoadedIntoWorld(stack, player);
            ItemBaubleBase.setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
    }

    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
    }

    public ItemStack getCosmeticItem(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound((ItemStack)stack, (String)TAG_COSMETIC_ITEM, (boolean)true);
        if (cmp == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(cmp);
    }

    public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (!cosmetic.isEmpty()) {
            cmp = cosmetic.writeToNBT(cmp);
        }
        ItemNBTHelper.setCompound((ItemStack)stack, (String)TAG_COSMETIC_ITEM, (NBTTagCompound)cmp);
    }

    public boolean hasContainerItem(ItemStack stack) {
        return !this.getContainerItem(stack).isEmpty();
    }

    @Nonnull
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
        return this.getCosmeticItem(itemStack);
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTHelper.getLong((ItemStack)stack, (String)TAG_BAUBLE_UUID_MOST, (long)0L);
        if (most == 0L) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong((ItemStack)stack, (String)TAG_BAUBLE_UUID_MOST, (long)uuid.getMostSignificantBits());
            ItemNBTHelper.setLong((ItemStack)stack, (String)TAG_BAUBLE_UUID_LEAST, (long)uuid.getLeastSignificantBits());
            return ItemBaubleBase.getBaubleUUID(stack);
        }
        long least = ItemNBTHelper.getLong((ItemStack)stack, (String)TAG_BAUBLE_UUID_LEAST, (long)0L);
        return new UUID(most, least);
    }

    public static int getLastPlayerHashcode(ItemStack stack) {
        return ItemNBTHelper.getInt((ItemStack)stack, (String)TAG_HASHCODE, (int)0);
    }

    public static void setLastPlayerHashcode(ItemStack stack, int hash) {
        ItemNBTHelper.setInt((ItemStack)stack, (String)TAG_HASHCODE, (int)hash);
    }

    public boolean hasPhantomInk(ItemStack stack) {
        return ItemNBTHelper.getBoolean((ItemStack)stack, (String)TAG_PHANTOM_INK, (boolean)false);
    }

    public void setPhantomInk(ItemStack stack, boolean ink) {
        ItemNBTHelper.setBoolean((ItemStack)stack, (String)TAG_PHANTOM_INK, (boolean)ink);
    }
}

