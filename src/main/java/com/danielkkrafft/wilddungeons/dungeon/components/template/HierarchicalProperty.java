package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry;
import com.danielkkrafft.wilddungeons.dungeon.registries.SoundscapeTemplateRegistry;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.google.gson.reflect.TypeToken;

import static com.danielkkrafft.wilddungeons.dungeon.registries.EnemyTableRegistry.BASIC_ENEMY_TABLE;

public class HierarchicalProperty<T> {

    public static final HierarchicalProperty<WeightedPool<DungeonMaterial>> MATERIAL = new HierarchicalProperty<>(new TypeToken<>() {}, DungeonMaterialPoolRegistry.ALL_MATERIAL_POOL);
    public static final HierarchicalProperty<String> DISPLAY_NAME = new HierarchicalProperty<>(new TypeToken<>() {}, "Unknown Area");
    public static final HierarchicalProperty<String> ICON = new HierarchicalProperty<>(new TypeToken<>() {}, "?-?");
    public static final HierarchicalProperty<Integer> PRIMARY_COLOR = new HierarchicalProperty<>(new TypeToken<>() {}, 0xFFFFFFFF);
    public static final HierarchicalProperty<Integer> SECONDARY_COLOR = new HierarchicalProperty<>(new TypeToken<>() {}, 0xFFFFFFFF);
    public static final HierarchicalProperty<Integer> TARGET_TIME = new HierarchicalProperty<>(new TypeToken<>() {}, 12000);
    public static final HierarchicalProperty<Integer> TARGET_DEATHS = new HierarchicalProperty<>(new TypeToken<>() {}, 0);
    public static final HierarchicalProperty<Integer> TARGET_SCORE = new HierarchicalProperty<>(new TypeToken<>() {}, 100000);
    public static final HierarchicalProperty<String> OPEN_BEHAVIOR = new HierarchicalProperty<>(new TypeToken<>() {}, DungeonOpenBehavior.NONE);
    public static final HierarchicalProperty<WeightedTable<DungeonRegistration.TargetTemplate>> ENEMY_TABLE = new HierarchicalProperty<>(new TypeToken<>() {}, BASIC_ENEMY_TABLE);
    public static final HierarchicalProperty<Double> DIFFICULTY_MODIFIER = new HierarchicalProperty<>(new TypeToken<>() {}, 1.0);
    public static final HierarchicalProperty<Double> DIFFICULTY_SCALING = new HierarchicalProperty<>(new TypeToken<>() {}, 1.1);
    public static final HierarchicalProperty<DungeonSession.DungeonExitBehavior> EXIT_BEHAVIOR = new HierarchicalProperty<>(new TypeToken<>() {}, DungeonSession.DungeonExitBehavior.DESTROY);
    public static final HierarchicalProperty<WeightedPool<DungeonTemplate>> NEXT_DUNGEON = new HierarchicalProperty<>(new TypeToken<>() {}, null);
    public static final HierarchicalProperty<Boolean> HAS_BEDROCK_SHELL = new HierarchicalProperty<>(new TypeToken<>() {}, true);
    public static final HierarchicalProperty<DungeonRoomTemplate.DestructionRule> DESTRUCTION_RULE = new HierarchicalProperty<>(new TypeToken<>() {}, DungeonRoomTemplate.DestructionRule.NONE);
    public static final HierarchicalProperty<Integer> BLOCKING_MATERIAL_INDEX = new HierarchicalProperty<>(new TypeToken<>() {}, 0); //TODO replace with Blocked Blockstate in the connection block entity
    public static final HierarchicalProperty<Integer> WAVE_SIZE = new HierarchicalProperty<>(new TypeToken<>() {}, 10);
    public static final HierarchicalProperty<Integer> INTENSITY = new HierarchicalProperty<>(new TypeToken<>() {}, 0);
    public static final HierarchicalProperty<DungeonRegistration.SoundscapeTemplate> SOUNDSCAPE = new HierarchicalProperty<>(new TypeToken<>() {}, SoundscapeTemplateRegistry.NETHER_CAVES);


    private final TypeToken<T> type;
    private final T defaultValue;

    public HierarchicalProperty(TypeToken<T> type, T defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public TypeToken<T> getType() {
        return type;
    }
    public T getDefaultValue() {return this.defaultValue;}
}
