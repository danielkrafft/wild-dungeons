package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;

import java.io.File;
import java.util.Map;

public class SaveFile {
    public static final SaveFile INSTANCE = new SaveFile();

    private Map<String, WDPlayer> players = null;

    public void save() {
        this.players = WDPlayerManager.getInstance().getPlayers();

        FileUtil.writeNbt(Serializer.toCompoundTag(this), getSaveFile());
    }

    public void load() {
        SaveFile saveFile = Serializer.fromCompoundTag(FileUtil.readNbt(getSaveFile()));
        if (saveFile == null) return;

        if (saveFile.players != null) WDPlayerManager.getInstance().setPlayers(saveFile.players);
    }

    public File getSaveFile() {
        return FileUtil.getWorldPath().resolve("data").resolve("dungeons.nbt").toFile();
    }
}
