package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntitySoulEnergy;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemSoulTome extends Item implements IRechargable {

    private static final float SOUL_DAMAGE_DIVISOR = 10.0F;

    public ItemSoulTome() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("soul_tome");
        this.setRegistryName("soul_tome");
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
        return RelicsConfigHandler.soulTomeMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemSoulTome1.lore", RelicsConfigHandler.soulTomeSearchRange));
            tooltip.add(I18n.format("item.ItemSoulTome2.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome4.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome5.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome6.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome7.lore", RelicsConfigHandler.soulTomeKnockbackRange));
            tooltip.add(I18n.format("item.ItemSoulTome8.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemSoulTome9.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.NuclearFuryVisCost.lore", RelicsConfigHandler.soulTomeVisCostPerTick));
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

        // Slow down the player while channeling
        player.motionX = 0;
        player.motionZ = 0;

        // Client side: render arena boundary particles (white, like Gaia Guardian)
        if (world.isRemote) {
            renderAuraBoundary(player);
            return;
        }

        int count = player.getItemInUseCount();
        int maxDuration = getMaxItemUseDuration(stack);
        int ticksUsed = maxDuration - count;

        if (ticksUsed < RelicsConfigHandler.soulTomeWarmupTicks) return; // warm-up

        // Get all entities in range
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
            new AxisAlignedBB(player.posX - RelicsConfigHandler.soulTomeSearchRange, player.posY - RelicsConfigHandler.soulTomeSearchRange, player.posZ - RelicsConfigHandler.soulTomeSearchRange,
                player.posX + RelicsConfigHandler.soulTomeSearchRange, player.posY + RelicsConfigHandler.soulTomeSearchRange, player.posZ + RelicsConfigHandler.soulTomeSearchRange));
        entities.removeIf(e -> e == player);

        // Close range knockback + lightning damage (within 4 blocks)
        for (EntityLivingBase target : entities) {
            if (player.getDistance(target) <= RelicsConfigHandler.soulTomeKnockbackRange) {
                int charge = RechargeHelper.getCharge(stack);
                if (charge < 2) continue;

                RechargeHelper.consumeCharge(stack, player, 2);

                // Knockback
                Vector3 entityVec = Vector3.fromEntityCenter(target);
                Vector3 playerVec = Vector3.fromEntityCenter(player);
                double dist = player.getDistance(target);
                Vector3 diff = new Vector3(
                    (entityVec.x - playerVec.x) / dist * 3.0F,
                    (entityVec.y - playerVec.y) / dist * 3.0F,
                    (entityVec.z - playerVec.z) / dist * 3.0F
                );

                // Lightning damage (20-100)
                float lightningDamage = 20.0F + (float) (80.0F * Math.random());
                target.attackEntityFrom(new DamageRegistryHandler.DamageSourceTLightning(player), lightningDamage);

                target.motionX = diff.x;
                target.motionY = diff.y + 1.0F;
                target.motionZ = diff.z;

                world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.PLAYERS, 1.0F, 0.8F);
            }
        }

        // Soul drain every 4 ticks
        if (ticksUsed % 4 == 0) {
            int charge = RechargeHelper.getCharge(stack);
            if (charge < 1) {
                player.stopActiveHand();
                return;
            }

            RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.soulTomeVisCostPerTick);

            if (!entities.isEmpty()) {
                EntityLivingBase randomEntity = entities.get((int) (entities.size() * Math.random()));

                float soulDamage = randomEntity.getMaxHealth() / SOUL_DAMAGE_DIVISOR;
                if (soulDamage > RelicsConfigHandler.soulTomeMaxDamage) soulDamage = RelicsConfigHandler.soulTomeMaxDamage;
                else if (soulDamage < RelicsConfigHandler.soulTomeMinDamage) soulDamage = RelicsConfigHandler.soulTomeMinDamage;

                randomEntity.attackEntityFrom(new DamageRegistryHandler.DamageSourceSoulDrain(player), soulDamage);
                spawnSoul(world, randomEntity, player);
            }
        }
    }

    private boolean spawnSoul(World world, EntityLivingBase source, EntityLivingBase target) {
        if (!world.isRemote) {
            Vector3 originalPos = Vector3.fromEntityCenter(source);
            Vector3 vector = new Vector3(originalPos.x, originalPos.y + 0.5, originalPos.z);

            EntitySoulEnergy orb = new EntitySoulEnergy(world, source, target);
            orb.setPosition(vector.x, vector.y, vector.z);

            // Motion toward target
            Vector3 targetPos = Vector3.fromEntityCenter(target);
            Vector3 motion = new Vector3(targetPos.x - vector.x, targetPos.y - vector.y, targetPos.z - vector.z);
            motion = motion.normalize().multiply(1.25F);
            orb.motionX = motion.x;
            orb.motionY = motion.y;
            orb.motionZ = motion.z;

            world.playSound(null, source.posX, source.posY, source.posZ,
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 2.0F,
                0.8F + (float) Math.random() * 0.2F);
            world.spawnEntity(orb);

            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void renderAuraBoundary(EntityPlayer player) {
        // White arena boundary ring at 20 blocks (like Gaia Guardian, but white)
        for (int i = 0; i < 360; i += 8) {
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;
            float m = 0.15F;
            float mv = 0.35F;

            float rad = i * (float) Math.PI / 180F;
            double x = player.posX - Math.cos(rad) * RelicsConfigHandler.soulTomeSearchRange;
            double y = player.posY;
            double z = player.posZ - Math.sin(rad) * RelicsConfigHandler.soulTomeSearchRange;

            Botania.proxy.wispFX(x, y, z, r, g, b, 0.5F,
                (float) (Math.random() - 0.5F) * m,
                (float) (Math.random() - 0.5F) * mv,
                (float) (Math.random() - 0.5F) * m,
                0.8F);
        }

        // Inner ring at knockback range (4 blocks)
        for (int i = 0; i < 360; i += 16) {
            float r = 1.0F;
            float g = 0.9F;
            float b = 0.9F;

            float rad = i * (float) Math.PI / 180F;
            double x = player.posX - Math.cos(rad) * RelicsConfigHandler.soulTomeKnockbackRange;
            double y = player.posY;
            double z = player.posZ - Math.sin(rad) * RelicsConfigHandler.soulTomeKnockbackRange;

            Botania.proxy.sparkleFX(x, y, z, r, g, b, 2.0F, 4);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
