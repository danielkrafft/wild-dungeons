package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RoomExportScreen extends Screen {
    private static final Component NAME_LABEL = Component.translatable("room_export_wand.name_label");
    private static final Component MATERIALS_LABEL = Component.translatable("room_export_wand.materials_label");

    private final RoomExportWand roomExportWand;
    private EditBox nameEdit;
    private Button saveButton;

    public RoomExportScreen(RoomExportWand roomExportWand) {
        super(CommonComponents.EMPTY);
        //grab data from wand
        this.roomExportWand = roomExportWand;

    }

    @Override
    protected void init() {
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 20, 250, 20, Component.translatable("structure_block.structure_name")) {
            public boolean charTyped(char charTyped, int modifiers) {
                return RoomExportScreen.this.isValidCharacterForName(this.getValue(), charTyped, this.getCursorPosition()) && super.charTyped(charTyped, modifiers);
            }
        };
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(roomExportWand.getRoomName());
        this.addWidget(this.nameEdit);
        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("structure_block.button.save"), button -> {
            onDone();
        }).bounds(this.width / 2 + 4 + 100, 20, 50, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(this.font, NAME_LABEL, this.width / 2 - 153, 10, new Color(0, 255, 233, 129).getRGB());
        this.nameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, MATERIALS_LABEL, this.width / 2 - 153, 45, new Color(0, 255, 233, 129).getRGB());
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
        this.sendToServer();
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    private void sendToServer() {
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ServerPacketHandler.Packets.ROOM_EXPORT_WAND_SAVE.toString());
        tag.putString("roomName", this.nameEdit.getValue());

        PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
    }

    @OnlyIn(Dist.CLIENT)
    public class DetailsList extends ObjectSelectionList<DetailsList.Entry>{
        public DetailsList() {
            super(RoomExportScreen.this.minecraft, RoomExportScreen.this.width, RoomExportScreen.this.height - 103, 43, 24);

//            for (int i = 0; i < RoomExportScreen.this.generator.getLayersInfo().size(); i++) {
//                this.addEntry(new Entry());
//            }
        }

        @OnlyIn(Dist.CLIENT)
        class Entry extends ObjectSelectionList.Entry<Entry>{

            @Override
            public Component getNarration() {
                return null;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {

            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                DetailsList.this.setSelected(this);
                return super.mouseClicked(mouseX, mouseY, button);
            }

            private void blitSlot(GuiGraphics guiGraphics, int x, int y, ItemStack stack) {
                this.blitSlotBg(guiGraphics, x + 1, y + 1);
                if (!stack.isEmpty()) {
                    guiGraphics.renderFakeItem(stack, x + 2, y + 2);
                }
            }

            private void blitSlotBg(GuiGraphics guiGraphics, int x, int y) {
                guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("container/slot"), x, y, 0, 18, 18);
            }
        }
    }
}