package com.forgottenrelics.forgotten_relics.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.botania.common.Botania;

import java.util.List;

public class EntityCrimsonZone extends Entity {

    private static final int LIFESPAN = 100; // 5 seconds
    private static final double RADIUS = 2.0;
    private int casterID = -1;

    public EntityCrimsonZone(World world) {
        super(world);
        this.setSize((float)(RADIUS * 2), 0.1F);
        this.noClip = true;
    }

    public EntityCrimsonZone(World world, double x, double y, double z, EntityLivingBase caster) {
        this(world);
        this.setPosition(x, y, z);
        if (caster != null) {
            this.casterID = caster.getEntityId();
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.casterID = compound.getInteger("casterID");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("casterID", this.casterID);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > LIFESPAN) {
            this.setDead();
            return;
        }

        // Apply debuffs to nearby entities every 20 ticks
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            AxisAlignedBB area = new AxisAlignedBB(
                this.posX - RADIUS, this.posY - 0.5, this.posZ - RADIUS,
                this.posX + RADIUS, this.posY + 1.5, this.posZ + RADIUS);
            List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
            Entity casterEntity = this.casterID >= 0 ? this.world.getEntityByID(this.casterID) : null;
            for (EntityLivingBase entity : entities) {
                if (entity != casterEntity) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 40, 0));
                }
            }
        }

        // Red particles on the ground (client side)
        if (this.world.isRemote) {
            float fade = 1.0F - (float) this.ticksExisted / LIFESPAN;
            for (int i = 0; i < 3; i++) {
                double px = this.posX + (Math.random() - 0.5) * RADIUS * 2;
                double py = this.posY + Math.random() * 0.1;
                double pz = this.posZ + (Math.random() - 0.5) * RADIUS * 2;
                Botania.proxy.sparkleFX(px, py, pz, 0.8F, 0.1F, 0.1F, fade, 3);
            }
        }
    }
}
