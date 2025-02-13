package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;

import java.util.HashMap;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry.TEST_FLOOR_POOL;

public final class DungeonTemplate implements DungeonComponent {

    private String name;
    private DungeonLayout<DungeonFloorTemplate> floorTemplates = new DungeonLayout<DungeonFloorTemplate>().add(TEST_FLOOR_POOL, 1);

    public final HashMap<HierarchicalProperty<?>, Object> PROPERTIES = new HashMap<>();
    public <T> DungeonTemplate set(HierarchicalProperty<T> property, T value) { this.PROPERTIES.put(property, value); return this; }
    public <T> T get(HierarchicalProperty<T> property) { return this.PROPERTIES.containsKey(property) ? (T) this.PROPERTIES.get(property) : property.getDefaultValue(); }

    public static DungeonTemplate create(String name) {
        return new DungeonTemplate().setName(name);
    }

    @Override public String name() {
        return name;
    }
    public DungeonLayout<DungeonFloorTemplate> floorTemplates() {
        return floorTemplates;
    }
    public DungeonTemplate setName(String name) {this.name = name;return this;}
    public DungeonTemplate setFloorTemplates(DungeonLayout<DungeonFloorTemplate> floorTemplates) {this.floorTemplates = floorTemplates;return this;}
}