package com.danielkkrafft.wilddungeons.render;

import com.danielkkrafft.wilddungeons.network.ClientPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.util.Serializer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class DecalRenderer {
    public static HashMap<ResourceKey<Level>, HashMap<ChunkPos, Set<Decal>>> SERVER_DECALS_MAP = new HashMap<>(); //TODO these are not being saved or loaded or restored to joining players
    public static HashMap<ResourceKey<Level>, HashMap<ChunkPos, Set<Decal>>> CLIENT_DECALS_MAP = new HashMap<>();
    public static int DECAL_RENDER_DISTANCE = 8;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null) return;

        HashMap<ChunkPos, Set<Decal>> decalsInThisLevel = CLIENT_DECALS_MAP.get(level.dimension());
        if (decalsInThisLevel == null) return;

        PoseStack poseStack = event.getPoseStack();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.disableCull();
        Tesselator tesselator = Tesselator.getInstance();

        Camera camera = event.getCamera();

        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        ChunkPos playerCurrentChunkPos = Minecraft.getInstance().player.chunkPosition();
        for (int x = playerCurrentChunkPos.x - DECAL_RENDER_DISTANCE; x < playerCurrentChunkPos.x + DECAL_RENDER_DISTANCE; x++) {
            for (int z = playerCurrentChunkPos.z - DECAL_RENDER_DISTANCE; z < playerCurrentChunkPos.z + DECAL_RENDER_DISTANCE; z++) {
                Set<Decal> decalsInThisChunk = decalsInThisLevel.get(new ChunkPos(x, z));
                if (decalsInThisChunk == null) continue;
                for (Decal decal : decalsInThisChunk) {
                    BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    decal.render(poseStack.last(), buffer);
                    BufferUploader.drawWithShader(buffer.buildOrThrow()); //TODO this is doing a separate draw call for every single decal LMAO
                }
            }
        }

        poseStack.popPose();

    }

    public static void addServerDecal(Decal decal) {
        if (!SERVER_DECALS_MAP.containsKey(decal.dimension)) SERVER_DECALS_MAP.put(decal.dimension, new HashMap<>());
        HashMap<ChunkPos, Set<Decal>> decalsInThisLevel = SERVER_DECALS_MAP.get(decal.dimension);

        if (!decalsInThisLevel.containsKey(decal.chunkPos)) decalsInThisLevel.put(decal.chunkPos, new HashSet<>());
        Set<Decal> decalsInThisChunk = decalsInThisLevel.get(decal.chunkPos);

        decalsInThisChunk.add(decal);
    }

    public static void removeServerDecal(Decal decal) {
        if (decal == null) return;
        if (!SERVER_DECALS_MAP.containsKey(decal.dimension)) return;
        HashMap<ChunkPos, Set<Decal>> decalsInThisLevel = SERVER_DECALS_MAP.get(decal.dimension);

        if (!decalsInThisLevel.containsKey(decal.chunkPos)) return;
        Set<Decal> decalsInThisChunk = decalsInThisLevel.get(decal.chunkPos);

        decalsInThisChunk.remove(decal);
    }

    public static void addClientDecal(Decal decal) {
        if (decal == null) return;
        if (!CLIENT_DECALS_MAP.containsKey(decal.dimension)) CLIENT_DECALS_MAP.put(decal.dimension, new HashMap<>());
        HashMap<ChunkPos, Set<Decal>> decalsInThisLevel = CLIENT_DECALS_MAP.get(decal.dimension);

        if (!decalsInThisLevel.containsKey(decal.chunkPos)) decalsInThisLevel.put(decal.chunkPos, new HashSet<>());
        Set<Decal> decalsInThisChunk = decalsInThisLevel.get(decal.chunkPos);

        decalsInThisChunk.add(decal);
    }

    public static void removeClientDecal(Decal decal) {
        if (decal == null) return;
        if (!CLIENT_DECALS_MAP.containsKey(decal.dimension)) return;
        HashMap<ChunkPos, Set<Decal>> decalsInThisLevel = CLIENT_DECALS_MAP.get(decal.dimension);

        if (!decalsInThisLevel.containsKey(decal.chunkPos)) return;
        Set<Decal> decalsInThisChunk = decalsInThisLevel.get(decal.chunkPos);

        decalsInThisChunk.remove(decal);
    }

    public static void syncClientDecals(ServerPlayer player){
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.SYNC_DECALS.toString());
        tag.put("decal", Serializer.toCompoundTag(SERVER_DECALS_MAP));
        player.connection.send(new SimplePacketManager.ClientboundTagPacket(tag));
    }

    public static void syncAllClientDecals() {
        WDPlayerManager.getInstance().getServerPlayers().forEach((s, wdPlayer) -> syncClientDecals(wdPlayer.getServerPlayer()));
    }

    public static void sendClientRemovalPacket(Decal decal) {
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.REMOVE_DECAL.toString());
        tag.put("decal", Serializer.toCompoundTag(decal));
        PacketDistributor.sendToAllPlayers(new SimplePacketManager.ClientboundTagPacket(tag));
    }

    public static void sendClientAdditionPacket(Decal decal) {
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ClientPacketHandler.Packets.ADD_DECAL.toString());
        tag.put("decal", Serializer.toCompoundTag(decal));
        PacketDistributor.sendToAllPlayers(new SimplePacketManager.ClientboundTagPacket(tag));
    }

    public static class Decal {
        public List<Vertex> vertices = new ArrayList<>();
        public ResourceLocation texture;
        public ChunkPos chunkPos;
        public ResourceKey<Level> dimension;

        public Decal(ResourceLocation texture, float originX, float originY, float originZ, float width, float height, Direction.Axis axis, int color, ResourceKey<Level> dimension) {
            this.texture = texture;
            this.dimension = dimension;
            this.chunkPos = new ChunkPos(new BlockPos((int) originX, (int) originY, (int) originZ));

            switch (axis) {
                case X -> {
                    this.vertices.add(new Vertex(originX, originY + height/2, originZ - width/2, 0.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX, originY + height/2, originZ + width/2, 1.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX, originY - height/2, originZ + width/2, 1.0f, 1.0f, color));
                    this.vertices.add(new Vertex(originX, originY - height/2, originZ - width/2, 0.0f, 1.0f, color));
                }
                case Y -> {
                    this.vertices.add(new Vertex(originX - width/2, originY, originZ + height/2, 0.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX + width/2, originY, originZ + height/2, 1.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX + width/2, originY, originZ - height/2, 1.0f, 1.0f, color));
                    this.vertices.add(new Vertex(originX - width/2, originY, originZ - height/2, 0.0f, 1.0f, color));
                }
                case Z -> {
                    this.vertices.add(new Vertex(originX - width/2, originY + height/2, originZ, 0.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX + width/2, originY + height/2, originZ, 1.0f, 0.0f, color));
                    this.vertices.add(new Vertex(originX + width/2, originY - height/2, originZ, 1.0f, 1.0f, color));
                    this.vertices.add(new Vertex(originX - width/2, originY - height/2, originZ, 0.0f, 1.0f, color));
                }
            }
        }

        public void render(PoseStack.Pose pose, BufferBuilder buffer) {
            RenderSystem.setShaderTexture(0, this.texture);
            for (Vertex vertex : this.vertices) {
                buffer.addVertex(pose, vertex.x, vertex.y, vertex.z).setColor(vertex.color).setUv(vertex.u, vertex.v);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Decal decal)) return false;
            return
                    decal.vertices.equals(this.vertices)
                    && decal.chunkPos.equals(this.chunkPos)
                    && decal.dimension.equals(this.dimension)
                    && decal.texture.equals(this.texture);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertices, texture, chunkPos, dimension);
        }

        public static class Vertex {
            public float x;
            public float y;
            public float z;
            public float u;
            public float v;
            public int color;

            public Vertex(float x, float y, float z, float u, float v, int color) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.u = u;
                this.v = v;
                this.color = color;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Vertex vertex)) return false;
                return Float.compare(this.x, vertex.x) == 0
                        && Float.compare(this.y, vertex.y) == 0
                        && Float.compare(this.z, vertex.z) == 0
                        && Float.compare(this.u, vertex.u) == 0
                        && Float.compare(this.v, vertex.v) == 0
                        && this.color == vertex.color;
            }

            @Override
            public int hashCode() {
                return Objects.hash(x, y, z, u, v, color);
            }
        }
    }
}
