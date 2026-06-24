package com.forgottenrelics.forgotten_relics.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class EntitySoulEnergy extends EntityThrowable implements IEntityAdditionalSpawnData {

    private int targetID;
    private EntityLivingBase target;

    public EntitySoulEnergy(World world) {
        super(world);
        this.targetID = 0;
        this.setSize(0.0F, 0.0F);
    }

    public EntitySoulEnergy(World world, EntityLivingBase thrower, EntityLivingBase target) {
        super(world, thrower);
        this.target = target;
        this.setSize(0.0F, 0.0F);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        int id = -1;
        if (this.target != null) {
            id = this.target.getEntityId();
        }
        data.writeInt(id);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        try {
            if (id >= 0) {
                Entity entity = this.world.getEntityByID(id);
                if (entity instanceof EntityLivingBase) {
                    this.target = (EntityLivingBase) entity;
                }
            }
        } catch (Exception ex) {}
    }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        // Impact handled in onUpdate via proximity check
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > 1000) {
            this.setDead();
        }

        // Trail particles (client side)
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
                    float rc = 1.0F;
                    float gc = 1.0F;
                    float bc = 1.0F;
                    Botania.proxy.sparkleFX(px, py, pz, rc, gc, bc, 0.8F, 2);

                    if (this.rand.nextInt(steps + 1) <= 1) {
                        Botania.proxy.sparkleFX(
                            px + (Math.random() - 0.5) * 0.4,
                            py + (Math.random() - 0.5) * 0.4,
                            pz + (Math.random() - 0.5) * 0.4,
                            rc, gc, bc, 0.8F, 2);
                    }

                    px += step.x;
                    py += step.y;
                    pz += step.z;
                }
            }
        }

        // Check if reached target (proximity check)
        if (this.target != null && !this.target.isDead) {
            double dist = this.getDistance(this.target);

            if (dist < 1.0) {
                // Reached target - heal and feed
                if (this.target instanceof EntityPlayer) {
                    ((EntityPlayer) this.target).heal(1.0F);
                    ((EntityPlayer) this.target).getFoodStats().addStats(1, 1.0F);
                } else {
                    this.target.heal(1.0F);
                }

                // Hit particles
                if (this.world.isRemote) {
                    for (int i = 0; i <= 6; i++) {
                        float r = 1.0F;
                        float g = 1.0F;
                        float b = 1.0F;
                        float s = 0.1F + (float) Math.random() * 0.1F;
                        float m = 0.15F;
                        float xm = ((float) Math.random() - 0.5F) * m;
                        float ym = ((float) Math.random() - 0.5F) * m;
                        float zm = ((float) Math.random() - 0.5F) * m;

                        Botania.proxy.wispFX(this.posX, this.posY, this.posZ,
                            r, g, b, s, xm, ym, zm, 0.8F);
                    }
                }

                this.world.playSound(null, this.target.posX, this.target.posY, this.target.posZ,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.6F,
                    0.8F + (float) Math.random() * 0.2F);

                this.setDead();
                return;
            }

            // Homing toward target
            double dx = this.target.posX - this.posX;
            double dy = (this.target.posY + this.target.height * 0.6) - this.posY;
            double dz = this.target.posZ - this.posZ;
            double d = (double) this.getDistance(this.target);

            dx /= d;
            dy /= d;
            dz /= d;

            double homingStrength = 0.3;
            this.motionX += dx * homingStrength;
            this.motionY += dy * homingStrength;
            this.motionZ += dz * homingStrength;

            this.motionX = MathHelper.clamp((float) this.motionX, -0.35F, 0.35F);
            this.motionY = MathHelper.clamp((float) this.motionY, -0.35F, 0.35F);
            this.motionZ = MathHelper.clamp((float) this.motionZ, -0.35F, 0.35F);
        } else {
            this.setDead();
        }
    }
}
