package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SaveSystem {
    private static final SaveSystem INSTANCE = new SaveSystem();
    private boolean saving = false;
    private boolean loading = false;
    private boolean loaded = false;

    public static void Save(){
        INSTANCE.save();
    }

    public static boolean Load(){
        DungeonRegistry.setupDungeons();
        return INSTANCE.load();
    }

    public static boolean isLoading() {
        return INSTANCE.loading;
    }

    public static boolean isLoaded() {
        return INSTANCE.loaded;
    }

    public static void DeleteSession(DungeonSession dungeonSession) {
        INSTANCE.deleteSession(dungeonSession);
    }

    private void deleteSession(DungeonSession dungeonSession) {
        Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons");
        FileUtil.deletePath(path.resolve(dungeonSession.getSessionKey()+".nbt"));
        FileUtil.deleteDirectoryContents(path.resolve(dungeonSession.getSessionKey()),true);
    }


    private void save() {
        if (saving) return;
        saving = true;

        SaveFile saveFile = new SaveFile();
        Stack<DungeonSession> sessions = new Stack<>();
        DungeonSessionManager.getInstance().getSessions().forEach((key, value) -> {
            if (value.isSafeToSerialize()) sessions.push(value);
        });
        //only add the players in the sessions that are safe to serialize
        if (!sessions.empty()){
            Map<String, WDPlayer> players = WDPlayerManager.getInstance().getPlayers();
            Map<String, WDPlayer> safePlayers = new HashMap<>();
            players.forEach((key, value) -> {
                DungeonSession session = value.getCurrentDungeon();
                if (session!=null && session.isSafeToSerialize()) safePlayers.put(key, value);
            });
            saveFile.AddPlayers(safePlayers);
        }

        while (sessions.iterator().hasNext()) {
            DungeonSession session = sessions.pop();
            DungeonSessionFile sessionFile = new DungeonSessionFile(session);
            Stack<DungeonFloor> floors = new Stack<>();
            session.getFloors().forEach(floors::push);
            while (floors.iterator().hasNext()) {
                DungeonFloor floor = floors.pop();
                DungeonFloorFile floorFile = new DungeonFloorFile(floor);
                Stack<DungeonBranch> branches = new Stack<>();
                floor.getBranches().forEach(branches::push);
                while (branches.iterator().hasNext()) {
                    DungeonBranch branch = branches.pop();
                    DungeonBranchFile branchFile = new DungeonBranchFile(branch);
                    Stack<DungeonRoom> rooms = new Stack<>();
                    branch.getRooms().forEach(rooms::push);
                    while (rooms.iterator().hasNext()) {
                        DungeonRoom room = rooms.pop();
                        DungeonRoomFile roomFile = new DungeonRoomFile(room);
                        Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons").resolve(session.getSessionKey()).resolve("fl" + floor.getIndex()).resolve("br" + branch.getIndex()).resolve("rm" + room.getIndex() + ".nbt");
                        FileUtil.writeNbt(Serializer.toCompoundTag(roomFile), path.toFile());
                        branchFile.AddRoomFile(path);
                    }
                    Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons").resolve(session.getSessionKey()).resolve("fl" + floor.getIndex()).resolve(("br" + branch.getIndex()) + ".nbt");
                    FileUtil.writeNbt(Serializer.toCompoundTag(branchFile), path.toFile());
                    floorFile.AddBranchFile(path);
                }

                Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons").resolve(session.getSessionKey()).resolve("fl" + floor.getIndex() + ".nbt");
                FileUtil.writeNbt(Serializer.toCompoundTag(floorFile), path.toFile());
                sessionFile.AddFloorFile(path);
            }

            Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons").resolve(session.getSessionKey() + ".nbt");
            FileUtil.writeNbt(Serializer.toCompoundTag(sessionFile), path.toFile());
            saveFile.AddSessionFile(path);
        }

        Path path = FileUtil.getWorldPath().resolve("data").resolve("dungeons.nbt");
        FileUtil.writeNbt(Serializer.toCompoundTag(saveFile), path.toFile());
        saving = false;
    }

    private boolean load() {
        loaded = false;
        loading = true;
        WDPlayerManager.getInstance().setPlayers(new HashMap<>());
        SaveFile saveFile = Serializer.fromCompoundTag(FileUtil.readNbt(FileUtil.getWorldPath().resolve("data").resolve("dungeons.nbt").toFile()));
        if (saveFile == null) {
            WildDungeons.getLogger().error("Failed to load save file");
            loading = false;
            loaded = true;
            return true;
        }
        HashMap<String, WDPlayer> players = new HashMap<>(saveFile.players);
        HashMap<String, DungeonSession> sessions = new HashMap<>();

        saveFile.sessionFilePaths.forEach(sessionPath -> {
            DungeonSessionFile sessionFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(sessionPath).toFile()));
            if (sessionFile == null) return;
            DungeonSession session = sessionFile.session;
            sessionFile.floorFilePaths.forEach(floorPath -> {
                DungeonFloorFile floorFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(floorPath).toFile()));
                if (floorFile == null) return;
                DungeonFloor floor = floorFile.floor;
                floorFile.branchFilePaths.forEach(branchPath -> {
                    DungeonBranchFile branchFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(branchPath).toFile()));
                    if (branchFile == null) return;
                    DungeonBranch branch = branchFile.branch;
                    branchFile.roomFilePaths.forEach(roomPath -> {
                        DungeonRoomFile roomFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(roomPath).toFile()));
                        if (roomFile == null) return;
                        DungeonRoom room = roomFile.room;
                        branch.addRoom(room);
                    });
                    branch.sortRooms();
                    floor.addBranch(branch);
                });
                floor.sortBranches();
                session.addFloor(floor);
            });
            session.sortFloors();
            sessions.put(session.getSessionKey(), session);
        });
        WDPlayerManager.getInstance().setPlayers(players);
        WildDungeons.getLogger().error("Loaded {} players", players.size());
        WildDungeons.getLogger().error("Loaded {} sessions", sessions.size());
        WildDungeons.getLogger().error("Loaded {} floors", sessions.values().stream().mapToInt(session -> session.getFloors().size()).sum());
        WildDungeons.getLogger().error("Loaded {} branches", sessions.values().stream().mapToInt(session -> session.getFloors().stream().mapToInt(floor -> floor.getBranches().size()).sum()).sum());
        WildDungeons.getLogger().error("Loaded {} rooms", sessions.values().stream().mapToInt(session -> session.getFloors().stream().mapToInt(floor -> floor.getBranches().stream().mapToInt(branch -> branch.getRooms().size()).sum()).sum()).sum());
        DungeonSessionManager.getInstance().setSessions(sessions);
        loading = false;
        loaded = true;
        return true;
    }

    public static class SaveFile {
        public Map<String, WDPlayer> players = null;
        public List<String> sessionFilePaths = new ArrayList<>();

        public void AddPlayers(Map<String, WDPlayer> players) {
            this.players = players;
        }

        public void AddSessionFile(Path sessionFile){
            sessionFilePaths.add(sessionFile.toString());
        }
    }

    public static class DungeonSessionFile {
        public DungeonSession session = null;
        public List<String> floorFilePaths = new ArrayList<>();


        public DungeonSessionFile(DungeonSession session) {
            this.session = session;
        }
        public void AddFloorFile(Path floorFile){
            floorFilePaths.add(floorFile.toString());
        }

    }

    public static class DungeonFloorFile {
        public DungeonFloor floor = null;
        public List<String> branchFilePaths = new ArrayList<>();


        public DungeonFloorFile(DungeonFloor floor) {
            this.floor = floor;
        }

        public void AddBranchFile(Path branchFile){
            branchFilePaths.add(branchFile.toString());
        }

    }

    public static class DungeonBranchFile {
        public DungeonBranch branch = null;
        public List<String> roomFilePaths = new ArrayList<>();


        public DungeonBranchFile(DungeonBranch branch) {
            this.branch = branch;
        }

        public void AddRoomFile(Path roomFile){
            roomFilePaths.add(roomFile.toString());
        }

    }

    public static class DungeonRoomFile {
        public DungeonRoom room = null;

        public DungeonRoomFile(DungeonRoom room) {
            this.room = room;
        }
    }
}
