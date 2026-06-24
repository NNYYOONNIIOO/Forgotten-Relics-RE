package com.forgottenrelics.forgotten_relics.items;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.packets.BanishmentCastingMessage;
import com.forgottenrelics.forgotten_relics.packets.InfernalParticleMessage;
import com.forgottenrelics.forgotten_relics.utils.ExtradimensionalTeleporter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.HashMap;
import java.util.List;

/**
 * Edict of Eternal Banishment - Banishes targeted entities to the Nether.
 * Uses IRechargable (Vis) instead of the original WandManager vis consumption.
 * Charge: 100 Vis, 0.2 Vis per tick while channeling.
 */
public class ItemEdictOfBanishment extends Item implements IRechargable {

    private static final float VIS_PER_TICK = 0.2F;

    private static HashMap<EntityPlayer, EntityLivingBase> targetList = new HashMap<>();

    public ItemEdictOfBanishment() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("edict_of_banishment");
        this.setRegistryName("edict_of_banishment");
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
        return RelicsConfigHandler.overthrowerMaxCharge;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("item.ItemOverthrower1.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower2.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower3.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower4.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower5.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower6.lore"));
            tooltip.add(I18n.format("item.ItemOverthrower7.lore"));
            tooltip.add(I18n.format("item.FREmpty.lore"));
            tooltip.add(I18n.format("item.NuclearFuryVisCost.lore", (int)(VIS_PER_TICK * 20)));
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
        return RelicsConfigHandler.overthrowerChannelDuration;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player.dimension == -1) {
            return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }

        // Raytrace to find target entity
        Entity pointedEntity = getPointedEntity(world, player, RelicsConfigHandler.overthrowerSearchRange, 3.0F);

        if (pointedEntity instanceof EntityLivingBase) {
            targetList.put(player, (EntityLivingBase) pointedEntity);
            player.setActiveHand(hand);
        } else {
            targetList.put(player, null);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (!(player instanceof EntityPlayer) || player.world.isRemote) return;

        EntityPlayer caster = (EntityPlayer) player;
        int ticksUsed = RelicsConfigHandler.overthrowerChannelDuration - count;

        // Consume Vis per tick
        int charge = RechargeHelper.getCharge(stack);
        if (charge < 1) {
            caster.stopActiveHand();
            return;
        }
        // Consume 0.2 Vis per tick = 1 Vis every 5 ticks
        if (ticksUsed % 5 == 0 && ticksUsed > 0) {
            RechargeHelper.consumeCharge(stack, caster, 1);
        }

        EntityLivingBase target = targetList.get(caster);
        if (target == null || target.isDead) {
            targetList.put(caster, null);
            caster.stopActiveHand();
            return;
        }

        Vector3 targetPos = Vector3.fromEntityCenter(target);

        // Play fire sound every 10 ticks
        if (count % 10 == 0 && count != RelicsConfigHandler.overthrowerChannelDuration) {
            caster.world.playSound(null, caster.posX, caster.posY, caster.posZ,
                SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 0.33F, 2.0F);
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 0.33F, 2.0F);
        }

        // Send casting particles to nearby clients
        Main.packetInstance.sendToAllAround(
            new BanishmentCastingMessage(targetPos.x, targetPos.y, targetPos.z, 5),
            new NetworkRegistry.TargetPoint(target.dimension, target.posX, target.posY, target.posZ, 64.0D));

        // Apply slowness to target
        try {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, RelicsConfigHandler.overthrowerLevitateSlowDuration, RelicsConfigHandler.overthrowerLevitateSlowLevel, true, false));
        } catch (Exception ex) {
            // ignore
        }

        // Channeling complete
        if (count == 1) {
            if (target.dimension != -1) {
                boolean success = false;
                for (int i = 0; i < 8; i++) {
                    if (overthrow(target, caster)) {
                        success = true;
                        break;
                    }
                }

                // If banishment failed and target is not a player, kill them
                if (!success && !(target instanceof EntityPlayerMP)) {
                    target.setPositionAndUpdate(0, 0, 0);
                    target.setDead();
                }
            }

            // Visual effects at the target position
            if (!caster.world.isRemote) {
                // Lightning bolts
                for (int i = 0; i < 3; i++) {
                    EntityLightningBolt lightning = new EntityLightningBolt(
                        caster.world, targetPos.x - 0.5D, targetPos.y - (target.height / 2), targetPos.z - 0.5D, false);
                    caster.world.spawnEntity(lightning);
                }

                // Infernal particle explosion
                Main.packetInstance.sendToAllAround(
                    new InfernalParticleMessage(targetPos.x, targetPos.y, targetPos.z, 128),
                    new NetworkRegistry.TargetPoint(caster.dimension, targetPos.x, targetPos.y, targetPos.z, 128.0D));

                // Pixie death sparkle effect (pink-purple sparkles)
                for (int i = 0; i < 12; i++) {
                    Botania.proxy.sparkleFX(
                        targetPos.x + (Math.random() - 0.5) * 0.25,
                        targetPos.y + 0.5 + (Math.random() - 0.5) * 0.25,
                        targetPos.z + (Math.random() - 0.5) * 0.25,
                        1.0F, 0.25F, 0.9F, 1.0F + (float) Math.random() * 0.25F, 5);
                }
            }
        }
    }

    /**
     * Banish the target entity to the Nether.
     * For non-player entities: copy via NBT to nether, kill original, spawn fire.
     * For players: transfer dimension and send chat message.
     */
    private boolean overthrow(EntityLivingBase entity, EntityPlayer overthrower) {
        int x = (int) ((Math.random() - 0.5D) * RelicsConfigHandler.overthrowerNetherRange * 2);
        int z = (int) ((Math.random() - 0.5D) * RelicsConfigHandler.overthrowerNetherRange * 2);
        int y = 124;

        World netherWorld = DimensionManager.getWorld(-1);
        if (netherWorld == null) return false;

        netherWorld.getChunkProvider().provideChunk(x >> 4, z >> 4);

        // Find valid Y position (2 air blocks above a solid, collidable block)
        for (int counter = 124; counter > 0; counter--) {
            BlockPos below = new BlockPos(x, counter - 1, z);
            BlockPos at = new BlockPos(x, counter, z);
            BlockPos above = new BlockPos(x, counter + 1, z);
            if (!netherWorld.isAirBlock(below) && netherWorld.getBlockState(below).getBlock().isCollidable()
                && netherWorld.isAirBlock(at) && netherWorld.isAirBlock(above)) {
                y = counter;
                break;
            }
        }

        if (y == 124) return false;

        // Check if target is a player and config disallows it
        if (entity instanceof EntityPlayerMP && !RelicsConfigHandler.overthrowerAffectsPlayers) {
            return false;
        }

        if (!(entity instanceof EntityPlayerMP)) {
            // Non-player: copy entity to nether via NBT
            NBTTagCompound nbt = new NBTTagCompound();
            entity.writeToNBTOptional(nbt);

            try {
                Entity overthrownEntity = EntityList.createEntityFromNBT(nbt, netherWorld);
                if (overthrownEntity instanceof EntityLivingBase) {
                    overthrownEntity.dimension = -1;
                    overthrownEntity.setPositionAndUpdate(x, y, z);
                    netherWorld.spawnEntity(overthrownEntity);
                }
            } catch (Exception ex) {
                // fallback
            }

            entity.setDead();

            // Spawn fire at original position
            for (int a = 0; a < 12; ++a) {
                int fx = MathHelper.floor(entity.posX) + itemRand.nextInt(4) - itemRand.nextInt(4);
                int fy = MathHelper.floor(entity.posY) + 4;
                int fz = MathHelper.floor(entity.posZ) + itemRand.nextInt(4) - itemRand.nextInt(4);

                for (; entity.world.isAirBlock(new BlockPos(fx, fy, fz)) && fy > MathHelper.floor(entity.posY) - 4; --fy) {}

                BlockPos firePos = new BlockPos(fx, fy + 1, fz);
                if (entity.world.isAirBlock(firePos) && !entity.world.isAirBlock(new BlockPos(fx, fy, fz))
                    && entity.world.getBlockState(firePos).getBlock() != Blocks.FIRE) {
                    entity.world.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 3);
                }
            }

            return true;
        } else {
            // Player: transfer to nether dimension
            EntityPlayerMP playerMP = (EntityPlayerMP) entity;
            playerMP.mcServer.getPlayerList().transferPlayerToDimension(playerMP, -1,
                new ExtradimensionalTeleporter(playerMP.mcServer.getWorld(-1), x + 0.5, y + 0.5, z + 0.5));
            playerMP.setPositionAndUpdate(x, y, z);

            // Send chat message
            TextComponentTranslation msg = new TextComponentTranslation("message.overthrown1",
                playerMP.getDisplayName(), overthrower.getDisplayName());
            TextComponentTranslation msg2 = new TextComponentTranslation("message.overthrown2");
            playerMP.mcServer.getPlayerList().sendMessage(
                new TextComponentTranslation("").appendSibling(overthrower.getDisplayName()).appendText(" ").appendSibling(msg).appendText(" ").appendSibling(playerMP.getDisplayName()).appendText(" ").appendSibling(msg2));

            return true;
        }
    }

    /**
     * Get the entity the player is looking at within range.
     */
    private Entity getPointedEntity(World world, EntityPlayer player, double range, float expand) {
        Vec3d lookVec = player.getLookVec();
        Vec3d eyePos = player.getPositionEyes(1.0F);
        Vec3d endPos = eyePos.addVector(lookVec.x * range, lookVec.y * range, lookVec.z * range);

        RayTraceResult rayTrace = world.rayTraceBlocks(eyePos, endPos, false, true, false);
        double traceDist = range;

        if (rayTrace != null) {
            traceDist = rayTrace.hitVec.distanceTo(eyePos);
        }

        AxisAlignedBB searchBox = player.getEntityBoundingBox()
            .expand(lookVec.x * range, lookVec.y * range, lookVec.z * range)
            .grow(expand, expand, expand);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);
        Entity pointedEntity = null;

        for (EntityLivingBase entity : entities) {
            if (entity == player) continue;

            AxisAlignedBB entityBox = entity.getEntityBoundingBox().grow(expand);
            RayTraceResult entityRay = entityBox.calculateIntercept(eyePos, endPos);

            if (entityBox.contains(eyePos)) {
                if (traceDist >= 0.0D) {
                    pointedEntity = entity;
                    traceDist = 0.0D;
                }
            } else if (entityRay != null) {
                double entityDist = eyePos.distanceTo(entityRay.hitVec);
                if (entityDist < traceDist) {
                    pointedEntity = entity;
                    traceDist = entityDist;
                }
            }
        }

        return pointedEntity;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
