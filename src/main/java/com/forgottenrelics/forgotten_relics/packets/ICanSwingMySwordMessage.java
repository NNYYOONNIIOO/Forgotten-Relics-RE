/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.forgottenrelics.forgotten_relics.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ICanSwingMySwordMessage
implements IMessage {
    private boolean swingTheSword;

    public ICanSwingMySwordMessage() {
    }

    public ICanSwingMySwordMessage(boolean swingTheSword) {
        this.swingTheSword = swingTheSword;
    }

    public void fromBytes(ByteBuf buf) {
        this.swingTheSword = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.swingTheSword);
    }

    public static class Handler
    implements IMessageHandler<ICanSwingMySwordMessage, IMessage> {
        @SideOnly(value=Side.CLIENT)
        public IMessage onMessage(ICanSwingMySwordMessage message, MessageContext ctx) {
            EntityPlayerSP player = FMLClientHandler.instance().getClientPlayerEntity();
            if (message.swingTheSword) {
                if (player.getActiveHand() != null) {
                    player.setActiveHand(player.getActiveHand());
                }
            }
            return null;
        }
    }
}

