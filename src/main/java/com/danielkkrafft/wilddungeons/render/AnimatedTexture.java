package com.danielkkrafft.wilddungeons.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AnimatedTexture {
    private final List<ResourceLocation> frames;
    private final int frameTime;

    public AnimatedTexture(List<ResourceLocation> frames, int frameTime) {
        this.frames = frames;
        this.frameTime = frameTime;
    }

    public ResourceLocation getCurrentFrame() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return frames.get(0);
        }

        long tickCount = level.getGameTime();
        int index = (int)((tickCount) / frameTime) % frames.size();
        return frames.get(index);
    }
}
