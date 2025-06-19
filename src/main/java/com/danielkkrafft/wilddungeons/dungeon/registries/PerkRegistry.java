package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.ExtraLifePerk;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonPerkTemplate;
import com.danielkkrafft.wilddungeons.dungeon.mob_effects.WDMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.joml.Vector2i;

public class PerkRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonPerkTemplate> DUNGEON_PERK_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonPerkTemplate EXTRA_LIFE = create(ExtraLifePerk.class, new Vector2i(3, 0));
    public static final DungeonPerkTemplate SWORD_DAMAGE = create(WDMobEffects.SWORD_DAMAGE, new Vector2i(0, 0), "SwordDamagePerk");
    public static final DungeonPerkTemplate AXE_DAMAGE = create(WDMobEffects.AXE_DAMAGE, new Vector2i(1, 0), "AxeDamagePerk");
    public static final DungeonPerkTemplate BOW_DAMAGE = create(WDMobEffects.BOW_DAMAGE, new Vector2i(2, 0), "BowDamagePerk");
    public static final DungeonPerkTemplate FIRE_RESIST = create(MobEffects.FIRE_RESISTANCE, new Vector2i(0, 1), "PermanentFireResistPerk").setUnique();
    public static final DungeonPerkTemplate STRENGTH = create(MobEffects.DAMAGE_BOOST, new Vector2i(1, 1), "PermanentStrengthPerk");
    public static final DungeonPerkTemplate NIGHT_VISION = create(MobEffects.NIGHT_VISION, new Vector2i(2, 1), "PermanentNightVisionPerk").setUnique();
    public static final DungeonPerkTemplate HEALTH_BOOST = create(MobEffects.HEALTH_BOOST, new Vector2i(3, 1), "PermanentHealthBoostPerk");
    public static final DungeonPerkTemplate MOVEMENT_SPEED = create(MobEffects.MOVEMENT_SPEED, new Vector2i(0, 2), "PermanentMovementSpeedPerk");
    public static final DungeonPerkTemplate HASTE = create(MobEffects.DIG_SPEED, new Vector2i(3, 2), "PermanentDigSpeedPerk");
    public static final DungeonPerkTemplate BIG_ABSORPTION = create(MobEffects.ABSORPTION,20, new Vector2i(2, 2), "BigAbsorptionPerk");
    public static final DungeonPerkTemplate POISON_IMMUNITY = create(WDMobEffects.POISON_RESISTANCE, new Vector2i(0, 3), "PoisonImmunePerk").setUnique();
    public static final DungeonPerkTemplate STEP_HEIGHT = create(WDMobEffects.STEP_HEIGHT, new Vector2i(1, 3), "StepHeightPerk");
    public static final DungeonPerkTemplate DODGE = create(WDMobEffects.EVASION, new Vector2i(2, 3), "DodgePerk");
    public static final DungeonPerkTemplate EXPLOSION_IMMUNITY = create(WDMobEffects.EXPLOSION_RESISTANCE, new Vector2i(0, 4), "ExplosionImmunePerk").setUnique();
    public static final DungeonPerkTemplate BIG_RED_BUTTON = create(WDMobEffects.BIG_RED_BUTTON, new Vector2i(1, 4), "BigRedButtonPerk");
    public static final DungeonPerkTemplate CRITICAL_HIT = create(WDMobEffects.KEEN_EDGE, new Vector2i(2, 4), "CriticalHitPerk");
    public static final DungeonPerkTemplate ONE_PUNCH_MAN = create(WDMobEffects.ONE_PUNCH_MAN, new Vector2i(3, 3), "OnePunchManPerk").setUnique();
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
    //Midas Touch


    public static DungeonPerkTemplate create(Class<? extends DungeonPerk> clazz, Vector2i position) {
        DungeonPerkTemplate perk = new DungeonPerkTemplate(clazz, position);
        DUNGEON_PERK_REGISTRY.add(perk);
        return perk;
    }

    public static DungeonPerkTemplate create(Holder<MobEffect> effect, Vector2i position, String name) {
        DungeonPerkTemplate perk = new DungeonPerkTemplate(effect, position).setName(name);
        DUNGEON_PERK_REGISTRY.add(perk);
        return perk;
    }

    public static DungeonPerkTemplate create(Holder<MobEffect> effect, int amplifier, Vector2i position, String name) {
        DungeonPerkTemplate perk = new DungeonPerkTemplate(effect, amplifier, position).setName(name);
        DUNGEON_PERK_REGISTRY.add(perk);
        return perk;
    }

}