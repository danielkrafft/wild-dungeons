package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomExportScreen extends Screen {
    private final ItemStack roomExportWand;
    private final List<Pair<BlockState, Integer>> dungeonMaterials;


    private static final Component NAME_LABEL = Component.translatable("room_export_wand.name_label");
    private static final Component MODE_LABEL = Component.translatable("room_export_wand.mode_label");
    private static final ImmutableList<StructureMode> ALL_MODES = ImmutableList.of(StructureMode.SAVE, StructureMode.LOAD, StructureMode.DATA);
    private StructureMode mode = StructureMode.SAVE;
    private CycleButton<StructureMode> modeButton;

    private EditBox nameEdit;

    private static final Component MATERIALS_LABEL = Component.translatable("room_export_wand.materials_label");
    private static final Component DUNGEON_MATERIAL_INDEX_LABEL = Component.translatable("room_export_wand.dungeon_material_index_label");
    private Button saveButton;
    private DetailsList detailsList;
    private boolean confirmAction = false;

    private Button loadButton;

    private Button cancelButton;

    public RoomExportScreen(ItemStack roomExportWand, List<Pair<BlockState, Integer>> dungeonMaterials) {
        super(CommonComponents.EMPTY);
        this.roomExportWand = roomExportWand;
        this.dungeonMaterials = dungeonMaterials;
        this.mode = RoomExportWand.getMode(roomExportWand);
    }

    @Override
    protected void init() {
        modeButton = this.addRenderableWidget(CycleButton.<StructureMode>builder(structureMode -> Component.translatable("structure_block.mode." + structureMode.getSerializedName()))
                .withValues(ALL_MODES)
                .displayOnlyValue()
                .withInitialValue(this.mode)
                .create(5, 20, 50, 20, Component.translatable("structure_block.mode"), (button, mode) -> {
                    this.updateMode(mode);
                }));
        this.nameEdit = new EditBox(this.font, 60, 20, this.width - 120, 20, Component.translatable("structure_block.structure_name")) {
            public boolean charTyped(char charTyped, int modifiers) {
                return RoomExportScreen.this.isValidCharacterForName(this.getValue(), charTyped, this.getCursorPosition()) && super.charTyped(charTyped, modifiers);
            }
        };
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(RoomExportWand.getRoomName(roomExportWand));
        this.addWidget(this.nameEdit);

        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("structure_block.button.save"), button -> {
            confirmAction = true;
            onDone();
        }).bounds(this.width - 55, 20, 50, 20).build());
        this.detailsList = this.addRenderableWidget(new DetailsList(this.width, this.height - 65, 60, 24));
        detailsList.children().forEach(entry -> {
            this.addRenderableWidget(entry.dungeonMaterialIDEdit);
            entry.dungeonMaterialIDEdit.active = entry.dungeonMaterialIDEdit.visible = false;
        });

        this.loadButton = this.addRenderableWidget(Button.builder(Component.translatable("structure_block.button.load"), button -> {
            confirmAction = true;
            onDone();
        }).bounds(this.width - 55, 20, 50, 20).build());

        this.cancelButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            confirmAction = false;
            this.onClose();
        }).bounds(5, 45, 50, 20).build());

        this.updateMode(this.mode);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //permanent render
        guiGraphics.drawString(this.font, NAME_LABEL, this.width / 2 - 153, 10, new Color(0, 255, 233, 255).getRGB());
        this.nameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, MODE_LABEL, 5, 10, new Color(255, 255, 255, 255).getRGB());
        //dynamic render
        switch (mode){
            case SAVE -> {
                guiGraphics.drawString(this.font, MATERIALS_LABEL, this.width / 2 - 153, 45, new Color(255, 255, 255, 255).getRGB());
                guiGraphics.drawString(this.font, DUNGEON_MATERIAL_INDEX_LABEL, this.width / 2 + 30, 45, new Color(255, 255, 255, 255).getRGB());
            }
            case LOAD -> {

            }
            case DATA -> {
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void updateMode(StructureMode mode) {
        saveButton.active = saveButton.visible = false;
        detailsList.active = detailsList.visible = false;
        detailsList.children().forEach(entry -> {
            entry.dungeonMaterialIDEdit.active = entry.dungeonMaterialIDEdit.visible = false;
        });

        loadButton.active = loadButton.visible = false;

        this.mode = mode;

        switch (mode) {
            case SAVE -> {
                saveButton.active = saveButton.visible = true;
                detailsList.active = detailsList.visible = true;
            }
            case LOAD -> {
                loadButton.active = loadButton.visible = true;
                //todo
            }
            case DATA -> {
                //todo room properties go here
            }
        }
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String s = this.nameEdit.getValue();
        super.resize(minecraft, width, height);
        this.init(minecraft, width, height);
        this.nameEdit.setValue(s);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void onClose() {
        onDone();
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
        switch (mode) {
            case SAVE -> {
                this.sendToServer(StructureBlockEntity.UpdateType.SAVE_AREA);
            }
            case LOAD -> {
                this.sendToServer(StructureBlockEntity.UpdateType.LOAD_AREA);
            }
            case DATA -> {
                this.sendToServer(StructureBlockEntity.UpdateType.UPDATE_DATA);
            }
        }
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    private void sendToServer(StructureBlockEntity.UpdateType updateType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ServerPacketHandler.Packets.ROOM_EXPORT_WAND_CLOSE.toString());
        tag.putString("updateType", updateType.toString());
        tag.putInt("mode", this.mode.ordinal());
        tag.putString("roomName", this.nameEdit.getValue());
        tag.putBoolean("confirmAction", confirmAction);
        confirmAction = false;

        switch (updateType){
            case SAVE_AREA, UPDATE_DATA -> {
                ListTag listTag = getDungeonMaterialsTag();
                tag.put("dungeonMaterials", listTag);
            }
            case LOAD_AREA -> {
            }
        }

        PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
    }

    private ListTag getDungeonMaterialsTag() {
        List<Pair<BlockState, Integer>> dungeonMaterials = new ArrayList<>();
        for (int i = 0; i < this.dungeonMaterials.size(); i++) {
            String value = this.detailsList.children().get(i).dungeonMaterialIDEdit.getValue();
            if (value.isBlank()){
                value = "0";
            }
            dungeonMaterials.add(Pair.of(this.dungeonMaterials.get(i).getFirst(),Integer.parseInt(value)));
        }

        ListTag listTag = new ListTag();
        dungeonMaterials.forEach((blockStateIntegerPair -> {
            if (blockStateIntegerPair.getFirst().is(Blocks.AIR)) {
                return;
            }
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", BuiltInRegistries.BLOCK.getKey(blockStateIntegerPair.getFirst().getBlock()).toString());
            compoundTag.putInt("dungeon_material_id", blockStateIntegerPair.getSecond());
            listTag.add(compoundTag);
        }));
        return listTag;
    }

    @OnlyIn(Dist.CLIENT)
    public class DetailsList extends ObjectSelectionList<DetailsList.Entry>{
        public DetailsList(int width, int height, int fromTop, int itemHeight) {
            super(RoomExportScreen.this.minecraft, width, height, fromTop, itemHeight);

            RoomExportScreen.this.dungeonMaterials.forEach(blockStateIntegerPair -> {
                this.addEntry(new Entry(blockStateIntegerPair.getSecond()));
            });
        }

        @OnlyIn(Dist.CLIENT)
        class Entry extends ObjectSelectionList.Entry<Entry>{
            public EditBox dungeonMaterialIDEdit;

            public Entry(int dungeonMatID) {
                this.dungeonMaterialIDEdit = new EditBox(RoomExportScreen.this.font, 0, 0, 20, 20, Component.translatable("room_export_wand.dungeon_material_id")) {
                    public boolean charTyped(char charTyped, int modifiers) {
                        return Character.isDigit(charTyped) && super.charTyped(charTyped, modifiers);
                    }
                };
                this.dungeonMaterialIDEdit.setMaxLength(128);
                this.dungeonMaterialIDEdit.setValue(Integer.toString(dungeonMatID));
                //todo checkboxes for which material type to use? stairs, slabs, walls, etc
            }

            @Override
            public @NotNull Component getNarration() {
                ItemStack itemStack = getDisplayItem(RoomExportScreen.this.dungeonMaterials.get(RoomExportScreen.this.dungeonMaterials.size() - RoomExportScreen.DetailsList.this.children().indexOf(this) - 1).getFirst());
                return !itemStack.isEmpty() ? Component.translatable("narrator.select", itemStack.getHoverName()) : CommonComponents.EMPTY;
            }

            @Override
            public void render(
                    @NotNull GuiGraphics guiGraphics,
                    int index,
                    int top,
                    int left,
                    int width,
                    int height,
                    int mouseX,
                    int mouseY,
                    boolean hovering,
                    float partialTick
            ) {
                BlockState blockState = RoomExportScreen.this.dungeonMaterials.get(index).getFirst();
                this.blitSlot(guiGraphics, left, top, getDisplayItem(blockState));
                guiGraphics.drawString(
                        RoomExportScreen.this.font,
                        blockState.getBlock().getName().getString(),
                        left + 20,
                        top + 6,
                        new Color(255, 255, 255, 255).getRGB()
                );
                this.dungeonMaterialIDEdit.setPosition(left + width - 20, top);
                this.dungeonMaterialIDEdit.active = this.dungeonMaterialIDEdit.visible = mode == StructureMode.SAVE && top >= RoomExportScreen.this.detailsList.getY() && top <= RoomExportScreen.this.detailsList.getY() + RoomExportScreen.this.detailsList.height - 20;
            }

            private ItemStack getDisplayItem(BlockState state) {
                Item item = state.getBlock().asItem();
                if (item == Items.AIR) {
                    if (state.is(Blocks.WATER)) {
                        item = Items.WATER_BUCKET;
                    } else if (state.is(Blocks.LAVA)) {
                        item = Items.LAVA_BUCKET;
                    }
                }

                return new ItemStack(item);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
//                DetailsList.this.setSelected(this);
//                return super.mouseClicked(mouseX, mouseY, button);
                return false;
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