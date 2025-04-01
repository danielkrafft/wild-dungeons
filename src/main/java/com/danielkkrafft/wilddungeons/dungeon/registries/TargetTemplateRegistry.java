package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.TargetTemplate;
import com.danielkkrafft.wilddungeons.entity.WDEntities;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

public class TargetTemplateRegistry {
    public static final DungeonComponentRegistry<TargetTemplate> TARGET_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();
    public static final ArrayList<TargetTemplate> targetTemplates = new ArrayList<>();

    public static final TargetTemplate ZOMBIE_NORMAL = createMob("ZOMBIE_NORMAL", EntityType.ZOMBIE);
    public static final TargetTemplate ZOMBIE_LEATHER = createMob("ZOMBIE_LEATHER", EntityType.ZOMBIE).setHelmet(Items.LEATHER_HELMET).setChestplate(Items.LEATHER_CHESTPLATE).setLeggings(Items.LEATHER_LEGGINGS).setBoots(Items.LEATHER_BOOTS);
    public static final TargetTemplate SKELETON_NORMAL = createMob("SKELETON_NORMAL", EntityType.SKELETON).setMainHandItem(Items.BOW,true);
    public static final TargetTemplate SKELETON_CHAIN = createMob("SKELETON_CHAIN", EntityType.SKELETON).setMainHandItem(Items.BOW,true).setHelmet(Items.CHAINMAIL_HELMET).setChestplate(Items.CHAINMAIL_CHESTPLATE,true).setLeggings(Items.CHAINMAIL_LEGGINGS).setBoots(Items.CHAINMAIL_BOOTS).setRandomChance(0.2f);
    public static final TargetTemplate SPIDER = createMob("SPIDER", EntityType.SPIDER);
    public static final TargetTemplate CREEPER = createMob("CREEPER", EntityType.CREEPER);
    public static final TargetTemplate FAST_CREEPER = createMob("FAST_CREEPER", EntityType.CREEPER).addMobEffect(MobEffects.MOVEMENT_SPEED, 2);
    public static final TargetTemplate PILLAGER = createMob("PILLAGER", EntityType.PILLAGER).setMainHandItem(Items.CROSSBOW);
    public static final TargetTemplate BLAZE = createMob("BLAZE", EntityType.BLAZE);
    public static final TargetTemplate BEEFY_BLAZE = createMob("BEEFY_BLAZE", EntityType.BLAZE).addMobEffect(MobEffects.HEALTH_BOOST, 3);
    public static final TargetTemplate CAVE_SPIDER = createMob("CAVE_SPIDER", EntityType.CAVE_SPIDER);
    public static final TargetTemplate ENDERMAN = createMob("ENDERMAN", EntityType.ENDERMAN);
    public static final TargetTemplate HUSK = createMob("HUSK", EntityType.HUSK);
    public static final TargetTemplate STRAY = createMob("STRAY", EntityType.STRAY);
    public static final TargetTemplate VINDICATOR = createMob("VINDICATOR", EntityType.VINDICATOR).setMainHandItem(Items.IRON_AXE);
    public static final TargetTemplate EVOKER = createMob("EVOKER", EntityType.EVOKER);
    public static final TargetTemplate WITHER_SKELETON = createMob("WITHER_SKELETON", EntityType.WITHER_SKELETON).setMainHandItem(Items.STONE_SWORD,true);
    public static final TargetTemplate PIGLIN = createMob("PIGLIN", EntityType.PIGLIN);
    public static final TargetTemplate PIGLIN_BRUTE = createMob("PIGLIN_BRUTE", EntityType.PIGLIN_BRUTE).setMainHandItem(Items.GOLDEN_AXE);
    public static final TargetTemplate GHAST = createMob("GHAST", EntityType.GHAST);
    public static final TargetTemplate HOGLIN = createMob("HOGLIN", EntityType.HOGLIN);

    public static final TargetTemplate BREEZE = createMob("BREEZE", EntityType.BREEZE);
    public static final TargetTemplate ENDERMITE = createMob("ENDERMITE", EntityType.ENDERMITE);
    public static final TargetTemplate BOGGED = createMob("BOGGED", EntityType.BOGGED);
    public static final TargetTemplate MAGMA_CUBE = createMob("MAGMA_CUBE", EntityType.MAGMA_CUBE);
    public static final TargetTemplate RAVAGER = createMob("RAVAGER", EntityType.RAVAGER);
    public static final TargetTemplate SILVERFISH = createMob("SILVERFISH", EntityType.SILVERFISH);
    public static final TargetTemplate SLIME = createMob("SLIME", EntityType.SLIME);
    public static final TargetTemplate VEX = createMob("VEX", EntityType.VEX);
    public static final TargetTemplate WITCH = createMob("WITCH", EntityType.WITCH);

    public static final TargetTemplate WARDEN = createMob("WARDEN", EntityType.WARDEN);
    public static final TargetTemplate WITHER = createMob("WITHER", EntityType.WITHER);
    public static final TargetTemplate NETHER_DRAGON = createMob("NETHER_DRAGON", WDEntities.NETHER_DRAGON.get());
    public static final TargetTemplate MUTANT_BOGGED = createMob("MUTANT_BOGGED", WDEntities.MUTANT_BOGGED.get());
    public static final TargetTemplate BREEZE_GOLEM = createMob("BREEZE_GOLEM", WDEntities.BREEZE_GOLEM.get());

    public static TargetTemplate createMob(String name, EntityType<?> entityType) {
        TargetTemplate targetTemplate = TargetTemplate.createMob(name, entityType);
        targetTemplates.add(targetTemplate);
        return targetTemplate;
    }

    public static void setupTargetTemplateRegistry() {
        targetTemplates.forEach(TARGET_TEMPLATE_REGISTRY::add);
    }
}