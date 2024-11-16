package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundUpdateConnectionBlockPacket;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class ConnectionBlockEditScreen extends Screen {
    private String occupiedBlockstate;
    private String unoccupiedBlockstate;
    private String pool;
    private int x;
    private int y;
    private int z;

    private static final Component OCCUPIED_LABEL = Component.translatable("connection_block.occupied_label");
    private static final Component UNOCCUPIED_LABEL = Component.translatable("connection_block.unoccupied_label");
    private static final Component POOL_LABEL = Component.translatable("connection_block.pool_label");
    private EditBox occupiedEdit;
    private EditBox unoccupiedEdit;
    private EditBox poolEdit;
    private Button doneButton;
    private Button cancelButton;

    public ConnectionBlockEditScreen(String occupiedBlockstate, String unoccupiedBlockstate, String pool, int x, int y, int z) {
        super(GameNarrator.NO_TITLE);
        this.occupiedBlockstate = occupiedBlockstate;
        this.unoccupiedBlockstate = unoccupiedBlockstate;
        this.pool = pool;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        this.occupiedEdit = new EditBox(this.font, this.width / 2 - 153, 20, 300, 20, OCCUPIED_LABEL);
        this.occupiedEdit.setMaxLength(128);
        this.occupiedEdit.setValue(this.occupiedBlockstate);
        this.addWidget(this.occupiedEdit);

        this.unoccupiedEdit = new EditBox(this.font, this.width / 2 - 153, 55, 300, 20, UNOCCUPIED_LABEL);
        this.unoccupiedEdit.setMaxLength(128);
        this.unoccupiedEdit.setValue(this.unoccupiedBlockstate);
        this.addWidget(this.unoccupiedEdit);

        this.poolEdit = new EditBox(this.font, this.width / 2 - 153, 90, 300, 20, POOL_LABEL);
        this.poolEdit.setMaxLength(128);
        this.poolEdit.setValue(this.pool);
        this.addWidget(this.poolEdit);

        this.doneButton = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, x -> this.onDone()).bounds(this.width / 2 - 4 - 150, 125, 150, 20).build()
        );
        this.cancelButton = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_CANCEL, x -> this.onCancel()).bounds(this.width / 2 + 4, 125, 150, 20).build()
        );
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.occupiedEdit);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.occupiedEdit.getValue();
        String s1 = this.unoccupiedEdit.getValue();
        String s2 = this.poolEdit.getValue();
        this.init(minecraft, width, height);
        this.occupiedEdit.setValue(s);
        this.unoccupiedEdit.setValue(s1);
        this.poolEdit.setValue(s2);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (!this.doneButton.active || keyCode != 257 && keyCode != 335) {
            return false;
        } else {
            this.onDone();
            return true;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, OCCUPIED_LABEL, this.width / 2 - 153, 10, 10526880);
        this.occupiedEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, UNOCCUPIED_LABEL, this.width / 2 - 153, 45, 10526880);
        this.unoccupiedEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, POOL_LABEL, this.width / 2 - 153, 80, 10526880);
        this.poolEdit.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sendToServer() {
        CompoundTag tag = new CompoundTag();
        tag.putString("occupiedBlockstate", this.occupiedEdit.getValue());
        tag.putString("unoccupiedBlockstate", this.unoccupiedEdit.getValue());
        tag.putString("pool", this.poolEdit.getValue());
        tag.putInt("x", this.x);
        tag.putInt("y", this.y);
        tag.putInt("z", this.z);

        PacketDistributor.sendToServer(new ServerboundUpdateConnectionBlockPacket(tag));
    }
}
