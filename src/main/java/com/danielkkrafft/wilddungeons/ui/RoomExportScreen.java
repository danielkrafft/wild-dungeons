package com.danielkkrafft.wilddungeons.ui;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.item.RoomExportWand;
import com.danielkkrafft.wilddungeons.network.ServerPacketHandler;
import com.danielkkrafft.wilddungeons.network.SimplePacketManager;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.world.dimension.tools.ReflectionBuddy;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplateManager;
import com.google.common.collect.ImmutableList;
import net.minecraft.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial.BlockSetting.BlockType.*;

public class RoomExportScreen extends Screen {
    private final ItemStack roomExportWand;
    private final List<DungeonMaterial.BlockSetting> selectedBlockSettings;


    private static final Component NAME_LABEL = Component.translatable("room_export_wand.name_label");
    private static final Component MODE_LABEL = Component.translatable("room_export_wand.mode_label");
    private static final ImmutableList<StructureMode> ALL_MODES = ImmutableList.of(StructureMode.SAVE, StructureMode.LOAD, StructureMode.DATA);
    private StructureMode mode = StructureMode.SAVE;
    private CycleButton<StructureMode> modeButton;

    private EditBox nameEdit;
    private boolean confirmAction = false;

    private static final Component MATERIALS_LABEL = Component.translatable("room_export_wand.materials_label");
    private static final Component MATERIAL_INDEX_LABEL = Component.translatable("room_export_wand.dungeon_material_index_label");
    private static final Component MATERIAL_BLOCK_TYPE = Component.translatable("room_export_wand.dungeon_material_block_type");
    private Button saveButton;
    private SelectedRegionDetailList selectedRegionDetailList;

    private static final Component ROOM_LABEL = Component.translatable("room_export_wand.predefined_room_label");
    private Button loadButton;
    private Checkbox loadWithMaterials;
    private SelectedMaterialDetailList dungeonMaterialList;
    private WDDropdown materialDropdown;
    private WDDropdown resourcePackDropdown;

    private Button cancelButton;

    public RoomExportScreen(ItemStack roomExportWand, List<DungeonMaterial.BlockSetting> selectedBlockSettings) {
        super(CommonComponents.EMPTY);
        this.roomExportWand = roomExportWand;
        this.selectedBlockSettings = selectedBlockSettings;
        this.mode = RoomExportWand.getMode(roomExportWand);
    }

