package com.forgottenrelics.forgotten_relics.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.utils.RelicsKeybindHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.List;

/**
 * Ring of Discord - Bauble (Ring slot).
 * Press X key to toggle discord mode on/off.
 * When discord mode is enabled:
 * - The ring has enchantment glint
 * - Using Tome of Discord (right-click) will teleport without range limit
 * Default state: disabled (no glint, normal tome range)
 * No Vis cost for the ring itself; the tome still consumes Vis.
 */
public class ItemRingOfDiscord extends ItemBaubleBase {

    private static final String TAG_DISCORD_ENABLED = "discordEnabled";

    public ItemRingOfDiscord() {
        super("discord_ring");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isDiscordEnabled(stack);
    }

    public static boolean isDiscordEnabled(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, TAG_DISCORD_ENABLED, false);
    }

    public static void setDiscordEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, TAG_DISCORD_ENABLED, enabled);
    }

    /**
     * Check if the player has the Ring of Discord equipped with discord mode enabled.
     */
    public static boolean isDiscordActive(EntityPlayer player) {
        // Find the ring in bauble slots
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack baubleStack = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (!baubleStack.isEmpty() && baubleStack.getItem() instanceof ItemRingOfDiscord) {
                return isDiscordEnabled(baubleStack);
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addHiddenTooltip(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags) {
        super.addHiddenTooltip(stack, world, tooltip, flags);
        tooltip.add(I18n.format("item.ItemDiscordRing1.lore"));
        tooltip.add(I18n.format("item.ItemDiscordRing2.lore"));
        tooltip.add(I18n.format("item.ItemDiscordRing3.lore"));
        tooltip.add(I18n.format("item.FREmpty.lore"));
        // Show current mode status
        boolean enabled = isDiscordEnabled(stack);
        String statusKey = enabled ? "item.ItemDiscordRing5.lore" : "item.ItemDiscordRing4.lore";
        tooltip.add(I18n.format(statusKey));
        tooltip.add(I18n.format("item.FREmpty.lore"));
        // Show bound key name
        String keyName = RelicsKeybindHandler.discordRingKey != null
            ? RelicsKeybindHandler.discordRingKey.getDisplayName()
            : "X";
        tooltip.add(I18n.format("item.ItemDiscordRing6.lore") + " " + keyName);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
    }
}
