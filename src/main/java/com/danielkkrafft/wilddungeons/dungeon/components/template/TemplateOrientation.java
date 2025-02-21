package com.danielkkrafft.wilddungeons.dungeon.components.template;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.Objects;

public class TemplateOrientation {
    public static final TemplateOrientation EMPTY = new TemplateOrientation();
    private Mirror mirror;
    private Rotation rotation;

    public TemplateOrientation() {
        mirror = Mirror.NONE;
        rotation = Rotation.NONE;
    }

    public TemplateOrientation setMirror(Mirror mirror) {this.mirror = mirror; return this;}
    public TemplateOrientation setRotation(Rotation rotation) {this.rotation = rotation; return this;}
    public Mirror getMirror() {return this.mirror;}
    public Rotation getRotation() {return this.rotation;}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateOrientation other)) return false;
        return other.mirror.equals(this.mirror) && other.rotation.equals(this.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mirror, rotation);
    }
}
