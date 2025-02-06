package com.danielkkrafft.wilddungeons.util.debug;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BedrockBlockstateHelper {
    public static void generateBedrockBlockstate() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Map<String, Object> variants = new HashMap<>();
        Map<String, Object> jsonData = new HashMap<>();

        Map<String, String> model = new HashMap<>();

        for (int i = 0; i < BuiltInRegistries.BLOCK.size(); i++) {
            ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(BuiltInRegistries.BLOCK.byId(i));
            model = new HashMap<>();
            model.put("model", resourceLocation.getNamespace() + ":block/" + resourceLocation.getPath());
            variants.put("mimic=" + i, model);
        }
        jsonData.put("variants", variants);

        String jsonContent = gson.toJson(jsonData);
        try {
            WildDungeons.getLogger().error("Writing bedrock blockstate to assets/" + WildDungeons.MODID + "/blockstates/wd_bedrock.json");
            //create the path
            Files.createDirectories(Paths.get(Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/assets/" + WildDungeons.MODID + "/blockstates/"));
            Files.write(Paths.get(Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/assets/" + WildDungeons.MODID + "/blockstates/wd_bedrock.json"), jsonContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
