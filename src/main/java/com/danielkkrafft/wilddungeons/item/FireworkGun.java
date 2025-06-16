package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FireworkGun extends WDWeapon {

    public static final String NAME = "firework_gun";
    public enum AnimationList { idle, rotate }

    public FireworkGun() {
        super(NAME);
        this.addLoopingAnimation(AnimationList.idle.toString());
        this.addLoopingAnimation(AnimationList.rotate.toString());
    }

    @Override @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player p, @NotNull InteractionHand hand)
    {
        p.startUsingItem(hand);
        return InteractionResultHolder.pass(p.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            Inventory inv = player.getInventory();
            if (player.getCooldowns().isOnCooldown(this)) return;
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack ammoStack = inv.getItem(i);
                if (ammoStack.getItem().equals(Items.FIREWORK_ROCKET)) {
                    playAnimation(AnimationList.rotate.toString(),stack,player,level);
                    shoot(level,player,ammoStack,player.getYRot(),player.getXRot());
                    break;
                }
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    public void shoot(Level level, Player player, ItemStack fireworks, float yaw, float pitch)
    {
        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.PLAYERS, 0.8f, 0.7f);
        Vec3 vec = MathUtil.displaceVector(0.5f,player.getEyePosition(),yaw,pitch);
        FireworkRocketEntity firework=new FireworkRocketEntity(level,fireworks,player,vec.x,vec.y,vec.z,true);
        firework.setDeltaMovement(MathUtil.velocity3d(2,yaw,pitch));
        level.addFreshEntity(firework);
        level.playSound(null,player.blockPosition(),SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,0.8f,0.7f);
        level.addAlwaysVisibleParticle(ParticleTypes.SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
        player.getCooldowns().addCooldown(this,4);
        fireworks.setCount(fireworks.getCount()-1);
    }


    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it) {
        return UseAnim.NONE;
    }
}
