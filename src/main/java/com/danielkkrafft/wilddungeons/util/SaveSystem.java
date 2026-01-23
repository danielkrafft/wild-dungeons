package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDProtectedRegion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SaveSystem {
    private static final SaveSystem INSTANCE = new SaveSystem();
    private boolean saving = false;
    private boolean loading = false;
    private boolean loaded = false;

    public static void Save() {
        INSTANCE.save();
    }

    public static void Load(){
        INSTANCE.load();
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

    public static void failLoading() {
        DungeonSessionManager.getInstance().setSessions(new HashMap<>());
        WDPlayerManager.getInstance().setServerPlayers(new HashMap<>());
        WDProtectedRegion.clearAllRegions();
        INSTANCE.loaded = true;
        INSTANCE.loading = false;
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
            if (!value.getPlayers().isEmpty() || value.dirty) {
                sessions.push(value);
                value.dirty = false;
            }
        });
        saveFile.AddPlayers(WDPlayerManager.getInstance().getServerPlayers());
        saveFile.AddProtectedRegions(WDProtectedRegion.getAllRegions());

        while (sessions.iterator().hasNext()) {
            DungeonSession session = sessions.pop();
            DungeonSessionFile sessionFile = new DungeonSessionFile(session);
            Stack<DungeonFloor> floors = new Stack<>();
            session.getFloors().forEach(floors::push);
            while (floors.iterator().hasNext()) {
                DungeonFloor floor = floors.pop();
                if (floor == null) continue;
                floor.halfGeneratedRooms = new ArrayList<>();
                DungeonFloorFile floorFile = new DungeonFloorFile(floor);
                Stack<DungeonBranch> branches = new Stack<>();
                floor.getBranches().forEach(branches::push);
                while (branches.iterator().hasNext()) {
                    DungeonBranch branch = branches.pop();
                    if (!branch.isFullyGenerated()) {
                        WildDungeons.getLogger().info("Skipping branch {} because it is not fully generated", branch.getIndex());
                        floorFile.floor.halfGeneratedRooms = new ArrayList<>();
                        branch.getRooms().forEach(dungeonRoom -> {
                            dungeonRoom.unsetAttachedPoints();
                            floorFile.floor.halfGeneratedRooms.addAll(dungeonRoom.getBoundingBoxes());
                        });
                        floorFile.floor.getChunkMap().forEach((key, value) -> {
                            value.removeIf(v -> v.x == branch.getIndex());
                        });
                        continue;
                    }
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

    private void load() {
        loaded = false;
        loading = true;
        WDPlayerManager.getInstance().setServerPlayers(new HashMap<>());
        DungeonSessionManager.getInstance().setSessions(new HashMap<>());
        WDProtectedRegion.clearAllRegions();
        SaveFile saveFile = Serializer.fromCompoundTag(FileUtil.readNbt(FileUtil.getWorldPath().resolve("data").resolve("dungeons.nbt").toFile()));
        if (saveFile == null) {
            WildDungeons.getLogger().error("NO DUNGEON FILES FOUND");
            loading = false;
            loaded = true;
            return;
        }
        HashMap<String, WDPlayer> players = new HashMap<>(saveFile.players);
        HashMap<String, DungeonSession> sessions = new HashMap<>();

        saveFile.sessionFilePaths.forEach(sessionPath -> {
            DungeonSessionFile sessionFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(sessionPath).toFile()));
            if (sessionFile == null) {
                WildDungeons.getLogger().error("Failed to load session file: {}", sessionPath);
                //this will load players into the dimension with no active session, so we should kick them out of the dimension
                return;
            }
            DungeonSession session = sessionFile.session;
            sessionFile.floorFilePaths.forEach(floorPath -> {
                DungeonFloorFile floorFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(floorPath).toFile()));
                if (floorFile == null) {
                    WildDungeons.getLogger().error("Failed to load floor file: {}", floorPath);
                    //this should trigger entire floor regen, kicking players back a floor if they are on this floor
                    return;
                }
                DungeonFloor floor = floorFile.floor;
                floorFile.branchFilePaths.forEach(branchPath -> {
                    DungeonBranchFile branchFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(branchPath).toFile()));
                    if (branchFile == null) {
                        WildDungeons.getLogger().error("Failed to load branch file: {}", branchPath);
                        //this should trigger entire branch regen, kicking players back a branch if they are in this branch
                        //it should also trigger all future branches to be regened
                        return;
                    }
                    DungeonBranch branch = branchFile.branch;
                    branchFile.roomFilePaths.forEach(roomPath -> {
                        DungeonRoomFile roomFile = Serializer.fromCompoundTag(FileUtil.readNbt(Paths.get(roomPath).toFile()));
                        if (roomFile == null) {
                            WildDungeons.getLogger().error("Failed to load room file: {}", roomPath);
                            //this should trigger a full branch regen like above
                            return;
                        }
                        DungeonRoom room = roomFile.room;
                        branch.addRoomFromSave(room);
                    });
                    branch.getRooms().sort(Comparator.comparingInt(DungeonRoom::getIndex));
                    floor.addBranchFromSave(branch);
                });
                floor.getBranches().sort(Comparator.comparingInt(DungeonBranch::getIndex));
                session.addFloorFromSave(floor);
            });
            session.getFloors().sort(Comparator.comparingInt(DungeonFloor::getIndex));
            sessions.put(session.getSessionKey(), session);
        });
        WDPlayerManager.getInstance().setServerPlayers(players);

        if (saveFile.protectedRegions != null) {
            saveFile.protectedRegions.forEach((dimension, regionList) -> {
                regionList.forEach(WDProtectedRegion::register);
            });
        }

        WildDungeons.getLogger().info("Loaded {} players", players.size());
        WildDungeons.getLogger().info("Loaded {} sessions", sessions.size());
        WildDungeons.getLogger().info("Loaded {} floors", sessions.values().stream().mapToInt(session -> session.getFloors().size()).sum());
        WildDungeons.getLogger().info("Loaded {} branches", sessions.values().stream().mapToInt(session -> session.getFloors().stream().mapToInt(floor -> floor.getBranches().size()).sum()).sum());
        WildDungeons.getLogger().info("Loaded {} rooms", sessions.values().stream().mapToInt(session -> session.getFloors().stream().mapToInt(floor -> floor.getBranches().stream().mapToInt(branch -> branch.getRooms().size()).sum()).sum()).sum());
        DungeonSessionManager.getInstance().setSessions(sessions);
        loading = false;
        loaded = true;
    }

    public static class SaveFile {
        public Map<String, WDPlayer> players = null;
        public Map<ResourceKey<Level>, List<WDProtectedRegion>> protectedRegions = null;
        public List<String> sessionFilePaths = new ArrayList<>();

        public void AddPlayers(Map<String, WDPlayer> players) {
            this.players = players;
        }

        public void AddProtectedRegions(Map<ResourceKey<Level>, List<WDProtectedRegion>> protectedRegions) {
            this.protectedRegions = protectedRegions;
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
