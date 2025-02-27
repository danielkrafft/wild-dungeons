package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import org.joml.Vector2i;

public class DungeonPerkTemplate implements DungeonRegistration.DungeonComponent {

    private final String name;
    private final Vector2i texCoords;
    private boolean isUnique = false;
    private boolean isPotionEffect = false;

    public DungeonPerkTemplate(String name, Vector2i texCoords) {
        this.name = name;
        this.texCoords = texCoords;
    }

    public boolean isUnique() {return isUnique;}
    public DungeonPerkTemplate setUnique() {isUnique = true; return this;}
    public boolean isPotionEffect() {return isPotionEffect;}
    public DungeonPerkTemplate setPotionEffect() {isPotionEffect = true; return this;}
    @Override
    public String name() {return this.name;}

    public Vector2i getTexCoords() {return texCoords;}
}
