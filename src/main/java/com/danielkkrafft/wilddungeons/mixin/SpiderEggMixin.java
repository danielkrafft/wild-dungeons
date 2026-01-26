package com.danielkkrafft.wilddungeons.mixin;


import com.danielkkrafft.wilddungeons.entity.Spiderling;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.danielkkrafft.wilddungeons.block.SpiderEggSacBlock.EGGS;

@Mixin(Spider.class)
public class SpiderEggMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        Spider spider = (Spider) (Object) this;
        if (spider instanceof Spiderling) return;// Only adult spiders
        Level level = spider.level();

        // Random chance to place an egg block
        if (!level.isClientSide && level.random.nextInt(12000) == 0) { // 1/12000 chance per tick
            BlockPos posBelow = spider.blockPosition();
            BlockState eggBlock = WDBlocks.SPIDER_EGG.get().defaultBlockState();
            int eggs = spider.level().random.nextInt(8);
            eggBlock = eggBlock.setValue(EGGS, eggs);

            // Check if the block below is replaceable
            if (level.isEmptyBlock(posBelow) && eggBlock.canSurvive(level, posBelow)) {
                level.playSound(spider,posBelow, SoundEvents.SLIME_SQUISH, spider.getSoundSource(), 0.5F, 1.0F);
                level.setBlock(posBelow, eggBlock, 3);
            }
        }
    }
}