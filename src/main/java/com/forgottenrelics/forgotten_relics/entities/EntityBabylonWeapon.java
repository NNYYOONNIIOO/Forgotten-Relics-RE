package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;
import java.util.UUID;

public class EntityBabylonWeapon extends EntityThrowable {

    private static final DataParameter<Byte> CHARGING = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> VARIETY = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CHARGE_TICKS = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LIVE_TICKS = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DELAY = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.VARINT);
    private static final DataParameter<Float> ROTATION = EntityDataManager.createKey(EntityBabylonWeapon.class, DataSerializers.FLOAT);

    private static final float DIRECT_DAMAGE = 100.0F;
    private static final float IMPACT_DAMAGE = 75.0F;
    private static final float IMPACT_RANGE = 3.0F;

    private UUID throwerUUID;

    public EntityBabylonWeapon(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityBabylonWeapon(World world, EntityLivingBase thrower) {
        super(world, thrower);
        this.throwerUUID = thrower.getUniqueID();
        this.setSize(0.25F, 0.25F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CHARGING, (byte) 0);
        this.dataManager.register(VARIETY, 0);
        this.dataManager.register(CHARGE_TICKS, 0);
        this.dataManager.register(LIVE_TICKS, 0);
        this.dataManager.register(DELAY, 0);
        this.dataManager.register(ROTATION, 0.0F);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    // Override isInRangeToRenderDist same as Botania's EntityThrowableCopy
    // This is critical - without it, the entity would be culled at any distance
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    public boolean isCharging() {
        return this.dataManager.get(CHARGING) == 1;
    }

    public void setCharging(boolean charging) {
        this.dataManager.set(CHARGING, (byte) (charging ? 1 : 0));
    }

    public int getVariety() {
        return this.dataManager.get(VARIETY);
    }

    public void setVariety(int var) {
        this.dataManager.set(VARIETY, var);
    }

    public int getChargeTicks() {
        return this.dataManager.get(CHARGE_TICKS);
    }

    public void setChargeTicks(int ticks) {
        this.dataManager.set(CHARGE_TICKS, ticks);
    }

    public int getLiveTicks() {
        return this.dataManager.get(LIVE_TICKS);
    }

    public void setLiveTicks(int ticks) {
        this.dataManager.set(LIVE_TICKS, ticks);
    }

    public int getDelay() {
        return this.dataManager.get(DELAY);
    }

    public void setDelay(int delay) {
        this.dataManager.set(DELAY, delay);
    }

    public float getRotation() {
        return this.dataManager.get(ROTATION);
    }

    public void setRotation(float rot) {
        this.dataManager.set(ROTATION, rot);
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
        EntityLivingBase thrower = this.getThrower();
        if (!this.world.isRemote && (thrower == null || !(thrower instanceof EntityPlayer) || thrower.isDead)) {
            setDead();
            return;
        }
        EntityPlayer player = (EntityPlayer) thrower;

        double x = this.motionX;
        double y = this.motionY;
        double z = this.motionZ;

        int liveTime = getLiveTicks();
        int delay = getDelay();

        if (this.ticksExisted <= 15) {
            // Charging phase - weapon stays still
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;

            int chargeTime = getChargeTicks();
            setChargeTicks(chargeTime + 1);

            if (this.world.rand.nextInt(20) == 0) {
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    vazkii.botania.common.core.handler.ModSounds.babylonSpawn, SoundCategory.PLAYERS,
                    0.1F, 1.0F + this.world.rand.nextFloat() * 3.0F);
            }
        } else {
            if (liveTime < delay) {
                // Delay phase - still waiting
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
            } else if (liveTime == delay && player != null) {
                // Launch phase - fly toward player's look direction
                Vec3d lookVec = player.getLookVec();
                RayTraceResult lookat = player.rayTrace(64, 1.0F);
                Vector3 playerLook;
                if (lookat == null) {
                    playerLook = new Vector3(lookVec.x, lookVec.y, lookVec.z).multiply(64).add(Vector3.fromEntity(player));
                } else {
                    playerLook = new Vector3(lookat.getBlockPos().getX() + 0.5, lookat.getBlockPos().getY() + 0.5, lookat.getBlockPos().getZ() + 0.5);
                }

                Vector3 thisVec = Vector3.fromEntityCenter(this);
                Vector3 motionVec = new Vector3(
                    playerLook.x - thisVec.x,
                    playerLook.y - thisVec.y,
                    playerLook.z - thisVec.z
                ).normalize().multiply(3.0D);

                x = motionVec.x;
                y = motionVec.y;
                z = motionVec.z;

                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    vazkii.botania.common.core.handler.ModSounds.babylonAttack, SoundCategory.PLAYERS,
                    2.0F, 1.0F + this.world.rand.nextFloat() * 3.0F);
            }
            setLiveTicks(liveTime + 1);

            // Check for entity collisions while flying
            if (!this.world.isRemote && liveTime >= delay) {
                AxisAlignedBB axis = new AxisAlignedBB(
                    this.posX, this.posY, this.posZ,
                    this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ)
                    .grow(2, 2, 2);
                List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
                for (EntityLivingBase living : entities) {
                    if (isThrower(living))
                        continue;

                    if (!living.isDead) {
                        if (player != null) {
                            living.attackEntityFrom(new DamageRegistryHandler.DamageSourceMagic(player), DIRECT_DAMAGE);
                        }

                        // Knockback
                        Vector3 targetPos = Vector3.fromEntityCenter(living);
                        Vector3 thisPos = Vector3.fromEntityCenter(this);
                        Vector3 diff = new Vector3(targetPos.x - thisPos.x, targetPos.y - thisPos.y, targetPos.z - thisPos.z);
                        diff = diff.normalize();
                        diff = diff.multiply(1.0 / (living.getDistance(this)));

                        if (diff.mag() > 1.0)
                            diff = diff.normalize();

                        if (living instanceof EntityWither)
                            diff = diff.multiply(0.5D);

                        living.motionX += diff.x;
                        living.motionY += diff.y;
                        living.motionZ += diff.z;

                        onImpact(new RayTraceResult(living));
                        return;
                    }
                }
            }
        }

        super.onUpdate();

        // Restore motion after super.onUpdate() which may apply gravity
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        // Trail particles when flying (golden wisp trail, same as Botania's King Key weapons)
        if (liveTime > delay && this.world.isRemote) {
            // Golden wisp trail
            Botania.proxy.wispFX(this.posX, this.posY, this.posZ, 1F, 1F, 0F, 0.3F,
                (float) (Math.random() - 0.5F) * 0.1F,
                (float) (Math.random() - 0.5F) * 0.1F,
                (float) (Math.random() - 0.5F) * 0.1F,
                0.8F);

            // Additional sparkle trail for more visibility
            if (this.ticksExisted % 2 == 0) {
                Botania.proxy.sparkleFX(this.posX + (Math.random() - 0.5F) * 0.5F,
                    this.posY + (Math.random() - 0.5F) * 0.5F,
                    this.posZ + (Math.random() - 0.5F) * 0.5F,
                    1.0F, 0.9F, 0.3F, 1.5F, 3);
            }
        }

        // Charging particles (golden sparkle around weapon while charging)
        if (this.ticksExisted <= 15 && this.world.isRemote) {
            float chargeProgress = (float) this.ticksExisted / 15F;
            for (int i = 0; i < 2; i++) {
                double angle = Math.random() * Math.PI * 2;
                double dist = 0.5 + Math.random() * 1.0;
                Botania.proxy.sparkleFX(
                    this.posX + Math.cos(angle) * dist,
                    this.posY + (Math.random() - 0.5F) * 1.0,
                    this.posZ + Math.sin(angle) * dist,
                    1.0F, 0.85F, 0.0F, 1.0F + chargeProgress, 4);
            }
        }

        // Despawn after 200 ticks of flight
        if (liveTime > 200 + delay) {
            setDead();
        }
    }

    public void invokeDamageEffects() {
        AxisAlignedBB area = new AxisAlignedBB(
            this.posX - IMPACT_RANGE, this.posY - IMPACT_RANGE, this.posZ - IMPACT_RANGE,
            this.posX + IMPACT_RANGE, this.posY + IMPACT_RANGE, this.posZ + IMPACT_RANGE);

        List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        targets.removeIf(e -> isThrower(e));

        for (int i = targets.size() - 1; i >= 0; i--) {
            EntityLivingBase target = targets.get(i);
            if (!target.isDead && !this.world.isRemote) {
                if (this.getThrower() != null) {
                    target.attackEntityFrom(new DamageRegistryHandler.DamageSourceMagic(this.getThrower()), IMPACT_DAMAGE);
                }

                // Knockback
                Vector3 targetPos = Vector3.fromEntityCenter(target);
                Vector3 thisPos = Vector3.fromEntityCenter(this);
                Vector3 diff = new Vector3(targetPos.x - thisPos.x, targetPos.y - thisPos.y, targetPos.z - thisPos.z);
                diff = diff.normalize();
                diff = diff.multiply(1.0 / (target.getDistance(this)));

                if (diff.mag() > 1.0)
                    diff = diff.normalize();

                if (target instanceof EntityWither)
                    diff = diff.multiply(0.5D);

                target.motionX += diff.x;
                target.motionY += diff.y;
                target.motionZ += diff.z;
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        EntityLivingBase thrower = this.getThrower();

        // Skip certain blocks
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = this.world.getBlockState(result.getBlockPos()).getBlock();
            if (block instanceof BlockBush || block instanceof BlockLeaves || block instanceof BlockLiquid) {
                return;
            }
        }

        if (result.entityHit == null || !isThrower(result.entityHit)) {
            if (!this.world.isRemote && thrower != null) {
                SuperpositionHandler.imposeBurst(this.world, this.dimension, this.posX, this.posY, this.posZ, 1.5F);
                this.invokeDamageEffects();
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                    8.0F, (float) (0.8F + (Math.random() * 0.2)));
            }

            // Send particle packet to nearby clients
            if (!this.world.isRemote) {
                Main.packetInstance.sendToAllAround(
                    new com.forgottenrelics.forgotten_relics.packets.ApotheosisParticleMessage(this.posX, this.posY, this.posZ, 70),
                    new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 128.0D));
            }

            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("charging", isCharging());
        compound.setInteger("variety", getVariety());
        compound.setInteger("chargeTicks", getChargeTicks());
        compound.setInteger("liveTicks", getLiveTicks());
        compound.setInteger("delay", getDelay());
        compound.setFloat("rotation", getRotation());
        if (throwerUUID != null) {
            compound.setUniqueId("ThrowerUUID", throwerUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setCharging(compound.getBoolean("charging"));
        setVariety(compound.getInteger("variety"));
        setChargeTicks(compound.getInteger("chargeTicks"));
        setLiveTicks(compound.getInteger("liveTicks"));
        setDelay(compound.getInteger("delay"));
        setRotation(compound.getFloat("rotation"));
        if (compound.hasUniqueId("ThrowerUUID")) {
            throwerUUID = compound.getUniqueId("ThrowerUUID");
        }
    }
}
