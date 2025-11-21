package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.enchantment.WDEnchantments;
import com.danielkkrafft.wilddungeons.entity.renderer.WarSpearRenderer;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public class WarSpearItem extends WDWeapon {

    private final SpearType type;

    public WarSpearItem(SpearType type, Properties properties) {
        super("war_spear" ,properties);
        this.type = type;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final GeoItemRenderer<?> renderer = new WarSpearRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        ItemStack stack = player.getItemInHand(usedHand);
        int lunge = stack.getEnchantmentLevel(WildDungeons.getEnchantment(WDEnchantments.LUNGE));
        if (lunge > 0 && !player.getCooldowns().isOnCooldown(this)) {
            double scale = 1f;
            switch (lunge) {
                case 1 : scale = 0.8f;
                case 2 : scale = 1.f;
                case 3 : scale = 1.5f;
            }
            player.causeFoodExhaustion(0.5f);
            player.addDeltaMovement(player.getLookAngle().scale(scale).multiply(1f,0f,1f));
            player.getCooldowns().addCooldown(this,40);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }
    public int getUseDuration(ItemStack stack, LivingEntity entity) {return 72000;}

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }


    public SpearType getType() {
        return this.type;
    }

    public enum SpearType {
        WOOD,
        STONE,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE,
        HEAVY
    }

}
