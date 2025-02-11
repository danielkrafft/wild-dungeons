package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.item.renderer.FireworkGunRenderer;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FireworkGun extends ProjectileWeaponItem implements GeoAnimatable, GeoItem
{
    private static final String CONTROLLER = "fireworkgun_controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop(FireworkGunRenderer.idleAnim);
    private static final RawAnimation ROTATE_ANIM = RawAnimation.begin().thenLoop(FireworkGunRenderer.rotateAnim);

    private static final Predicate<ItemStack>projectiles=it->it.getItem().equals(Items.FIREWORK_ROCKET);

    public FireworkGun()
    {
        super(new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(500)
        );
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new FireworkGunRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() { return this.renderer; }
        });
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
                level.playSound(null,p.blockPosition(),SoundEvents.CROSSBOW_LOADING_START.value(),SoundSource.PLAYERS,0.8f,0.7f);
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

    @Override
    public void inventoryTick(@NotNull ItemStack it,@NotNull Level level,@NotNull Entity en, int slot, boolean inMainHand)
    {
        if(en instanceof Player p)
        {
            ItemCooldowns cds=p.getCooldowns();
            if(!cds.isOnCooldown(this))setAnimation(FireworkGunRenderer.idleAnim,it,p,level);
        }
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

    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {return 100000;}
    @Override@NotNull public UseAnim getUseAnimation(@NotNull ItemStack it){return UseAnim.BOW;}
    @Override @NotNull public Predicate<ItemStack> getAllSupportedProjectiles() {return projectiles;}
    @Override public int getDefaultProjectileRange() {return 8;}
    @Override protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {}



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<GeoAnimatable>(this, CONTROLLER, 1, state -> PlayState.CONTINUE)
                .triggerableAnim(FireworkGunRenderer.idleAnim, IDLE_ANIM)
                .triggerableAnim(FireworkGunRenderer.rotateAnim, ROTATE_ANIM)
        );
    }

    private void setAnimation(String anim, ItemStack stack, Player player, Level level)
    {
        if(level instanceof ServerLevel serverLevel)
        {
            triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), CONTROLLER, anim);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
