package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundUpdateConnectionBlockPacket;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ConnectionBlockEditScreen extends Screen {
    private String lockedBlockstate;
    private String unlockedBlockstate;
    private String pool;
    private int x;
    private int y;
    private int z;

    private static final Component LOCKED_LABEL = Component.translatable("connection_block.locked_label");
    private static final Component UNLOCKED_LABEL = Component.translatable("connection_block.unlocked_label");
    private static final Component POOL_LABEL = Component.translatable("connection_block.pool_label");
    private EditBox lockedEdit;
    private EditBox unlockedEdit;
    private EditBox poolEdit;
    private Button doneButton;
    private Button cancelButton;

    public ConnectionBlockEditScreen(String lockedBlockstate, String unlockedBlockstate, String pool, int x, int y, int z) {
        super(GameNarrator.NO_TITLE);
        this.lockedBlockstate = lockedBlockstate;
        this.unlockedBlockstate = unlockedBlockstate;
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
        this.lockedEdit = new EditBox(this.font, this.width / 2 - 153, 20, 300, 20, LOCKED_LABEL);
        this.lockedEdit.setMaxLength(128);
        this.lockedEdit.setValue(this.lockedBlockstate);
        this.addWidget(this.lockedEdit);

        this.unlockedEdit = new EditBox(this.font, this.width / 2 - 153, 55, 300, 20, UNLOCKED_LABEL);
        this.unlockedEdit.setMaxLength(128);
        this.unlockedEdit.setValue(this.unlockedBlockstate);
        this.addWidget(this.unlockedEdit);

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
        this.setInitialFocus(this.lockedEdit);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.lockedEdit.getValue();
        String s1 = this.unlockedEdit.getValue();
        String s2 = this.poolEdit.getValue();
        this.init(minecraft, width, height);
        this.lockedEdit.setValue(s);
        this.unlockedEdit.setValue(s1);
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
        guiGraphics.drawString(this.font, LOCKED_LABEL, this.width / 2 - 153, 10, 10526880);
        this.lockedEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, UNLOCKED_LABEL, this.width / 2 - 153, 45, 10526880);
        this.unlockedEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, POOL_LABEL, this.width / 2 - 153, 80, 10526880);
        this.poolEdit.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sendToServer() {
        CompoundTag tag = new CompoundTag();
        tag.putString("lockedBlockstate", this.lockedEdit.getValue());
        tag.putString("unlockedBlockstate", this.unlockedEdit.getValue());
        tag.putString("pool", this.poolEdit.getValue());
        tag.putInt("x", this.x);
        tag.putInt("y", this.y);
        tag.putInt("z", this.z);

        PacketDistributor.sendToServer(new ServerboundUpdateConnectionBlockPacket(tag));
    }
}
