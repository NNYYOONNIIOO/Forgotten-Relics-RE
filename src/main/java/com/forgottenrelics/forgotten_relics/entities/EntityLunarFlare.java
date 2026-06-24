package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.packets.LunarFlareParticleMessage;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;
import java.util.UUID;

public class EntityLunarFlare extends EntityThrowable implements IEntityAdditionalSpawnData {

    private static final float DIRECT_DAMAGE = 72.0F;
    private static final float IMPACT_DAMAGE = 40.0F;
    private static final float IMPACT_RANGE = 2.5F;

    private int lockX;
    private int lockY;
    private int lockZ;
    private UUID throwerUUID;

    public EntityLunarFlare(World world) {
        super(world);
        this.setSize(0.0F, 0.0F);
    }

    public EntityLunarFlare(World world, EntityLivingBase thrower, int x, int y, int z) {
        super(world, thrower);
        this.throwerUUID = thrower.getUniqueID();
        this.lockX = x;
        this.lockY = y;
        this.lockZ = z;
        this.setSize(0.0F, 0.0F);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(this.lockX);
        data.writeInt(this.lockY);
        data.writeInt(this.lockZ);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        this.lockX = data.readInt();
        this.lockY = data.readInt();
        this.lockZ = data.readInt();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.getThrower() == null && !this.world.isRemote) {
            this.setDead();
            return;
        }

        // Determine explosion position
        double explodeX = this.lockX + 0.5;
        double explodeY = this.lockY + 1.25;
        double explodeZ = this.lockZ + 0.5;

        // Direct hit on entity
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (result.entityHit instanceof EntityLivingBase && !isThrower(result.entityHit)) {
                EntityLivingBase target = (EntityLivingBase) result.entityHit;
                DamageSource damageSource = new DamageRegistryHandler.DamageSourceMagic(this.getThrower());
                target.attackEntityFrom(damageSource, DIRECT_DAMAGE);
            }
        }

        // Block hit - check if near the locked target
        boolean isTargetBlock = false;
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = result.getBlockPos();
            double dist = Math.sqrt(
                Math.pow(hitPos.getX() - this.lockX, 2) +
                Math.pow(hitPos.getY() - this.lockY, 2) +
                Math.pow(hitPos.getZ() - this.lockZ, 2));
            isTargetBlock = dist <= IMPACT_RANGE + 1.0;

            if (isTargetBlock) {
                // Area damage around the target block
                AxisAlignedBB area = new AxisAlignedBB(
                    this.lockX - IMPACT_RANGE, this.lockY - IMPACT_RANGE, this.lockZ - IMPACT_RANGE,
                    this.lockX + IMPACT_RANGE + 1, this.lockY + IMPACT_RANGE + 1, this.lockZ + IMPACT_RANGE + 1);

                List<EntityLivingBase> affectedEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
                affectedEntities.removeIf(e -> isThrower(e));

                for (EntityLivingBase affected : affectedEntities) {
                    if (!affected.isDead) {
                        DamageSource damageSource = new DamageRegistryHandler.DamageSourceMagic(this.getThrower());
                        affected.attackEntityFrom(damageSource, IMPACT_DAMAGE);

                        // Knockback
                        Vector3 targetPos = Vector3.fromEntityCenter(affected);
                        Vector3 thisPos = new Vector3(this.lockX + 0.5, this.lockY + 0.5, this.lockZ + 0.5);
                        Vector3 diff = new Vector3(targetPos.x - thisPos.x, targetPos.y - thisPos.y, targetPos.z - thisPos.z);
                        diff = diff.normalize();
                        diff = diff.multiply(1.0 / affected.getDistance(this.lockX + 0.5, this.lockY + 0.5, this.lockZ + 0.5));

                        if (diff.mag() > 1.0) {
                            diff = diff.normalize();
                        }

                        // Bosses get half knockback
                        if (affected instanceof EntityWither) {
                            diff = diff.multiply(0.5D);
                        }

                        affected.motionX += diff.x;
                        affected.motionY += diff.y;
                        affected.motionZ += diff.z;
                    }
                }
            }
        }

        // Trigger explosion effects on any non-thrower collision
        if (result.entityHit == null || !isThrower(result.entityHit)) {
            // Particle effects at impact - send packet to clients
            if (!this.world.isRemote) {
                Main.packetInstance.sendToAllAround(
                    new LunarFlareParticleMessage(explodeX, explodeY, explodeZ),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        this.dimension, explodeX, explodeY, explodeZ, 128.0D));
            }

            // Sound - orechid sound
            this.world.playSound(null, explodeX, this.lockY + 0.5, explodeZ,
                vazkii.botania.common.core.handler.ModSounds.orechid, SoundCategory.PLAYERS, 2.0F, 1.0F);

            // Burst effect
            SuperpositionHandler.imposeBurst(this.world, this.dimension,
                this.lockX + 0.5, this.lockY + 1.5, this.lockZ + 0.5, 2.0F);

            this.setDead();
        }
    }

    private boolean isThrower(Entity entity) {
        if (throwerUUID != null && entity.getUniqueID().equals(throwerUUID)) {
            return true;
        }
        EntityLivingBase thrower = this.getThrower();
        return thrower != null && entity == thrower;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > 1000) {
            this.setDead();
        }

        // Trail particles (client side) - green sparkle trail
        if (this.world.isRemote) {
            double lastTickPosX = this.lastTickPosX;
            double lastTickPosY = this.lastTickPosY;
            double lastTickPosZ = this.lastTickPosZ;

            Vector3 thisVec = new Vector3(this.posX, this.posY, this.posZ);
            Vector3 oldPos = new Vector3(lastTickPosX, lastTickPosY, lastTickPosZ);
            Vector3 diff = new Vector3(thisVec.x - oldPos.x, thisVec.y - oldPos.y, thisVec.z - oldPos.z);
            double diffMag = diff.mag();

            if (diffMag > 0) {
                Vector3 step = diff.normalize().multiply(0.05);
                int steps = (int) (diffMag / step.mag());
                double px = oldPos.x, py = oldPos.y, pz = oldPos.z;

                for (int i = 0; i < steps; i++) {
                    float r = 0.0F;
                    float g = (float) (0.8F + (Math.random() * 0.2F));
                    float b = (float) (0.4F + (Math.random() * 0.6F));
                    Botania.proxy.sparkleFX(
                        px + (Math.random() - 0.5) * 0.2,
                        py + (Math.random() - 0.5) * 0.2,
                        pz + (Math.random() - 0.5) * 0.2,
                        r, g, b, 2.0F, 2);

                    if (this.rand.nextInt(steps + 1) <= 1) {
                        Botania.proxy.sparkleFX(
                            px + (Math.random() - 0.5) * 1.0,
                            py + (Math.random() - 0.5) * 1.0,
                            pz + (Math.random() - 0.5) * 1.0,
                            r, g, b, 2.4F, 4);
                    }

                    px += step.x;
                    py += step.y;
                    pz += step.z;
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("lockX", this.lockX);
        compound.setInteger("lockY", this.lockY);
        compound.setInteger("lockZ", this.lockZ);
        if (throwerUUID != null) {
            compound.setUniqueId("ThrowerUUID", throwerUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.lockX = compound.getInteger("lockX");
        this.lockY = compound.getInteger("lockY");
        this.lockZ = compound.getInteger("lockZ");
        if (compound.hasUniqueId("ThrowerUUID")) {
            throwerUUID = compound.getUniqueId("ThrowerUUID");
        }
    }
}
