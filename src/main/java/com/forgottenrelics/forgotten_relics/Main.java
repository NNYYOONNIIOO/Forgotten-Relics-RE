/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityInject
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.SidedProxy
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.NetworkRegistry
 *  net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  net.minecraftforge.fml.common.registry.EntityRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  thaumcraft.api.ThaumcraftApi
 *  thaumcraft.api.items.ItemsTC
 *  thaumcraft.api.research.IScanThing
 *  thaumcraft.api.research.ResearchCategories
 *  thaumcraft.api.research.ScanItem
 *  thaumcraft.api.research.ScanningManager
 *  vazkii.botania.common.item.ModItems
 */
package com.forgottenrelics.forgotten_relics;

import com.forgottenrelics.forgotten_relics.RelicsEventHandler;
import com.forgottenrelics.forgotten_relics.config.RelicsConfigHandler;
import com.forgottenrelics.forgotten_relics.entities.EntityShinyEnergy;
import com.forgottenrelics.forgotten_relics.entities.EntityRageousMissile;
import com.forgottenrelics.forgotten_relics.entities.EntityCrimsonOrb;
import com.forgottenrelics.forgotten_relics.entities.EntityCrimsonZone;
import com.forgottenrelics.forgotten_relics.entities.EntityDarkMatterOrb;
import com.forgottenrelics.forgotten_relics.entities.EntityLunarFlare;
import com.forgottenrelics.forgotten_relics.entities.EntitySoulEnergy;
import com.forgottenrelics.forgotten_relics.entities.EntityBabylonWeapon;
import com.forgottenrelics.forgotten_relics.entities.EntityPrimalOrb;
import com.forgottenrelics.forgotten_relics.entities.EntityThunderpealOrb;
import com.forgottenrelics.forgotten_relics.network.PlayerVariables;
import com.forgottenrelics.forgotten_relics.packets.BurstMessage;
import com.forgottenrelics.forgotten_relics.packets.ICanSwingMySwordMessage;
import com.forgottenrelics.forgotten_relics.packets.NotificationMessage;
import com.forgottenrelics.forgotten_relics.packets.PlayerMotionUpdateMessage;
import com.forgottenrelics.forgotten_relics.packets.TelekinesisAttackMessage;
import com.forgottenrelics.forgotten_relics.packets.TelekinesisUseMessage;
import com.forgottenrelics.forgotten_relics.packets.NuclearFuryLeftClickMessage;
import com.forgottenrelics.forgotten_relics.packets.ApotheosisParticleMessage;
import com.forgottenrelics.forgotten_relics.packets.LightningMessage;
import com.forgottenrelics.forgotten_relics.packets.LunarFlareParticleMessage;
import com.forgottenrelics.forgotten_relics.packets.BanishmentCastingMessage;
import com.forgottenrelics.forgotten_relics.packets.InfernalParticleMessage;
import com.forgottenrelics.forgotten_relics.packets.VoidGrimoireParticleMessage;
import com.forgottenrelics.forgotten_relics.packets.DiscordKeybindMessage;
import com.forgottenrelics.forgotten_relics.proxy.CommonProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.item.ModItems;

