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

public class BanishmentCastingMessage implements IMessage {

    private double x;
    private double y;
    private double z;
    private int amount;

    public BanishmentCastingMessage() {
    }

    public BanishmentCastingMessage(double x, double y, double z, int amount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.amount);
    }

    public static class Handler implements IMessageHandler<BanishmentCastingMessage, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(BanishmentCastingMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Vector3 thisPos = new Vector3(message.x, message.y, message.z);

                for (int counter = 0; counter < message.amount; counter++) {
                    double calculatedX = message.x + ((Math.random() - 0.5D) * 8.0D);
                    double calculatedY = message.y + ((Math.random() - 0.5D) * 8.0D);
                    double calculatedZ = message.z + ((Math.random() - 0.5D) * 8.0D);

                    Vector3 targetPos = new Vector3(calculatedX, calculatedY, calculatedZ);
                    Vector3 diff = new Vector3(thisPos.x, thisPos.y, thisPos.z).subtract(targetPos);
                    diff = diff.multiply(0.08F);

                    float r = 0.9F + (float) Math.random() * 0.1F;
                    float g = 0.1F + (float) Math.random() * 0.15F;
                    float b = 0.0F;
                    float s = 0.2F + (float) Math.random() * 0.2F;

                    Botania.proxy.wispFX(calculatedX, calculatedY, calculatedZ, r, g, b, s,
                        (float) diff.x, (float) diff.y, (float) diff.z, 0.5F);
                }
            });
            return null;
        }
    }
}