    @Override
    protected void init() {
        modeButton = addRenderableWidget(CycleButton.<StructureMode>builder(structureMode -> Component.translatable("structure_block.mode." + structureMode.getSerializedName()))
                .withValues(ALL_MODES)
                .displayOnlyValue()
                .withInitialValue(mode)
                .create(5, 15, 50, 20, Component.translatable("structure_block.mode"), (button, mode) -> {
                    updateMode(mode);
                }));
        nameEdit = new EditBox(font, 60, 15, width - 120, 20, Component.translatable("structure_block.structure_name")) {
            public boolean charTyped(char charTyped, int modifiers) {
                return RoomExportScreen.this.isValidCharacterForName(getValue(), charTyped, getCursorPosition()) && super.charTyped(charTyped, modifiers);
            }
        };
        nameEdit.setMaxLength(128);
        nameEdit.setValue(RoomExportWand.getRoomName(roomExportWand));
        addWidget(nameEdit);

        saveButton = addRenderableWidget(Button.builder(Component.translatable("structure_block.button.save"), button -> {
            confirmAction = true;
            onDone();
        }).bounds(width - 55, 15, 50, 20).build());
        selectedRegionDetailList = addRenderableWidget(new SelectedRegionDetailList(width-10, height - 80, 75, 24));
        selectedRegionDetailList.children().forEach(selectionRegionBlockEntry -> {
            addRenderableWidget(selectionRegionBlockEntry.dungeonMaterialIDEdit);
            selectionRegionBlockEntry.dungeonMaterialIDEdit.active = selectionRegionBlockEntry.dungeonMaterialIDEdit.visible = false;
            addRenderableWidget(selectionRegionBlockEntry.blockTypeButton);
            selectionRegionBlockEntry.blockTypeButton.active = selectionRegionBlockEntry.blockTypeButton.visible = false;
        });

        loadButton = addRenderableWidget(Button.builder(Component.translatable("structure_block.button.load"), button -> {
            confirmAction = true;
            onDone();
        }).bounds(width - 55, 15, 50, 20).build());
        int materialIndex = RoomExportWand.getMaterialIndex(roomExportWand);
        loadWithMaterials = addRenderableWidget(Checkbox.builder(Component.translatable("room_export_wand.button.load_with_materials"), font)
                .pos(60, 38)
                .selected(materialIndex != -1)
                .onValueChange(((checkbox, b) -> updateMode(mode))).build());
        if (materialIndex == -1) {
            materialIndex = 0;
        }
        materialDropdown = addRenderableWidget(new WDDropdown(minecraft, 5, 59, 200, 20, Component.translatable("room_export_wand.dungeon_materials")));
        materialDropdown.setOptions(DungeonMaterialRegistry.dungeonMaterials.stream().map(dungeonMaterial ->(Component)Component.literal(dungeonMaterial.name())).toList());
        materialDropdown.setSelectedIndex(materialIndex);
        materialDropdown.setMaxVisibleOptions(7);
        dungeonMaterialList = addRenderableWidget(new SelectedMaterialDetailList(width-10, height - 90, 85, 30, materialIndex));
        materialDropdown.setSelectionChangeListener(index -> {
            dungeonMaterialList.setMaterialIndex(index);
        });


        resourcePackDropdown = addRenderableWidget(new WDDropdown(minecraft, width-205, 59, 200, 20, Component.translatable("room_export_wand.dungeon_materials")));
        Map<ResourceLocation, Resource> resourceLocationPackResourcesMap = DungeonSessionManager.getInstance().server.getResourceManager().listResources("structure", (resourceLocation -> resourceLocation.getNamespace().equals("wilddungeons") && resourceLocation.getPath().endsWith(".nbt")));
        ArrayList<Component> resourceLocations = WDStructureTemplateManager.INSTANCE.listGenerated().map(resourceLocation -> Component.literal(resourceLocation.getPath().replace("structure/", "").replace(".nbt", ""))).collect(Collectors.toCollection(ArrayList::new));
        for (ResourceLocation resourceLocation : resourceLocationPackResourcesMap.keySet()) {
            resourceLocations.add(Component.literal(resourceLocation.getPath().replace("structure/", "").replace(".nbt", "")));
        }
        resourceLocations = new ArrayList<>(resourceLocations.stream().distinct().toList());
        //remove duplicates
        resourceLocations.sort(Comparator.comparing(Component::getString));
        resourcePackDropdown.setOptions(resourceLocations);
        resourcePackDropdown.setMaxVisibleOptions(7);
        resourcePackDropdown.setSelectionChangeListener(index -> {
            nameEdit.setValue(resourcePackDropdown.getOptions().get(index).getString());
        });

        cancelButton = addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            confirmAction = false;
            onClose();
        }).bounds(5, 36, 50, 20).build());

        updateMode(mode);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //permanent render
        guiGraphics.drawString(font, NAME_LABEL, 60, 5, new Color(0, 255, 233, 255).getRGB());
        nameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, MODE_LABEL, 5, 5, new Color(255, 255, 255, 255).getRGB());
        //dynamic render
        switch (mode){
            case SAVE -> {
                guiGraphics.drawString(font, MATERIALS_LABEL, 5, 60, new Color(255, 255, 255, 255).getRGB());
                guiGraphics.drawString(font, MATERIAL_INDEX_LABEL, width - 65, 60, new Color(255, 255, 255, 255).getRGB());
                guiGraphics.drawString(font, MATERIAL_BLOCK_TYPE, width - 140 , 60, new Color(255, 255, 255, 255).getRGB());
            }
            case LOAD -> {
                guiGraphics.drawString(font, ROOM_LABEL , width - 205, 45, new Color(255, 255, 255, 255).getRGB());
            }
            case DATA -> {
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void updateMode(StructureMode mode) {
        saveButton.active = saveButton.visible = false;
        selectedRegionDetailList.active = selectedRegionDetailList.visible = false;
        selectedRegionDetailList.children().forEach(selectionRegionBlockEntry -> {
            selectionRegionBlockEntry.dungeonMaterialIDEdit.active = selectionRegionBlockEntry.dungeonMaterialIDEdit.visible = false;
            selectionRegionBlockEntry.blockTypeButton.active = selectionRegionBlockEntry.blockTypeButton.visible = false;
        });

        loadButton.active = loadButton.visible = false;
        loadWithMaterials.active = loadWithMaterials.visible = false;
        materialDropdown.active = materialDropdown.visible = false;
        dungeonMaterialList.active = dungeonMaterialList.visible = false;
        resourcePackDropdown.active = resourcePackDropdown.visible = false;
        this.mode = mode;

        switch (mode) {
            case SAVE -> {
                saveButton.active = saveButton.visible = true;
                selectedRegionDetailList.active = selectedRegionDetailList.visible = true;
            }
            case LOAD -> {
                loadButton.active = loadButton.visible = true;
                loadWithMaterials.active = loadWithMaterials.visible = true;
                materialDropdown.active = materialDropdown.visible = loadWithMaterials.selected();
                dungeonMaterialList.active = dungeonMaterialList.visible = loadWithMaterials.selected();
                resourcePackDropdown.active = resourcePackDropdown.visible = true;
            }
            case DATA -> {
                //todo room properties go here
            }
        }
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String s = nameEdit.getValue();
        super.resize(minecraft, width, height);
        init(minecraft, width, height);
        nameEdit.setValue(s);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
    }

    @Override
    protected void setInitialFocus() {
        setInitialFocus(nameEdit);
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
            confirmAction = true;
            onDone();
            return true;
        }
    }

    private void onDone() {
        switch (mode) {
            case SAVE -> sendToServer(StructureBlockEntity.UpdateType.SAVE_AREA);
            case LOAD -> sendToServer(StructureBlockEntity.UpdateType.LOAD_AREA);
            case DATA -> sendToServer(StructureBlockEntity.UpdateType.UPDATE_DATA);
        }
        assert minecraft != null;
        minecraft.setScreen(null);
    }

    private void sendToServer(StructureBlockEntity.UpdateType updateType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("packet", ServerPacketHandler.Packets.ROOM_EXPORT_WAND_CLOSE.toString());
        tag.putString("updateType", updateType.toString());
        tag.putInt("mode", mode.ordinal());
        tag.putString("roomName", nameEdit.getValue());
        tag.putBoolean("confirmAction", confirmAction);
        confirmAction = false;

        switch (updateType){
            case SAVE_AREA, UPDATE_DATA -> {
                ListTag listTag = getDungeonMaterialsTag();
                tag.put("dungeonMaterials", listTag);
            }
            case LOAD_AREA -> {
                tag.putBoolean("loadWithMaterials", loadWithMaterials.selected());
                tag.putInt("materialIndex", materialDropdown.getSelectedIndex());
            }
        }

        PacketDistributor.sendToServer(new SimplePacketManager.ServerboundTagPacket(tag));
    }

    private ListTag getDungeonMaterialsTag() {
        ListTag listTag = new ListTag();

        for (int i = 0; i < selectedBlockSettings.size(); i++) {
            SelectedRegionDetailList.SelectionRegionBlockEntry selectionRegionBlockEntry = selectedRegionDetailList.children().get(i);
            String value = selectionRegionBlockEntry.dungeonMaterialIDEdit.getValue();
            int intValue = 0;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException ignored){}
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", BuiltInRegistries.BLOCK.getKey(selectedBlockSettings.get(i).blockState.getBlock()).toString());
            compoundTag.putInt("dungeon_material_id", intValue);
            compoundTag.putInt("blockType", selectedBlockSettings.get(i).blockType.ordinal());

            listTag.add(compoundTag);
        }
        return listTag;
    }




    @OnlyIn(Dist.CLIENT)
    public class SelectedRegionDetailList extends ObjectSelectionList<SelectedRegionDetailList.SelectionRegionBlockEntry>{
        public SelectedRegionDetailList(int width, int height, int fromTop, int itemHeight) {
            super(RoomExportScreen.this.minecraft, width, height, fromTop, itemHeight);

            RoomExportScreen.this.selectedBlockSettings.forEach(blockSetting -> {
                addEntry(new SelectionRegionBlockEntry(blockSetting));
            });
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            if (!this.active || !this.visible) {
                return false;
            }
            return super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public int getRowWidth() {
            return width-20;
        }

        @OnlyIn(Dist.CLIENT)
        class SelectionRegionBlockEntry extends ObjectSelectionList.Entry<SelectionRegionBlockEntry>{
            public EditBox dungeonMaterialIDEdit;
            public CycleButton<DungeonMaterial.BlockSetting.BlockType> blockTypeButton;

            public SelectionRegionBlockEntry(DungeonMaterial.BlockSetting blockSetting) {
                dungeonMaterialIDEdit = new EditBox(RoomExportScreen.this.font, 0, 0, 30, 20, Component.translatable("room_export_wand.dungeon_material_id")) {
                    public boolean charTyped(char charTyped, int modifiers) {
                        return (Character.isDigit(charTyped) || charTyped == '-') && super.charTyped(charTyped, modifiers);
                    }
                };
                dungeonMaterialIDEdit.setMaxLength(128);
                dungeonMaterialIDEdit.setValue(Integer.toString(blockSetting.materialIndex));
                blockTypeButton = CycleButton.<DungeonMaterial.BlockSetting.BlockType>builder(blockType -> Component.empty())
                        .withValues(DungeonMaterial.BlockSetting.BlockType.values())
                        .displayOnlyValue()
                        .withInitialValue(blockSetting.blockType)
                        .create(0, 0, 30, 20, Component.translatable("room_export_wand.block_type"), (button, blockType) -> {
                            blockSetting.blockType = blockType;
                        });
            }

            @Override
            public @NotNull Component getNarration() {
                ItemStack itemStack = getDisplayItem(RoomExportScreen.this.selectedBlockSettings.get(RoomExportScreen.this.selectedBlockSettings.size() - SelectedRegionDetailList.this.children().indexOf(this) - 1).blockState);
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
                BlockState blockState = RoomExportScreen.this.selectedBlockSettings.get(index).blockState;
                RoomExportScreen.blitSlot(guiGraphics, left, top, getDisplayItem(blockState));
                guiGraphics.drawString(
                        RoomExportScreen.this.font,
                        blockState.getBlock().getName().getString(),
                        left + 20,
                        top + 6,
                        new Color(255, 255, 255, 255).getRGB()
                );
                boolean flag = top >= RoomExportScreen.this.selectedRegionDetailList.getY() && top <= RoomExportScreen.this.selectedRegionDetailList.getY() + RoomExportScreen.this.selectedRegionDetailList.height - 20;
                blockTypeButton.setPosition(left + width - 120, top);
                renderButtonItem(guiGraphics,blockTypeButton.getValue(), blockTypeButton);
                blockTypeButton.active = blockTypeButton.visible = flag;
                dungeonMaterialIDEdit.setPosition(left + width - 50, top);
                dungeonMaterialIDEdit.active = dungeonMaterialIDEdit.visible = flag && blockTypeButton.getValue() != DungeonMaterial.BlockSetting.BlockType.NONE;
            }

            private void renderButtonItem(GuiGraphics guiGraphics, DungeonMaterial.BlockSetting.BlockType blockType, CycleButton<?> button){
                int x = button.getX() + button.getWidth() / 2 - 8;
                int y = button.getY() + button.getHeight() / 2 - 8;
                new ItemStack(Items.BARRIER);
                ItemStack itemStack = switch (blockType) {
                    case NONE -> Items.BARRIER.getDefaultInstance();
                    case BASIC -> Items.STONE_BRICKS.getDefaultInstance();
                    case STAIR -> Items.STONE_BRICK_STAIRS.getDefaultInstance();
                    case SLAB -> Items.STONE_BRICK_SLAB.getDefaultInstance();
                    case WALL -> Items.STONE_BRICK_WALL.getDefaultInstance();
                    case LIGHT -> Items.TORCH.getDefaultInstance();
                    case HANGING_LIGHT -> Items.LANTERN.getDefaultInstance();
                    case HIDDEN -> Items.CHEST.getDefaultInstance();
                };
                guiGraphics.renderFakeItem(itemStack, x, y);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return false;
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public class SelectedMaterialDetailList extends ObjectSelectionList<SelectedMaterialDetailList.MaterialDetailEntry>{
        private int materialIndex = 0;
        public SelectedMaterialDetailList(int width, int height, int fromTop, int itemHeight, int materialIndex) {
            super(RoomExportScreen.this.minecraft, width, height, fromTop, itemHeight);
            this.setMaterialIndex(materialIndex);
        }

        @Override
        public int getRowWidth() {
            return this.width-20;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            if (!this.active || !this.visible || materialDropdown.isIsCoveredByDropdown((int) mouseX, (int) mouseY, this.width) || resourcePackDropdown.isIsCoveredByDropdown((int) mouseX, (int) mouseY, this.width)){
                return false;
            }
            return super.isMouseOver(mouseX, mouseY);
        }

        public void setMaterialIndex(int valueInt) {
            this.materialIndex = valueInt;
            List<MaterialDetailEntry> children = new ArrayList<>(this.children());
            children.forEach(this::removeEntry);
            setupMaterialEntries();
        }

        public void setupMaterialEntries(){
            DungeonMaterial dungeonMaterial = getMaterial();
            addEntryPool(dungeonMaterial.basicBlockStates, BASIC);
            addEntryPool(dungeonMaterial.stairBlockStates, STAIR);
            addEntryPool(dungeonMaterial.slabBlockStates, SLAB);
            addEntryPool(dungeonMaterial.wallBlockStates, WALL);
            addEntryPool(dungeonMaterial.lightBlockStates, LIGHT);
            addEntryPool(dungeonMaterial.hangingLightBlockStates, HANGING_LIGHT);
            addEntryPool(dungeonMaterial.hiddenBlockStates, HIDDEN);
        }

        private void addEntryPool(ArrayList<WeightedPool<BlockState>> weightedPools, DungeonMaterial.BlockSetting.BlockType blockType) {
            if (weightedPools != null) {
                for (int i = 0; i < weightedPools.size(); i++) {
                    WeightedPool<BlockState> pool = weightedPools.get(i);
                    addEntry(new MaterialDetailEntry(blockType, pool, i));
                }
            } else {
                addEntry(new MaterialDetailEntry(blockType, WeightedPool.of(Blocks.AIR.defaultBlockState()), 0));
            }
        }

        public DungeonMaterial getMaterial(){
            return DungeonMaterialRegistry.dungeonMaterials.get(materialIndex);
        }

        @OnlyIn(Dist.CLIENT)
        class MaterialDetailEntry extends ObjectSelectionList.Entry<MaterialDetailEntry>{
            private static final String BASIC = "dungeon_material.basic";
            private static final String STAIR = "dungeon_material.stair";
            private static final String SLAB = "dungeon_material.slab";
            private static final String WALL = "dungeon_material.wall";
            private static final String LIGHT = "dungeon_material.light";
            private static final String HANGING_LIGHT = "dungeon_material.hanging_light";
            private static final String HIDDEN = "dungeon_material.hidden";
            private DungeonMaterial.BlockSetting.BlockType blockType = DungeonMaterial.BlockSetting.BlockType.NONE;
            private final List<BlockState> blockStates = new ArrayList<>();
            private final int index;

            public MaterialDetailEntry(DungeonMaterial.BlockSetting.BlockType blockType, WeightedPool<BlockState> pool , int index) {
                this.blockType = blockType;
                this.index = index+1;
                blockStates.addAll(pool.getAll());
            }

            @Override
            public @NotNull Component getNarration() {
                return getBlockTypeComponent();
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
                // Only render if within parent list's visible area and not covered by dropdown
                if (top >= SelectedMaterialDetailList.this.getY() &&
                        top + height <= SelectedMaterialDetailList.this.getY() + SelectedMaterialDetailList.this.height) {
                    int y = top + height - 20;
                    int maxItemsPerRow = Math.min(blockStates.size(), Math.max(1, (width - 10) / 20));
                        for (int i = 0; i < maxItemsPerRow; i++) {
                            int x = left + i * 20;
                            boolean isCoveredByDropdown = materialDropdown.isIsCoveredByDropdown(x, y, 18) || resourcePackDropdown.isIsCoveredByDropdown(x, y, 18);
                            if (!isCoveredByDropdown)
                                RoomExportScreen.blitSlot(guiGraphics, x, y, blockStates.get(i).getBlock().asItem().getDefaultInstance());
                        }
                        int widthOfText = font.width(getBlockTypeComponent());
                        if (!materialDropdown.isIsCoveredByDropdown(left + 5, top, widthOfText) && !resourcePackDropdown.isIsCoveredByDropdown(left + 5, top, widthOfText)) {
                        guiGraphics.drawString(
                                RoomExportScreen.this.font,
                                getBlockTypeComponent(),
                                left + 5,
                                top,
                                new Color(255, 255, 255, 255).getRGB()
                        );
                    }
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return false;
            }

            @Override
            public boolean isMouseOver(double mouseX, double mouseY) {
                return false;
            }

            private Component getBlockTypeComponent(){
                return switch (blockType) {
                    case BASIC -> Component.translatable(BASIC, index);
                    case STAIR -> Component.translatable(STAIR, index);
                    case SLAB -> Component.translatable(SLAB, index);
                    case WALL -> Component.translatable(WALL, index);
                    case LIGHT -> Component.translatable(LIGHT, index);
                    case HANGING_LIGHT -> Component.translatable(HANGING_LIGHT, index);
                    case HIDDEN -> Component.translatable(HIDDEN, index);
                    case NONE -> Component.empty();
                };
            }

        }
    }


    public static ItemStack getDisplayItem(BlockState state) {
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

    public static void blitSlot(GuiGraphics guiGraphics, int x, int y, ItemStack stack) {
        guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("container/slot"), x + 1, y + 1, 0, 18, 18);
        if (!stack.isEmpty()) {
            guiGraphics.renderFakeItem(stack, x + 2, y + 2);
        }
    }
}