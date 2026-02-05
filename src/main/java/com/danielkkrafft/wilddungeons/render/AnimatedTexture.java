package com.danielkkrafft.wilddungeons.render;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AnimatedTexture {
    private final List<Identifier> frames;
    private final int frameTime;

    public AnimatedTexture(List<Identifier> frames, int frameTime) {
        this.frames = frames;
        this.frameTime = frameTime;
    }

    public Identifier getCurrentFrame() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return frames.get(0);
        }

        long tickCount = level.getGameTime();
        int index = (int)((tickCount) / frameTime) % frames.size();
        return frames.get(index);
    }

    //Must follow 0001.png format
    public static AnimatedTexture auto(String path, int frames, int frameTime) {
        List<Identifier> list = new ArrayList<>();
        for (int i = 1; i <= frames; i++) {
            list.add(WildDungeons.rl(path+"/"+String.format("%04d", i)+".png"));
        }
        return new AnimatedTexture(list, frameTime);
    }
}
