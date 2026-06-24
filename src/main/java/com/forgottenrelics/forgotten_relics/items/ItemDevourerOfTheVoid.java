package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.entities.EntityFluxRift;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.List;

public class ItemDevourerOfTheVoid extends Item implements IRechargable {

    private static final boolean TA_LOADED = Loader.isModLoaded("thaumicaugmentation");

    public ItemDevourerOfTheVoid() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("devourer_of_the_void");
        this.setRegistryName("devourer_of_the_void");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isActive(stack);
    }

    private boolean isActive(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", true);
    }

    private void setActive(ItemStack stack, boolean active) {
        ItemNBTHelper.setBoolean(stack, "IsActive", active);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        String status = isActive(stack)
            ? I18n.format("item.ItemDevourerOfTheVoidOn.lore")
            : I18n.format("item.ItemDevourerOfTheVoidOff.lore");

        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoid1.lore"));
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoid2.lore", RelicsConfigHandler.devourerRange));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoid3.lore"));
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoid4.lore", RelicsConfigHandler.devourerDrainRate));
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoid5.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemDevourerOfTheVoidToggle.lore"));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
        tooltip.add(I18n.format("item.FREmpty.lore"));
        tooltip.add(I18n.format("item.ItemDevourerOfTheVoidStatus.lore", status));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            boolean current = isActive(stack);
            setActive(stack, !current);
            world.playSound(null, player.posX, player.posY, player.posZ,
                current ? SoundEvents.BLOCK_NOTE_HAT : SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.5F, current ? 0.5F : 1.0F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer) || world.isRemote || !isActive(stack)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;

        // Don't work if both health and hunger are full
        if (player.getHealth() >= player.getMaxHealth() && player.getFoodStats().getFoodLevel() >= 20) {
            return;
        }

        // Find nearby Flux Rifts with enough energy (requires Thaumic Augmentation)
        if (!TA_LOADED) return;

        List<EntityFluxRift> rifts = world.getEntitiesWithinAABB(EntityFluxRift.class,
            new AxisAlignedBB(player.posX - RelicsConfigHandler.devourerRange, player.posY - RelicsConfigHandler.devourerRange, player.posZ - RelicsConfigHandler.devourerRange,
                player.posX + RelicsConfigHandler.devourerRange, player.posY + RelicsConfigHandler.devourerRange, player.posZ + RelicsConfigHandler.devourerRange));

        for (EntityFluxRift rift : rifts) {
            if (rift.getRiftSize() >= RelicsConfigHandler.devourerMinRiftSize) {
                // Drain 1 rift energy per tick
                int currentSize = rift.getRiftSize();
                int drain = Math.min(RelicsConfigHandler.devourerDrainRate, currentSize - 1);
                if (drain > 0) {
                    rift.setRiftSize(currentSize - drain);

                    // 1 energy = 1 HP + 1 hunger
                    for (int i = 0; i < drain; i++) {
                        if (player.getHealth() < player.getMaxHealth()) {
                            player.heal(1.0F);
                        }
                        if (player.getFoodStats().getFoodLevel() < 20) {
                            player.getFoodStats().addStats(1, 0.0F);
                        }
                    }

                    // Reduce stability slightly
                    rift.setRiftStability(rift.getRiftStability() - 0.5F);
                }
                break; // Only drain from one rift per tick
            }
        }
    }

    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.devourerMaxCharge;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
