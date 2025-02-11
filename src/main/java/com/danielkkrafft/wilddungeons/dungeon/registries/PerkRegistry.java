package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import org.joml.Vector2i;

import java.util.ArrayList;

public class PerkRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonPerkTemplate> DUNGEON_PERK_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonPerkTemplate> perks = new ArrayList<>();

    public static final DungeonPerkTemplate SWORD_DAMAGE = create("SWORD_DAMAGE", new Vector2i(0,0));
    public static final DungeonPerkTemplate AXE_DAMAGE = create("AXE_DAMAGE", new Vector2i(1,0));
    public static final DungeonPerkTemplate BOW_DAMAGE = create("BOW_DAMAGE", new Vector2i(2,0));
    public static final DungeonPerkTemplate EXTRA_LIFE = create("EXTRA_LIFE", new Vector2i(3,0));


    public static DungeonPerkTemplate create(String name, Vector2i position){
        DungeonPerkTemplate perk = new DungeonPerkTemplate(name, position);
        perks.add(perk);
        return perk;
    }

    public static void setupPerks(){
        perks.forEach(DUNGEON_PERK_REGISTRY::add);
    }
}
