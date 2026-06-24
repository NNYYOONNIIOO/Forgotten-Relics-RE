package com.forgottenrelics.forgotten_relics.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.Vector3;

public class VoidGrimoireParticleMessage implements IMessage {

    private double x;
    private double y;
    private double z;
    private boolean finish;

    public VoidGrimoireParticleMessage() {
    }

    public VoidGrimoireParticleMessage(double x, double y, double z, boolean finish) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.finish = finish;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.finish = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeBoolean(this.finish);
    }

    public static class Handler implements IMessageHandler<VoidGrimoireParticleMessage, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(VoidGrimoireParticleMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (!message.finish) {
                    // Casting phase: dark purple wisps converging toward target
                    Vector3 thisPos = new Vector3(message.x, message.y, message.z);

                    for (int counter = 0; counter < 8; counter++) {
                        double calculatedX = message.x + ((Math.random() - 0.5D) * 12.0D);
                        double calculatedY = message.y + ((Math.random() - 0.5D) * 12.0D);
                        double calculatedZ = message.z + ((Math.random() - 0.5D) * 12.0D);

                        Vector3 targetPos = new Vector3(calculatedX, calculatedY, calculatedZ);
                        Vector3 diff = new Vector3(thisPos.x, thisPos.y, thisPos.z).subtract(targetPos);
                        diff = diff.multiply(0.08F);

                        float r = 0.2F + (float) Math.random() * 0.3F;
                        float g = 0.0F;
                        float b = 0.5F + (float) Math.random() * 0.2F;
                        float s = 0.2F + (float) Math.random() * 0.2F;

                        Botania.proxy.wispFX(calculatedX, calculatedY, calculatedZ, r, g, b, s,
                            (float) diff.x, (float) diff.y, (float) diff.z, 0.5F);
                    }

                    // Portal sparkle particles expanding outward
                    for (int counter = 0; counter < 5; counter++) {
                        float r = 0.4F + (float) Math.random() * 0.2F;
                        float g = 0.0F;
                        float b = 0.6F + (float) Math.random() * 0.2F;
                        float xm = ((float) Math.random() - 0.5F) * 0.3F;
                        float ym = ((float) Math.random() - 0.5F) * 0.3F;
                        float zm = ((float) Math.random() - 0.5F) * 0.3F;

                        Botania.proxy.sparkleFX(message.x + xm * 3, message.y + ym * 3, message.z + zm * 3,
                            r, g, b, 1.2F, 3);
                    }
                } else {
                    // Finish burst: massive dark purple wisp explosion
                    for (int i = 0; i < 128; i++) {
                        float r = 0.2F + (float) Math.random() * 0.3F;
                        float g = 0.0F;
                        float b = 0.5F + (float) Math.random() * 0.2F;
                        float s = 0.4F + (float) Math.random() * 0.4F;
                        float m = 0.5F;
                        float xm = ((float) Math.random() - 0.5F) * m;
                        float ym = ((float) Math.random() - 0.5F) * m;
                        float zm = ((float) Math.random() - 0.5F) * m;

                        Botania.proxy.setWispFXDistanceLimit(false);
                        Botania.proxy.wispFX(message.x, message.y, message.z, r, g, b, s, xm, ym, zm, 1.0F);
                        Botania.proxy.setWispFXDistanceLimit(true);
                    }

                    // Pixie death sparkle burst (dark purple)
                    for (int i = 0; i < 12; i++) {
                        Botania.proxy.sparkleFX(
                            message.x + (Math.random() - 0.5) * 0.5,
                            message.y + (Math.random() - 0.5) * 0.5,
                            message.z + (Math.random() - 0.5) * 0.5,
                            0.3F, 0.0F, 0.5F, 1.5F + (float) Math.random() * 0.5F, 5);
                    }
                }
            });
            return null;
        }
    }
}
