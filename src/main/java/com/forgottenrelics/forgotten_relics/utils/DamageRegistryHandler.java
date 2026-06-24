/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSource
 */
package com.forgottenrelics.forgotten_relics.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class DamageRegistryHandler {

    public static class DamageSourceSuperpositionDefined
    extends EntityDamageSource {
        public DamageSourceSuperpositionDefined(Entity entity) {
            super("superpositionedDamage", entity);
        }
    }

    public static class DamageSourceSuperposition
    extends DamageSource {
        public DamageSourceSuperposition() {
            super("superpositionedDamage");
        }
    }

    public static class DamageSourceMagic
    extends EntityDamageSource {
        public DamageSourceMagic(Entity entity) {
            super("forgottenMagic", entity);
            this.setDamageBypassesArmor();
        }
    }

    public static class DamageSourceDarkMatter
    extends EntityDamageSource {
        public DamageSourceDarkMatter(Entity entity) {
            super("attackDarkMatter", entity);
            this.setDamageBypassesArmor();
            this.setExplosion();
        }
    }

    public static class DamageSourceTLightning
    extends EntityDamageSource {
        public DamageSourceTLightning(Entity entity) {
            super("attackLightning", entity);
        }
    }

    public static class DamageSourceFate
    extends DamageSource {
        public DamageSourceFate() {
            super("attackFate");
            this.setMagicDamage();
            this.setDamageIsAbsolute();
            this.setDamageBypassesArmor();
            this.setDamageAllowedInCreativeMode();
        }
    }

    public static class DamageSourceOblivion
    extends DamageSource {
        public DamageSourceOblivion() {
            super("attackOblivion");
            this.setMagicDamage();
        }
    }

    public static class DamageSourceTrueDamageUndef
    extends DamageSource {
        public DamageSourceTrueDamageUndef() {
            super("trueDamageUndef");
            this.setMagicDamage();
            this.setDamageIsAbsolute();
            this.setDamageBypassesArmor();
            this.setDamageAllowedInCreativeMode();
        }
    }

    public static class DamageSourceSoulDrain
    extends EntityDamageSource {
        public DamageSourceSoulDrain(Entity entity) {
            super("soulAttack", entity);
            this.setDamageBypassesArmor();
            this.setDamageIsAbsolute();
            this.setExplosion();
        }
    }

    public static class DamageSourceTrueDamage
    extends EntityDamageSource {
        public DamageSourceTrueDamage(Entity entity) {
            super("trueDamage", entity);
            this.setDamageBypassesArmor();
            this.setDamageIsAbsolute();
            this.setExplosion();
            this.setDamageAllowedInCreativeMode();
        }
    }
}

