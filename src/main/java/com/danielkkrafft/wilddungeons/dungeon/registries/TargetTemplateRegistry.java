package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonComponentRegistry;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.TargetTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget.Type;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class TargetTemplateRegistry {
    public static final DungeonComponentRegistry<TargetTemplate> TARGET_TEMPLATE_REGISTRY = new DungeonComponentRegistry<>();

    public static final TargetTemplate ZOMBIE = new TargetTemplate("ZOMBIE", Type.ENTITY).setEntityType(EntityType.ZOMBIE).setHelmet(Items.LEATHER_HELMET).addMobEffect(MobEffects.GLOWING, 0);
    public static final TargetTemplate SKELETON = new TargetTemplate("SKELETON", Type.ENTITY).setEntityType(EntityType.SKELETON).setMainHandItem(Items.BOW);
    public static final TargetTemplate SPIDER = new TargetTemplate("SPIDER", Type.ENTITY).setEntityType(EntityType.SPIDER);
    public static final TargetTemplate CREEPER = new TargetTemplate("CREEPER", Type.ENTITY).setEntityType(EntityType.CREEPER);
    public static final TargetTemplate PILLAGER = new TargetTemplate("PILLAGER", Type.ENTITY).setEntityType(EntityType.PILLAGER);

    public static final TargetTemplate BLAZE = new TargetTemplate("BLAZE", Type.ENTITY).setEntityType(EntityType.BLAZE);
    public static final TargetTemplate CAVE_SPIDER = new TargetTemplate("CAVE_SPIDER", Type.ENTITY).setEntityType(EntityType.CAVE_SPIDER);
    public static final TargetTemplate ENDERMAN = new TargetTemplate("ENDERMAN", Type.ENTITY).setEntityType(EntityType.ENDERMAN);
    public static final TargetTemplate HUSK = new TargetTemplate("HUSK", Type.ENTITY).setEntityType(EntityType.HUSK);
    public static final TargetTemplate STRAY = new TargetTemplate("STRAY", Type.ENTITY).setEntityType(EntityType.STRAY);

    public static final TargetTemplate VINDICATOR = new TargetTemplate("VINDICATOR", Type.ENTITY).setEntityType(EntityType.VINDICATOR);
    public static final TargetTemplate WITHER_SKELETON = new TargetTemplate("WITHER_SKELETON", Type.ENTITY).setEntityType(EntityType.WITHER_SKELETON);

    public static void setupTargetTemplateRegistry(){

        TARGET_TEMPLATE_REGISTRY.add(ZOMBIE);
        TARGET_TEMPLATE_REGISTRY.add(SKELETON);
        TARGET_TEMPLATE_REGISTRY.add(SPIDER);
        TARGET_TEMPLATE_REGISTRY.add(CREEPER);
        TARGET_TEMPLATE_REGISTRY.add(PILLAGER);
        TARGET_TEMPLATE_REGISTRY.add(BLAZE);
        TARGET_TEMPLATE_REGISTRY.add(CAVE_SPIDER);
        TARGET_TEMPLATE_REGISTRY.add(ENDERMAN);
        TARGET_TEMPLATE_REGISTRY.add(HUSK);
        TARGET_TEMPLATE_REGISTRY.add(STRAY);
        TARGET_TEMPLATE_REGISTRY.add(VINDICATOR);
        TARGET_TEMPLATE_REGISTRY.add(WITHER_SKELETON);

    }
}
