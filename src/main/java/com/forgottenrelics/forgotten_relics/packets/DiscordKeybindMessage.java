package com.forgottenrelics.forgotten_relics.packets;

import com.forgottenrelics.forgotten_relics.items.ItemRingOfDiscord;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import com.forgottenrelics.forgotten_relics.utils.SuperpositionHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DiscordKeybindMessage implements IMessage {

    public DiscordKeybindMessage() {
    }

    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<DiscordKeybindMessage, IMessage> {
        public IMessage onMessage(DiscordKeybindMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            // Check if player has the Ring of Discord equipped as bauble
            int slot = baubles.api.BaublesApi.isBaubleEquipped(player, CommonProxy.ringOfDiscord);
            if (slot < 0) {
                return null;
            }

            ItemStack ringStack = baubles.api.BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
            if (ringStack.isEmpty() || !(ringStack.getItem() instanceof ItemRingOfDiscord)) {
                return null;
            }

            // Toggle discord mode
            boolean newState = !ItemRingOfDiscord.isDiscordEnabled(ringStack);
            ItemRingOfDiscord.setDiscordEnabled(ringStack, newState);

            // Send status message to player
            String statusKey = newState ? "item.ItemDiscordRing5.lore" : "item.ItemDiscordRing4.lore";
            player.sendMessage(new TextComponentTranslation(statusKey));

            return null;
        }
    }
}
