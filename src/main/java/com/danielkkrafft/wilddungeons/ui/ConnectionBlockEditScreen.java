package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.network.serverbound.ServerboundUpdateConnectionBlockPacket;
import com.mojang.datafixers.types.templates.Check;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class ConnectionBlockEditScreen extends Screen {
    private String unblockedBlockstate;
    private String pool;
    private String type;
    private int x;
    private int y;
    private int z;

    private static final Component UNBLOCKED_LABEL = Component.translatable("connection_block.unblocked_label");
    private static final Component POOL_LABEL = Component.translatable("connection_block.pool_label");
    private static final Component ENTRANCE_LABEL = Component.translatable("connection_block.entrance_label");
    private static final Component EXIT_LABEL = Component.translatable("connection_block.exit_label");
    private EditBox unblockedEdit;
    private EditBox poolEdit;
    private Button doneButton;
    private Button cancelButton;
    private Checkbox entranceCheckbox;
    private Checkbox exitCheckbox;

    public ConnectionBlockEditScreen(String unblockedBlockstate, String pool, String type, int x, int y, int z) {
        super(GameNarrator.NO_TITLE);
        this.unblockedBlockstate = unblockedBlockstate;
        this.pool = pool;
        this.type = type;
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

        this.entranceCheckbox = Checkbox.builder(ENTRANCE_LABEL, this.font).pos(this.width / 2 - 153, 20).onValueChange(this::handleCheckboxes).selected(Objects.equals(type, "entrance") || Objects.equals(type, "both")).build();
        this.exitCheckbox = Checkbox.builder(EXIT_LABEL, this.font).pos(this.width / 2 - 153, 35).onValueChange(this::handleCheckboxes).selected(Objects.equals(type, "exit") || Objects.equals(type, "both")).build();
        this.addWidget(this.entranceCheckbox);
        this.addWidget(this.exitCheckbox);

        this.unblockedEdit = new EditBox(this.font, this.width / 2 - 153, 65, 300, 20, UNBLOCKED_LABEL);
        this.unblockedEdit.setMaxLength(128);
        this.unblockedEdit.setValue(this.unblockedBlockstate);
        this.addWidget(this.unblockedEdit);

        this.poolEdit = new EditBox(this.font, this.width / 2 - 153, 100, 300, 20, POOL_LABEL);
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
        this.setInitialFocus(this.unblockedEdit);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s1 = this.unblockedEdit.getValue();
        String s2 = this.poolEdit.getValue();
        this.init(minecraft, width, height);
        this.unblockedEdit.setValue(s1);
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
        guiGraphics.drawString(this.font, UNBLOCKED_LABEL, this.width / 2 - 153, 55, 10526880);
        this.unblockedEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, POOL_LABEL, this.width / 2 - 153, 90, 10526880);
        this.poolEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        this.entranceCheckbox.render(guiGraphics, mouseX, mouseY, partialTick);
        this.exitCheckbox.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sendToServer() {
        CompoundTag tag = new CompoundTag();
        tag.putString("unblockedBlockstate", this.unblockedEdit.getValue());
        tag.putString("pool", this.poolEdit.getValue());
        tag.putString("type", this.type);
        tag.putInt("x", this.x);
        tag.putInt("y", this.y);
        tag.putInt("z", this.z);

        PacketDistributor.sendToServer(new ServerboundUpdateConnectionBlockPacket(tag));
    }

    private void handleCheckboxes(Checkbox box, boolean value) {
        boolean a = entranceCheckbox.selected();
        boolean b = exitCheckbox.selected();

        if (a && !b) {
            this.type = "entrance";
        } else if (b && !a) {
            this.type = "exit";
        } else if (a && b) {
            this.type = "both";
        } else if (!a && !b) {
            this.type = "both";
        }

        WildDungeons.getLogger().info("SET TYPE TO {}", this.type);
    }
}
