package com.danielkkrafft.wilddungeons.entity.attachmenttypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import java.util.UUID;

public class HomingTargetAttachmentType {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final HomingTargetAttachmentType EMPTY = new HomingTargetAttachmentType(Optional.empty(), 1);

    public static final MapCodec<HomingTargetAttachmentType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(UUID_CODEC.optionalFieldOf("Target").forGetter(HomingTargetAttachmentType::getTarget), Codec.INT.fieldOf("Level").forGetter(HomingTargetAttachmentType::getLevel)).apply(instance, HomingTargetAttachmentType::new));

    private Optional<UUID> target;
    private int level;

    public HomingTargetAttachmentType(Optional<UUID> target, int level) {
        this.target = target;
        this.level = level;
    }

    public Optional<UUID> getTarget() {
        return this.target;
    }

    public void setTarget(Optional<UUID> target) {
        this.target = target;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public HomingTargetAttachmentType copy() {
        return new HomingTargetAttachmentType(this.target, this.level);
    }

    public static HomingTargetAttachmentType getDefault() {
        return EMPTY;
    }
}