package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.item.EssenceBottleItem;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class ThrownEssenceBottle extends ThrowableItemProjectile implements IEntityWithComplexSpawn {

    public EssenceOrb.Type essenceType = null;

    public ThrownEssenceBottle(EntityType<ThrownEssenceBottle> entityType, Level level) {
        super(WDEntities.ESSENCE_BOTTLE.get(), level);
    }

    public ThrownEssenceBottle(Level level, LivingEntity shooter) {
        super(WDEntities.ESSENCE_BOTTLE.get(), shooter, level);
    }

    public ThrownEssenceBottle(Level level, double x, double y, double z) {
        super(WDEntities.ESSENCE_BOTTLE.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return WDItems.ESSENCE_BOTTLE.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.07;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel) {
            this.level().levelEvent(2002, this.blockPosition(), PotionContents.getColor(Potions.WATER));
            int i = 3 + this.level().random.nextInt(5) + this.level().random.nextInt(5);
            EssenceOrb.award((ServerLevel) this.level(), this.position(), this.essenceType, i);
            this.discard();
        }

    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(this.essenceType.ordinal());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this.essenceType = EssenceOrb.Type.values()[registryFriendlyByteBuf.readInt()];
    }
}
