/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NotificationMessage
implements IMessage {
    private int type;

    public NotificationMessage() {
    }

    public NotificationMessage(int type) {
        this.type = type;
    }

    public void fromBytes(ByteBuf buf) {
        this.type = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.type);
    }

    public static class Handler
    implements IMessageHandler<NotificationMessage, IMessage> {
        @SideOnly(value=Side.CLIENT)
        public IMessage onMessage(NotificationMessage message, MessageContext ctx) {
            String notification;
            EntityPlayerSP player = FMLClientHandler.instance().getClientPlayerEntity();
            switch (message.type) {
                case 1: {
                    notification = I18n.format((String)"notification.fate_cooldown_over", (Object[])new Object[0]);
                    break;
                }
                case 2: {
                    notification = I18n.format((String)"notification.overdamage_block", (Object[])new Object[0]);
                    break;
                }
                default: {
                    Main.log.error("Recived invalid notification!");
                    return null;
                }
            }
            player.sendStatusMessage((ITextComponent)new TextComponentString(I18n.format((String)notification, (Object[])new Object[0])), true);
            return null;
        }
    }
}

