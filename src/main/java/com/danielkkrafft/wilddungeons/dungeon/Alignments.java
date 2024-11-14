package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class Alignments {

    public static final HashMap<String, Alignment> ALIGNMENTS = new HashMap<>();

    public static void setupAlignments() {

        ALIGNMENTS.put("nether", new Alignment(
                WildDungeons.rl("hud/nether_essence_bar_background"),
                WildDungeons.rl("hud/nether_essence_bar_progress"),
                0xfa4b4b,
                270
        ));

        ALIGNMENTS.put("end", new Alignment(
                WildDungeons.rl("hud/end_essence_bar_background"),
                WildDungeons.rl("hud/end_essence_bar_progress"),
                0xfa4bda,
                135
        ));

    }

    public record Alignment(
            ResourceLocation ESSENCE_BAR_BACKGROUND_SPRITE,
            ResourceLocation ESSENCE_BAR_PROGRESS_SPRITE,
            int FONT_COLOR,
            int ORB_HUE_OFFSET
    ) {}
}
