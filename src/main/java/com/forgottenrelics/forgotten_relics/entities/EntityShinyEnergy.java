/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
 *  vazkii.botania.common.Botania
 *  vazkii.botania.common.core.helper.Vector3
 */
package com.forgottenrelics.forgotten_relics.entities;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

public class EntityShinyEnergy
extends EntityThrowable
implements IEntityAdditionalSpawnData {
    double lockX;
    double lockY;
    double lockZ;
    int targetID;
    EntityLivingBase thrower;
    EntityLivingBase target;

    public EntityShinyEnergy(World par1World) {
        super(par1World);
        this.targetID = 0;
        this.setSize(0.0f, 0.0f);
    }

    public EntityShinyEnergy(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase t, double x, double y, double z) {
        super(par1World, par2EntityLiving);
        this.thrower = par2EntityLiving;
        this.targetID = 0;
        this.target = t;
        this.lockX = x;
        this.lockY = y;
        this.lockZ = z;
        this.setSize(0.0f, 0.0f);
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }

    public void writeSpawnData(ByteBuf data) {
        int id = -1;
        if (this.target != null) {
            id = this.target.getEntityId();
        }
        data.writeInt(id);
        data.writeDouble(this.lockX);
        data.writeDouble(this.lockY);
        data.writeDouble(this.lockZ);
    }

    public void readSpawnData(ByteBuf data) {
        int id = data.readInt();
        try {
            if (id >= 0) {
                this.target = (EntityLivingBase)this.world.getEntityByID(id);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.lockX = data.readDouble();
        this.lockY = data.readDouble();
        this.lockZ = data.readDouble();
    }

    public void particleExplosion() {
        for (int i = 0; i < 24; ++i) {
            float r = 0.0f;
            float g = 0.8f + (float)Math.random() * 0.2f;
            float b = 0.4f + (float)Math.random() * 0.6f;
            float s = 0.3f + (float)Math.random() * 0.3f;
            float m = 0.4f;
            float xm = ((float)Math.random() - 0.5f) * m;
            float ym = ((float)Math.random() - 0.5f) * m;
            float zm = ((float)Math.random() - 0.5f) * m;
            Botania.proxy.setWispFXDistanceLimit(false);
            Botania.proxy.wispFX(this.lockX + 0.5, this.lockY + 1.25, this.lockZ + 0.5, r, g, b, s, xm, ym, zm, 1.0f);
            Botania.proxy.setWispFXDistanceLimit(true);
        }
    }

    protected void onImpact(RayTraceResult mop) {
    }

    public float getShadowSize() {
        return 0.1f;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 30) {
            this.setDead();
        }
        if (this.target == null) {
            this.setDead();
            return;
        }
        float size = 1.0f / this.getDistance((Entity)this.target);
        if (size > 1.5f) {
            size = 1.5f;
        }
        for (int i = 0; i < 8; ++i) {
            Botania.proxy.sparkleFX(this.posX + (Math.random() - 0.5) * 0.1, this.posY + (Math.random() - 0.5) * 0.1, this.posZ + (Math.random() - 0.5) * 0.1, (float)((double)0.9f + Math.random() * (double)0.1f), (float)((double)0.2f + Math.random() * (double)0.2f), 0.0f, size, 2);
        }
        Vector3 thisVec = Vector3.fromEntityCenter((Entity)this);
        Vector3 targetVec = Vector3.fromEntityCenter((Entity)this.target);
        Vector3 diffVec = targetVec.subtract(thisVec);
        Vector3 motionVec = diffVec.normalize().multiply(0.15);
        this.motionX = motionVec.x;
        this.motionY = motionVec.y;
        this.motionZ = motionVec.z;
        if (!this.world.isRemote) {
            if (this.target == null) {
                this.setDead();
                return;
            }
            List targetList = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0.1, 0.1, 0.1));
            if (targetList.contains(this.target)) {
                this.setDead();
            }
        }
    }
}

