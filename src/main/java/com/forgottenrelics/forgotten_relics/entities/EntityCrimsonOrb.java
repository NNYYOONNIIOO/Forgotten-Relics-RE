package com.forgottenrelics.forgotten_relics.entities;

import com.forgottenrelics.forgotten_relics.utils.DamageRegistryHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

import java.util.List;

public class EntityCrimsonOrb extends EntityThrowable implements IEntityAdditionalSpawnData {

    private EntityLivingBase target;
    public boolean red;
    private EntityLivingBase caster;

    private static final float DAMAGE_MIN = 42.0F;
    private static final float DAMAGE_MAX = 100.0F;

    public EntityCrimsonOrb(World world) {
        super(world);
        this.setSize(0.3F, 0.3F);
    }

    public EntityCrimsonOrb(World world, EntityLivingBase caster, EntityLivingBase target, boolean red) {
        super(world, caster);
        this.setSize(0.3F, 0.3F);
        this.target = target;
        this.caster = caster;
        this.red = red;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        int idTarget = -1;
        int idCaster = -1;
        if (this.target != null) {
            idTarget = this.target.getEntityId();
        }
        if (this.caster != null) {
            idCaster = this.caster.getEntityId();
        }
        data.writeInt(idTarget);
        data.writeInt(idCaster);
        data.writeBoolean(this.red);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int targetID = data.readInt();
        int casterID = data.readInt();
        try {
            if (targetID >= 0) {
                this.target = (EntityLivingBase) this.world.getEntityByID(targetID);
            }
        } catch (Exception ex) {}
        try {
            if (casterID >= 0) {
                this.caster = (EntityLivingBase) this.world.getEntityByID(casterID);
            }
        } catch (Exception ex) {}
        this.red = data.readBoolean();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote && this.getThrower() != null) {
            double hitX = this.posX;
            double hitY = this.posY;
            double hitZ = this.posZ;

            if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
                float damage = DAMAGE_MIN + (float) (Math.random() * (DAMAGE_MAX - DAMAGE_MIN));
                result.entityHit.attackEntityFrom(new DamageRegistryHandler.DamageSourceMagic(this.getThrower()), damage);
                hitX = result.entityHit.posX;
                hitY = result.entityHit.posY;
                hitZ = result.entityHit.posZ;
            } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                Block block = this.world.getBlockState(result.getBlockPos()).getBlock();
                if (block instanceof BlockBush || block instanceof BlockLeaves || block instanceof BlockLiquid) {
                    return;
                }
            }

            // Small non-block-damaging explosion
            this.world.createExplosion(null, hitX, hitY, hitZ, 1.5F, false);

            // Hit particles (client side)
            spawnHitParticles(hitX, hitY, hitZ);

            // Check if near ground - place TC effect block
            if (isNearGround(hitX, hitY, hitZ)) {
                placeEffectZone(hitX, hitY, hitZ);
            }

            this.setDead();
        }
    }

    private boolean isNearGround(double x, double y, double z) {
        for (int i = 0; i <= 2; i++) {
            if (this.world.isBlockFullCube(new BlockPos(x, y - i, z))) {
                return true;
            }
        }
        return false;
    }

    private void placeEffectZone(double x, double y, double z) {
        // Try to find TC effect block by registry name (not reflection, avoids obfuscation issues)
        Block effectBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thaumcraft", "effect_shock"));
        if (effectBlock == null) {
            // Fallback: try effect_sap
            effectBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thaumcraft", "effect_sap"));
        }
        if (effectBlock != null) {
            BlockPos center = new BlockPos(x, y - 1, z);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.add(dx, 0, dz);
                    if (this.world.isAirBlock(pos) && this.world.isBlockFullCube(pos.down())) {
                        this.world.setBlockState(pos, effectBlock.getDefaultState());
                    }
                }
            }
        }
    }

    private void spawnHitParticles(double hx, double hy, double hz) {
        if (world.isRemote) {
            for (int i = 0; i < 15; i++) {
                float r = 0.8F + (float) (Math.random() * 0.2F);
                float g = 0.1F + (float) (Math.random() * 0.2F);
                float b = 0.0F + (float) (Math.random() * 0.1F);
                float size = 0.1F + (float) (Math.random() * 0.3F);
                float xm = (float) (Math.random() - 0.5F) * 0.15F;
                float ym = (float) (Math.random() - 0.5F) * 0.15F;
                float zm = (float) (Math.random() - 0.5F) * 0.15F;
                Botania.proxy.wispFX(hx, hy, hz, r, g, b, size, xm, ym, zm, 0.9F);
            }
            Botania.proxy.sparkleFX(hx, hy, hz, 1.0F, 0.2F, 0.1F, 2.0F, 4);
        }
    }

    public void getNewTarget() {
        int searchRange = 32;
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
            new AxisAlignedBB(this.posX - searchRange, this.posY - searchRange, this.posZ - searchRange,
                this.posX + searchRange, this.posY + searchRange, this.posZ + searchRange));

        if (entities.size() > 0) {
            for (int counter = entities.size() - 1; counter >= 0; counter--) {
                if (!entities.get(counter).canEntityBeSeen(this)) {
                    entities.remove(counter);
                    counter = entities.size();
                }
            }
        }

        if (entities.contains(this)) entities.remove(this);
        if (entities.contains(caster)) entities.remove(caster);

        if (entities.size() > 0) {
            this.target = entities.get((int) ((entities.size() - 1) * Math.random()));
        }
    }

    @Override
    public void onUpdate() {
        double lastTickPosX = this.lastTickPosX;
        double lastTickPosY = this.lastTickPosY;
        double lastTickPosZ = this.lastTickPosZ;

        super.onUpdate();

        if (this.ticksExisted > 1000) {
            this.setDead();
            return;
        }

        if (!this.red) {
            this.setDead();
            return;
        }

        // Trail particles (client side)
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
                    float r = 0.8F + (float) (Math.random() * 0.2F);
                    float g = 0.1F + (float) (Math.random() * 0.2F);
                    float b = 0.0F + (float) (Math.random() * 0.1F);
                    Botania.proxy.sparkleFX(px, py, pz, r, g, b, 0.8F, 2);
                    if (world.rand.nextInt(steps) <= 1) {
                        Botania.proxy.sparkleFX(
                            px + (Math.random() - 0.5) * 0.4,
                            py + (Math.random() - 0.5) * 0.4,
                            pz + (Math.random() - 0.5) * 0.4,
                            r, g, b, 0.8F, 2);
                    }
                    px += step.x; py += step.y; pz += step.z;
                }
            }
        }

        // Tracking logic
        if (this.target != null) {
            if (this.target.isDead) {
                this.target = null;
                this.getNewTarget();
            }
        } else {
            this.getNewTarget();
        }

        if (this.target != null) {
            double d = this.getDistanceSq(this.target);
            double dx = this.target.posX - this.posX;
            double dy = (this.target.getEntityBoundingBox().minY + this.target.height * 0.6) - this.posY;
            double dz = this.target.posZ - this.posZ;
            double d2 = 0.3;
            dx /= d;
            dy /= d;
            dz /= d;
            this.motionX += dx * d2;
            this.motionY += dy * d2;
            this.motionZ += dz * d2;
            this.motionX = MathHelper.clamp((float) this.motionX, -0.25F, 0.25F);
            this.motionY = MathHelper.clamp((float) this.motionY, -0.25F, 0.25F);
            this.motionZ = MathHelper.clamp((float) this.motionZ, -0.25F, 0.25F);

            if (this.ticksExisted < 5 && this.motionY < 0.0D) {
                this.motionY = Math.abs(this.motionY);
            }
        }
        // If no target found, continue flying in current direction (no tracking adjustments)
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
                this.motionX = vec3.x;
                this.motionY = vec3.y;
                this.motionZ = vec3.z;
                this.motionX *= 0.9;
                this.motionY *= 0.9;
                this.motionZ *= 0.9;
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            }
            return true;
        }
        return false;
    }
}
