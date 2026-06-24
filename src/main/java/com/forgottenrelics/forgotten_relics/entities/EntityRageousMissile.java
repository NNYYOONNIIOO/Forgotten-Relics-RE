package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;
import java.util.UUID;

public class EntityRageousMissile extends EntityThrowable implements IEntityAdditionalSpawnData {

    private static final DataParameter<Byte> EVIL = EntityDataManager.createKey(EntityRageousMissile.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(EntityRageousMissile.class, DataSerializers.VARINT);

    private static final String TAG_TIME = "time";

    double lockX, lockY = -1, lockZ;
    int time = 0;

    private EntityLivingBase caster;
    private UUID casterUUID;

    public EntityRageousMissile(World world) {
        super(world);
        this.setSize(0.15F, 0.15F);
    }

    public EntityRageousMissile(World world, EntityLivingBase caster) {
        super(world, caster);
        this.setSize(0.15F, 0.15F);
        this.caster = caster;
        this.casterUUID = caster.getUniqueID();
    }

    public EntityRageousMissile(EntityPlayer thrower, boolean evil) {
        this(thrower.world, (EntityLivingBase) thrower);
        setEvil(evil);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(EVIL, (byte) 0);
        dataManager.register(TARGET_ID, -1);
    }

    public void setEvil(boolean evil) {
        dataManager.set(EVIL, (byte) (evil ? 1 : 0));
    }

    public boolean isEvil() {
        return dataManager.get(EVIL) == 1;
    }

    public void setTarget(EntityLivingBase e) {
        dataManager.set(TARGET_ID, e == null ? -1 : e.getEntityId());
    }

    public EntityLivingBase getTargetEntity() {
        int id = dataManager.get(TARGET_ID);
        Entity e = world.getEntityByID(id);
        if (e != null && e instanceof EntityLivingBase)
            return (EntityLivingBase) e;
        return null;
    }

    private boolean isCaster(Entity entity) {
        if (casterUUID != null && entity.getUniqueID().equals(casterUUID)) return true;
        if (caster != null && entity == caster) return true;
        EntityLivingBase builtInThrower = this.getThrower();
        if (builtInThrower != null && entity == builtInThrower) return true;
        return false;
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
                    this.casterUUID = this.caster.getUniqueID();
                }
            }
        } catch (Exception ex) {}
    }

    @Override
    public void onUpdate() {
        // Ensure casterUUID is set from getThrower() if null
        if (this.casterUUID == null && this.getThrower() != null) {
            this.casterUUID = this.getThrower().getUniqueID();
            this.caster = this.getThrower();
        }

        double lastTickPosX = this.lastTickPosX;
        double lastTickPosY = this.lastTickPosY;
        double lastTickPosZ = this.lastTickPosZ;

        super.onUpdate();

        if (!world.isRemote && !getTarget() && time > 160) {
            setDead();
            return;
        }

        // Trail particles (client side only)
        if (world.isRemote) {
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
                    float g = 0.8F + (float) (Math.random() * 0.2F);
                    float b = 0.4F + (float) (Math.random() * 0.6F);
                    Botania.proxy.sparkleFX(px, py, pz, r, g, b, 0.8F, 2);
                    if (world.rand.nextInt(steps) <= 1) {
                        Botania.proxy.sparkleFX(
                            px + (Math.random() - 0.5) * 0.4,
                            py + (Math.random() - 0.5) * 0.4,
                            pz + (Math.random() - 0.5) * 0.4,
                            r, g, b, 0.8F, 2);
                    }
                    px += step.x;
                    py += step.y;
                    pz += step.z;
                }
            }
        }

        EntityLivingBase target = getTargetEntity();
        if (target != null) {
            if (lockY == -1) {
                lockX = target.posX;
                lockY = target.posY;
                lockZ = target.posZ;
            }

            double dx = target.posX - this.posX;
            double dy = (target.posY + target.getEyeHeight() / 2.0) - this.posY;
            double dz = target.posZ - this.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > 0) {
                double speed = 0.5;
                motionX = dx / dist * speed;
                motionY = dy / dist * speed;
                if (time < 30)
                    motionY = Math.abs(motionY);
                motionZ = dz / dist * speed;
            }

            if (isEvil() && dist < 1)
                setDead();
        } else {
            double tx = this.posX + ((Math.random() - 0.5D) * 16);
            double ty = this.posY + ((Math.random() - 0.5D) * 16);
            double tz = this.posZ + ((Math.random() - 0.5D) * 16);
            double dx = tx - this.posX;
            double dy = ty - this.posY;
            double dz = tz - this.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0) {
                double speed = 0.5;
                motionX = dx / dist * speed;
                motionY = dy / dist * speed;
                motionZ = dz / dist * speed;
            }
        }

        time++;
    }

    public boolean getTarget() {
        EntityLivingBase target = getTargetEntity();
        if (target != null && target.getHealth() > 0 && !target.isDead) {
            return true;
        }
        if (target != null)
            setTarget(null);

        double range = 32;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
            new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range));

        // Remove caster from candidate list
        if (caster != null) {
            entities.remove(caster);
        }
        EntityLivingBase builtInThrower = this.getThrower();
        if (builtInThrower != null) {
            entities.remove(builtInThrower);
        }
        entities.removeIf(e -> isCaster(e));

        if (entities.size() > 0) {
            EntityLivingBase e = entities.get(world.rand.nextInt(entities.size()));
            setTarget(e);
            return true;
        }

        return false;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) return;

        // Ignore collision with caster
        if (result.entityHit != null && isCaster(result.entityHit)) {
            return;
        }

        if (result.entityHit != null && getTargetEntity() == result.entityHit) {
            EntityLivingBase thrower = this.getThrower();
            if (thrower != null) {
                float damage = 24.0F + (float)(Math.random() * 8.0F);
                result.entityHit.attackEntityFrom(new DamageRegistryHandler.DamageSourceMagic(thrower), damage);
            }

            this.world.playSound(null, this.posX, this.posY, this.posZ,
                net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, net.minecraft.util.SoundCategory.PLAYERS,
                2.0F, (float)(0.8F + Math.random() * 0.2F));
            setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger(TAG_TIME, time);
        if (casterUUID != null) {
            compound.setUniqueId("CasterUUID", casterUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        time = compound.getInteger(TAG_TIME);
        if (compound.hasUniqueId("CasterUUID")) {
            casterUUID = compound.getUniqueId("CasterUUID");
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
