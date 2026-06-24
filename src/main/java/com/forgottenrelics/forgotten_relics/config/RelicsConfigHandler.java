/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.common.config.Property
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 */
package com.forgottenrelics.forgotten_relics.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class RelicsConfigHandler {
    public static float damageApotheosisDirect;
    public static float damageApotheosisImpact;
    public static float damageLunarFlareDirect;
    public static float damageLunarFlareImpact;
    public static float paradoxDamageCap;
    public static float telekinesisTomeDamageMIN;
    public static float telekinesisTomeDamageMAX;
    public static float nuclearFuryDamageMIN;
    public static float nuclearFuryDamageMAX;
    public static float crimsonSpellDamageMIN;
    public static float crimsonSpellDamageMAX;
    public static float weatherStoneVisMult;
    public static float chaosTomeDamageCap;
    public static float eldritchSpellDamage;
    public static float eldritchSpellDamageEx;
    public static float discordTomeVisMult;
    public static float telekinesisTomeVisMult;
    public static float chaosTomeVisMult;
    public static float eldritchSpellVisMult;
    public static float crimsonSpellVisMult;
    public static float soulTomeVisMult;
    public static float nuclearFuryVisMult;
    public static float lunarFlaresVisMult;
    public static float apotheosisVisMult;
    public static float fateTomeVisMult;
    public static float obeliskDrainerVisMult;
    public static float oblivionAmuletVisMult;
    public static float deificAmuletVisMult;
    public static float dormantArcanumVisMult;
    public static float arcanumGenRate;
    public static float soulTomeDivisor;
    public static boolean falseJusticeEnabled;
    public static int shinyStoneCheckrate;
    public static boolean deificAmuletInvincibility;
    public static boolean deificAmuletEffectImmunity;
    public static float darkSunRingDamageCap;
    public static float darkSunRingDeflectChance;
    public static boolean darkSunRingHealLimit;
    public static boolean interdimensionalMirror;
    public static float ancientAegisDamageReduction;
    public static float nebulousCoreDodgeChance;
    public static float miningCharmBoost;
    public static float miningCharmReach;
    public static float advancedMiningCharmBoost;
    public static float advancedMiningCharmReach;
    public static float damageThunderpealDirect;
    public static float damageThunderpealBolt;
    public static float thunderpealVisMult;
    public static float overthrowerVisMult;
    public static boolean overthrowerAffectsPlayers;
    public static float voidGrimoireVisMult;
    public static int runicCost;
    public static int runicRechargeDelay;
    public static int runicRechargeSpeed;
    public static int notificationDelay;
    public static int fateTomeCooldownMIN;
    public static int fateTomeCooldownMAX;
    public static boolean telekinesisOnPlayers;
    public static float revelationModifier;
    public static int researchInspectionFrequency;
    public static double knowledgeChance;
    public static int outerLandsCheckrate;
    public static float outerLandsAntiAbuseDamage;
    public static boolean outerLandsAntiAbuseEnabled;
    public static boolean voidGrimoireEnabled;
    public static boolean updateNotificationsEnabled;
    public static boolean memesEnabled;
    public static int oblivionStoneSoftCap;
    public static int oblivionStoneHardCap;
    public static float guardianNotificationRadius;
    public static String[] forgottenKnowledgeOverrides;
    public static boolean forgottenKnowledgeOverridingEnabled;
    public static float guardianAntiAbuseRadius;
    public static boolean chaosTomeExplosionBlockDamage;
    public static boolean altTelekinesisAlgorithm;
    public static boolean deificAmuletOnlyNegatesDebuffs;
    public static String[] exampleOverrides;

    // Vis Max Charge configs
    public static int apotheosisMaxCharge;
    public static int crimsonSpellMaxCharge;
    public static int darkSunRingMaxCharge;
    public static int deificAmuletMaxCharge;
    public static int devourerMaxCharge;
    public static int discordTomeMaxCharge;
    public static int eldritchSpellMaxCharge;
    public static int lunarFlaresMaxCharge;
    public static int nuclearFuryMaxCharge;
    public static int oblivionAmuletMaxCharge;
    public static int overthrowerMaxCharge;
    public static int soulTomeMaxCharge;
    public static int thunderpealMaxCharge;
    public static int weatherStoneMaxCharge;
    public static int fateTomeMaxCharge;
    public static int voidGrimoireMaxCharge;
    public static int primalChaosMaxCharge;
    public static int predestinyMaxCharge;
    public static int dormantArcanumMaxCharge;
    public static int arcanumMaxCharge;
    public static int dimensionalMirrorMaxCharge;

    // New configurable fields
    public static int paradoxRepairRate;
    public static int paradoxRepairAmount;
    public static int paradoxWarp;
    public static float ancientAegisHealAmount;
    public static int ancientAegisHealInterval;
    public static float ancientAegisKnockbackResistance;
    public static int discordTomeVisCost;
    public static int discordTomeTeleportRange;
    public static int discordTomeDiscordRange;
    public static int discordTomeShiftRange;
    public static int discordTomeCooldown;
    public static int voidGrimoireChannelDuration;
    public static int voidGrimoireSearchRange;
    public static double voidGrimoireLevitateSpeed;
    public static int voidGrimoireLevitateSlowDuration;
    public static int voidGrimoireLevitateSlowLevel;
    public static int overthrowerChannelDuration;
    public static int overthrowerSearchRange;
    public static int overthrowerNetherRange;
    public static int overthrowerLevitateSlowDuration;
    public static int overthrowerLevitateSlowLevel;
    public static int lunarFlaresVisCost;
    public static int lunarFlaresRayTraceRange;
    public static int apotheosisVisCost;
    public static int nuclearFuryVisCostPerSecond;
    public static int nuclearFuryClearRange;
    public static int soulTomeVisCostPerTick;
    public static int soulTomeSearchRange;
    public static int soulTomeKnockbackRange;
    public static float soulTomeMaxDamage;
    public static float soulTomeMinDamage;
    public static int soulTomeWarmupTicks;
    public static int eldritchSpellVisCost;
    public static int eldritchSpellCooldown;
    public static int crimsonSpellVisCost;
    public static float crimsonSpellSearchRange;
    public static int crimsonSpellCooldown;
    public static int thunderpealVisCost;
    public static int thunderpealCooldown;
    public static int telekinesisVisCostPerTick;
    public static int telekinesisLightningVisCost;
    public static int telekinesisThrowVisCost;
    public static double telekinesisReach;
    public static float telekinesisMoveForce;
    public static double telekinesisCloseThreshold;
    public static double telekinesisFarThreshold;
    public static int telekinesisTargetExpireTicks;
    public static int telekinesisLeftClickRange;
    public static int telekinesisThrowCooldown;
    public static int telekinesisLightningCount;
    public static int telekinesisLightningChainTargets;
    public static int telekinesisLightningCooldown;
    public static int chaosTomeHomingChance;
    public static int xpTomeTransferRate;
    public static int weatherStoneVisCost;
    public static int weatherStoneChannelDuration;
    public static int weatherStoneCooldown;
    public static int fateTomeVisCost;
    public static double fateTomeMultiHeldChance;
    public static float fateTomeDamage;
    public static float fateTomeExplosionRadius;
    public static float fateTomeBigExplosionRadius;
    public static double fateTomeBuffChance;
    public static int terrorCrownHavocRange;
    public static float terrorCrownScanRange;
    public static int terrorCrownBlindnessDuration;
    public static int terrorCrownWitherDuration;
    public static int terrorCrownWitherLevel;
    public static int terrorCrownNauseaDuration;
    public static int terrorCrownNauseaLevel;
    public static int terrorCrownSlownessDuration;
    public static int terrorCrownSlownessLevel;
    public static int terrorCrownWeaknessDuration;
    public static int terrorCrownWeaknessLevel;
    public static int terrorCrownManaCost;
    public static int terrorCrownWarp;
    public static double superpositionRingSwapChance;
    public static int superpositionRingCheckInterval;
    public static int shinyStoneStillThreshold2;
    public static int shinyStoneStillThreshold3;
    public static int shinyStoneStillThreshold4;
    public static float shinyStoneStillIncrement;
    public static float shinyStoneHealAmount;
    public static double oblivionAmuletDamageReleaseChance;
    public static float oblivionAmuletDamageCap;
    public static double oblivionAmuletHighDamageReductionChance;
    public static double oblivionAmuletPotionChance;
    public static int oblivionAmuletPotionDurationMin;
    public static int oblivionAmuletPotionDurationMax;
    public static int oblivionAmuletPotionLevelMin;
    public static int oblivionAmuletPotionLevelMax;
    public static int oblivionAmuletWarp;
    public static int devourerRange;
    public static int devourerMinRiftSize;
    public static int devourerDrainRate;
    public static int deificAmuletFireDuration;
    public static int deificAmuletFireVisCost;
    public static int deificAmuletInvincibilityExtension;
    public static int deificAmuletInvincibilityCooldown;
    public static int chaosCoreWarp;
    public static double chaosCorePotionChance;
    public static int chaosCorePotionDurationMin;
    public static int chaosCorePotionDurationMax;
    public static int chaosCorePotionLevelMin;
    public static int chaosCorePotionLevelMax;
    public static double arcanumTeleportChance;
    public static int arcanumTeleportRange;
    public static double arcanumDormantTransformChance;
    public static int arcanumDormantLifeMin;
    public static int arcanumDormantLifeMax;
    public static float arcanumVisDiscount;
    public static int dimensionalMirrorChannelDuration;
    public static int dormantArcanumVisCostPerTick;
    public static boolean primalDiscordComboEnabled;

    public static void configDisposition(FMLPreInitializationEvent event) {
        String overridesDesc = "Allows you to override item triggers for any research classified as forgotten knowledge." + System.lineSeparator() + System.lineSeparator() + "Your overrides should be formatted like this: ResearchKey[modid:itemname:meta, modid:itemname:meta, ..., modid:itemname:meta]" + System.lineSeparator() + System.lineSeparator() + "Notice: there are no optional parameters, even metadata should be always specified - even though it makes no difference" + System.lineSeparator() + "for 'damageable' items, like Diamond Sword or Iron Helmet. The Thaumcraft alone decides what to do with one or another" + System.lineSeparator() + "type of item, but items should always be passed like ItemStack with defined metadata - so here it is." + System.lineSeparator() + "If you leave research key with no items specified (for instance: 'AncientAegis[]'), it would simply be unlocked without" + System.lineSeparator() + "scanning any items, but that will still take some time and you will receive respective notification upon unlocking." + System.lineSeparator() + System.lineSeparator() + "Comlete list of forgotten knowledge research keys is always printed into log file upon post-initialization." + System.lineSeparator() + "Needless to say, setting triggers for researches that are not classified as forgotten knowledge won't have any effect." + System.lineSeparator() + System.lineSeparator() + "If you are using MineTweaker, you may prefer using integration features that were designed for it." + System.lineSeparator() + "You can read about them here: https://github.com/Extegral/Forgotten-Relics/wiki/MineTweaker-Integration" + System.lineSeparator() + System.lineSeparator() + "If you have no idea what the Justice Handler is, read this article: " + System.lineSeparator() + "https://github.com/Extegral/Forgotten-Relics/wiki/Research-Trigger-System";
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        Property theOverrides = config.get("Justice Handler Overrides", "justiceHandlerOverrides", exampleOverrides);
        theOverrides.setComment("Here is some working expamles. You may want to replace them with your own ones if you enabled overriding.");
        forgottenKnowledgeOverrides = theOverrides.getStringList();
        forgottenKnowledgeOverridingEnabled = config.getBoolean("justiceOverridingEnabled", "Justice Handler Overrides", false, "Whether or not overriding triggers by config should be enabled. If it's disabled, no overrides specified here would take effect.");
        config.addCustomCategoryComment("Justice Handler Overrides", overridesDesc);
        deificAmuletOnlyNegatesDebuffs = config.getBoolean("deificAmuletOnlyNegatesDebuffs", "Generic Config", false, "Allows you to prevent Deific Amulet from dispelling positive potion effects, so that it would only cure debuffs.");
        altTelekinesisAlgorithm = config.getBoolean("altTelekinesisAlgorithm", "Generic Config", false, "Whether or not Tome of Predestiny should be handled in alternative way, that uses vanilla .onUsingTick(). Basically, it just adds charging animation and slows player down while casting.");
        guardianAntiAbuseRadius = config.getFloat("guardianAntiAbuseRadius", "Generic Config", 16.0f, 0.0f, 1024.0f, "Defines the radius in which anti-abuse system of Guardian of Gaia checks for liquids. Set to 0 to disable it... and proclaim yourself as wuss.");
        guardianNotificationRadius = config.getFloat("guardianNotificationRadius", "Generic Config", 64.0f, -32768.0f, 32768.0f, "Defines the radius in which players receive chat notification upon force despawn of Guardian of Gaia (when it's anti-abuse system triggers). Set to 0 to disable notification. Set to any negative value for message to be sent to ALL players that are present in the world at the moment.");
        oblivionStoneHardCap = config.getInt("oblivionStoneHardCap", "Generic Config", 64, 0, 2048, "How much items you can add into list of single Keystone of The Oblivion before you would be unable add nothing more. This limit exists to prevent players from occasional or intentional abusing, since multiple keystones with huge lists (like tens of thousands of items) may cause significant performance impact.");
        oblivionStoneSoftCap = config.getInt("oblivionStoneSoftCap", "Generic Config", 28, 0, 2048, "Controls the amount of items that can be added into list of Keystone of The Oblivion, before displayble list in Ctrl tooltip stops expanding and becomes unreadable. You may want to increase or decrease it, depending on your screen resolution.");
        memesEnabled = config.getBoolean("memesEnabled", "Generic Config", false, "Enables super secret memes. You are not prepared!");
        updateNotificationsEnabled = config.getBoolean("updateNotificationsEnabled", "Generic Config", true, "Whether or not update notifications should be enabled. I have no idea why someone may not want to behold greatness of new versions, but alright, it's all up to you.");
        voidGrimoireEnabled = config.getBoolean("voidGrimoireEnabled", "Generic Config", true, "Whethere or not Grimoire of The Abyss should be enabled. Note that it will only remove respective research, so it would be impossible to create this relic legally - it won't remove existing copies from world or prevent it's spawning from Creative Mode.");
        outerLandsCheckrate = config.getInt("outerLandsCheckrate", "Generic Config", 20, 1, 1024000, "Checkrate for Outer Lands anti-abuse system, if it's enabled. Measured in ticks. Setting this value to 20 means that it would check each player once in 20 ticks, or once per second.");
        outerLandsAntiAbuseEnabled = config.getBoolean("outerLandsAntiAbuseEnabled", "Generic Config", true, "Whether or not anti-abuse system for Outer Lands should be enabled. Disable if you like cheating or don't want it for some other reason.");
        revelationModifier = config.getFloat("revelationModifier", "Generic Config", 1.0f, 0.001f, 32.0f, "Multiplier for probability of revealing forgotten knowledge. This multiplies both inspection frequency and individual chance for each check, so increasing it more than few times over is highly unrecommended.");
        telekinesisOnPlayers = config.getBoolean("telekinesisOnPlayers", "Generic Config", true, "In a perfect world, this option would disable Tome of Predestiny's ability to affect players... BUT IT'S A WRONG WORLD BRO, AHAHAHAHAHAHAHAHAAHAHAHAHAHAH");
        fateTomeCooldownMIN = config.getInt("fateTomeCooldownMIN", "Generic Config", 30, 0, 32768, "Minimal possible cooldown (in seconds) for triggering Tome of Broken Fates' death prevention effect.");
        fateTomeCooldownMAX = config.getInt("fateTomeCooldownMAX", "Generic Config", 90, 0, 32768, "Maximal possible cooldown (in seconds) for triggering Tome of Broken Fates' death prevention effect. Setting this to 0 will disable cooldown entirely.");
        notificationDelay = config.getInt("notificationDelay", "Thaumcraft Overrides", 2000, 0, 32768, "Determines how fast notifications scroll downwards. Overrides respective option in default Thaumcraft config.");
        runicRechargeSpeed = config.getInt("runicRechargeSpeed", "Thaumcraft Overrides", 750, 0, 32768, "How many milliseconds pass between Runic Shield recharge ticks. Setting this value lower than 50 is not recommended. Overrides respective option in default Thaumcraft config.");
        runicRechargeDelay = config.getInt("runicRechargeDelay", "Thaumcraft Overrides", 40, 0, 32768, "How many game ticks pass after Runic Shield has been reduced to zero before it can start recharging again. Overrides respective option in default Thaumcraft config.");
        runicCost = config.getInt("runicCost", "Thaumcraft Overrides", 10, 0, 32768, "How much Aer and Terra centi-vis (0.01 vis) it costs to reacharge a single unit of Runic Shield. Overrides respective option in default Thaumcraft config.");
        advancedMiningCharmReach = config.getFloat("advancedMiningCharmReach", "Generic Config", 4.0f, 0.0f, 32.0f, "Block reach increase for Ethereal Mining Charm.");
        miningCharmReach = config.getFloat("miningCharmReach", "Generic Config", 2.0f, 0.0f, 32.0f, "Block reach increase for Mining Charm.");
        advancedMiningCharmBoost = config.getFloat("advancedMiningCharmBoost", "Generic Config", 3.0f, 0.0f, 32000.0f, "Mining speed boost for Ethereal Mining Charm. 3.0 means that it is boosted by 300%.");
        miningCharmBoost = config.getFloat("miningCharmBoost", "Generic Config", 1.0f, 0.0f, 32000.0f, "Mining speed boost for Mining Charm. 1.0 means that it is boosted by 100%.");
        nebulousCoreDodgeChance = config.getFloat("nebulousCoreDodgeChance", "Generic Config", 0.4f, 0.0f, 1.0f, "Chance to dodge attack by teleporting player from it for Nebulous Core. 1.0 equals 100% chance, 0.0 - 0%.");
        ancientAegisDamageReduction = config.getFloat("ancientAegisDamageReduction", "Generic Config", 0.25f, 0.0f, 1.0f, "Damage Reduction for Ancient Aegis. 1.0 equals 100% reduction, 0.0 - 0%.");
        deificAmuletEffectImmunity = config.getBoolean("deificAmuletEffectImmunity", "Generic Config", true, "Whether or not Deific Amulet should provide immunity to status effects. Note, that it includes buffs as well as debuffs.");
        deificAmuletInvincibility = config.getBoolean("deificAmuletInvincibility", "Generic Config", true, "Whether or not Deific Amulet should provide prolonged invincibility frames.");
        darkSunRingDeflectChance = config.getFloat("darkSunRingDeflectChance", "Generic Config", 0.2f, 0.0f, 1.0f, "Chance to deflect enemy's attack back to it, while wearing Ring of The Seven Suns. 1.0 equals 100% chance, 0.0 - 0%.");
        darkSunRingDamageCap = config.getFloat("darkSunRingDamageCap", "Generic Config", 100.0f, 0.0f, 32768.0f, "Damage cap for Ring of The Seven Suns. Any attacks that exceed this amount of damage will be completely negated while wearing it.");
        darkSunRingHealLimit = config.getBoolean("darkSunRingHealLimit", "Generic Config", false, "Enables the cooldown on Ring of The Seven Sun's healing effect, so standing in fire or lava wouldn't replenish your health insanely fast. WARNING: This config option is experimental, only touch it if you really want this.");
        interdimensionalMirror = config.getBoolean("interdimensionalMirror", "Generic Config", true, "Whether or not Dimensional Mirror should be capable of teleporting player across dimensions. If this is set to false, player must reside in the dimension of saved location in order to teleport to it.");
        shinyStoneCheckrate = config.getInt("shinyStoneCheckrate", "Generic Config", 4, 1, 2048, "Checkrate for Shiny Stone effects. The greater it is, the less frequently health regen would happen, and the more time acceleration would take. WARNING: This config option is experimental, only touch it if you really want this.");
        obeliskDrainerVisMult = config.getFloat("obeliskDrainerVisGen", "Generic Config", 1.0f, 0.0f, 32000.0f, "Vis production multiplier for Devourer of The Void.");
        arcanumGenRate = config.getFloat("arcanumGenRate", "Generic Config", 1.0f, 0.0f, 32000.0f, "Multiplier for Vis generation rate of Nebulous Core.");
        soulTomeDivisor = config.getFloat("soulTomeDivisor", "Generic Config", 10.0f, 0.0f, Float.POSITIVE_INFINITY, "Divisor, used during damage calculations by Edict of a Thousand Damned Souls. Setting this value to 10 basically means that most of the time it will drain 1/10 of entity's max health per attack.");
        falseJusticeEnabled = config.getBoolean("falseJusticeEnabled", "Generic Config", true, "Whether or not False Justice should be enabled. Note that it will only remove respective research, so it would be impossible to create this relic legally - it won't remove existing copies from world or prevent it's spawning from Creative Mode.");
        damageThunderpealDirect = config.getFloat("damageThunderpealDirect", "Damage Values", 24.0f, 0.0f, 32000.0f, "How much damage inflicts direct hit of Thunderpeal's electrical orbs.");
        damageThunderpealBolt = config.getFloat("damageThunderpealBolt", "Damage Values", 16.0f, 0.0f, 32000.0f, "How much damage deal lightning bolts of Thunderpeal's electrical orbs.");
        damageApotheosisDirect = config.getFloat("damageApotheosisDirect", "Damage Values", 100.0f, 0.0f, 32000.0f, "How much damage inflicts direct hit of Babylon Weapons, summoned by Apotheosis.");
        damageApotheosisImpact = config.getFloat("damageApotheosisImpact", "Damage Values", 75.0f, 0.0f, 32000.0f, "How much damage receive entities within impact zone of Babylon Weapons, summoned by Apotheosis.");
        damageLunarFlareDirect = config.getFloat("damageLunarFlareDirect", "Damage Values", 72.0f, 0.0f, 32000.0f, "How much damage inflicts direct hit of Lunar Flare.");
        damageLunarFlareImpact = config.getFloat("damageLunarFlareImpact", "Damage Values", 40.0f, 0.0f, 32000.0f, "How much damage receive entities within impact zone of Lunar Flare.");
        paradoxDamageCap = config.getFloat("paradoxDamageCap", "Damage Values", 200.0f, 0.0f, 32000.0f, "Maximum damage The Paradox can deal. Damage dealt varies between 1 and this value for each hit.");
        telekinesisTomeDamageMIN = config.getFloat("telekinesisTomeDamageMIN", "Damage Values", 16.0f, 0.0f, 32000.0f, "Minimal damage that can be dealt by lightning attack of Tome of Predestiny.");
        telekinesisTomeDamageMAX = config.getFloat("telekinesisTomeDamageMAX", "Damage Values", 40.0f, 0.0f, 32000.0f, "Maximal damage that can be dealt by lightning attack of Tome of Predestiny.");
        nuclearFuryDamageMIN = config.getFloat("nuclearFuryDamageMIN", "Damage Values", 24.0f, 0.0f, 32000.0f, "Minimal damage that can be dealt by charges of Nuclear Fury.");
        nuclearFuryDamageMAX = config.getFloat("nuclearFuryDamageMAX", "Damage Values", 32.0f, 0.0f, 32000.0f, "Maximal damage that can be dealt by charges of Nuclear Fury.");
        crimsonSpellDamageMIN = config.getFloat("crimsonSpellDamageMIN", "Damage Values", 42.0f, 0.0f, 32000.0f, "Minimal damage that can be dealt by projectiles of Crimson Spell.");
        crimsonSpellDamageMAX = config.getFloat("crimsonSpellDamageMAX", "Damage Values", 100.0f, 0.0f, 32000.0f, "Maximal damage that can be dealt by projectiles of Crimson Spell.");
        chaosTomeDamageCap = config.getFloat("chaosTomeDamageCap", "Damage Values", 100.0f, 0.0f, 32000.0f, "Maximum damage that projectile of Tome of Primal Chaos can deal. Damage dealt varies between 1 and this value for each hit.");
        chaosTomeExplosionBlockDamage = config.getBoolean("chaosTomeExplosionBlockDamage", "Generic Config", false, "Whether or not explosions from Tome of Primal Chaos projectiles should destroy blocks.");
        eldritchSpellDamage = config.getFloat("eldritchSpellDamage", "Damage Values", 32.5f, 0.0f, 32000.0f, "Default damage dealt by projectiles of Eldritch Spell.");
        eldritchSpellDamageEx = config.getFloat("eldritchSpellDamageEx", "Damage Values", 100.0f, 0.0f, 32000.0f, "Damage dealt by projectiles of Eldritch Spell, while it is used in Outer Lands.");
        apotheosisVisMult = config.getFloat("apotheosisVisMult", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Apotheosis.");
        chaosTomeVisMult = config.getFloat("chaosTomeVisMult", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Tome of Primal Chaos.");
        crimsonSpellVisMult = config.getFloat("crimsonSpellVisMult", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Crimson Spell.");
        deificAmuletVisMult = config.getFloat("deificAmuletVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Deific Amulet.");
        discordTomeVisMult = config.getFloat("discordTomeVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Tome of Discord.");
        dormantArcanumVisMult = config.getFloat("dormantArcanumVisMult", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Dormant Nebulous Core (applies in the moment of transormation; final amount of Vis required for re-awakening depends on this.)");
        eldritchSpellVisMult = config.getFloat("eldritchSpellVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Eldritch Spell.");
        fateTomeVisMult = config.getFloat("fateTomeVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Tome of Broken Fates.");
        lunarFlaresVisMult = config.getFloat("lunarFlaresVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Tome of Lunar Flares.");
        nuclearFuryVisMult = config.getFloat("nuclearFuryVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Nuclear Fury.");
        oblivionAmuletVisMult = config.getFloat("oblivionAmuletVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Amulet of The Oblivion.");
        soulTomeVisMult = config.getFloat("soulTomeVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Edict of a Thousand Damned Souls.");
        telekinesisTomeVisMult = config.getFloat("telekinesisTomeVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Tome of Predestiny.");
        weatherStoneVisMult = config.getFloat("weatherStoneVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Runic Stone.");
        thunderpealVisMult = config.getFloat("thunderpealVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Thunderpeal.");
        overthrowerVisMult = config.getFloat("overthrowerVisCost", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Edict of Eternal Banishment.");
        overthrowerAffectsPlayers = config.getBoolean("overthrowerAffectsPlayers", "Generic Config", false, "Whether or not Edict of Eternal Banishment can banish players to the Nether. Default is false.");
        voidGrimoireVisMult = config.getFloat("voidGrimoireVisMult", "Vis Costs", 1.0f, 0.0f, 1024.0f, "Vis cost multiplier for Grimoire of The Abyss.");

        // Paradox
        paradoxRepairRate = config.getInt("paradoxRepairRate", "Paradox", 20, 1, 32768, "How many ticks between each durability repair for The Paradox.");
        paradoxRepairAmount = config.getInt("paradoxRepairAmount", "Paradox", 1, 0, 32768, "How much durability is repaired each interval for The Paradox.");
        paradoxWarp = config.getInt("paradoxWarp", "Paradox", 8, 0, 100, "Warp value for The Paradox.");

        // Ancient Aegis
        ancientAegisHealAmount = config.getFloat("ancientAegisHealAmount", "Ancient Aegis", 1.0f, 0.0f, 1024.0f, "Amount of health regenerated per interval by Ancient Aegis.");
        ancientAegisHealInterval = config.getInt("ancientAegisHealInterval", "Ancient Aegis", 20, 1, 32768, "How many ticks between each health regeneration by Ancient Aegis.");
        ancientAegisKnockbackResistance = config.getFloat("ancientAegisKnockbackResistance", "Ancient Aegis", 1.0f, 0.0f, 1.0f, "Knockback resistance provided by Ancient Aegis. 1.0 = full immunity.");

        // Tome of Discord
        discordTomeVisCost = config.getInt("discordTomeVisCost", "Tome of Discord", 6, 0, 32768, "Base Vis cost per cast for Tome of Discord.");
        discordTomeTeleportRange = config.getInt("discordTomeTeleportRange", "Tome of Discord", 128, 1, 32768, "Teleport range for Tome of Discord.");
        discordTomeDiscordRange = config.getInt("discordTomeDiscordRange", "Tome of Discord", 1024, 1, 32768, "Teleport range for Tome of Discord when Ring of Discord is active.");
        discordTomeShiftRange = config.getInt("discordTomeShiftRange", "Tome of Discord", 16, 1, 32768, "Shift-teleport range for Tome of Discord.");
        discordTomeCooldown = config.getInt("discordTomeCooldown", "Tome of Discord", 20, 0, 32768, "Cooldown in ticks after using Tome of Discord.");

        // Void Grimoire
        voidGrimoireChannelDuration = config.getInt("voidGrimoireChannelDuration", "Void Grimoire", 100, 1, 32768, "Channel duration in ticks for Grimoire of The Abyss.");
        voidGrimoireSearchRange = config.getInt("voidGrimoireSearchRange", "Void Grimoire", 64, 1, 32768, "Target search range for Grimoire of The Abyss.");
        voidGrimoireLevitateSpeed = config.getFloat("voidGrimoireLevitateSpeed", "Void Grimoire", 0.03f, 0.0f, 1.0f, "Levitation speed (blocks per tick) for Grimoire of The Abyss target.");
        voidGrimoireLevitateSlowDuration = config.getInt("voidGrimoireLevitateSlowDuration", "Void Grimoire", 30, 0, 32768, "Duration of slowness effect on target of Grimoire of The Abyss.");
        voidGrimoireLevitateSlowLevel = config.getInt("voidGrimoireLevitateSlowLevel", "Void Grimoire", 100, 0, 100, "Level of slowness effect on target of Grimoire of The Abyss.");

        // Edict of Banishment
        overthrowerChannelDuration = config.getInt("overthrowerChannelDuration", "Edict of Banishment", 150, 1, 32768, "Channel duration in ticks for Edict of Eternal Banishment.");
        overthrowerSearchRange = config.getInt("overthrowerSearchRange", "Edict of Banishment", 64, 1, 32768, "Target search range for Edict of Eternal Banishment.");
        overthrowerNetherRange = config.getInt("overthrowerNetherRange", "Edict of Banishment", 1000, 1, 32768, "Random teleport range in the Nether for Edict of Eternal Banishment.");
        overthrowerLevitateSlowDuration = config.getInt("overthrowerLevitateSlowDuration", "Edict of Banishment", 30, 0, 32768, "Duration of slowness effect on target of Edict of Eternal Banishment.");
        overthrowerLevitateSlowLevel = config.getInt("overthrowerLevitateSlowLevel", "Edict of Banishment", 2, 0, 100, "Level of slowness effect on target of Edict of Eternal Banishment.");

        // Lunar Flares
        lunarFlaresVisCost = config.getInt("lunarFlaresVisCost", "Tome of Lunar Flares", 20, 0, 32768, "Base Vis cost per cast for Tome of Lunar Flares.");
        lunarFlaresRayTraceRange = config.getInt("lunarFlaresRayTraceRange", "Tome of Lunar Flares", 128, 1, 32768, "Ray trace range for Tome of Lunar Flares.");

        // Apotheosis
        apotheosisVisCost = config.getInt("apotheosisVisCost", "Apotheosis", 7, 0, 32768, "Vis cost per weapon for Apotheosis.");

        // Nuclear Fury
        nuclearFuryVisCostPerSecond = config.getInt("nuclearFuryVisCostPerSecond", "Nuclear Fury", 5, 0, 32768, "Vis cost per second for Nuclear Fury.");
        nuclearFuryClearRange = config.getInt("nuclearFuryClearRange", "Nuclear Fury", 32, 0, 32768, "Range at which Nuclear Fury missiles are cleared.");

        // Soul Tome
        soulTomeVisCostPerTick = config.getInt("soulTomeVisCostPerTick", "Soul Tome", 1, 0, 32768, "Vis cost per tick for Edict of a Thousand Damned Souls.");
        soulTomeSearchRange = config.getInt("soulTomeSearchRange", "Soul Tome", 20, 1, 32768, "Search range for Edict of a Thousand Damned Souls.");
        soulTomeKnockbackRange = config.getInt("soulTomeKnockbackRange", "Soul Tome", 4, 0, 32768, "Knockback range for Edict of a Thousand Damned Souls.");
        soulTomeMaxDamage = config.getFloat("soulTomeMaxDamage", "Soul Tome", 20.0f, 0.0f, 32000.0f, "Maximum soul damage for Edict of a Thousand Damned Souls.");
        soulTomeMinDamage = config.getFloat("soulTomeMinDamage", "Soul Tome", 1.0f, 0.0f, 32000.0f, "Minimum soul damage for Edict of a Thousand Damned Souls.");
        soulTomeWarmupTicks = config.getInt("soulTomeWarmupTicks", "Soul Tome", 20, 0, 32768, "Warmup ticks before Edict of a Thousand Damned Souls starts dealing damage.");

        // Eldritch Spell
        eldritchSpellVisCost = config.getInt("eldritchSpellVisCost", "Eldritch Spell", 4, 0, 32768, "Base Vis cost per cast for Eldritch Spell.");
        eldritchSpellCooldown = config.getInt("eldritchSpellCooldown", "Eldritch Spell", 20, 0, 32768, "Cooldown in ticks after using Eldritch Spell.");

        // Crimson Spell
        crimsonSpellVisCost = config.getInt("crimsonSpellVisCost", "Crimson Spell", 8, 0, 32768, "Base Vis cost per cast for Crimson Spell.");
        crimsonSpellSearchRange = config.getFloat("crimsonSpellSearchRange", "Crimson Spell", 3.0f, 0.0f, 32768.0f, "Target search range for Crimson Spell.");
        crimsonSpellCooldown = config.getInt("crimsonSpellCooldown", "Crimson Spell", 30, 0, 32768, "Cooldown in ticks after using Crimson Spell.");

        // Thunderpeal
        thunderpealVisCost = config.getInt("thunderpealVisCost", "Thunderpeal", 2, 0, 32768, "Base Vis cost per cast for Thunderpeal.");
        thunderpealCooldown = config.getInt("thunderpealCooldown", "Thunderpeal", 30, 0, 32768, "Cooldown in ticks after using Thunderpeal.");

        // Telekinesis Tome
        telekinesisVisCostPerTick = config.getInt("telekinesisVisCostPerTick", "Tome of Predestiny", 1, 0, 32768, "Vis cost per tick while using Tome of Predestiny.");
        telekinesisLightningVisCost = config.getInt("telekinesisLightningVisCost", "Tome of Predestiny", 3, 0, 32768, "Vis cost for lightning attack of Tome of Predestiny.");
        telekinesisThrowVisCost = config.getInt("telekinesisThrowVisCost", "Tome of Predestiny", 3, 0, 32768, "Vis cost for throw attack of Tome of Predestiny.");
        telekinesisReach = config.getFloat("telekinesisReach", "Tome of Predestiny", 7.5f, 0.0f, 32768.0f, "Default reach distance for Tome of Predestiny.");
        telekinesisMoveForce = config.getFloat("telekinesisMoveForce", "Tome of Predestiny", 0.66666f, 0.0f, 10.0f, "Movement force for telekinetically controlled entities.");
        telekinesisCloseThreshold = config.getFloat("telekinesisCloseThreshold", "Tome of Predestiny", 1.5f, 0.0f, 32768.0f, "Close distance threshold for Tome of Predestiny.");
        telekinesisFarThreshold = config.getFloat("telekinesisFarThreshold", "Tome of Predestiny", 8.0f, 0.0f, 32768.0f, "Far distance threshold for Tome of Predestiny.");
        telekinesisTargetExpireTicks = config.getInt("telekinesisTargetExpireTicks", "Tome of Predestiny", 5, 1, 32768, "Ticks before telekinesis target expires.");
        telekinesisLeftClickRange = config.getInt("telekinesisLeftClickRange", "Tome of Predestiny", 16, 1, 32768, "Maximum range for left-click attack of Tome of Predestiny.");
        telekinesisThrowCooldown = config.getInt("telekinesisThrowCooldown", "Tome of Predestiny", 40, 0, 32768, "Cooldown in ticks after throw attack of Tome of Predestiny.");
        telekinesisLightningCount = config.getInt("telekinesisLightningCount", "Tome of Predestiny", 4, 0, 100, "Number of lightning bolts spawned by Tome of Predestiny.");
        telekinesisLightningChainTargets = config.getInt("telekinesisLightningChainTargets", "Tome of Predestiny", 3, 0, 100, "Maximum chain targets for lightning of Tome of Predestiny.");
        telekinesisLightningCooldown = config.getInt("telekinesisLightningCooldown", "Tome of Predestiny", 10, 0, 32768, "Cooldown in ticks after lightning attack of Tome of Predestiny.");

        // Chaos Tome
        chaosTomeHomingChance = config.getInt("chaosTomeHomingChance", "Tome of Primal Chaos", 35, 0, 100, "Chance (in percent) for Tome of Primal Chaos projectiles to become homing.");

        // XP Tome
        xpTomeTransferRate = config.getInt("xpTomeTransferRate", "XP Tome", 5, 1, 32768, "Experience points transferred per tick by Tome of Experience.");

        // Weather Stone
        weatherStoneVisCost = config.getInt("weatherStoneVisCost", "Weather Stone", 25, 0, 32768, "Base Vis cost per cast for Runic Stone.");
        weatherStoneChannelDuration = config.getInt("weatherStoneChannelDuration", "Weather Stone", 60, 1, 32768, "Channel duration in ticks for Runic Stone.");
        weatherStoneCooldown = config.getInt("weatherStoneCooldown", "Weather Stone", 100, 0, 32768, "Cooldown in ticks after using Runic Stone.");

        // Fate Tome
        fateTomeVisCost = config.getInt("fateTomeVisCost", "Tome of Broken Fates", 100, 0, 32768, "Vis cost per trigger for Tome of Broken Fates.");
        fateTomeMultiHeldChance = config.getFloat("fateTomeMultiHeldChance", "Tome of Broken Fates", 0.000016f, 0.0f, 1.0f, "Chance per tick for multi-held disaster trigger of Tome of Broken Fates.");
        fateTomeDamage = config.getFloat("fateTomeDamage", "Tome of Broken Fates", 40000.0f, 0.0f, 32000.0f, "Damage value for Tome of Broken Fates death prevention.");
        fateTomeExplosionRadius = config.getFloat("fateTomeExplosionRadius", "Tome of Broken Fates", 16.0f, 0.0f, 32000.0f, "Explosion radius for Tome of Broken Fates.");
        fateTomeBigExplosionRadius = config.getFloat("fateTomeBigExplosionRadius", "Tome of Broken Fates", 100.0f, 0.0f, 32000.0f, "Big explosion radius for Tome of Broken Fates.");
        fateTomeBuffChance = config.getFloat("fateTomeBuffChance", "Tome of Broken Fates", 0.75f, 0.0f, 1.0f, "Chance for positive effect instead of negative for Tome of Broken Fates.");

        // Terror Crown
        terrorCrownHavocRange = config.getInt("terrorCrownHavocRange", "Terror Crown", 24, 0, 32768, "Havoc range for Crown of Terror.");
        terrorCrownScanRange = config.getFloat("terrorCrownScanRange", "Terror Crown", 32.0f, 0.0f, 32768.0f, "Entity scan range for Crown of Terror.");
        terrorCrownBlindnessDuration = config.getInt("terrorCrownBlindnessDuration", "Terror Crown", 100, 0, 32768, "Blindness duration in ticks for Crown of Terror.");
        terrorCrownWitherDuration = config.getInt("terrorCrownWitherDuration", "Terror Crown", 40, 0, 32768, "Wither duration in ticks for Crown of Terror.");
        terrorCrownWitherLevel = config.getInt("terrorCrownWitherLevel", "Terror Crown", 0, 0, 100, "Wither effect level for Crown of Terror.");
        terrorCrownNauseaDuration = config.getInt("terrorCrownNauseaDuration", "Terror Crown", 100, 0, 32768, "Nausea duration in ticks for Crown of Terror.");
        terrorCrownNauseaLevel = config.getInt("terrorCrownNauseaLevel", "Terror Crown", 1, 0, 100, "Nausea effect level for Crown of Terror.");
        terrorCrownSlownessDuration = config.getInt("terrorCrownSlownessDuration", "Terror Crown", 30, 0, 32768, "Slowness duration in ticks for Crown of Terror.");
        terrorCrownSlownessLevel = config.getInt("terrorCrownSlownessLevel", "Terror Crown", 1, 0, 100, "Slowness effect level for Crown of Terror.");
        terrorCrownWeaknessDuration = config.getInt("terrorCrownWeaknessDuration", "Terror Crown", 80, 0, 32768, "Weakness duration in ticks for Crown of Terror.");
        terrorCrownWeaknessLevel = config.getInt("terrorCrownWeaknessLevel", "Terror Crown", 2, 0, 100, "Weakness effect level for Crown of Terror.");
        terrorCrownManaCost = config.getInt("terrorCrownManaCost", "Terror Crown", 200, 0, 32768, "Mana cost per repair for Crown of Terror.");
        terrorCrownWarp = config.getInt("terrorCrownWarp", "Terror Crown", 3, 0, 100, "Warp value for Crown of Terror.");

        // Superposition Ring
        superpositionRingSwapChance = config.getFloat("superpositionRingSwapChance", "Superposition Ring", 0.025f, 0.0f, 1.0f, "Chance per check for Ring of Superposition to swap positions.");
        superpositionRingCheckInterval = config.getInt("superpositionRingCheckInterval", "Superposition Ring", 600, 1, 32768, "Check interval in ticks for Ring of Superposition.");

        // Shiny Stone
        shinyStoneStillThreshold2 = config.getInt("shinyStoneStillThreshold2", "Shiny Stone", 40, 1, 32768, "Stillness threshold for Shiny Stone heal level 2.");
        shinyStoneStillThreshold3 = config.getInt("shinyStoneStillThreshold3", "Shiny Stone", 80, 1, 32768, "Stillness threshold for Shiny Stone heal level 3.");
        shinyStoneStillThreshold4 = config.getInt("shinyStoneStillThreshold4", "Shiny Stone", 200, 1, 32768, "Stillness threshold for Shiny Stone heal level 4.");
        shinyStoneStillIncrement = config.getFloat("shinyStoneStillIncrement", "Shiny Stone", 4.0f, 0.0f, 32768.0f, "Stillness value increment per check for Shiny Stone.");
        shinyStoneHealAmount = config.getFloat("shinyStoneHealAmount", "Shiny Stone", 1.0f, 0.0f, 1024.0f, "Heal amount per regeneration for Shiny Stone.");

        // Oblivion Amulet
        oblivionAmuletDamageReleaseChance = config.getFloat("oblivionAmuletDamageReleaseChance", "Oblivion Amulet", 0.0008f, 0.0f, 1.0f, "Chance per tick for Amulet of The Oblivion to release stored damage.");
        oblivionAmuletDamageCap = config.getFloat("oblivionAmuletDamageCap", "Oblivion Amulet", 100.0f, 0.0f, 32000.0f, "Damage cap for Amulet of The Oblivion random reduction.");
        oblivionAmuletHighDamageReductionChance = config.getFloat("oblivionAmuletHighDamageReductionChance", "Oblivion Amulet", 0.9f, 0.0f, 1.0f, "Chance to reduce damage when above cap for Amulet of The Oblivion.");
        oblivionAmuletPotionChance = config.getFloat("oblivionAmuletPotionChance", "Oblivion Amulet", 0.0004f, 0.0f, 1.0f, "Chance per tick for Amulet of The Oblivion to apply random potion effect.");
        oblivionAmuletPotionDurationMin = config.getInt("oblivionAmuletPotionDurationMin", "Oblivion Amulet", 100, 0, 32768, "Minimum potion effect duration for Amulet of The Oblivion.");
        oblivionAmuletPotionDurationMax = config.getInt("oblivionAmuletPotionDurationMax", "Oblivion Amulet", 2100, 0, 32768, "Maximum potion effect duration for Amulet of The Oblivion.");
        oblivionAmuletPotionLevelMin = config.getInt("oblivionAmuletPotionLevelMin", "Oblivion Amulet", 0, 0, 100, "Minimum potion effect level for Amulet of The Oblivion.");
        oblivionAmuletPotionLevelMax = config.getInt("oblivionAmuletPotionLevelMax", "Oblivion Amulet", 3, 0, 100, "Maximum potion effect level for Amulet of The Oblivion.");
        oblivionAmuletWarp = config.getInt("oblivionAmuletWarp", "Oblivion Amulet", 4, 0, 100, "Warp value for Amulet of The Oblivion.");

        // Devourer of the Void
        devourerRange = config.getInt("devourerRange", "Devourer of The Void", 16, 1, 32768, "Search range for Flux Rifts for Devourer of The Void.");
        devourerMinRiftSize = config.getInt("devourerMinRiftSize", "Devourer of The Void", 50, 1, 32768, "Minimum Flux Rift size for Devourer of The Void to drain.");
        devourerDrainRate = config.getInt("devourerDrainRate", "Devourer of The Void", 1, 1, 32768, "Energy drained per tick from Flux Rifts by Devourer of The Void.");

        // Deific Amulet
        deificAmuletFireDuration = config.getInt("deificAmuletFireDuration", "Deific Amulet", 300, 0, 32768, "Fire duration in ticks when Deific Amulet prevents suffocation.");
        deificAmuletFireVisCost = config.getInt("deificAmuletFireVisCost", "Deific Amulet", 10, 0, 32768, "Vis cost for Deific Amulet suffocation prevention.");
        deificAmuletInvincibilityExtension = config.getInt("deificAmuletInvincibilityExtension", "Deific Amulet", 40, 0, 32768, "Invincibility frame extension in ticks for Deific Amulet.");
        deificAmuletInvincibilityCooldown = config.getInt("deificAmuletInvincibilityCooldown", "Deific Amulet", 32, 0, 32768, "Invincibility frame cooldown in ticks for Deific Amulet.");

        // Chaos Core
        chaosCoreWarp = config.getInt("chaosCoreWarp", "Chaos Core", 2, 0, 100, "Warp value for Nebulous Chaos Core.");
        chaosCorePotionChance = config.getFloat("chaosCorePotionChance", "Chaos Core", 0.000208f, 0.0f, 1.0f, "Chance per tick for Nebulous Chaos Core to apply random potion effect.");
        chaosCorePotionDurationMin = config.getInt("chaosCorePotionDurationMin", "Chaos Core", 100, 0, 32768, "Minimum potion effect duration for Nebulous Chaos Core.");
        chaosCorePotionDurationMax = config.getInt("chaosCorePotionDurationMax", "Chaos Core", 2500, 0, 32768, "Maximum potion effect duration for Nebulous Chaos Core.");
        chaosCorePotionLevelMin = config.getInt("chaosCorePotionLevelMin", "Chaos Core", 0, 0, 100, "Minimum potion effect level for Nebulous Chaos Core.");
        chaosCorePotionLevelMax = config.getInt("chaosCorePotionLevelMax", "Chaos Core", 3, 0, 100, "Maximum potion effect level for Nebulous Chaos Core.");

        // Arcanum
        arcanumTeleportChance = config.getFloat("arcanumTeleportChance", "Arcanum", 0.000208f, 0.0f, 1.0f, "Chance per tick for Nebulous Arcanum to teleport player randomly.");
        arcanumTeleportRange = config.getInt("arcanumTeleportRange", "Arcanum", 32, 1, 32768, "Random teleport range for Nebulous Arcanum.");
        arcanumDormantTransformChance = config.getFloat("arcanumDormantTransformChance", "Arcanum", 0.000027f, 0.0f, 1.0f, "Chance per tick for Nebulous Arcanum to transform into Dormant Core.");
        arcanumDormantLifeMin = config.getInt("arcanumDormantLifeMin", "Arcanum", 12, 0, 32768, "Minimum lifespan in seconds for Dormant Core from Arcanum.");
        arcanumDormantLifeMax = config.getInt("arcanumDormantLifeMax", "Arcanum", 72, 0, 32768, "Maximum lifespan in seconds for Dormant Core from Arcanum.");
        arcanumVisDiscount = config.getFloat("arcanumVisDiscount", "Arcanum", 35.0f, 0.0f, 100.0f, "Vis discount percentage provided by Nebulous Arcanum.");

        // Dimensional Mirror
        dimensionalMirrorChannelDuration = config.getInt("dimensionalMirrorChannelDuration", "Dimensional Mirror", 80, 1, 32768, "Channel duration in ticks for Dimensional Mirror.");

        // Dormant Arcanum
        dormantArcanumVisCostPerTick = config.getInt("dormantArcanumVisCostPerTick", "Dormant Arcanum", 3, 0, 32768, "Vis cost per tick for Dormant Nebulous Core.");

        // Primal + Discord combo
        primalDiscordComboEnabled = config.getBoolean("primalDiscordComboEnabled", "Generic Config", true, "Whether the Tome of Primal Chaos + Tome of Discord combo skill is enabled.");

        // Vis Max Charge configs
        apotheosisMaxCharge = config.getInt("apotheosisMaxCharge", "Vis", 400, 0, 32768, "Max Vis charge for Apotheosis.");
        crimsonSpellMaxCharge = config.getInt("crimsonSpellMaxCharge", "Vis", 200, 0, 32768, "Max Vis charge for Crimson Spell.");
        darkSunRingMaxCharge = config.getInt("darkSunRingMaxCharge", "Vis", 500, 0, 32768, "Max Vis charge for Dark Sun Ring.");
        deificAmuletMaxCharge = config.getInt("deificAmuletMaxCharge", "Vis", 200, 0, 32768, "Max Vis charge for Deific Amulet.");
        devourerMaxCharge = config.getInt("devourerMaxCharge", "Vis", 300, 0, 32768, "Max Vis charge for Devourer of The Void.");
        discordTomeMaxCharge = config.getInt("discordTomeMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Tome of Discord.");
        eldritchSpellMaxCharge = config.getInt("eldritchSpellMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Eldritch Spell.");
        lunarFlaresMaxCharge = config.getInt("lunarFlaresMaxCharge", "Vis", 500, 0, 32768, "Max Vis charge for Tome of Lunar Flares.");
        nuclearFuryMaxCharge = config.getInt("nuclearFuryMaxCharge", "Vis", 500, 0, 32768, "Max Vis charge for Nuclear Fury.");
        oblivionAmuletMaxCharge = config.getInt("oblivionAmuletMaxCharge", "Vis", 400, 0, 32768, "Max Vis charge for Amulet of The Oblivion.");
        overthrowerMaxCharge = config.getInt("overthrowerMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Edict of Eternal Banishment.");
        soulTomeMaxCharge = config.getInt("soulTomeMaxCharge", "Vis", 1000, 0, 32768, "Max Vis charge for Edict of a Thousand Damned Souls.");
        thunderpealMaxCharge = config.getInt("thunderpealMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Thunderpeal.");
        weatherStoneMaxCharge = config.getInt("weatherStoneMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Runic Stone.");
        fateTomeMaxCharge = config.getInt("fateTomeMaxCharge", "Vis", 600, 0, 32768, "Max Vis charge for Tome of Broken Fates.");
        voidGrimoireMaxCharge = config.getInt("voidGrimoireMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Grimoire of The Abyss.");
        primalChaosMaxCharge = config.getInt("primalChaosMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Tome of Primal Chaos.");
        predestinyMaxCharge = config.getInt("predestinyMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Tome of Predestiny.");
        dormantArcanumMaxCharge = config.getInt("dormantArcanumMaxCharge", "Vis", 300, 0, 32768, "Max Vis charge for Dormant Nebulous Core.");
        arcanumMaxCharge = config.getInt("arcanumMaxCharge", "Vis", 500, 0, 32768, "Max Vis charge for Nebulous Arcanum.");
        dimensionalMirrorMaxCharge = config.getInt("dimensionalMirrorMaxCharge", "Vis", 100, 0, 32768, "Max Vis charge for Dimensional Mirror.");

        config.save();
        researchInspectionFrequency = (int)(600.0f / revelationModifier);
        knowledgeChance = 0.1 * (double)revelationModifier;
    }

    static {
        weatherStoneVisMult = 1.0f;
        exampleOverrides = new String[]{"EldritchSpell[Thaumcraft:ItemEldritchObject:2]", "AdvancedMiningCharm[Botania:manaResource:5, ForgottenRelicsRE:ItemMiningCharm:0, minecraft:diamond_pickaxe:0]", "TerrorCrown[minecraft:ender_eye:0, minecraft:nether_star:0, minecraft:golden_helmet:0, Botania:manaResource:15]"};
    }
}

