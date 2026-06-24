package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityDarkMatterOrb;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class ItemEldritchSpell extends Item implements IRechargable {

    private static final boolean TA_LOADED = Loader.isModLoaded("thaumicaugmentation");

    public ItemEldritchSpell() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("eldritch_spell");
        this.setRegistryName("eldritch_spell");
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
        return RelicsConfigHandler.eldritchSpellMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemEldritchSpell1.lore"));
            tooltip.add(I18n.format("item.ItemEldritchSpell2.lore"));
            tooltip.add(I18n.format("item.ItemEldritchSpell3.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemEldritchSpell4.lore"));
            tooltip.add(I18n.format("item.ItemEldritchSpell5.lore", (int) RelicsConfigHandler.eldritchSpellDamage));
            tooltip.add(I18n.format("item.ItemEldritchSpell6.lore"));
            if (TA_LOADED) {
                tooltip.add(I18n.format("item.FREmpty.lore"));
                tooltip.add(I18n.format("item.ItemEldritchSpell7.lore"));
            }
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.FateTomeVisCost.lore", RelicsConfigHandler.eldritchSpellVisCost));
        } else {
            tooltip.add(I18n.format("item.FRShiftTooltip.lore"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote && !SuperpositionHandler.isOnCoodown(player)) {
            int charge = RechargeHelper.getCharge(stack);
            if (charge < RelicsConfigHandler.eldritchSpellVisCost) {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }

            if (RechargeHelper.consumeCharge(stack, player, RelicsConfigHandler.eldritchSpellVisCost)) {
                spawnOrb(world, player);

                // Haste reduces cooldown: each level -10%, max -70%
                int baseCooldown = RelicsConfigHandler.eldritchSpellCooldown;
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

    public boolean spawnOrb(World world, EntityPlayer player) {
        if (!world.isRemote) {
            Vector3 originalPos = Vector3.fromEntityCenter(player);
            Vec3d lookVec = player.getLookVec();
            Vector3 vector = originalPos.add(new Vector3(lookVec.x, lookVec.y, lookVec.z).multiply(1.0F));
            vector = new Vector3(vector.x, vector.y + 0.5, vector.z);

            // Fast straight-line speed: 1.5x look vector
            Vector3 motion = new Vector3(lookVec.x, lookVec.y, lookVec.z).multiply(1.5F);

            EntityDarkMatterOrb orb = new EntityDarkMatterOrb(world, player);
            orb.setPosition(vector.x, vector.y, vector.z);
            orb.motionX = motion.x;
            orb.motionY = motion.y;
            orb.motionZ = motion.z;

            world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 0.6F, 0.8F + (float) Math.random() * 0.2F);
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
