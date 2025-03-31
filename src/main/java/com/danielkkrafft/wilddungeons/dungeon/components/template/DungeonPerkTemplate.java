package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.DungeonPerk;
import org.joml.Vector2i;

public class DungeonPerkTemplate implements DungeonRegistration.DungeonComponent {

    private final String name;
    private final Class<? extends DungeonPerk> clazz;
    private final Vector2i texCoords;
    private boolean isUnique = false;

    public DungeonPerkTemplate(Class<? extends DungeonPerk> clazz, Vector2i texCoords) {
        this.name = clazz.getSimpleName();
        this.clazz = clazz;
        this.texCoords = texCoords;
    }

    public Class<? extends DungeonPerk> getClazz() {return this.clazz;}
    public boolean isUnique() {return isUnique;}
    public DungeonPerkTemplate setUnique() {isUnique = true; return this;}
    @Override public String name() {return this.name;}

    public DungeonPerk asPerk(String sessionKey) {
        try {
            return (DungeonPerk) this.clazz.getDeclaredConstructor(String.class).newInstance(sessionKey);
        } catch (Exception e) {
            WildDungeons.getLogger().error("Failed to create instance of DungeonRoom class for room template: {}", this.name);
            e.printStackTrace();
            return null;
        }
    }

    public Vector2i getTexCoords() {return texCoords;}
}
