package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.TargetTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget.Type;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;

public class TargetTemplateRegistry {
    public static final DungeonComponentRegistry<TargetTemplate> TARGET_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();
    public static final ArrayList<TargetTemplate> targetTemplates = new ArrayList<>();

    public static final TargetTemplate ZOMBIE_NORMAL = createMob("ZOMBIE_NORMAL", EntityType.ZOMBIE);
    public static final TargetTemplate ZOMBIE_LEATHER = createMob("ZOMBIE_LEATHER", EntityType.ZOMBIE).setHelmet(Items.LEATHER_HELMET).setChestplate(Items.LEATHER_CHESTPLATE).setLeggings(Items.LEATHER_LEGGINGS).setBoots(Items.LEATHER_BOOTS);
    public static final TargetTemplate SKELETON_NORMAL = createMob("SKELETON_NORMAL", EntityType.SKELETON).setMainHandItem(Items.BOW,true);
    public static final TargetTemplate SKELETON_CHAIN = createMob("SKELETON_CHAIN", EntityType.SKELETON).setMainHandItem(Items.BOW,true).setHelmet(Items.CHAINMAIL_HELMET).setChestplate(Items.CHAINMAIL_CHESTPLATE,true).setLeggings(Items.CHAINMAIL_LEGGINGS).setBoots(Items.CHAINMAIL_BOOTS).setRandomChance(0.2f);
    public static final TargetTemplate SPIDER = createMob("SPIDER", EntityType.SPIDER);
    public static final TargetTemplate CREEPER = createMob("CREEPER", EntityType.CREEPER);
    public static final TargetTemplate PILLAGER = createMob("PILLAGER", EntityType.PILLAGER);
    public static final TargetTemplate BLAZE = createMob("BLAZE", EntityType.BLAZE);
    public static final TargetTemplate CAVE_SPIDER = createMob("CAVE_SPIDER", EntityType.CAVE_SPIDER);
    public static final TargetTemplate ENDERMAN = createMob("ENDERMAN", EntityType.ENDERMAN);
    public static final TargetTemplate HUSK = createMob("HUSK", EntityType.HUSK);
    public static final TargetTemplate STRAY = createMob("STRAY", EntityType.STRAY);
    public static final TargetTemplate VINDICATOR = createMob("VINDICATOR", EntityType.VINDICATOR);
    public static final TargetTemplate WITHER_SKELETON = createMob("WITHER_SKELETON", EntityType.WITHER_SKELETON).setMainHandItem(Items.STONE_SWORD,true);

    public static TargetTemplate createMob(String name, EntityType<?> entityType) {
        TargetTemplate targetTemplate = TargetTemplate.createMob(name, entityType);
        targetTemplates.add(targetTemplate);
        return targetTemplate;
    }

    public static void setupTargetTemplateRegistry() {
        targetTemplates.forEach(TARGET_TEMPLATE_REGISTRY::add);
    }
}