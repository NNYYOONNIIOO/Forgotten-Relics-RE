package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.packets.LightningMessage;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;

import java.util.List;
import java.util.UUID;

public class EntityThunderpealOrb extends EntityThrowable {

    private static final float DIRECT_DAMAGE = 24.0F;
    private static final float BOLT_DAMAGE = 16.0F;
    private static final int CHAIN_RANGE = 4;
    private static final int MAX_CHAIN_TARGETS = 3;
    private static final int AREA_RANGE = 4;

    private UUID throwerUUID;

    public EntityThunderpealOrb(World world) {
        super(world);
        this.setSize(0.3F, 0.3F);
    }

    public EntityThunderpealOrb(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.setSize(0.3F, 0.3F);
        this.throwerUUID = thrower.getUniqueID();
    }

    private boolean isThrower(Entity entity) {
        if (throwerUUID != null && entity.getUniqueID().equals(throwerUUID)) return true;
        Entity thrower = this.getThrower();
        if (thrower != null && entity == thrower) return true;
        return false;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        if (Double.isNaN(d0)) d0 = 4.0D;
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @Override
    public void onUpdate() {
        // Ensure throwerUUID is set from getThrower() if null
        if (this.throwerUUID == null && this.getThrower() != null) {
            this.throwerUUID = this.getThrower().getUniqueID();
        }

        super.onUpdate();

        // Client-side trail particles (blue electric)
        if (this.world.isRemote) {
            float r = (float)(Math.random() * 0.1F);
            float g = 0.4F + (float)(Math.random() * 0.3F);
            float b = 0.9F + (float)(Math.random() * 0.1F);
            Botania.proxy.wispFX(this.posX, this.posY, this.posZ,
                r, g, b, 0.12F + (float)Math.random() * 0.08F,
                (float)(Math.random() - 0.5F) * 0.05F,
                (float)(Math.random() - 0.5F) * 0.05F,
                (float)(Math.random() - 0.5F) * 0.05F,
                0.6F);

            if (this.ticksExisted % 2 == 0) {
                Botania.proxy.sparkleFX(this.posX, this.posY, this.posZ,
                    0.4F, 0.6F, 1.0F, 1.0F, 2);
            }
        }

        if (this.ticksExisted > 500) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        // Ignore collision with thrower
        if (result.entityHit != null && isThrower(result.entityHit)) {
            return;
        }

        Entity hitEntity = null;
        if (result.entityHit != null) {
            hitEntity = result.entityHit;
            if (this.getThrower() != null) {
                hitEntity.attackEntityFrom(new DamageRegistryHandler.DamageSourceTLightning(this.getThrower()), DIRECT_DAMAGE);
                hitEntity.hurtResistantTime = 0;
            }
        }

        if (!this.world.isRemote) {
            // Find entities in area around impact point
            AxisAlignedBB searchBox = new AxisAlignedBB(
                this.posX - AREA_RANGE, this.posY - AREA_RANGE, this.posZ - AREA_RANGE,
                this.posX + AREA_RANGE, this.posY + AREA_RANGE, this.posZ + AREA_RANGE);
            List<EntityLivingBase> areaEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);

            // Remove thrower and directly hit entity
            areaEntities.removeIf(e -> isThrower(e));
            if (areaEntities.contains(hitEntity)) areaEntities.remove(hitEntity);

            // Limit primary chain targets to MAX_CHAIN_TARGETS
            while (areaEntities.size() > MAX_CHAIN_TARGETS) {
                areaEntities.remove((int)(Math.random() * areaEntities.size()));
            }

            Entity thrower = this.getThrower();

            // Lightning chain: each area entity gets hit by a lightning bolt
            for (Entity e : areaEntities) {
                // Shoot lightning visual from orb to target
                Main.packetInstance.sendToAllAround(
                    new LightningMessage(this.posX, this.posY, this.posZ, e.posX, e.posY + e.height / 2.0D, e.posZ, true),
                    new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 64.0D));

                e.hurtResistantTime = 0;
                if (thrower != null) {
                    e.attackEntityFrom(new DamageRegistryHandler.DamageSourceTLightning(thrower), BOLT_DAMAGE);
                }

                // Chain: find up to 3 nearby entities for secondary lightning
                AxisAlignedBB chainBox = new AxisAlignedBB(
                    e.posX - CHAIN_RANGE, e.posY - CHAIN_RANGE, e.posZ - CHAIN_RANGE,
                    e.posX + CHAIN_RANGE, e.posY + CHAIN_RANGE, e.posZ + CHAIN_RANGE);
                List<EntityLivingBase> chainEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, chainBox);

                chainEntities.removeIf(c -> isThrower(c));
                if (chainEntities.contains(e)) chainEntities.remove(e);
                if (chainEntities.contains(hitEntity)) chainEntities.remove(hitEntity);

                // Limit to MAX_CHAIN_TARGETS
                while (chainEntities.size() > MAX_CHAIN_TARGETS) {
                    chainEntities.remove((int)(Math.random() * chainEntities.size()));
                }

                for (Entity chainTarget : chainEntities) {
                    // Shoot secondary lightning visual
                    Main.packetInstance.sendToAllAround(
                        new LightningMessage(e.posX, e.posY + e.height / 2.0D, e.posZ,
                            chainTarget.posX, chainTarget.posY + chainTarget.height / 2.0D, chainTarget.posZ, false),
                        new NetworkRegistry.TargetPoint(this.dimension, e.posX, e.posY, e.posZ, 64.0D));

                    chainTarget.hurtResistantTime = 0;
                    if (thrower != null) {
                        chainTarget.attackEntityFrom(new DamageRegistryHandler.DamageSourceTLightning(thrower), BOLT_DAMAGE / 2.0F);
                    }
                }
            }

            // Burst effects
            SuperpositionHandler.imposeBurst(this.world, this.dimension, this.posX, this.posY, this.posZ, 2.0F);
            SuperpositionHandler.imposeBurst(this.world, this.dimension, this.posX, this.posY, this.posZ, 2.0F);

            this.world.playSound(null, this.posX, this.posY, this.posZ,
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 2.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        }

        this.setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        this.markVelocityChanged();
        if (source.getTrueSource() != null) {
            Vec3d vec3 = source.getTrueSource().getLookVec();
            if (vec3 != null) {
                this.motionX = vec3.x * 0.9;
                this.motionY = vec3.y * 0.9;
                this.motionZ = vec3.z * 0.9;
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            }
            return true;
        }
        return false;
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
}
