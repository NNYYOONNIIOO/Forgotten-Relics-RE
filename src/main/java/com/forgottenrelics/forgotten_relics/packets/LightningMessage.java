package com.forgottenrelics.forgotten_relics.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class LightningMessage implements IMessage {

    private double x, y, z;
    private double tx, ty, tz;
    private boolean main;
    private float width;

    public LightningMessage() {
    }

    public LightningMessage(double x, double y, double z, double tx, double ty, double tz, boolean main) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.main = main;
        this.width = main ? 0.075F : 0.04F;
    }

    public LightningMessage(double x, double y, double z, double tx, double ty, double tz, boolean main, float width) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.main = main;
        this.width = width;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.tx = buf.readDouble();
        this.ty = buf.readDouble();
        this.tz = buf.readDouble();
        this.main = buf.readBoolean();
        this.width = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.tx);
        buf.writeDouble(this.ty);
        buf.writeDouble(this.tz);
        buf.writeBoolean(this.main);
        buf.writeFloat(this.width);
    }

    public static class Handler implements IMessageHandler<LightningMessage, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(LightningMessage message, MessageContext ctx) {
            FXDispatcher.INSTANCE.arcBolt(
                message.x, message.y, message.z,
                message.tx, message.ty, message.tz,
                0.4F, 0.6F, 1.0F, message.width);
            return null;
        }
    }
}
