/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package com.forgottenrelics.forgotten_relics.proxy;

import com.forgottenrelics.forgotten_relics.items.ItemAdvancedMiningCharm;
import com.forgottenrelics.forgotten_relics.items.ItemAncientAegis;
import com.forgottenrelics.forgotten_relics.items.ItemArcanum;
import com.forgottenrelics.forgotten_relics.items.ItemChaosCore;
import com.forgottenrelics.forgotten_relics.items.ItemDarkSunRing;
import com.forgottenrelics.forgotten_relics.items.ItemDeificAmulet;
import com.forgottenrelics.forgotten_relics.items.ItemDimensionalMirror;
import com.forgottenrelics.forgotten_relics.items.ItemDormantArcanum;
import com.forgottenrelics.forgotten_relics.items.ItemFalseJustice;
import com.forgottenrelics.forgotten_relics.items.ItemMiningCharm;
import com.forgottenrelics.forgotten_relics.items.ItemNuclearFury;
import com.forgottenrelics.forgotten_relics.items.ItemCrimsonSpell;
import com.forgottenrelics.forgotten_relics.items.ItemDevourerOfTheVoid;
import com.forgottenrelics.forgotten_relics.items.ItemEldritchSpell;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfLunarFlares;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfDiscord;
import com.forgottenrelics.forgotten_relics.items.ItemSoulTome;
import com.forgottenrelics.forgotten_relics.items.ItemApotheosis;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfPrimalChaos;
import com.forgottenrelics.forgotten_relics.items.ItemThunderpeal;
import com.forgottenrelics.forgotten_relics.items.ItemEdictOfBanishment;
import com.forgottenrelics.forgotten_relics.items.ItemVoidGrimoire;
import com.forgottenrelics.forgotten_relics.items.ItemRingOfDiscord;
import com.forgottenrelics.forgotten_relics.items.ItemOblivionAmulet;
import com.forgottenrelics.forgotten_relics.items.ItemOblivionStone;
import com.forgottenrelics.forgotten_relics.items.ItemOmegaCore;
import com.forgottenrelics.forgotten_relics.items.ItemParadox;
import com.forgottenrelics.forgotten_relics.items.ItemShinyStone;
import com.forgottenrelics.forgotten_relics.items.ItemSuperpositionRing;
import com.forgottenrelics.forgotten_relics.items.ItemTerrorCrown;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfBrokenFates;
import com.forgottenrelics.forgotten_relics.items.ItemTomeOfPredestiny;
import com.forgottenrelics.forgotten_relics.items.ItemWeatherStone;
import com.forgottenrelics.forgotten_relics.items.ItemXPTome;
import com.forgottenrelics.forgotten_relics.network.IPlayerStorage;
import com.forgottenrelics.forgotten_relics.network.PlayerVariables;
import com.forgottenrelics.forgotten_relics.utils.RelicsMaterialHandler;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CommonProxy {
    public static ItemAdvancedMiningCharm AdvancedMiningCharm = new ItemAdvancedMiningCharm();
    public static ItemMiningCharm MiningCharm = new ItemMiningCharm();
    public static ItemAncientAegis AncientAegis = new ItemAncientAegis();
    public static ItemChaosCore chaosCore = new ItemChaosCore();
    public static ItemDarkSunRing darkSunRing = new ItemDarkSunRing();
    public static ItemShinyStone shinyStone = new ItemShinyStone();
    public static ItemParadox paradox = new ItemParadox(RelicsMaterialHandler.materialParadoxicalStuff);
    public static ItemTerrorCrown terrorCrown = new ItemTerrorCrown(EntityEquipmentSlot.HEAD, ItemArmor.ArmorMaterial.GOLD);
    public static ItemDeificAmulet DeificAmulet = new ItemDeificAmulet();
    public static ItemXPTome XPTome = new ItemXPTome();
    public static ItemWeatherStone weatherStone = new ItemWeatherStone();
    public static ItemSuperpositionRing superpositionRing = new ItemSuperpositionRing();
    public static ItemOblivionStone oblivionStone = new ItemOblivionStone();
    public static ItemOblivionAmulet oblivionAmulet = new ItemOblivionAmulet();
    public static ItemDimensionalMirror dimensionalMirror = new ItemDimensionalMirror();
    public static ItemArcanum arcanum = new ItemArcanum();
    public static ItemDormantArcanum dormantArcanum = new ItemDormantArcanum();
    public static ItemOmegaCore omegaCore = new ItemOmegaCore();
    public static ItemFalseJustice falseJustice = new ItemFalseJustice();
    public static ItemTomeOfBrokenFates tomeOfBrokenFates = new ItemTomeOfBrokenFates();
    public static ItemTomeOfPredestiny tomeOfPredestiny = new ItemTomeOfPredestiny();
    public static ItemNuclearFury nuclearFury = new ItemNuclearFury();
    public static ItemCrimsonSpell crimsonSpell = new ItemCrimsonSpell();
    public static ItemDevourerOfTheVoid devourerOfTheVoid = new ItemDevourerOfTheVoid();
    public static ItemEldritchSpell eldritchSpell = new ItemEldritchSpell();
    public static ItemTomeOfLunarFlares tomeOfLunarFlares = new ItemTomeOfLunarFlares();
    public static ItemTomeOfDiscord tomeOfDiscord = new ItemTomeOfDiscord();
    public static ItemSoulTome soulTome = new ItemSoulTome();
    public static ItemApotheosis apotheosis = new ItemApotheosis();
    public static ItemTomeOfPrimalChaos tomeOfPrimalChaos = new ItemTomeOfPrimalChaos();
    public static ItemThunderpeal thunderpeal = new ItemThunderpeal();
    public static ItemEdictOfBanishment edictOfBanishment = new ItemEdictOfBanishment();
    public static ItemVoidGrimoire voidGrimoire = new ItemVoidGrimoire();
    public static ItemRingOfDiscord ringOfDiscord = new ItemRingOfDiscord();

    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(PlayerVariables.class, (Capability.IStorage)new IPlayerStorage(), () -> null);
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(AdvancedMiningCharm);
        event.getRegistry().register(MiningCharm);
        event.getRegistry().register(AncientAegis);
        event.getRegistry().register(chaosCore);
        event.getRegistry().register(darkSunRing);
        event.getRegistry().register(shinyStone);
        event.getRegistry().register(terrorCrown);
        event.getRegistry().register(paradox);
        event.getRegistry().register(DeificAmulet);
        event.getRegistry().register(XPTome);
        event.getRegistry().register(weatherStone);
        event.getRegistry().register(superpositionRing);
        event.getRegistry().register(oblivionStone);
        event.getRegistry().register(oblivionAmulet);
        event.getRegistry().register(dimensionalMirror);
        event.getRegistry().register(arcanum);
        event.getRegistry().register(dormantArcanum);
        event.getRegistry().register(omegaCore);
        event.getRegistry().register(falseJustice);
        event.getRegistry().register(tomeOfBrokenFates);
        event.getRegistry().register(tomeOfPredestiny);
        event.getRegistry().register(nuclearFury);
        event.getRegistry().register(crimsonSpell);
        event.getRegistry().register(devourerOfTheVoid);
        event.getRegistry().register(eldritchSpell);
        event.getRegistry().register(tomeOfLunarFlares);
        event.getRegistry().register(tomeOfDiscord);
        event.getRegistry().register(soulTome);
        event.getRegistry().register(apotheosis);
        event.getRegistry().register(tomeOfPrimalChaos);
        event.getRegistry().register(thunderpeal);
        event.getRegistry().register(edictOfBanishment);
        event.getRegistry().register(voidGrimoire);
        event.getRegistry().register(ringOfDiscord);
    }

    @SideOnly(value=Side.CLIENT)
    public void modelRegistryEvent(ModelRegistryEvent event) {
    }

    public void addRenderLayers() {
    }
}

