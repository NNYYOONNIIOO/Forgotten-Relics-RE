/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.common.util.INBTSerializable
 */
package com.forgottenrelics.forgotten_relics.network;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.network.PlayerVariables;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class IPlayerProvider
implements ICapabilityProvider,
INBTSerializable<NBTTagCompound> {
    private final PlayerVariables playerVariables;

    public IPlayerProvider(PlayerVariables playerVariables) {
        this.playerVariables = playerVariables;
    }

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == Main.PLAYER;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == Main.PLAYER) {
            return (T)this.playerVariables;
        }
        return null;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound output = new NBTTagCompound();
        if (this.playerVariables != null) {
            this.playerVariables.saveNBTData(output);
        }
        return output;
    }

    public void deserializeNBT(NBTTagCompound input) {
        if (this.playerVariables != null) {
            this.playerVariables.loadNBTData(input);
        }
    }
}

