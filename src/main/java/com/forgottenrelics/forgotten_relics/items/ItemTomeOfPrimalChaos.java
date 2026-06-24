package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityPrimalOrb;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemTomeOfPrimalChaos extends Item implements IRechargable {

    private static final float MIN_VIS_COST = 0.1F;
    private static final float MAX_VIS_COST = 10.0F;

    public ItemTomeOfPrimalChaos() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("tome_of_primal_chaos");
        this.setRegistryName("tome_of_primal_chaos");
        this.setCreativeTab(Main.tabForgottenRelics);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return RelicsConfigHandler.primalChaosMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemChaosTome1.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemChaosTome2.lore"));
            tooltip.add(I18n.format("item.ItemChaosTome3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemChaosTome4.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemChaosTome5.lore", (int) RelicsConfigHandler.chaosTomeDamageCap));
            tooltip.add(I18n.format("item.ItemChaosTome6.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.chaosTomeVisMult));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
        tooltip.add(I18n.format("item.FREmpty.lore"));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        ItemStack activeItem = player.getActiveItemStack();
        if (activeItem.isEmpty() || activeItem.getItem() != this) return;

        int count = player.getItemInUseCount();
        int maxDuration = getMaxItemUseDuration(stack);

        // Every 2 ticks, spawn an orb
        if (count != maxDuration && count % 2 == 0 && !world.isRemote) {
            float visCost = MIN_VIS_COST + (float) (Math.random() * (MAX_VIS_COST - MIN_VIS_COST));
            int chargeCost = Math.max(1, Math.round(visCost));

            int charge = RechargeHelper.getCharge(stack);
            if (charge >= chargeCost) {
                RechargeHelper.consumeCharge(stack, player, chargeCost);
                spawnOrb(player);
            } else {
                player.stopActiveHand();
            }
        }
    }

    private void spawnOrb(EntityPlayer player) {
        if (player.world.isRemote) return;

        Vector3 originalPos = Vector3.fromEntityCenter(player);

        boolean seeker = Math.random() <= RelicsConfigHandler.chaosTomeHomingChance / 100.0D;

        EntityPrimalOrb orb = new EntityPrimalOrb(player.world, player, seeker);
        orb.setPosition(
            originalPos.x + (Math.random() - 0.5D) * 3.0D,
            originalPos.y + (Math.random() - 0.5D) * 1.0D,
            originalPos.z + (Math.random() - 0.5D) * 3.0D);

        // Initial outward motion from player
        Vector3 motion = new Vector3(
            orb.posX - originalPos.x,
            orb.posY - originalPos.y,
            orb.posZ - originalPos.z
        ).multiply(0.2D + Math.random() * 0.2D);

        orb.motionX = motion.x;
        orb.motionY = motion.y;
        orb.motionZ = motion.z;

        // Discord synergy: if player has Tome of Discord in inventory, orbs fly toward the pointed target
        if (RelicsConfigHandler.primalDiscordComboEnabled) {
            boolean hasDiscord = hasTomeOfDiscord(player);
            if (hasDiscord) {
                RayTraceResult rayTrace = player.rayTrace(128.0D, 1.0F);
                if (rayTrace != null) {
                    double targetX, targetY, targetZ;
                    if (rayTrace.typeOfHit == RayTraceResult.Type.ENTITY && rayTrace.entityHit != null) {
                        targetX = rayTrace.entityHit.posX;
                        targetY = rayTrace.entityHit.posY + rayTrace.entityHit.height / 2.0D;
                        targetZ = rayTrace.entityHit.posZ;
                    } else {
                        targetX = rayTrace.hitVec.x;
                        targetY = rayTrace.hitVec.y;
                        targetZ = rayTrace.hitVec.z;
                    }
                    orb.setTarget(targetX, targetY, targetZ);
                    orb.setDiscordAimed(true);
                }
            }
        }

        player.world.spawnEntity(orb);

        player.world.playSound(null, orb.posX, orb.posY, orb.posZ,
            net.minecraft.init.SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS,
            0.3F, 0.8F + player.world.rand.nextFloat() * 0.1F);
    }

    private boolean hasTomeOfDiscord(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemTomeOfDiscord) {
                return true;
            }
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemTomeOfDiscord) {
                return true;
            }
        }
        // Also check Baubles slots
        try {
            for (int i = 0; i < 7; i++) {
                ItemStack bauble = baubles.api.BaublesApi.getBaublesHandler(player).getStackInSlot(i);
                if (!bauble.isEmpty() && bauble.getItem() instanceof ItemTomeOfDiscord) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