@Mod(modid="forgotten_relics", name="Forgotten Relics RE", version="0.7.0", acceptedMinecraftVersions="[1.12.2]")
public class Main {
    public static final String MOD_ID = "forgotten_relics";
    public static final String MOD_NAME = "Forgotten Relics RE";
    public static final String VERSION = "0.7.0";
    @CapabilityInject(value=PlayerVariables.class)
    public static Capability<PlayerVariables> PLAYER = null;
    public static SimpleNetworkWrapper packetInstance;
    public static final Logger log;
    public RelicsConfigHandler configHandler = new RelicsConfigHandler();
    @SidedProxy(clientSide="com.forgottenrelics.forgotten_relics.proxy.ClientProxy", serverSide="com.forgottenrelics.forgotten_relics.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static List<String> darkRingDamageNegations;
    public static HashMap<EntityPlayer, Integer> castingCooldowns;
    public static CreativeTabs tabForgottenRelics;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RelicsConfigHandler.configDisposition(event);
        packetInstance = NetworkRegistry.INSTANCE.newSimpleChannel("RelicsChannel");
        packetInstance.registerMessage(BurstMessage.Handler.class, BurstMessage.class, 2, Side.CLIENT);
        packetInstance.registerMessage(ICanSwingMySwordMessage.Handler.class, ICanSwingMySwordMessage.class, 8, Side.CLIENT);
        packetInstance.registerMessage(NotificationMessage.Handler.class, NotificationMessage.class, 15, Side.CLIENT);
        packetInstance.registerMessage(PlayerMotionUpdateMessage.Handler.class, PlayerMotionUpdateMessage.class, 16, Side.CLIENT);
        packetInstance.registerMessage(TelekinesisUseMessage.Handler.class, TelekinesisUseMessage.class, 17, Side.SERVER);
        packetInstance.registerMessage(TelekinesisAttackMessage.Handler.class, TelekinesisAttackMessage.class, 18, Side.SERVER);
        packetInstance.registerMessage(NuclearFuryLeftClickMessage.Handler.class, NuclearFuryLeftClickMessage.class, 19, Side.SERVER);
        packetInstance.registerMessage(ApotheosisParticleMessage.Handler.class, ApotheosisParticleMessage.class, 20, Side.CLIENT);
        packetInstance.registerMessage(LightningMessage.Handler.class, LightningMessage.class, 21, Side.CLIENT);
        packetInstance.registerMessage(LunarFlareParticleMessage.Handler.class, LunarFlareParticleMessage.class, 22, Side.CLIENT);
        packetInstance.registerMessage(BanishmentCastingMessage.Handler.class, BanishmentCastingMessage.class, 23, Side.CLIENT);
        packetInstance.registerMessage(InfernalParticleMessage.Handler.class, InfernalParticleMessage.class, 24, Side.CLIENT);
        packetInstance.registerMessage(VoidGrimoireParticleMessage.Handler.class, VoidGrimoireParticleMessage.class, 25, Side.CLIENT);
        packetInstance.registerMessage(DiscordKeybindMessage.Handler.class, DiscordKeybindMessage.class, 26, Side.SERVER);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "shiny_energy"), EntityShinyEnergy.class, (String)"shiny_energy", (int)0, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "rageous_missile"), EntityRageousMissile.class, (String)"rageous_missile", (int)1, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "crimson_orb"), EntityCrimsonOrb.class, (String)"crimson_orb", (int)2, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "crimson_zone"), EntityCrimsonZone.class, (String)"crimson_zone", (int)3, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "dark_matter_orb"), EntityDarkMatterOrb.class, (String)"dark_matter_orb", (int)4, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "lunar_flare"), EntityLunarFlare.class, (String)"lunar_flare", (int)5, (Object)MOD_ID, (int)196, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "soul_energy"), EntitySoulEnergy.class, (String)"soul_energy", (int)6, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "babylon_weapon"), EntityBabylonWeapon.class, (String)"babylon_weapon", (int)7, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "primal_orb"), EntityPrimalOrb.class, (String)"primal_orb", (int)8, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation(MOD_ID, "thunderpeal_orb"), EntityThunderpealOrb.class, (String)"thunderpeal_orb", (int)9, (Object)MOD_ID, (int)64, (int)20, (boolean)true);
        MinecraftForge.EVENT_BUS.register((Object)new RelicsEventHandler());
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!NetherStar", new ItemStack(Items.NETHER_STAR)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!VoidSeerCharm", new ItemStack(ItemsTC.charmVoidseer)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EternalLifeEssence", new ItemStack(ModItems.manaResource, 1, 5)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!MiningCharm", new ItemStack((Item)CommonProxy.MiningCharm)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!PixieDust", new ItemStack(ModItems.manaResource, 1, 8)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!DragonStone", new ItemStack(ModItems.manaResource, 1, 9)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!VoidIngot", new ItemStack(ItemsTC.ingots, 1, 1)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EnchantedGoldenApple", new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!ChaosCore", new ItemStack((Item)CommonProxy.chaosCore)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!BloodPendant", new ItemStack(ModItems.bloodPendant)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!BlazeRod", new ItemStack(Items.BLAZE_ROD)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!SuperpositionRing", new ItemStack((Item)CommonProxy.superpositionRing)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!ThaumiumIngot", new ItemStack(ItemsTC.ingots, 1, 0)));
        // Spellbook research scan triggers
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!ElementiumIngot", new ItemStack(ModItems.manaResource, 1, 7)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!GaiaSpirit", new ItemStack(ModItems.manaResource, 1, 5)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!DragonStone", new ItemStack(ModItems.manaResource, 1, 9)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EldritchEye", new ItemStack(ItemsTC.eldritchEye)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!CrimsonRites", new ItemStack(ItemsTC.curio, 1, 6)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!PrimalFocus", new ItemStack(ItemsTC.focus1)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!MissileRod", new ItemStack(ModItems.missileRod)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!TerrasteelIngot", new ItemStack(ModItems.manaResource, 1, 4)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!Alumentum", new ItemStack(ItemsTC.alumentum)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!PechFocus", new ItemStack(ItemsTC.pechWand)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!JarredBrain", new ItemStack(BlocksTC.jarBrain)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EnderEye", new ItemStack(Items.ENDER_EYE)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!StarSword", new ItemStack(ModItems.starSword)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EnderAir", new ItemStack(ModItems.manaResource, 1, 15)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!HellFocus", new ItemStack(ItemsTC.focus1)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!NetherWart", new ItemStack(Items.NETHER_WART)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!EldritchTablet", new ItemStack(ItemsTC.runedTablet)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!VoidSeed", new ItemStack(ItemsTC.voidSeed)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!Bedrock", new ItemStack(Blocks.BEDROCK)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!GravityRod", new ItemStack(ModItems.gravityRod)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!ShockFocus", new ItemStack(ItemsTC.focus1)));
        // New spellbook research scan triggers
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!TotemOfUndying", new ItemStack(Items.TOTEM_OF_UNDYING)));
        ScanningManager.addScannableThing((IScanThing)new ScanEntity("!SkeletonHorse", net.minecraft.entity.passive.EntitySkeletonHorse.class, true));
        ScanningManager.addScannableThing((IScanThing)new ScanEntity("!CrimsonCultist", EntityCultist.class, true));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!ParadoxMaterial", new ItemStack(ItemsTC.causalityCollapser)));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!KingsKey", new ItemStack(ModItems.kingKey)));
        ScanningManager.addScannableThing((IScanThing)new ScanEntity("!GaiaGuardian", EntityDoppleganger.class, true));
        ScanningManager.addScannableThing((IScanThing)new ScanItem("!TerrorCrown", new ItemStack((Item)CommonProxy.terrorCrown)));
        ResearchCategories.registerCategory((String)"FORGOTTEN_RELICS", (String)"UNLOCKELDRITCH", null, (ResourceLocation)new ResourceLocation(MOD_ID, "textures/items/omega_core.png"), (ResourceLocation)new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_5.jpg"));
        ThaumcraftApi.registerResearchLocation((ResourceLocation)new ResourceLocation(MOD_ID, "research/baubles"));
        ThaumcraftApi.registerResearchLocation((ResourceLocation)new ResourceLocation(MOD_ID, "research/spellbooks"));
        darkRingDamageNegations.add(DamageSource.LAVA.damageType);
        darkRingDamageNegations.add(DamageSource.IN_FIRE.damageType);
        darkRingDamageNegations.add(DamageSource.ON_FIRE.damageType);
        proxy.addRenderLayers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    static {
        log = LogManager.getLogger((String)"ForgottenRelicsRE");
        darkRingDamageNegations = new ArrayList<String>();
        castingCooldowns = new HashMap();
        tabForgottenRelics = new CreativeTabs("tabForgottenRelics"){

            @SideOnly(value=Side.CLIENT)
            public ItemStack getTabIconItem() {
                return new ItemStack((Item)CommonProxy.terrorCrown);
            }
        };
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            proxy.registerItems(event);
        }

        @SubscribeEvent
        @SideOnly(value=Side.CLIENT)
        public static void modelRegistryEvent(ModelRegistryEvent event) {
            proxy.modelRegistryEvent(event);
        }
    }
}

