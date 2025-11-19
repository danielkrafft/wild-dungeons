package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.attachmenttypes.HomingTargetAttachmentType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class WDAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WildDungeons.MODID);


    public static final Supplier<AttachmentType<HomingTargetAttachmentType>> HOMING_ATTACHMENT =
            ATTACHMENT_TYPES.register("homing_attachment",
                    () -> AttachmentType.builder(() -> new HomingTargetAttachmentType(Optional.empty(),1))
                            .serialize(HomingTargetAttachmentType.CODEC).build());
}
