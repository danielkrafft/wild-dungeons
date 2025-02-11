package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.Laserbeam;
import com.danielkkrafft.wilddungeons.item.renderer.LaserSwordRenderer;
import com.danielkkrafft.wilddungeons.util.MathUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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

public class LaserSword extends SwordItem implements GeoAnimatable, GeoItem
{
    private static final String CONTROLLER = "lasersword_controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.idleAnim);
    private static final RawAnimation TRANSFORM_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.transformAnim);
    private static final RawAnimation CHARGE_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.chargeAnim);
    private static final RawAnimation FULL_CHARGE_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.fullChargeAnim);
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.shootAnim);
    private static final RawAnimation TRANSFORM_BACK_ANIM = RawAnimation.begin().thenLoop(LaserSwordRenderer.transformBackAnim);

    static final Tier swordTier = new Tier() {
        @Override
        public int getUses() {return 0;}
        @Override
        public float getSpeed() {return 0;}
        @Override
        public float getAttackDamageBonus() {return 0;}

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return null;
        }
        @Override
        public int getEnchantmentValue(){return 5;}
        @Override
        public Ingredient getRepairIngredient() {return null;}
    };

    public LaserSword()
    {
        super(swordTier, new Item.Properties()
                .rarity(Rarity.RARE)
                .durability(1000)
        );
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new LaserSwordRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() { return this.renderer; }
        });
    }

    @Override
    public void inventoryTick(@NotNull ItemStack it, @NotNull Level level, @NotNull Entity en, int slot, boolean inMainHand)
    {
        if(en instanceof Player p)
        {
            ItemCooldowns cds=p.getCooldowns();
            boolean hasCooldown=cds.isOnCooldown(this);
            if(p.isUsingItem()||hasCooldown)
            {
                if(hasCooldown)
                {
                    if(cds.getCooldownPercent(this,0)<=0.769f)setAnimation(LaserSwordRenderer.transformBackAnim, it, p, level);
                    else setAnimation(LaserSwordRenderer.shootAnim,it,p,level);
                }
            }
            else setAnimation(LaserSwordRenderer.idleAnim, it, p, level);
        }
    }
    @Override @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player p, @NotNull InteractionHand hand)
    {
        ItemStack it=p.getItemInHand(hand);
        p.startUsingItem(hand);return InteractionResultHolder.consume(it);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if(livingEntity instanceof Player p)
        {
            int charge=getUseDuration(stack, livingEntity)-remainingUseDuration;
            if(charge==0)setAnimation(LaserSwordRenderer.transformAnim, stack, p, p.level());
            if(charge==100)setAnimation(LaserSwordRenderer.chargeAnim, stack, p, p.level());
            if(charge==(15*20)+100)setAnimation(LaserSwordRenderer.fullChargeAnim, stack, p, p.level());
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack it, @NotNull Level level, @NotNull LivingEntity le, int count)
    {
        //LogUtils.getLogger().info("Count:"+count);
        if(le instanceof Player p)
        {
            int charge=getUseDuration(it, le)-count-100;
            if(charge>=0)
            {
                int d=0;
                float dmg,rad=0.5f,range,explosionradius=0;
                boolean explosion=false,debris=false;
                if(charge<20)
                {
                    dmg=0.75f;
                    range=15;
                }
                else if(charge<3*20)
                {
                    d=1;
                    dmg=1.5f;
                    range=25;
                }
                else if(charge<6*20)
                {
                    d=2;
                    dmg=3f;
                    range=40;
                    explosion=true;
                    explosionradius=2;
                }
                else if(charge<10*20)
                {
                    d=3;
                    dmg=5f;
                    range=60;
                    explosion=true;
                    explosionradius=5;
                }
                else if(charge<15*20)
                {
                    d=4;
                    dmg=10f;
                    range=80;
                    explosion=true;
                    explosionradius=15;
                    debris=true;
                }
                else
                {
                    d=5;
                    dmg=20f;
                    range=160;
                    explosion=true;
                    explosionradius=30;
                    debris=true;
                }
                if(!p.isCreative())it.setDamageValue(it.getDamageValue()+d);
                p.getCooldowns().addCooldown(this,(int)(6.5*20));
                shoot(d,level,p,dmg,rad,range,explosion,explosionradius,debris);
                setAnimation(LaserSwordRenderer.shootAnim, it, p, level);
            }
        }
    }
    private void shoot(int lvl,Level level, Player p,float damage,float radius,float range,boolean explosion,float explosionradius,boolean debris)
    {
        float yaw=p.getYRot(),pitch=p.getXRot();
        Vec3 vec = MathUtil.displaceVector(0.5f,p.getEyePosition(),yaw,pitch);
        level.addFreshEntity(new Laserbeam(p,vec,p.getYRot(),p.getXRot(),damage,radius,range,explosion,explosionradius,debris));
        Vec3 oppositeLook = MathUtil.velocity3d(1,p.getYRot()+180,-p.getXRot());
        switch(lvl)
        {
            case 0-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(0.4,0.4,0.4)));
                for(int i=0;i<5;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.playSound(null, p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,2f);
                level.playSound(null, p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.5f, 2f);
            }
            case 1-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(0.7,0.7,0.7)));
                for(int i=0;i<7;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,1.5f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,1.6f);
            }
            case 2-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(1.3,1.3,1.3)));
                for(int i=0;i<11;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,1f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,1.4f);
            }
            case 3-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(1.8,1.8,1.8)));
                for(int i=0;i<14;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION,true,vec.x,vec.y,vec.z,0,0,0);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,1f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,1.25f);
            }
            case 4-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(2,2,2)));
                for(int i=0;i<17;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION,true,vec.x,vec.y,vec.z,0,0,0);
                level.addAlwaysVisibleParticle(ParticleTypes.SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,0.9f);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,2f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,0.9f);
            }
            case 5-> {
                p.setDeltaMovement(p.getDeltaMovement().add(oppositeLook.multiply(2.7,2.7,2.7)));
                for(int i=0;i<20;i++)level.addAlwaysVisibleParticle(ParticleTypes.POOF,true,vec.x,vec.y,vec.z,0.005f,0.005f,0.005f);
                level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION_EMITTER,true,vec.x,vec.y,vec.z,0,0,0);
                level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE,true,vec.x,vec.y,vec.z,0,0,0);
                level.playSound(null,p.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,0.5f,0.7f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,0.8f);
                level.playSound(null,p.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS,1f,0.75f);
            }
        }
    }

    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {return 100000;}
    @Override public @NotNull UseAnim getUseAnimation(@NotNull ItemStack it){return UseAnim.BOW;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, CONTROLLER,1, state -> PlayState.CONTINUE)
                .triggerableAnim(LaserSwordRenderer.idleAnim, IDLE_ANIM)
                .triggerableAnim(LaserSwordRenderer.transformAnim, TRANSFORM_ANIM)
                .triggerableAnim(LaserSwordRenderer.chargeAnim, CHARGE_ANIM)
                .triggerableAnim(LaserSwordRenderer.fullChargeAnim, FULL_CHARGE_ANIM)
                .triggerableAnim(LaserSwordRenderer.shootAnim, SHOOT_ANIM)
                .triggerableAnim(LaserSwordRenderer.transformBackAnim, TRANSFORM_BACK_ANIM)
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
