package com.forgottenrelics.forgotten_relics.utils;

import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.packets.DiscordKeybindMessage;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RelicsKeybindHandler {
    @SideOnly(value=Side.CLIENT)
    public static boolean checkVariable;
    public static KeyBinding discordRingKey;

    @SideOnly(value=Side.CLIENT)
    public static void registerKeybinds() {
        discordRingKey = new KeyBinding("key.discordRing", 45, "key.categories.ForgottenRelicsRE");
        ClientRegistry.registerKeyBinding(discordRingKey);
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (discordRingKey.isPressed() && !checkVariable) {
            Main.packetInstance.sendToServer(new DiscordKeybindMessage());
            checkVariable = true;
        } else if (!discordRingKey.isPressed() && checkVariable) {
            checkVariable = false;
        }
    }
}
