/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 */
package com.forgottenrelics.forgotten_relics.network;

import com.forgottenrelics.forgotten_relics.network.PlayerVariables;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class IPlayerStorage
implements Capability.IStorage<PlayerVariables> {
    public NBTBase writeNBT(Capability<PlayerVariables> capability, PlayerVariables instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        instance.saveNBTData(compound);
        return compound;
    }

    public void readNBT(Capability<PlayerVariables> capability, PlayerVariables instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            instance.loadNBTData((NBTTagCompound)nbt);
        }
    }
}

