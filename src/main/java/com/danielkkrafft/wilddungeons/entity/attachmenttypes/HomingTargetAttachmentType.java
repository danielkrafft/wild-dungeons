package com.danielkkrafft.wilddungeons.entity.attachmenttypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class HomingTargetAttachmentType {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final HomingTargetAttachmentType EMPTY = new HomingTargetAttachmentType(Optional.empty(),1);
        public static final Codec<HomingTargetAttachmentType> CODEC = RecordCodecBuilder.create(questInstance ->
                questInstance.group(
                        Codec.optionalField("Taget", UUID_CODEC,false).forGetter(HomingTargetAttachmentType::getTarget),
                        Codec.INT.fieldOf("Level").forGetter(HomingTargetAttachmentType::getLevel)
                ).apply(questInstance, HomingTargetAttachmentType::new));

        public static final StreamCodec<ByteBuf, HomingTargetAttachmentType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);


        private Optional<UUID> target;
        private int level;

        public HomingTargetAttachmentType(Optional<UUID> stack, int level) {
            this.target = stack;
            this.level= level;
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
            return new HomingTargetAttachmentType(this.target,this.level);
    }

    public static HomingTargetAttachmentType getDefault() {
            return new HomingTargetAttachmentType(Optional.empty(),1);
        }



}
