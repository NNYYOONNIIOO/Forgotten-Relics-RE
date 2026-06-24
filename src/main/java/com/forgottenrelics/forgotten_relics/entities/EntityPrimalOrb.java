package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntityPrimalOrb extends EntityThrowable implements IEntityAdditionalSpawnData {

    private static final DataParameter<Byte> SEEKER = EntityDataManager.createKey(EntityPrimalOrb.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> OWNER_ID = EntityDataManager.createKey(EntityPrimalOrb.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLOR_INDEX = EntityDataManager.createKey(EntityPrimalOrb.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> DISCORD_AIMED = EntityDataManager.createKey(EntityPrimalOrb.class, DataSerializers.BYTE);

    private UUID throwerUUID;
    private int count = 0;
    // Target coordinates for Discord synergy (not synced via DataParameter, uses spawn data + NBT)
    private double targetX, targetY, targetZ;
    private boolean hasTarget = false;

    public EntityPrimalOrb(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityPrimalOrb(World world, EntityLivingBase thrower, boolean seeker) {
        super(world, thrower);
        this.throwerUUID = thrower.getUniqueID();
        this.setSize(0.25F, 0.25F);
        setSeeker(seeker);
        setOwnerId(thrower.getEntityId());
        setColorIndex(world.rand.nextInt(6));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SEEKER, (byte) 0);
        this.dataManager.register(OWNER_ID, -1);
        this.dataManager.register(COLOR_INDEX, 0);
        this.dataManager.register(DISCORD_AIMED, (byte) 0);
    }

    public boolean isSeeker() {
        return this.dataManager.get(SEEKER) == 1;
    }

    public void setSeeker(boolean seeker) {
        this.dataManager.set(SEEKER, (byte) (seeker ? 1 : 0));
    }

    public int getOwnerId() {
        return this.dataManager.get(OWNER_ID);
    }

    public void setOwnerId(int id) {
        this.dataManager.set(OWNER_ID, id);
    }

    public int getColorIndex() {
        return this.dataManager.get(COLOR_INDEX);
    }

    public void setColorIndex(int index) {
        this.dataManager.set(COLOR_INDEX, index);
    }

    public boolean isDiscordAimed() {
        return this.dataManager.get(DISCORD_AIMED) == 1;
    }

    public void setDiscordAimed(boolean aimed) {
        this.dataManager.set(DISCORD_AIMED, (byte) (aimed ? 1 : 0));
    }

    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.hasTarget = true;
    }

    private boolean isThrower(Entity entity) {
        if (throwerUUID != null && entity.getUniqueID().equals(throwerUUID)) return true;
        EntityLivingBase thrower = this.getThrower();
        return thrower != null && entity == thrower;
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeBoolean(isSeeker());
        buf.writeInt(getOwnerId());
        buf.writeInt(getColorIndex());
        buf.writeBoolean(hasTarget);
        if (hasTarget) {
            buf.writeDouble(targetX);
            buf.writeDouble(targetY);
            buf.writeDouble(targetZ);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        setSeeker(buf.readBoolean());
        setOwnerId(buf.readInt());
        setColorIndex(buf.readInt());
        hasTarget = buf.readBoolean();
        if (hasTarget) {
            targetX = buf.readDouble();
            targetY = buf.readDouble();
            targetZ = buf.readDouble();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.001F;
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
        count++;

        if (this.isInsideOfMaterial(Material.PORTAL)) {
            this.onImpact(new RayTraceResult(this));
        }

        // Client-side trail particles (like Thaumcraft wisp)
        if (this.world.isRemote) {
            float[][] colors = {
                {0.2F, 0.6F, 1.0F},   // Aer - blue
                {0.6F, 1.0F, 0.2F},   // Terra - green
                {1.0F, 0.4F, 0.1F},   // Ignis - orange
                {0.2F, 0.4F, 1.0F},   // Aqua - deep blue
                {0.9F, 0.9F, 0.9F},   // Ordo - white
                {0.6F, 0.1F, 0.8F}    // Perditio - purple
            };

            int idx = getColorIndex() % colors.length;
            float r = colors[idx][0];
            float g = colors[idx][1];
            float b = colors[idx][2];

            // Discord-aimed orbs get a golden tint
            if (isDiscordAimed()) {
                r = 1.0F;
                g = MathHelper.clamp(g + 0.3F, 0.0F, 1.0F);
                b = 0.0F;
            }

            Botania.proxy.wispFX(this.posX, this.posY, this.posZ,
                r, g, b, 0.15F + (float)Math.random() * 0.1F,
                (float)(Math.random() - 0.5F) * 0.05F,
                (float)(Math.random() - 0.5F) * 0.05F,
                (float)(Math.random() - 0.5F) * 0.05F,
                0.8F);

            if (this.ticksExisted % 3 == 0) {
                Botania.proxy.sparkleFX(this.posX, this.posY, this.posZ,
                    r, g, b, 1.0F, 3);
            }
        }

        // After 20 ticks, start seeking or drifting
        if (this.ticksExisted > 20) {
            // Discord synergy: fly toward the target point
            if (hasTarget) {
                double dx = targetX - this.posX;
                double dy = targetY - this.posY;
                double dz = targetZ - this.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (dist > 1.0D) {
                    double speed = 0.25D;
                    this.motionX += (dx / dist) * speed;
                    this.motionY += (dy / dist) * speed;
                    this.motionZ += (dz / dist) * speed;

                    this.motionX = MathHelper.clamp((float)this.motionX, -0.3F, 0.3F);
                    this.motionY = MathHelper.clamp((float)this.motionY, -0.3F, 0.3F);
                    this.motionZ = MathHelper.clamp((float)this.motionZ, -0.3F, 0.3F);
                } else {
                    // Reached target, impact
                    this.onImpact(new RayTraceResult(this));
                    return;
                }
            } else if (!isSeeker()) {
                // Non-seeker: random drift
                Random rr = new Random(this.getEntityId() + count);
                this.motionX += (rr.nextFloat() - rr.nextFloat()) * 0.01F;
                this.motionY += (rr.nextFloat() - rr.nextFloat()) * 0.01F;
                this.motionZ += (rr.nextFloat() - rr.nextFloat()) * 0.01F;
            } else {
                // Seeker: find nearest living entity within 16 blocks
                double range = 16.0D;
                AxisAlignedBB searchBox = new AxisAlignedBB(
                    this.posX - range, this.posY - range, this.posZ - range,
                    this.posX + range, this.posY + range, this.posZ + range);
                List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);

                double closestDist = Double.MAX_VALUE;
                Entity closest = null;

                for (Entity e : entities) {
                    // 80% chance to skip the thrower (so sometimes it attacks the player!)
                    if (e.getEntityId() == getOwnerId()) {
                        if (Math.random() < 0.8D) continue;
                    }
                    if (e.isDead) continue;

                    double dist = this.getDistanceSq(e);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = e;
                    }
                }

                if (closest != null) {
                    double dx = closest.posX - this.posX;
                    double dy = (closest.getEntityBoundingBox().minY + closest.height * 0.9) - this.posY;
                    double dz = closest.posZ - this.posZ;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (dist > 0) {
                        double speed = 0.2D;
                        this.motionX += (dx / dist) * speed;
                        this.motionY += (dy / dist) * speed;
                        this.motionZ += (dz / dist) * speed;

                        this.motionX = MathHelper.clamp((float)this.motionX, -0.2F, 0.2F);
                        this.motionY = MathHelper.clamp((float)this.motionY, -0.2F, 0.2F);
                        this.motionZ = MathHelper.clamp((float)this.motionZ, -0.2F, 0.2F);
                    }
                }
            }
        }

        super.onUpdate();

        // Despawn after 5000 ticks
        if (this.ticksExisted > 5000) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        // Client-side explosion particles
        if (this.world.isRemote) {
            float[][] colors = {
                {0.2F, 0.6F, 1.0F},
                {0.6F, 1.0F, 0.2F},
                {1.0F, 0.4F, 0.1F},
                {0.2F, 0.4F, 1.0F},
                {0.9F, 0.9F, 0.9F},
                {0.6F, 0.1F, 0.8F}
            };

            for (int a = 0; a < 6; a++) {
                for (int b = 0; b < 4; b++) {
                    float fx = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    float fy = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    float fz = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    int idx = this.world.rand.nextInt(colors.length);
                    Botania.proxy.wispFX(this.posX + fx, this.posY + fy, this.posZ + fz,
                        colors[idx][0], colors[idx][1], colors[idx][2],
                        0.2F + this.world.rand.nextFloat() * 0.3F,
                        fx * 2.0F, fy * 2.0F, fz * 2.0F, 0.9F);
                }
            }
        }

        if (!this.world.isRemote) {
            // Direct hit damage: 1 to chaosTomeDamageCap
            if (result.entityHit != null && this.getThrower() != null) {
                float damage = 1.0F + (float)(Math.random() * RelicsConfigHandler.chaosTomeDamageCap);
                result.entityHit.attackEntityFrom(new DamageRegistryHandler.DamageSourceMagic(this.getThrower()), damage);
            }

            // Random explosion strength (1-7), controlled by config for block damage
            float explosionStrength = 1.0F + (float)(Math.random() * 6.0F);
            boolean blockDamage = RelicsConfigHandler.chaosTomeExplosionBlockDamage;
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, explosionStrength, blockDamage);

            this.world.playSound(null, this.posX, this.posY, this.posZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                2.0F, 0.8F + this.world.rand.nextFloat() * 0.4F);

            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("seeker", isSeeker());
        compound.setInteger("ownerId", getOwnerId());
        compound.setInteger("colorIndex", getColorIndex());
        compound.setBoolean("discordAimed", isDiscordAimed());
        compound.setBoolean("hasTarget", hasTarget);
        if (hasTarget) {
            compound.setDouble("targetX", targetX);
            compound.setDouble("targetY", targetY);
            compound.setDouble("targetZ", targetZ);
        }
        if (throwerUUID != null) {
            compound.setUniqueId("ThrowerUUID", throwerUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSeeker(compound.getBoolean("seeker"));
        setOwnerId(compound.getInteger("ownerId"));
        setColorIndex(compound.getInteger("colorIndex"));
        setDiscordAimed(compound.getBoolean("discordAimed"));
        hasTarget = compound.getBoolean("hasTarget");
        if (hasTarget) {
            targetX = compound.getDouble("targetX");
            targetY = compound.getDouble("targetY");
            targetZ = compound.getDouble("targetZ");
        }
        if (compound.hasUniqueId("ThrowerUUID")) {
            throwerUUID = compound.getUniqueId("ThrowerUUID");
        }
    }
}
