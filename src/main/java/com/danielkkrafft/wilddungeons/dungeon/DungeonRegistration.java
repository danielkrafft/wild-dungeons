package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class DungeonRegistration {

    public static class DungeonComponentRegistry<T extends DungeonComponent> {
        private final HashMap<String, T> registry;
        public DungeonComponentRegistry() { registry = new HashMap<>(); }

        public T add(T component) { registry.put(component.name(), component); return component;}
        public T get(String key) { return registry.get(key); }

    }

    public static class DungeonLayout<T> {
        private final List<WeightedPool<T>> order;
        public DungeonLayout() {order = new ArrayList<>();}

        public DungeonLayout<T> add(WeightedPool<T> pool, int count) {
            for (int i = 0; i < count; i++) {
                this.order.add(pool);
            }
            return this;
        }

        public DungeonLayout<T> addMaybe(WeightedPool<T> pool, int count, int chance) {
            int roll = ThreadLocalRandom.current().nextInt(1, 101); // 1 to 100 inclusive
            if (roll <= chance) {
                this.add(pool, count);
            }
            return this;
        }

        public DungeonLayout<T> addSimple(T entry) {
            this.order.add(new WeightedPool<T>().add(entry, 1));
            return this;
        }

        public WeightedPool<T> get(int index) {return this.order.get(index);}
        public int size() {return this.order.size();}
        public WeightedPool<T> getFirst() {return this.order.getFirst();}
        public WeightedPool<T> getLast() {return this.order.getLast();}
    }

    public static final class OfferingTemplate implements DungeonComponent {
        private final String name;
        private final Offering.Type type;
        private final int amount;
        private final String id;
        private Offering.CostType costType;
        private Item costItem;
        private int costAmount;
        private final float deviance;
        private float renderScale = 1.0f;
        private int colorTint;
        private int soundLoop;
        private boolean showRing = false;
        private String riftDestination;

        public OfferingTemplate(String name, Offering.Type type, int amount, String id, Offering.CostType costType, int costAmount, float costDeviance) {
            this.name = name;
            this.type = type;
            this.amount = amount;
            this.id = id;
            this.costType = costType;
            this.costAmount = costAmount;
            this.deviance = costDeviance;
        }

        public OfferingTemplate setRiftDestination(String destination) {
            this.riftDestination = destination;
            return this;
        }
        public String getRiftDestination() {
            return this.riftDestination;
        }

        public OfferingTemplate setRenderScale(float renderScale) {this.renderScale = renderScale; return this;}
        public OfferingTemplate setColorTint(int colorTint) {this.colorTint = colorTint; return this;}
        public OfferingTemplate setSoundLoop(SoundEvent soundLoop) {this.soundLoop = BuiltInRegistries.SOUND_EVENT.getId(soundLoop); return this;}
        public OfferingTemplate setShowRing(boolean showRing) {this.showRing = showRing; return this;}
        public OfferingTemplate setCostItem(Item item, int amount) {
            this.costType = Offering.CostType.ITEM;
            this.costAmount = amount;
            this.costItem = item;
            return this;
        }

        public OfferingTemplate(String name, ItemTemplate itemTemplate, Offering.CostType costType, int costAmount, float costDeviance) {
             this(name, Offering.Type.ITEM, itemTemplate.getDeviatedCount(), String.valueOf(itemTemplate.itemID), costType, costAmount, costDeviance);
        }

        public Offering asOffering(Level level) {
            int adjustedAmount = RandomUtil.randIntBetween((int) (amount / deviance), (int) (amount * deviance));
            adjustedAmount = Math.max(1, adjustedAmount);
            int adjustedCost = RandomUtil.randIntBetween((int) (costAmount / deviance), (int) (costAmount * deviance));
            Offering offering = new Offering(level, type, adjustedAmount, id, costType, adjustedCost).setRiftDestination(getRiftDestination());
            if (costType.equals(Offering.CostType.ITEM)){
                offering.setCostItem(costItem);
            }
            offering.setRenderScale(renderScale);
            offering.setSoundLoop(soundLoop);
            offering.setShowItemHighlight(showRing);
            if (this.type == Offering.Type.RIFT && this.id.split("-")[0].equals("wd")) {
                DungeonTemplate dungeonTemplate = DungeonRegistry.DUNGEON_REGISTRY.get(this.id.split("wd-")[1]);

                if (dungeonTemplate == null) {
                    dungeonTemplate = DungeonRegistry.DUNGEON_REGISTRY.get(offering.getRiftDestination());
                }

                // let's gracefully fail if we run into an issue. Worse case, the rift is colored wrong but still works.
                if (dungeonTemplate == null) {
                    WildDungeons.getLogger().error("No dungeon found by that id");
                    return offering;
                }

                offering.setPrimaryColor(dungeonTemplate.get(HierarchicalProperty.PRIMARY_COLOR));
                offering.setSecondaryColor(dungeonTemplate.get(HierarchicalProperty.SECONDARY_COLOR));
            }
            return offering;
        }

        @Override
        public String name() {return name;}
        public Offering.Type type() {return type;}
        public int amount() {return amount;}
        public String id() {return id;}
        public Offering.CostType costType() {return costType;}
        public int costAmount() {return costAmount;}
        public float deviance() {return deviance;}
        public float renderScale() {return renderScale;}
        public int colorTint() {return colorTint;}
        public int soundLoop() {return soundLoop;}
    }

    public static class TargetTemplate implements DungeonComponent{
        private String name;
        private String type;
        private String entityType;
        private Consumer<Object> behavior;
        private int helmetItem = -1;
        private boolean helmetAlwaysSpawn = false;
        private int chestItem =- 1;
        private boolean chestAlwaysSpawn = false;
        private int legsItem = -1;
        private boolean legsAlwaysSpawn = false;
        private int bootsItem = -1;
        private boolean bootsAlwaysSpawn = false;
        private int mainHandItem = -1;
        private boolean mainHandAlwaysSpawn = false;
        private int offHandItem = -1;
        private boolean offHandAlwaysSpawn = false;
        private List<Pair<Integer, Integer>> mobEffects = new ArrayList<>();
        private boolean allEffects = false;
        private float randomChance = 0.5f;
        private double healthMultiplier = 1.0f;

        public TargetTemplate(String name, DungeonTarget.Type type) {
            this.name = name;
            this.type = type.toString();
        }

        public static TargetTemplate createMob(String name, EntityType<?> entityType) {
            return new TargetTemplate(name, DungeonTarget.Type.ENTITY).setEntityType(entityType);
        }

        public DungeonTarget asEnemy() {
            DungeonTarget enemy = new DungeonTarget(DungeonTarget.Type.valueOf(this.type), this.entityType);
            enemy.behavior = behavior;
            enemy.helmetItem = helmetAlwaysSpawn ? helmetItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? helmetItem : -1;
            enemy.chestItem = chestAlwaysSpawn ? chestItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? chestItem : -1;
            enemy.legsItem = legsAlwaysSpawn ? legsItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? legsItem : -1;
            enemy.bootsItem = bootsAlwaysSpawn ? bootsItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? bootsItem : -1;
            enemy.mainHandItem = mainHandAlwaysSpawn ? mainHandItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? mainHandItem : -1;
            enemy.offHandItem = offHandAlwaysSpawn ? offHandItem : RandomUtil.randFloatBetween(0, 1) < randomChance ? offHandItem : -1;
            enemy.healthMultiplier = healthMultiplier;
            if (allEffects) {
                enemy.mobEffects.addAll(mobEffects);
            } else for (Pair<Integer, Integer> mobEffect : mobEffects) {
                boolean applyEffect = RandomUtil.randFloatBetween(0, 1) < randomChance;
                if (applyEffect) {
                    enemy.mobEffects.add(mobEffect);
                }
            }
            return enemy;
        }

        @Override
        public String name() {return this.name;}
        public TargetTemplate setEntityType(EntityType<?> entityType) {this.entityType = EntityType.getKey(entityType).toString(); return this;}
        public EntityType<?> getEntityType() {return EntityType.byString(this.entityType).orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + this.entityType));}
        public String getEntityTypeString() {return this.entityType;}
        public TargetTemplate setHelmet(Item item) {this.helmetItem = Item.getId(item); return this;}
        public TargetTemplate setHelmet(Item item, boolean alwaysSpawn) {this.helmetItem = Item.getId(item);this.helmetAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate setChestplate(Item item) {this.chestItem = Item.getId(item); return this;}
        public TargetTemplate setChestplate(Item item, boolean alwaysSpawn) {this.chestItem = Item.getId(item);this.chestAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate setLeggings(Item item) {this.legsItem = Item.getId(item); return this;}
        public TargetTemplate setLeggings(Item item, boolean alwaysSpawn) {this.legsItem = Item.getId(item);this.legsAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate setBoots(Item item) {this.bootsItem = Item.getId(item); return this;}
        public TargetTemplate setBoots(Item item, boolean alwaysSpawn) {this.bootsItem = Item.getId(item);this.bootsAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate setMainHandItem(Item item) {this.mainHandItem = Item.getId(item); return this;}
        public TargetTemplate setMainHandItem(Item item, boolean alwaysSpawn) {this.mainHandItem = Item.getId(item);this.mainHandAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate setOffHandItem(Item item) {this.offHandItem = Item.getId(item); return this;}
        public TargetTemplate setOffHandItem(Item item, boolean alwaysSpawn) {this.offHandItem = Item.getId(item);this.offHandAlwaysSpawn=alwaysSpawn; return this;}
        public TargetTemplate addMobEffect(Holder<MobEffect> effect, int amplifier) {this.mobEffects.add(Pair.of(BuiltInRegistries.MOB_EFFECT.getId(effect.value()), amplifier)); return this;}
        public TargetTemplate addMobEffect(Holder<MobEffect> effect, int amplifier, boolean alwaysSpawnAll) {this.mobEffects.add(Pair.of(BuiltInRegistries.MOB_EFFECT.getId(effect.value()), amplifier));allEffects=alwaysSpawnAll; return this;}
        public TargetTemplate setRandomChance(float randomChance) {this.randomChance = randomChance; return this;}
        public TargetTemplate setHealthMultiplier(double mult) {this.healthMultiplier = mult; return this;}
        public TargetTemplate addSpawnBehavior(Consumer<Object> behavior) {this.behavior = behavior; return this;}


    }

    public static class ItemTemplate implements DungeonComponent {
        public String name;
        public int itemID;
        public int count = 1;
        public float deviance = 1;
        public int potionIndex = -1;
        public ItemTemplate(String name, Item item, int count) {
            this.name = name;
            this.itemID = Item.getId(item);
            this.count = count;
        }
        public ItemTemplate(String name, Holder<Potion> potion) {
            this.name = name;
            this.itemID = Item.getId(Items.POTION);
            this.potionIndex = BuiltInRegistries.POTION.getId(potion.value());
        }

        public int getDeviatedCount() {
            return RandomUtil.randIntBetween((int) (count / deviance), (int) (count * deviance));
        }

        public ItemStack asItemStack() {
            if (potionIndex != -1) {
                return PotionContents.createItemStack(Items.POTION, BuiltInRegistries.POTION.getHolder(potionIndex).get());
            }
            return new ItemStack(Item.byId(itemID), getDeviatedCount());
        }

        @Override
        public String name() {return this.name;}
        public ItemTemplate setCount(int count) {this.count = count; return this;}
        public ItemTemplate setDeviance(float deviance) {this.deviance = deviance; return this;}
        public ItemTemplate setSplashPotion() {this.itemID=Item.getId(Items.SPLASH_POTION); return this;}
        public ItemTemplate setLingeringPotion() {this.itemID=Item.getId(Items.LINGERING_POTION); return this;}
    }

    public static void AddSoundEventToSoundList(List<List<Holder<SoundEvent>>> list, Holder<SoundEvent> event, int intensity) {
        while (list.size() <= intensity) {
            list.add(new ArrayList<>());
        }
        list.get(intensity).add(event);
    }

    public static class SoundscapeTemplate implements DungeonComponent {
        public String name;
        public List<List<Holder<SoundEvent>>> soundsList = new ArrayList<>();
        public SoundscapeTemplate(String name) {this.name = name;}

        public SoundscapeTemplate addSound(Holder<SoundEvent> event, int intensity) {
            AddSoundEventToSoundList(soundsList, event, intensity);
            return this;
        }

        public List<List<Holder<SoundEvent>>> underwaterSoundsList = new ArrayList<>();
        public SoundscapeTemplate addUnderwaterSound(Holder<SoundEvent> event, int intensity) {
            AddSoundEventToSoundList(underwaterSoundsList, event, intensity);
            return this;
        }
        public String name() {return this.name;}
    }

    public interface DungeonComponent { String name();}
}
