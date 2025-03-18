package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomExportScreen extends Screen {
    private static final Component NAME_LABEL = Component.translatable("room_export_wand.name_label");
    private static final Component MATERIALS_LABEL = Component.translatable("room_export_wand.materials_label");

    private final RoomExportWand roomExportWand;
    private List<Pair<BlockState, Integer>> dungeonMaterials;
    private EditBox nameEdit;
    private Button saveButton;
    private DetailsList detailsList;

    public RoomExportScreen(RoomExportWand roomExportWand, List<Pair<BlockState, Integer>> dungeonMaterials) {
        super(CommonComponents.EMPTY);
        //grab data from wand
        this.roomExportWand = roomExportWand;
        this.dungeonMaterials = dungeonMaterials;
        dungeonMaterials.forEach(blockStateIntegerPair -> {
            WildDungeons.getLogger().info("BlockState: {} DungeonMaterialID: {}", blockStateIntegerPair.getFirst().toString(), blockStateIntegerPair.getSecond());
        });
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
        this.detailsList = this.addRenderableWidget(new DetailsList(this.width, this.height - 65, 55, 24));
        detailsList.children().forEach(entry -> {
            this.addRenderableWidget(entry.dungeonMaterialIDEdit);
            entry.dungeonMaterialIDEdit.active = entry.dungeonMaterialIDEdit.visible = false;
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(this.font, NAME_LABEL, this.width / 2 - 153, 10, new Color(0, 255, 233, 255).getRGB());
        this.nameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, MATERIALS_LABEL, this.width / 2 - 153, 45, new Color(255, 255, 255, 255).getRGB());
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
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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

        List<Pair<BlockState, Integer>> dungeonMaterials = new ArrayList<>();
        for (int i = 0; i < this.dungeonMaterials.size(); i++) {
            dungeonMaterials.add(Pair.of(this.dungeonMaterials.get(i).getFirst(),Integer.parseInt(detailsList.children().get(i).dungeonMaterialIDEdit.getValue())));
        }

        ListTag listTag = new ListTag();
        dungeonMaterials.forEach((blockStateIntegerPair -> {
            if (blockStateIntegerPair.getFirst().is(Blocks.AIR)) {
                return;
            }
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("blockstate", NbtUtils.writeBlockState(blockStateIntegerPair.getFirst()));
            compoundTag.putInt("dungeon_material_id", blockStateIntegerPair.getSecond());
            listTag.add(compoundTag);
        }));
        tag.put("dungeonMaterials", listTag);
        PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
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
                int x = 0;
                int y = 0;
                int width = 50;
                int height = 20;
                this.dungeonMaterialIDEdit = new EditBox(RoomExportScreen.this.font, x, y, width, height, Component.translatable("room_export_wand.dungeon_material_id")) {
                    public boolean charTyped(char charTyped, int modifiers) {
                        return Character.isDigit(charTyped) && super.charTyped(charTyped, modifiers);
                    }
                };
                this.dungeonMaterialIDEdit.setMaxLength(128);
                this.dungeonMaterialIDEdit.setValue(Integer.toString(dungeonMatID));
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
                this.dungeonMaterialIDEdit.active = this.dungeonMaterialIDEdit.visible = top >= RoomExportScreen.this.detailsList.getY() && top <= RoomExportScreen.this.detailsList.getY() + RoomExportScreen.this.detailsList.height - 20;
                this.dungeonMaterialIDEdit.setPosition(left + width - 50, top);
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