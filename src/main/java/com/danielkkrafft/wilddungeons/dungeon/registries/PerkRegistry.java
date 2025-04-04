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
    public static final DungeonPerkTemplate DODGE = create(DodgePerk.class, new Vector2i(2, 3)); // not implemented
    public static final DungeonPerkTemplate ONE_PUNCH_MAN = create(OnePunchManPerk.class, new Vector2i(3, 3)); // not implemented
    public static final DungeonPerkTemplate EXPLOSION_IMMUNITY = create(ExplosionImmunePerk.class, new Vector2i(0, 4)).setUnique(); // not implemented
    public static final DungeonPerkTemplate BIG_RED_BUTTON = create(BigRedButtonPerk.class, new Vector2i(1, 4)); // not implemented
    public static final DungeonPerkTemplate CRITICAL_HIT = create(CriticalHitPerk.class, new Vector2i(2, 4)); // not implemented
    //Critical hit - dealing damage has a stackable 10% chance to deal 3x damage
    //Gills - you can breathe indefinitely underwater
    //XP Boost - XP and essence orbs grant +10% points
    //Overkill - Enemies that get one-shot drop 1.5x XP
    //Shop Discount - Shop prices are cut by 25%
    //Flight - Grants a stackable 1 maximum second of flight (shared by all players), recharges at 1/3 speed
    //Jump boost - stackable jump boost
    //Three Vexes - 3 friendly vex familiars that attack mobs and refresh after each room
    //Death Explosion - Killing mobs causes an explosion
    //Glass Cannon - Deal 10X damage while not wearing armor
    //Invisibility - permanent invisibility
    //No more secrets - secret room entrances are replaced with glass
    //Life Steal - Heal yourself for a stackable 5% of damage dealt
    //Beefy Perks - New stackable perks count for 2 instances instead of 1
    //Risky Business - Deal 5X damage while under 10% max health
    //No Regrets - Gain +1 Strength for each additional life above 1, reduces lives to 1
    //Glowing enemies - All enemies glow, not just the last 3
    //Wither immunity - No longer inflicted with wither
    //Wild Thorns - Reflect 100% of incoming damage (you still take the damage)
    //Toxic - Inflict strong poison on contact (stack increases amplifier)
    //Ender Syndrone - Teleport away when you would be hit by a projectile
    //Stronger Totems - Stackable 10% chance for lives not to be consumed
    //I'm feeling lucky - Shifting 10 times teleports you to a random room (we would need to support going further than next combat room
    //Slow aura - Inflict weak slowness on entities around you in a 1 + 1 (stackable) range
    //Food Critic - Adds 1 extra heart per 5 unique foods eaten, max of 5 (stackable)
    //Homing Projectiles - Projectiles home in on entities close-by, stacking increases strength (would probably require mixing into projectile)
    //Chaos Arrows - Gives fired arrows random effects
    //Poison Aura - Inflict weak poison in a 1 + 1 (stackable) range
    //Wither Aura - Inflict wither in a 1 + 1 (stackable) range
    //Superglue - Inflict strong slowness on contact, stacking increases amplifier
    //Oozing - Taking damage spawns 1 (stacks) small friendly slime
    //Bulldozer - Lives no longer respawn players (they can keep fighting until no more lives)
    //Hurricane - Taking damage spawns 4 (stacks) wind charges in random directions
    //Reach - +10% Reach
    //Friend of Arthropods - All arthropods become friendly towards the player
    //Medusa - Inflict weak slowness on enemies in a 10 (stackable) degree cone within 5 (stackable) blocks of the player's look direction
    //Vine Boom - Randomly every 8-16 seconds, emits a knockback pulse which pushes enemies away (also plays vine boom sound) (stacking decreases duration)
    //Benefit Plan - Combat rooms have a 10% chance to spawn a perk upon completion, up to 3 times (stackable)
    //Magnet - Orbs/Items pull towards the player from farther away and with stronger force
    //Attack knockback +10% attack knockback
    //Ricochet - Player projectiles bounce on surfaces 1 (stacks) time
    //Fast Projectiles -Player projectiles move 10% (stacks) faster
    //Confetti - Causes a fuck ton of XP/Essence orbs to rain down in the current room
    //Loot Multiplier - Increases loot/shop quality by +10% (stacks)
    //Double Projectiles - Player projectiles spawn twice (stacks)
    //Second Guess - +10% stackable chance for perks to spawn another free random perk in their place when obtained
    //Friend of the Undead - All undead mobs become friendly towards the player
    //Infested - Taking damage spawns 1 (stacks) friendly silverfish
    //The Force - Inflict weak levitation on enemies in a 10 (stackable) degree cone within 5 (stackable) blocks of the player's look direction
    //Juggernaut - Gain +1 Hearts for each additional life above 1, reduces lives to 1
    //Allowance - 50% chance to gain a life for every 5 minutes that pass
    //Connoisseur - Unused hunger points (e.g. eating steak at 9/10 shanks) are converted into absorption
    //Strength of Spirit - 25% of your current absorption amount is converted into permanent health, the rest is removed
    //Two of a Kind - Gain two random perks
    //Explorer's Instinct - 25% chance to gain 1 absorption heart (stackable) for each new room you discover
    //Shareholder Distribution - Deletes all items from the collector's inventory. Gain 1 life for each 200 items destroyed in this way
    //Divine Guidance - Decals which lead to the exit are colored green
    //Iron Guard - Spawn 10 Iron Golems which teleport into active combat rooms and fight mobs but do not refresh on death





    public static DungeonPerkTemplate create(Class<? extends DungeonPerk> clazz, Vector2i position){
        DungeonPerkTemplate perk = new DungeonPerkTemplate(clazz, position);
        perks.add(perk);
        return perk;
    }

    public static void setupPerks(){
        perks.forEach(DUNGEON_PERK_REGISTRY::add);
    }
}
