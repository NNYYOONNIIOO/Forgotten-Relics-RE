package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityCrimsonOrb;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.ArrayList;
import java.util.List;

public class ItemCrimsonSpell extends Item implements IRechargable {

    private static final float DAMAGE_MIN = 42.0F;
    private static final float DAMAGE_MAX = 100.0F;

    public ItemCrimsonSpell() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("crimson_spell");
        this.setRegistryName("crimson_spell");
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
        return RelicsConfigHandler.crimsonSpellMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemCrimsonSpell1.lore"));
            tooltip.add(I18n.format("item.ItemCrimsonSpell2.lore"));
            tooltip.add(I18n.format("item.ItemCrimsonSpell3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemCrimsonSpell4.lore"));
            tooltip.add(I18n.format("item.ItemCrimsonSpell5.lore"));
            tooltip.add(I18n.format("item.ItemCrimsonSpell6.lore", (int) RelicsConfigHandler.crimsonSpellDamageMIN, (int) RelicsConfigHandler.crimsonSpellDamageMAX));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.crimsonSpellVisCost));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote && !SuperpositionHandler.isOnCoodown(player)) {
            int charge = RechargeHelper.getCharge(stack);
            if (charge < RelicsConfigHandler.crimsonSpellVisCost) {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }

            int gRange = (int)RelicsConfigHandler.crimsonSpellSearchRange;
            EntityLivingBase target = null;

            // Search along player's look vector
            Vec3d lookVec = player.getLookVec();
            List<EntityLivingBase> entities = new ArrayList<>();
            int distance = 1;
            while (entities.size() == 0 && distance < gRange) {
                float superposition = 0;
                if (distance > 10) superposition = 3.0F;
                if (distance > 20) superposition = 5.0F;

                double vecX = player.posX + lookVec.x * distance;
                double vecY = player.posY + player.getEyeHeight() + lookVec.y * distance;
                double vecZ = player.posZ + lookVec.z * distance;

                entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    new AxisAlignedBB(vecX - (RelicsConfigHandler.crimsonSpellSearchRange + superposition), vecY - (RelicsConfigHandler.crimsonSpellSearchRange + superposition), vecZ - (RelicsConfigHandler.crimsonSpellSearchRange + superposition),
                        vecX + (RelicsConfigHandler.crimsonSpellSearchRange + superposition), vecY + (RelicsConfigHandler.crimsonSpellSearchRange + superposition), vecZ + (RelicsConfigHandler.crimsonSpellSearchRange + superposition)));

                if (entities.contains(player)) entities.remove(player);
                distance++;
            }

            boolean notFound = false;
            if (entities.size() == 0) {
                notFound = true;
                entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    new AxisAlignedBB(player.posX - gRange, player.posY - gRange, player.posZ - gRange,
                        player.posX + gRange, player.posY + gRange, player.posZ + gRange));
            }

            if (entities.size() > 0 && notFound) {
                for (int counter = entities.size() - 1; counter >= 0; counter--) {
                    if (!entities.get(counter).canEntityBeSeen(player)) {
                        entities.remove(counter);
                        counter = entities.size();
                    }
                }
            }

            if (entities.contains(player)) entities.remove(player);

            // Consume Vis
            if (RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.crimsonSpellVisCost)) {
                if (entities.size() > 0) {
                    target = entities.get((int) ((entities.size() - 1) * Math.random()));
                }

                spawnOrb(world, player, target);

                // Haste reduces cooldown: each level -10%, max -70%
                int baseCooldown = RelicsConfigHandler.crimsonSpellCooldown;
                int hasteLevel = 0;
                PotionEffect haste = player.getActivePotionEffect(MobEffects.HASTE);
                if (haste != null) {
                    hasteLevel = haste.getAmplifier() + 1;
                }
                float reduction = Math.min(hasteLevel * 0.1F, 0.7F);
                int cooldown = Math.max(1, (int) (baseCooldown * (1.0F - reduction)));
                SuperpositionHandler.setCasted(player, cooldown, true);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public boolean spawnOrb(World world, EntityPlayer player, EntityLivingBase target) {
        if (!world.isRemote) {
            Vector3 originalPos = Vector3.fromEntityCenter(player);
            Vec3d lookVec = player.getLookVec();
            Vector3 vector = originalPos.add(new Vector3(lookVec.x, lookVec.y, lookVec.z).multiply(1.0F));
            vector = new Vector3(vector.x, vector.y + 0.5, vector.z);

            Vector3 motion = new Vector3(lookVec.x, lookVec.y, lookVec.z).multiply(0.75F);

            EntityCrimsonOrb orb = new EntityCrimsonOrb(world, player, target, true);
            orb.setPosition(vector.x, vector.y, vector.z);
            orb.motionX = motion.x;
            orb.motionY = motion.y;
            orb.motionZ = motion.z;

            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.6F, 0.8F + (float) Math.random() * 0.2F);
            world.spawnEntity(orb);

            SuperpositionHandler.imposeBurst(world, player.dimension, vector.x, vector.y, vector.z, 1.0F);

            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
