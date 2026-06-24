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

public class LunarFlareParticleMessage implements IMessage {

    private double x;
    private double y;
    private double z;

    public LunarFlareParticleMessage() {
    }

    public LunarFlareParticleMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static class Handler implements IMessageHandler<LunarFlareParticleMessage, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(LunarFlareParticleMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // Green wisp explosion particles
                for (int i = 0; i < 48; i++) {
                    float r = 0.0F;
                    float g = 0.8F + (float) Math.random() * 0.2F;
                    float b = 0.4F + (float) Math.random() * 0.6F;
                    float s = 0.5F + (float) Math.random() * 0.5F;
                    float m = 0.25F;
                    float xm = ((float) Math.random() - 0.5F) * m;
                    float ym = ((float) Math.random() - 0.5F) * m;
                    float zm = ((float) Math.random() - 0.5F) * m;

                    Botania.proxy.wispFX(message.x, message.y, message.z, r, g, b, s, xm, ym, zm, 1.0F);
                }

                // Ward hemisphere effect
                FXBlockWard ward = new FXBlockWard(
                    Minecraft.getMinecraft().world,
                    message.x, message.y, message.z);
                ward.setRBGColorF(0.0F, 1.0F, 0.6F);
                Minecraft.getMinecraft().effectRenderer.addEffect(ward);
            });
            return null;
        }
    }
}
