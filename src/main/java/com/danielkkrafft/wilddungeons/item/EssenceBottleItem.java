package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.EssenceOrb;
import com.danielkkrafft.wilddungeons.entity.ThrownEssenceBottle;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

import static com.danielkkrafft.wilddungeons.registry.WDDataComponents.ESSENCE_TYPE;

public class EssenceBottleItem extends Item implements ProjectileItem {

    public EssenceBottleItem() {
        super(new Item.Properties());
    }

    public static ItemStack setEssenceType(ItemStack stack, EssenceOrb.Type type) {
        stack.set(ESSENCE_TYPE, type.ordinal());
        return stack;
    }

    public static EssenceOrb.Type getEssenceType(ItemStack stack) {
        return stack.has(ESSENCE_TYPE) ? EssenceOrb.Type.values()[stack.get(ESSENCE_TYPE)] : null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            ThrownEssenceBottle thrownEssenceBottle = new ThrownEssenceBottle(level, player);
            thrownEssenceBottle.setItem(itemstack);
            thrownEssenceBottle.essenceType = EssenceBottleItem.getEssenceType(itemstack);
            thrownEssenceBottle.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.7F, 1.0F);
            level.addFreshEntity(thrownEssenceBottle);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownExperienceBottle thrownexperiencebottle = new ThrownExperienceBottle(level, pos.x(), pos.y(), pos.z());
        thrownexperiencebottle.setItem(stack);
        return thrownexperiencebottle;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder().uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F).power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F).build();
    }

}
