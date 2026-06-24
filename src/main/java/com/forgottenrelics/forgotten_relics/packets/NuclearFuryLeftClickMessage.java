package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.items.ItemNuclearFury;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class NuclearFuryLeftClickMessage implements IMessage {

    public NuclearFuryLeftClickMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<NuclearFuryLeftClickMessage, IMessage> {
        @Override
        public IMessage onMessage(NuclearFuryLeftClickMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemNuclearFury.clearMissiles(player);
            });
            return null;
        }
    }
}
