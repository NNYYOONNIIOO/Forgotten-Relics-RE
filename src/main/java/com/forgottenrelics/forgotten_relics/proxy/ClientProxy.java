/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.RenderPlayer
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 */
package com.forgottenrelics.forgotten_relics.proxy;

import com.forgottenrelics.forgotten_relics.client.LayerCrown;
import com.forgottenrelics.forgotten_relics.client.RenderRageousMissile;
import com.forgottenrelics.forgotten_relics.client.RenderCrimsonOrb;
import com.forgottenrelics.forgotten_relics.client.RenderDarkMatterOrb;
import com.forgottenrelics.forgotten_relics.client.RenderBabylonWeapon;
import com.forgottenrelics.forgotten_relics.client.RenderPrimalOrb;
import com.forgottenrelics.forgotten_relics.client.RenderThunderpealOrb;
import com.forgottenrelics.forgotten_relics.utils.RelicsKeybindHandler;
import com.forgottenrelics.forgotten_relics.client.RenderLunarFlare;
import com.forgottenrelics.forgotten_relics.items.ItemNuclearFury;
import com.forgottenrelics.forgotten_relics.Main;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy
extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityRageousMissile.class, RenderRageousMissile::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityCrimsonOrb.class, RenderCrimsonOrb::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityDarkMatterOrb.class, RenderDarkMatterOrb::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityBabylonWeapon.class, RenderBabylonWeapon::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityPrimalOrb.class, RenderPrimalOrb::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityThunderpealOrb.class, RenderThunderpealOrb::new);
        RenderingRegistry.registerEntityRenderingHandler(com.forgottenrelics.forgotten_relics.entities.EntityLunarFlare.class, RenderLunarFlare::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);
        RelicsKeybindHandler.registerKeybinds();
        MinecraftForge.EVENT_BUS.register(new RelicsKeybindHandler());
    }

    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemNuclearFury) {
            Main.packetInstance.sendToServer(new com.forgottenrelics.forgotten_relics.packets.NuclearFuryLeftClickMessage());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void modelRegistryEvent(ModelRegistryEvent event) {
        AdvancedMiningCharm.registerModels();
        MiningCharm.registerModels();
        AncientAegis.registerModels();
        chaosCore.registerModels();
        darkSunRing.registerModels();
        shinyStone.registerModels();
        terrorCrown.registerModels();
        paradox.registerModels();
        DeificAmulet.registerModels();
        XPTome.registerModels();
        weatherStone.registerModels();
        superpositionRing.registerModels();
        oblivionStone.registerModels();
        oblivionAmulet.registerModels();
        dimensionalMirror.registerModels();
        arcanum.registerModels();
        dormantArcanum.registerModels();
        omegaCore.registerModels();
        falseJustice.registerModels();
        tomeOfBrokenFates.registerModels();
        tomeOfPredestiny.registerModels();
        nuclearFury.registerModels();
        crimsonSpell.registerModels();
        devourerOfTheVoid.registerModels();
        eldritchSpell.registerModels();
        tomeOfLunarFlares.registerModels();
        tomeOfDiscord.registerModels();
        soulTome.registerModels();
        apotheosis.registerModels();
        tomeOfPrimalChaos.registerModels();
        thunderpeal.registerModels();
        edictOfBanishment.registerModels();
        voidGrimoire.registerModels();
        ringOfDiscord.registerModels();
    }

    @Override
    public void addRenderLayers() {
        Map skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        ClientProxy.addLayersToSkin((RenderPlayer)skinMap.get("default"));
        ClientProxy.addLayersToSkin((RenderPlayer)skinMap.get("slim"));
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer) {
        renderPlayer.addLayer((LayerRenderer)new LayerCrown(renderPlayer));
    }
}

