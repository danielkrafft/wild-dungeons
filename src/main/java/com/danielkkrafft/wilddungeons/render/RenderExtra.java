package com.danielkkrafft.wilddungeons.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.*;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RenderExtra {
    public static final Set<ExtraRenderComponent> components = new HashSet<>();
    public static long lastMS = System.currentTimeMillis();

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (components.isEmpty()) return;
        int elapsed = (int) (System.currentTimeMillis() - lastMS);
        lastMS = System.currentTimeMillis();

        PoseStack poseStack = event.getPoseStack();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.lineWidth(4.0f);
        RenderSystem.disableDepthTest();
        Camera camera = event.getCamera();

        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Set<ExtraRenderComponent> toRemove = new HashSet<>();
        for (ExtraRenderComponent component : components) {
            for (ExtraRenderVertex vertex : component.verts) {
                buffer.addVertex(poseStack.last(), vertex.x, vertex.y, vertex.z).setColor(vertex.color);
            }
            component.lifespan -= elapsed;
            if (component.lifespan < 0) toRemove.add(component);
        }

        for (ExtraRenderComponent component : toRemove) {
            components.remove(component);
        }

        poseStack.popPose();
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static final class ExtraRenderComponent {
        public final List<ExtraRenderVertex> verts;
        public int lifespan = 100000;

        public ExtraRenderComponent(List<ExtraRenderVertex> verts) {
            this.verts = verts;
        }

        public ExtraRenderComponent(BoundingBox box, int color) {
            this(new ArrayList<>());
            this.verts.add(new ExtraRenderVertex(box.minX(), box.minY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.minX(), box.minY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.minX(), box.maxY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.minX(), box.minY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.minX(), box.minY(), box.maxZ(), color));

            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.minX(), box.maxY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.minZ(), color));

            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.minZ(), color));

            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.minZ(), color));

            this.verts.add(new ExtraRenderVertex(box.minX(), box.maxY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.minZ(), color));

            this.verts.add(new ExtraRenderVertex(box.minX(), box.minY(), box.maxZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.maxZ(), color));

            this.verts.add(new ExtraRenderVertex(box.minX(), box.maxY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.minZ(), color));

            this.verts.add(new ExtraRenderVertex(box.maxX(), box.minY(), box.minZ(), color));
            this.verts.add(new ExtraRenderVertex(box.maxX(), box.maxY(), box.minZ(), color));


        }
    }

    public static final class ExtraRenderVertex {

        public final float x;
        public final float y;
        public final float z;
        public final int color;

        public ExtraRenderVertex(float x, float y, float z, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }
}
