/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.Teleporter
 *  net.minecraft.world.WorldServer
 */
package com.forgottenrelics.forgotten_relics.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ExtradimensionalTeleporter
extends Teleporter {
    private double X;
    private double Y;
    private double Z;

    public ExtradimensionalTeleporter(WorldServer p_i1963_1_, double x, double y, double z) {
        super(p_i1963_1_);
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public void placeInPortal(Entity entity, float rotationYaw) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            player.setPosition(this.X, this.Y, this.Z);
        } else {
            entity.posX = this.X;
            entity.posY = this.Y;
            entity.posZ = this.Z;
        }
    }
}

