package com.danielkkrafft.wilddungeons.item;

import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.item.itemhelpers.WDWeapon;
import com.danielkkrafft.wilddungeons.registry.WDDataComponents;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindCannon extends WDWeapon {

    public static final String NAME = "wind_cannon";
    public static final String COMPRESS_DATA = "compress_data";

    private enum AnimationList {
        compress
    }

    public WindCannon() {
        super(
                NAME,
                new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .durability(2000)
                        .stacksTo(1));

        animator.addAnimation(AnimationList.compress.toString());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity en, int slotIndex, boolean held)
    {
        if(held)
        {
            int c=getCompressions(stack);
            if(c>=10)
            {
                if(en.tickCount%20==0)
                {
                    en.playSound(WDSoundEvents.WIND_CANNON_RUMBLE.value(),0.4f,1);
                }
            }
        }
    }

    @Override
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!(entity instanceof Player player)) {
            return super.onEntitySwing(stack, entity, hand);
        }
        if (getCompressions(stack) > 0) {
            if (player.getCooldowns().getCooldownPercent(this, (Minecraft.getInstance().getFrameTimeNs() / 50_000_000f)) <= 0) {
                launchWindCharge(player, stack);
            }
        }
        return super.onEntitySwing(stack, entity, hand);
    }

    public void launchWindCharge(@NotNull LivingEntity entity, @NotNull ItemStack stack)
    {
//        WildDungeons.getLogger().info("LaunchWindCharge");
        int comps = getCompressions(stack);
        Level lvl = entity.level();
        stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        lvl.playSound(null, entity, WDSoundEvents.WIND_CANNON_SHOOT.value(), SoundSource.PLAYERS, 1.0F,  1.0F);
        Vec3 pos = entity.position();

        if(lvl instanceof ServerLevel server) {
            UtilityMethods.sendParticles(server, ParticleTypes.CLOUD, true, Math.min(15 * comps + 1, 60), pos.x, entity.getEyeY(), pos.z, 0.025f, 0.025f, 0.025f, Math.min(comps / 5f + 0.002f, 0.15f));
            WindChargeProjectile wind = WDEntities.WIND_CHARGE_PROJECTILE.value().create(lvl);

            if (wind != null) {
                Vec3 eyePos = entity.getEyePosition();
                Vec3 launchDir = entity.getLookAngle().normalize();
                Vec3 spawnPos = eyePos.add(launchDir.scale(1.5)); // Spawns half a block ahead of eyes

                wind.setPos(spawnPos);
                //wind.setDeltaMovement(launchDir.scale(1.5)); // or whatever velocity you want
                wind.hasImpulse = true;
                wind.setCompressions(false, false, launchDir.scale(1.5), comps, entity);
                lvl.addFreshEntity(wind);
            }
        }

        double vel = Math.min(comps / 3. ,2);
        entity.setDeltaMovement(Vec3.directionFromRotation(0,entity.getYHeadRot()).scale(-1).multiply(vel,0,vel).add(0,vel,0));
        stack.set(WDDataComponents.WIND_CANNON_COMPRESSION.get(), 0);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player p)
    {
        return !p.getAbilities().instabuild;
    }

    @Override@NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        boolean canCharge = player.getAbilities().instabuild;
        if(!canCharge)
        {
            Inventory inv=player.getInventory();
            int size=inv.getContainerSize();
            ItemStack stack;
            for(int i = 0; i < size; i++)
            {
                stack=inv.getItem(i);
                if(stack.getItem() instanceof WindChargeItem)
                {
                    stack.shrink(1);
                    canCharge=true;
                    break;
                }
            }
        }
        if(canCharge)
        {
            ItemStack it = player.getItemInHand(hand);
            int c = Math.min(getCompressions(it),20);

            if (level instanceof ServerLevel server)
            {
                if(c >= 20)
                {
                    Holder<Enchantment> unbreakingHolder = server.registryAccess()
                            .registryOrThrow(Registries.ENCHANTMENT)
                            .getHolderOrThrow(Enchantments.UNBREAKING);

                    int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(unbreakingHolder, player);

                    if(it.isDamageableItem() && enchantmentLevel == 0 && UtilityMethods.RNG( 0, 9) == 0)
                    {
                        it.set(WDDataComponents.WIND_CANNON_COMPRESSION.get(), 0);
                        if(!player.getAbilities().instabuild)it.shrink(1);
                        player.hurt(player.damageSources().mobAttack(player),10);

                        server.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.GENERIC_EXPLODE,
                                SoundSource.PLAYERS,
                                1f,
                                1.5f);

                        WindChargeProjectile wind= WDEntities.WIND_CHARGE_PROJECTILE.get().create(server);
                        if(wind!=null)
                        {
                            wind.setCompressions(true,false,new Vec3(2.5,2.5,2.5),c,player);
                            server.addFreshEntity(wind);
                        }
                    }
                }
                if(c >= 10)
                {
                    server.playSound(null,player,SoundEvents.GENERIC_EXTINGUISH_FIRE,SoundSource.PLAYERS,1f,2);
                    UtilityMethods.sendParticles(server,ParticleTypes.CLOUD,true,c/4,player.getX(),player.getEyeY()-0.1,player.getZ(),0,0,0,0.3f);
                }

                server.playSound(null,player,WDSoundEvents.WIND_CANNON_RELOAD.value(), SoundSource.PLAYERS,1f,1+(0.5f*c/10f));

                animator.playAnimation(this, AnimationList.compress.toString(), it, player, level);
                animator.setAnimationSpeed((float) (1+(c/10.*0.5)),level);

                addCompression(player.getItemInHand(hand));
            }
            player.getCooldowns().addCooldown(this,(int)(20-c/2f));
        }
        return super.use(level,player,hand);
    }

    public static void addCompression(@NotNull ItemStack stack)
    {
        int current = stack.getOrDefault(WDDataComponents.WIND_CANNON_COMPRESSION.get(), 0);
        stack.set(WDDataComponents.WIND_CANNON_COMPRESSION.get(), current + 1);
    }

    public static int getCompressions(@NotNull ItemStack stack)
    {
        return stack.getOrDefault(WDDataComponents.WIND_CANNON_COMPRESSION.get(), 0);
    }
}
