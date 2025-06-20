package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.DungeonPerk;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;

public class DungeonPerkTemplate implements DungeonRegistration.DungeonComponent {

    private String name;
    private final Class<? extends DungeonPerk> clazz;
    private final ResourceLocation texture;
    private boolean isUnique = false;
    private @Nullable Pair<Holder<MobEffect>, Integer> effectWithAmplifier; // Optional effect with amplifier, can be null


    public DungeonPerkTemplate(Class<? extends DungeonPerk> clazz, ResourceLocation texture) {
        this.name = clazz.getSimpleName();
        this.clazz = clazz;
        this.texture = texture;
    }

    public DungeonPerkTemplate(Holder<MobEffect> effect, int amplifier, ResourceLocation texture) {
        this(DungeonPerk.class, texture);
        this.effectWithAmplifier = Pair.of(effect, amplifier);
    }

    public DungeonPerkTemplate(Holder<MobEffect> effect, ResourceLocation texture) {
        this(effect, -1, texture);
    }

    public Class<? extends DungeonPerk> getClazz() {return this.clazz;}
    public boolean isUnique() {return isUnique;}
    public DungeonPerkTemplate setUnique() {isUnique = true; return this;}
    @Override public String name() {return this.name;}
    public DungeonPerkTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public DungeonPerk asPerk(String sessionKey) {
        try {
            return (DungeonPerk) this.clazz.getDeclaredConstructor(String.class, String.class).newInstance(sessionKey, name);
        } catch (Exception e) {
            WildDungeons.getLogger().error("Failed to create instance of DungeonRoom class for room template: {}", this.name);
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Holder<MobEffect> getEffect() {
        return effectWithAmplifier != null ? effectWithAmplifier.getFirst() : null;
    }

    public DungeonPerkTemplate setEffect(@Nullable Holder<MobEffect> effect) {
        if (this.effectWithAmplifier != null) {
            this.effectWithAmplifier = Pair.of(effect, this.effectWithAmplifier.getSecond());
        }
        return this;
    }

    public DungeonPerkTemplate setAmplifier(int amplifier) {
        if (this.effectWithAmplifier != null) {
            this.effectWithAmplifier = Pair.of(this.effectWithAmplifier.getFirst(), amplifier);
        }
        return this;
    }

    public DungeonPerkTemplate setEffectWithAmplifier(Holder<MobEffect> effect, int amplifier) {
        this.effectWithAmplifier = Pair.of(effect, amplifier);
        return this;
    }

    public int getAmplifier() {
        if (effectWithAmplifier != null) {
            return effectWithAmplifier.getSecond();
        }
        return -1;
    }

    public ResourceLocation getTextureLocation() {
        return this.texture;
    }
}
