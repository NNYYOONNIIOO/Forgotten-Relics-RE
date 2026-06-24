package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.botania.common.Botania;

import java.util.UUID;

public class EntityDarkMatterOrb extends EntityThrowable implements IEntityAdditionalSpawnData {

    private static final float BASE_DAMAGE = 32.5F;
    private static final float EMPTINESS_DAMAGE_MULTIPLIER = 2.0F;
    private UUID throwerUUID;
    private EntityLivingBase caster;

    public EntityDarkMatterOrb(World world) {
        super(world);
    }

    public EntityDarkMatterOrb(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.throwerUUID = thrower.getUniqueID();
        this.caster = thrower;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        int casterId = -1;
        if (this.caster != null) {
            casterId = this.caster.getEntityId();
        }
        data.writeInt(casterId);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int casterId = data.readInt();
        try {
            if (casterId >= 0) {
                Entity e = this.world.getEntityByID(casterId);
                if (e instanceof EntityLivingBase) {
                    this.caster = (EntityLivingBase) e;
                    this.throwerUUID = this.caster.getUniqueID();
                }
            }
        } catch (Exception ex) {}
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) return;

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = this.world.getBlockState(result.getBlockPos()).getBlock();
            if (block instanceof BlockBush || block instanceof BlockLeaves || block instanceof BlockLiquid) {
                return;
            }
            this.playSound(net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            spawnHitParticles();
            this.setDead();
            return;
        }

        if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) result.entityHit;

            // Never hit the caster
            if (isCaster(target)) {
                return;
            }

            Entity thrower = this.getThrower();
            boolean inEmptiness = isInEmptinessDimension();

            float damage = inEmptiness ? BASE_DAMAGE * EMPTINESS_DAMAGE_MULTIPLIER : BASE_DAMAGE;
            DamageSource damageSource = new DamageRegistryHandler.DamageSourceDarkMatter(thrower);
            target.attackEntityFrom(damageSource, damage);

            // Apply debuffs
            if (inEmptiness) {
                target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 320, 2));
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 2));
                target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 250, 3));
            } else {
                target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160, 1));
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1));
                target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0));
            }

            this.playSound(net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            spawnHitParticles();
            this.setDead();
        }
    }

    private boolean isCaster(Entity entity) {
        if (throwerUUID != null && entity.getUniqueID().equals(throwerUUID)) return true;
        if (caster != null && entity == caster) return true;
        EntityLivingBase builtInThrower = this.getThrower();
        if (builtInThrower != null && entity == builtInThrower) return true;
        return false;
    }

    @Override
    public void onUpdate() {
        // Ensure casterUUID is set from getThrower() if null
        if (this.throwerUUID == null && this.getThrower() != null) {
            this.throwerUUID = this.getThrower().getUniqueID();
            this.caster = this.getThrower();
        }

        super.onUpdate();

        if (this.ticksExisted >= 200) {
            this.setDead();
        } else if (this.ticksExisted >= 100) {
            double absMotionX = Math.abs(this.motionX);
            double absMotionY = Math.abs(this.motionY);
            double absMotionZ = Math.abs(this.motionZ);
            if (absMotionX < 0.01D && absMotionY < 0.01D && absMotionZ < 0.01D) {
                this.setDead();
            }
        }

        // Trail particles (client side) - dark eldritch wisp trail
        if (this.world.isRemote) {
            for (int i = 0; i < 2; i++) {
                float fx = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.25F;
                float fy = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.25F;
                float fz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.25F;
                Botania.proxy.wispFX(
                    this.posX + fx, this.posY + fy + 0.22 * this.height, this.posZ + fz,
                    0.05F, 0.05F, 0.1F, 0.25F,
                    (float)(Math.random() - 0.5F) * 0.04F,
                    (float)(Math.random() - 0.5F) * 0.04F,
                    (float)(Math.random() - 0.5F) * 0.04F,
                    0.6F);
            }
        }
    }

    private boolean isInEmptinessDimension() {
        // Only detect TA's Emptiness dimension; requires Thaumic Augmentation
        try {
            Class<?> taDimensionsClass = Class.forName("thecodex6824.thaumicaugmentation.api.world.TADimensions");
            java.lang.reflect.Field emptinessField = taDimensionsClass.getField("EMPTINESS");
            net.minecraft.world.DimensionType emptinessType = (net.minecraft.world.DimensionType) emptinessField.get(null);
            if (emptinessType != null) {
                return this.world.provider.getDimensionType() == emptinessType;
            }
        } catch (Exception e) {
            // TA not loaded or Emptiness dimension not available
        }
        return false;
    }

    private void spawnHitParticles() {
        if (this.world.isRemote) {
            for (int a = 0; a < 30; ++a) {
                float fx = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
                float fy = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
                float fz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
                Botania.proxy.wispFX(
                    this.posX + fx, this.posY + fy, this.posZ + fz,
                    0.1F, 0.1F, 0.15F, 0.25F,
                    fx * 0.5F, fy * 0.5F, fz * 0.5F,
                    0.8F);
            }
            Botania.proxy.sparkleFX(this.posX, this.posY, this.posZ, 0.4F, 0.4F, 0.6F, 2.0F, 4);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (throwerUUID != null) {
            compound.setUniqueId("ThrowerUUID", throwerUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("ThrowerUUID")) {
            throwerUUID = compound.getUniqueId("ThrowerUUID");
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            spawnHitParticles();
        } else {
            super.handleStatusUpdate(id);
        }
    }
}
