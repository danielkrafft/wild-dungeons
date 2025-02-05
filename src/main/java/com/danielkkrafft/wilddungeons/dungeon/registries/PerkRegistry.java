package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import org.joml.Vector2i;

public class PerkRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonPerkTemplate> DUNGEON_PERK_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonPerkTemplate SWORD_DAMAGE = new DungeonPerkTemplate("SWORD_DAMAGE", new Vector2i(0,0));
    public static final DungeonPerkTemplate AXE_DAMAGE = new DungeonPerkTemplate("AXE_DAMAGE", new Vector2i(1,0));
    public static final DungeonPerkTemplate BOW_DAMAGE = new DungeonPerkTemplate("BOW_DAMAGE", new Vector2i(2,0));
    public static final DungeonPerkTemplate EXTRA_LIFE = new DungeonPerkTemplate("EXTRA_LIFE", new Vector2i(3,0));

    public static void setupPerks(){
        DUNGEON_PERK_REGISTRY.add(SWORD_DAMAGE);
        DUNGEON_PERK_REGISTRY.add(AXE_DAMAGE);
        DUNGEON_PERK_REGISTRY.add(BOW_DAMAGE);
        DUNGEON_PERK_REGISTRY.add(EXTRA_LIFE);
    }
}
