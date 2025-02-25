package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import org.joml.Vector2i;

public class DungeonPerkTemplate implements DungeonRegistration.DungeonComponent {

    private final String name;
    private final Vector2i texCoords;

    public DungeonPerkTemplate(String name, Vector2i texCoords) {
        this.name = name;
        this.texCoords = texCoords;
    }

    @Override
    public String name() {return this.name;}

    public Vector2i getTexCoords() {return texCoords;}
}
