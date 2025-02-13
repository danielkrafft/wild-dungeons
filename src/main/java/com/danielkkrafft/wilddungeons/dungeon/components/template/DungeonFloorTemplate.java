package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import net.minecraft.core.BlockPos;

import java.util.HashMap;

public final class DungeonFloorTemplate implements DungeonComponent {
    private String name;
    private DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates;
    private BlockPos origin = null;

    public HashMap<HierarchicalProperty<?>, Object> PROPERTIES = new HashMap<>();
    public <T> DungeonFloorTemplate set(HierarchicalProperty<T> property, T value) { this.PROPERTIES.put(property, value); return this; }
    public <T> T get(HierarchicalProperty<T> property) { return (T) this.PROPERTIES.get(property); }

    public static DungeonFloorTemplate create(String name) {
        return new DungeonFloorTemplate().setName(name);
    }

    public DungeonFloor placeInWorld(DungeonSession session, BlockPos position) {
        WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
        DungeonFloor newFloor = new DungeonFloor(this.name, session.getSessionKey(), position);
        newFloor.generateBranches();
        return newFloor;
    }

    @Override public String name() {
        return name;
    }
    public DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates() {
        return branchTemplates;
    }
    public BlockPos origin() {return this.origin;}

    public DungeonFloorTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonFloorTemplate setProperties(HashMap<HierarchicalProperty<?>, Object> prop) {this.PROPERTIES = prop; return this;}

    public DungeonFloorTemplate setBranchTemplates(DungeonRegistration.DungeonLayout<DungeonBranchTemplate> branchTemplates) {
        this.branchTemplates = branchTemplates;
        return this;
    }

    public DungeonFloorTemplate setOrigin(BlockPos origin) {
        this.origin = origin;
        return this;
    }

    public static DungeonFloorTemplate copyOf(DungeonFloorTemplate template, String newName) {
        return new DungeonFloorTemplate()
                .setName(newName)
                .setBranchTemplates(template.branchTemplates)
                .setOrigin(template.origin)
                .setProperties(template.PROPERTIES);
    }
}
