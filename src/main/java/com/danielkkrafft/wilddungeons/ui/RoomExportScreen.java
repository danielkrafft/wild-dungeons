package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RoomExportScreen extends Screen {
    private static final Component NAME_LABEL = Component.translatable("structure_block.structure_name");

    private final RoomExportWand roomExportWand;
    private EditBox nameEdit;

    public RoomExportScreen(RoomExportWand roomExportWand) {
        super(CommonComponents.EMPTY);
        //grab data from wand
        this.roomExportWand = roomExportWand;

    }

    @Override
    protected void init() {
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, Component.translatable("structure_block.structure_name")) {
            public boolean charTyped(char charTyped, int modifiers) {
                return RoomExportScreen.this.isValidCharacterForName(this.getValue(), charTyped, this.getCursorPosition()) && super.charTyped(charTyped, modifiers);
            }
        };
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(roomExportWand.getRoomName());
        this.addWidget(this.nameEdit);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(this.font, NAME_LABEL, this.width / 2 - 153, 30, new Color(0, 255, 233, 129).getRGB());
        this.nameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String s = this.nameEdit.getValue();
        super.resize(minecraft, width, height);
        this.init(minecraft, width, height);
        this.nameEdit.setValue(s);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    private void onCancel() {
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode != 257 && keyCode != 335) {
            return false;
        } else {
            this.onDone();
            return true;
        }
    }

    private void onDone() {
        this.sendToServer(StructureBlockEntity.UpdateType.UPDATE_DATA);
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    private void sendToServer(StructureBlockEntity.UpdateType updateType) {
        assert this.minecraft != null;
        //todo send data to server
        //todo custom packet
//        this.minecraft.getConnection().send(new ServerboundSetStructureBlockPacket(this.structure.getBlockPos(), updateType, this.structure.getMode(), this.nameEdit.getValue(), blockpos, vec3i, this.structure.getMirror(), this.structure.getRotation(), this.dataEdit.getValue(), this.structure.isIgnoreEntities(), this.structure.getShowAir(), this.structure.getShowBoundingBox(), f, i));
    }


}