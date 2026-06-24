package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TelekinesisUseMessage implements IMessage {

    public TelekinesisUseMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<TelekinesisUseMessage, IMessage> {
        @Override
        public IMessage onMessage(TelekinesisUseMessage message, MessageContext ctx) {
            net.minecraft.entity.player.EntityPlayerMP player = ctx.getServerHandler().player;
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() == CommonProxy.tomeOfPredestiny) {
                player.getServerWorld().addScheduledTask(() -> {
                    CommonProxy.tomeOfPredestiny.onUsingTickServer(stack, player);
                });
            }
            return null;
        }
    }
}
