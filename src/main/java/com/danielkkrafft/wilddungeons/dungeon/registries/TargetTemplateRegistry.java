package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.TargetTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget.Type;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.Arrays;

public class TargetTemplateRegistry {
    public static final DungeonComponentRegistry<TargetTemplate> TARGET_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();

    public static final TargetTemplate ZOMBIE_NORMAL = TargetTemplate.createMob("ZOMBIE_NORMAL", EntityType.ZOMBIE);
    public static final TargetTemplate ZOMBIE_LEATHER = TargetTemplate.createMob("ZOMBIE_LEATHER", EntityType.ZOMBIE).setHelmet(Items.LEATHER_HELMET).setChestplate(Items.LEATHER_CHESTPLATE).setLeggings(Items.LEATHER_LEGGINGS).setBoots(Items.LEATHER_BOOTS);
    public static final TargetTemplate SKELETON_NORMAL = TargetTemplate.createMob("SKELETON_NORMAL", EntityType.SKELETON).setMainHandItem(Items.BOW,true);
    public static final TargetTemplate SKELETON_CHAIN = TargetTemplate.createMob("SKELETON_CHAIN", EntityType.SKELETON).setMainHandItem(Items.BOW,true).setHelmet(Items.CHAINMAIL_HELMET).setChestplate(Items.CHAINMAIL_CHESTPLATE,true).setLeggings(Items.CHAINMAIL_LEGGINGS).setBoots(Items.CHAINMAIL_BOOTS).setRandomChance(0.2f);
    public static final TargetTemplate SPIDER = TargetTemplate.createMob("SPIDER", EntityType.SPIDER);
    public static final TargetTemplate CREEPER = TargetTemplate.createMob("CREEPER", EntityType.CREEPER);
    public static final TargetTemplate PILLAGER = TargetTemplate.createMob("PILLAGER", EntityType.PILLAGER);
    public static final TargetTemplate BLAZE = TargetTemplate.createMob("BLAZE", EntityType.BLAZE);
    public static final TargetTemplate CAVE_SPIDER = TargetTemplate.createMob("CAVE_SPIDER", EntityType.CAVE_SPIDER);
    public static final TargetTemplate ENDERMAN = TargetTemplate.createMob("ENDERMAN", EntityType.ENDERMAN);
    public static final TargetTemplate HUSK = TargetTemplate.createMob("HUSK", EntityType.HUSK);
    public static final TargetTemplate STRAY = TargetTemplate.createMob("STRAY", EntityType.STRAY);
    public static final TargetTemplate VINDICATOR = TargetTemplate.createMob("VINDICATOR", EntityType.VINDICATOR);
    public static final TargetTemplate WITHER_SKELETON = TargetTemplate.createMob("WITHER_SKELETON", EntityType.WITHER_SKELETON).setMainHandItem(Items.STONE_SWORD,true);

    public static void setupTargetTemplateRegistry() {
        Arrays.asList(ZOMBIE_NORMAL, ZOMBIE_LEATHER, SKELETON_NORMAL, SKELETON_CHAIN, SPIDER, CREEPER, PILLAGER, BLAZE, CAVE_SPIDER, ENDERMAN, HUSK, STRAY, VINDICATOR, WITHER_SKELETON).forEach(TARGET_TEMPLATE_REGISTRY::add);
    }
}