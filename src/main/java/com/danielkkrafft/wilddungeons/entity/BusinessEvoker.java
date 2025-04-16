package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;

public class BusinessEvoker extends Evoker {
    private static final int LARGE_WISP_CAP = 3;

    public BusinessEvoker(EntityType<? extends Evoker> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6, 1.0F));
        this.goalSelector.addGoal(4, new BusinessEvokerSummonSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(300));

    }

    class BusinessEvokerSummonSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions wispCountTargeting = TargetingConditions.forNonCombat()
                .range(16.0F)
                .ignoreLineOfSight()
                .ignoreInvisibilityTesting();

        BusinessEvokerSummonSpellGoal() {
            super();
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }

            // Check if we have room for more wisps
            int largeWispCount = BusinessEvoker.this.level().getNearbyEntities(
                    LargeEmeraldWisp.class, this.wispCountTargeting, BusinessEvoker.this,
                    BusinessEvoker.this.getBoundingBox().inflate(32)).size();

            // Only allow summoning if we're under the large wisp cap
            return largeWispCount < LARGE_WISP_CAP;
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected void performSpellCasting() {
            ServerLevel serverLevel = (ServerLevel) BusinessEvoker.this.level();
            PlayerTeam playerTeam = BusinessEvoker.this.getTeam();

            // Count current large wisps
            int largeWispCount = BusinessEvoker.this.level().getNearbyEntities(
                    LargeEmeraldWisp.class, this.wispCountTargeting, BusinessEvoker.this,
                    BusinessEvoker.this.getBoundingBox().inflate(32f)).size();

            // Summon 2 wisps
            for (int i = 0; i < 2; ++i) {
                BlockPos blockPos = BusinessEvoker.this.blockPosition()
                        .offset(-2 + BusinessEvoker.this.random.nextInt(5),
                                1,
                                -2 + BusinessEvoker.this.random.nextInt(5));

                // 20% chance for large wisp, 80% for small wisp, respecting the cap
                boolean summonLargeWisp = BusinessEvoker.this.random.nextFloat() < 0.2F
                        && largeWispCount < LARGE_WISP_CAP;

                EmeraldWisp wisp;
                if (summonLargeWisp) {
                    wisp = WDEntities.LARGE_EMERALD_WISP.get().create(BusinessEvoker.this.level());
                    largeWispCount++; // Increment count for next iteration
                } else {
                    wisp = WDEntities.SMALL_EMERALD_WISP.get().create(BusinessEvoker.this.level());
                }

                if (wisp != null) {
                    wisp.setPos(blockPos.getCenter());
                    wisp.moveTo(blockPos, 0.0F, 0.0F);
                    wisp.setOwner(BusinessEvoker.this);
                    wisp.setTarget(BusinessEvoker.this.getTarget());

                    // Add to team if we have one
                    if (playerTeam != null) {
                        serverLevel.getScoreboard().addPlayerToTeam(wisp.getScoreboardName(), playerTeam);
                    }

                    serverLevel.addFreshEntityWithPassengers(wisp);
                    serverLevel.gameEvent(GameEvent.ENTITY_PLACE, blockPos,
                            GameEvent.Context.of(BusinessEvoker.this));
                }
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcasterIllager.@NotNull IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }

    // TODO: Implement dungeon room detection logic to influence large wisp cap
}