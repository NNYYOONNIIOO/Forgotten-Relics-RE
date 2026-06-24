package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.client.fx.FXBlockWard;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;

public class ApotheosisParticleMessage implements IMessage {

    private double x;
    private double y;
    private double z;
    private int quantity;

    public ApotheosisParticleMessage() {
    }

    public ApotheosisParticleMessage(double x, double y, double z, int quantity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.quantity);
    }

    public static class Handler implements IMessageHandler<ApotheosisParticleMessage, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ApotheosisParticleMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // Golden wisp explosion particles
                for (int i = 0; i < message.quantity; i++) {
                    float r = 0.8F + (float) Math.random() * 0.2F;
                    float g = 0.8F + (float) Math.random() * 0.2F;
                    float b = 0.0F;
                    float s = 0.3F + (float) Math.random() * 0.3F;
                    float m = 0.25F;
                    float xm = ((float) Math.random() - 0.5F) * m;
                    float ym = ((float) Math.random() - 0.5F) * m;
                    float zm = ((float) Math.random() - 0.5F) * m;

                    Botania.proxy.wispFX(message.x, message.y, message.z, r, g, b, s, xm, ym, zm, 1.0F);
                }

                // Ward hemisphere effect - billboard style, always faces the player
                FXBlockWard ward = new FXBlockWard(
                    Minecraft.getMinecraft().world,
                    message.x, message.y, message.z);
                Minecraft.getMinecraft().effectRenderer.addEffect(ward);
            });
            return null;
        }
    }
}
