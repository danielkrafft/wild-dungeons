package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SaveFile {
    public static final SaveFile INSTANCE = new SaveFile();

    private Map<String, WDPlayer> players = null;
    private Map<String, DungeonSession> sessions = null;

    private boolean saving = false;

    public void save() {
        if (saving) return;

        saving = true;
        this.players = WDPlayerManager.getInstance().getPlayers();
        this.sessions = new HashMap<>();
        DungeonSessionManager.getInstance().getSessions().entrySet().forEach(e -> {
            if (e.getValue().isSafeToSerialize()) this.sessions.put(e.getKey(), e.getValue());
        });

        FileUtil.writeNbt(Serializer.toCompoundTag(this), getSaveFile());
        WildDungeons.getLogger().info("SERIALIZATION #{}", Serializer.serializations);
        saving = false;
    }

    public void load() {
        SaveFile saveFile = Serializer.fromCompoundTag(FileUtil.readNbt(getSaveFile()));
        if (saveFile == null) return;

        if (saveFile.players != null) WDPlayerManager.getInstance().setPlayers(saveFile.players);
        if (saveFile.sessions != null) DungeonSessionManager.getInstance().setSessions(saveFile.sessions);
    }

    public File getSaveFile() {
        return FileUtil.getWorldPath().resolve("data").resolve("dungeons.nbt").toFile();
    }
}
