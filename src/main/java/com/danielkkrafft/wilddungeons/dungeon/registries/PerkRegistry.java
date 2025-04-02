package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import org.joml.Vector2i;

import java.util.ArrayList;

public class PerkRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonPerkTemplate> DUNGEON_PERK_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonPerkTemplate> perks = new ArrayList<>();

    public static final DungeonPerkTemplate SWORD_DAMAGE = create(SwordDamagePerk.class, new Vector2i(0,0));
    public static final DungeonPerkTemplate AXE_DAMAGE = create(AxeDamagePerk.class, new Vector2i(1,0));
    public static final DungeonPerkTemplate BOW_DAMAGE = create(BowDamagePerk.class, new Vector2i(2,0));
    public static final DungeonPerkTemplate EXTRA_LIFE = create(ExtraLifePerk.class, new Vector2i(3,0));
    public static final DungeonPerkTemplate FIRE_RESIST = create(PermanentFireResistPerk.class, new Vector2i(0,1)).setUnique();
    public static final DungeonPerkTemplate STRENGTH = create(PermanentStrengthPerk.class, new Vector2i(1,1));
    public static final DungeonPerkTemplate NIGHT_VISION = create(PermanentNightVisionPerk.class, new Vector2i(2,1)).setUnique();
    public static final DungeonPerkTemplate HEALTH_BOOST = create(PermanentHealthBoostPerk.class, new Vector2i(3,1));
    public static final DungeonPerkTemplate MOVEMENT_SPEED = create(PermanentMovementSpeedPerk.class, new Vector2i(0, 2));
    public static final DungeonPerkTemplate DIG_SPEED = create(PermanentDigSpeedPerk.class, new Vector2i(1, 2));
    public static final DungeonPerkTemplate BIG_ABSORPTION = create(BigAbsorptionPerk.class, new Vector2i(2, 2));
    public static final DungeonPerkTemplate ATTACK_SPEED = create(AttackSpeedPerk.class, new Vector2i(3,2));
    public static final DungeonPerkTemplate POISON_IMMUNITY = create(PoisonImmunePerk.class, new Vector2i(0, 3)).setUnique();
    public static final DungeonPerkTemplate STEP_HEIGHT = create(StepHeightPerk.class, new Vector2i(1, 3));


    public static DungeonPerkTemplate create(Class<? extends DungeonPerk> clazz, Vector2i position){
        DungeonPerkTemplate perk = new DungeonPerkTemplate(clazz, position);
        perks.add(perk);
        return perk;
    }

    public static void setupPerks(){
        perks.forEach(DUNGEON_PERK_REGISTRY::add);
    }
}
