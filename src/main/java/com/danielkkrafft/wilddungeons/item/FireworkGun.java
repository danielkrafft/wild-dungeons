package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        Inventory inv=p.getInventory();
        for(int i=0;i<inv.getContainerSize();i++)
        {
            ItemStack stack=inv.getItem(i);
            if(stack.getItem().equals(Items.FIREWORK_ROCKET))
            {
                level.playSound(null,p.blockPosition(), SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.PLAYERS,0.8f,0.7f);
                fireworkBurst(level,p.getItemInHand(hand),p);
                break;
            }
        }
        return InteractionResultHolder.pass(p.getItemInHand(hand));
    }

    private void fireworkBurst(Level level,ItemStack stack,Player p)
    {
        Inventory inv=p.getInventory();
        for(int i=0;i<inv.getContainerSize();i++)
        {
            ItemStack it=inv.getItem(i);
            if(it.getItem().equals(Items.FIREWORK_ROCKET))
            {
                shoot(level,p,it,p.getYRot(),p.getXRot());
                inv.removeItem(i,1);
                break;
            }
        }

        setAnimation(FireworkGunRenderer.rotateAnim,stack,p,level);
        p.getCooldowns().addCooldown(this,1);
    }

    public void shoot(Level level, Player p, ItemStack fireworks, float yaw, float pitch)
    {
        Vec3 vec = MathUtil.displaceVector(0.5f,p.getEyePosition(),yaw,pitch);
        FireworkRocketEntity firework=new FireworkRocketEntity(level,fireworks,p,vec.x,vec.y,vec.z,true);
        firework.setDeltaMovement(MathUtil.velocity3d(4,yaw,pitch));
        level.addFreshEntity(firework);
        level.playSound(null,p.blockPosition(),SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,0.8f,0.7f);
        level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
    }
}
