package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import org.joml.Vector2i;

public class DungeonPerkTemplate implements DungeonComponent {

    private final String name;
    private final Vector2i texCoords;

    public DungeonPerkTemplate(String name, Vector2i texCoords) {
        this.name = name;
        this.texCoords = texCoords;
    }

    @Override
    public String name() {return this.name;}

    public static void onPerk(String perkId, DungeonSession session) {
        switch (perkId) {
            case "EXTRA_LIFE" -> session.offsetLives(1);
        }
    }

    public Vector2i getTexCoords() {return texCoords;}
}
