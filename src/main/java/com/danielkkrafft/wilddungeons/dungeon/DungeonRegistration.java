package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.dungeon.registries.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.*;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.danielkkrafft.wilddungeons.entity.Offering.CostType.*;
import static com.danielkkrafft.wilddungeons.entity.Offering.Type.*;

public class DungeonRegistration {
    public static void setupRegistries() {
        LootEntryRegistry.setupLootEntries();
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

    public record LootEntry(String name, Item item, int count, float deviance) implements DungeonComponent {
        public ItemStack asItemStack() {
            return new ItemStack(item, RandomUtil.randIntBetween((int) (count / deviance), (int) (count * deviance)));
        }
    }

    public record OfferingTemplate(String name, Offering.Type type, int amount, String id, Offering.CostType costType, int costAmount, float deviance) implements DungeonComponent {
        public Offering asOffering(Level level) {
            int adjustedAmount = RandomUtil.randIntBetween((int) (amount / deviance), (int) (amount * deviance));
            int adjustedCost = RandomUtil.randIntBetween((int) (costAmount / deviance), (int) (costAmount * deviance));
            return new Offering(level, type, adjustedAmount, id, costType, adjustedCost);
        }

        public OfferingTemplate pool(WeightedPool<OfferingTemplate> pool, int weight) {pool.add(this, weight); return this;}
    }
}
