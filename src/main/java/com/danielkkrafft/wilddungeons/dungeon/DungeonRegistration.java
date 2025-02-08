package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonTarget;
import com.danielkkrafft.wilddungeons.dungeon.registries.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.*;
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

public class DungeonRegistration {
    public static void setupRegistries() {
        ItemTemplateRegistry.setupLootEntries();
        LootPoolRegistry.setupLootPools();
        LootTableRegistry.setupLootTables();

        EnemyPoolRegistry.setupEnemyPools();
        EnemyTableRegistry.setupEnemyTables();

        DungeonMaterialRegistry.setupDungeonMaterials();
        DungeonMaterialPoolRegistry.setupMaterialPools();

        DungeonRoomRegistry.setupDungeonRooms();
        DungeonRoomPoolRegistry.setupRoomPools();

        DungeonBranchRegistry.setupBranches();

        DungeonFloorRegistry.setupFloors();
        DungeonFloorPoolRegistry.setupFloorPools();

        DungeonRegistry.setupDungeons();
        DungeonPoolRegistry.setupDungeonPools();

        PerkRegistry.setupPerks();
        PerkPoolRegistry.setupPerkPools();

        OfferingTemplateRegistry.setupOfferings();
        OfferingTemplatePoolRegistry.setupOfferingPools();
        OfferingTemplateTableRegistry.setupOfferingTables();

        RiftPoolRegistry.setupRiftPools();
        TargetTemplateRegistry.setupTargetTemplateRegistry();
    }

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
        private final Offering.CostType costType;
        private final int costAmount;
        private final float deviance;
        private float renderScale = 1.0f;
        private int colorTint;
        private int soundLoop;

        public OfferingTemplate(String name, Offering.Type type, int amount, String id, Offering.CostType costType, int costAmount, float costDeviance) {
            this.name = name;
            this.type = type;
            this.amount = amount;
            this.id = id;
            this.costType = costType;
            this.costAmount = costAmount;
            this.deviance = costDeviance;
        }

        public OfferingTemplate setRenderScale(float renderScale) {this.renderScale = renderScale; return this;}
        public OfferingTemplate setColorTint(int colorTint) {this.colorTint = colorTint; return this;}
        public OfferingTemplate setSoundLoop(SoundEvent soundLoop) {this.soundLoop = BuiltInRegistries.SOUND_EVENT.getId(soundLoop); return this;}

        public OfferingTemplate(String name, ItemTemplate itemTemplate, Offering.CostType costType, int costAmount, float costDeviance) {
             this(name, Offering.Type.ITEM, itemTemplate.getDeviatedCount(), String.valueOf(itemTemplate.itemID), costType, costAmount, costDeviance);
        }

        public Offering asOffering(Level level) {
            int adjustedAmount = RandomUtil.randIntBetween((int) (amount / deviance), (int) (amount * deviance));
            int adjustedCost = RandomUtil.randIntBetween((int) (costAmount / deviance), (int) (costAmount * deviance));
            Offering offering = new Offering(level, type, adjustedAmount, id, costType, adjustedCost);
            offering.setRenderScale(renderScale);
            offering.setSoundLoop(soundLoop);
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
        public String name;
        public String type;
        public String entityType;
        public int helmetItem = -1;
        public int chestItem =- 1;
        public int legsItem = -1;
        public int bootsItem = -1;
        public int mainHandItem = -1;
        public int offHandItem = -1;
        public List<Pair<Integer, Integer>> mobEffects = new ArrayList<>();

        public TargetTemplate(String name, DungeonTarget.Type type) {
            this.name = name;
            this.type = type.toString();
        }

        public static TargetTemplate createMob(String name, EntityType<?> entityType) {
            return new TargetTemplate(name, DungeonTarget.Type.ENTITY).setEntityType(entityType);
        }

        public DungeonTarget asEnemy() {
            DungeonTarget enemy = new DungeonTarget(DungeonTarget.Type.valueOf(this.type), this.entityType);
            enemy.helmetItem = helmetItem;
            enemy.chestItem = chestItem;
            enemy.legsItem = legsItem;
            enemy.bootsItem = bootsItem;
            enemy.mainHandItem = mainHandItem;
            enemy.offHandItem = offHandItem;
            enemy.mobEffects.addAll(this.mobEffects);
            return enemy;
        }

        @Override
        public String name() {return this.name;}
        public TargetTemplate setEntityType(EntityType<?> entityType) {this.entityType = EntityType.getKey(entityType).toString(); return this;}
        public TargetTemplate setHelmet(Item item) {this.helmetItem = Item.getId(item); return this;}
        public TargetTemplate setChestplate(Item item) {this.chestItem = Item.getId(item); return this;}
        public TargetTemplate setLeggings(Item item) {this.legsItem = Item.getId(item); return this;}
        public TargetTemplate setBoots(Item item) {this.bootsItem = Item.getId(item); return this;}
        public TargetTemplate setMainHandItem(Item item) {this.mainHandItem = Item.getId(item); return this;}
        public TargetTemplate setOffHandItem(Item item) {this.offHandItem = Item.getId(item); return this;}
        public TargetTemplate addMobEffect(Holder<MobEffect> effect, int amplifier) {this.mobEffects.add(Pair.of(BuiltInRegistries.MOB_EFFECT.getId(effect.value()), amplifier)); return this;}
    }

    public static class ItemTemplate implements DungeonComponent {
        public String name;
        public int itemID;
        public int count = 1;
        public float deviance = 1;
        public int potionType = -1;

        public ItemTemplate(String name, Item item, int count) {
            this.name = name;
            this.itemID = Item.getId(item);
            this.count = count;
        }
        public ItemTemplate(String name, Holder<Potion> potion) {
            this.name = name;
            this.itemID = Item.getId(Items.POTION);
            this.potionType = BuiltInRegistries.POTION.getId(potion.value());
        }

        public int getDeviatedCount() {
            return RandomUtil.randIntBetween((int) (count / deviance), (int) (count * deviance));
        }

        public ItemStack asItemStack() {
            if (potionType != -1) {
                return PotionContents.createItemStack(Items.POTION, BuiltInRegistries.POTION.getHolder(potionType).get());
            }
            return new ItemStack(Item.byId(itemID), getDeviatedCount());
        }

        @Override
        public String name() {return this.name;}
        public ItemTemplate setCount(int count) {this.count = count; return this;}
        public ItemTemplate setDeviance(float deviance) {this.deviance = deviance; return this;}
    }
}
