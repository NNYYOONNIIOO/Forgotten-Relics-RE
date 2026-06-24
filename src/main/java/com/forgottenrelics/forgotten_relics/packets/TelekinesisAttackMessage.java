package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TelekinesisAttackMessage implements IMessage {

    public TelekinesisAttackMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<TelekinesisAttackMessage, IMessage> {
        @Override
        public IMessage onMessage(TelekinesisAttackMessage message, MessageContext ctx) {
            net.minecraft.entity.player.EntityPlayerMP player = ctx.getServerHandler().player;
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() == CommonProxy.tomeOfPredestiny) {
                player.getServerWorld().addScheduledTask(() -> {
                    CommonProxy.tomeOfPredestiny.leftClick(player);
                });
            }
            return null;
        }
    }
}
