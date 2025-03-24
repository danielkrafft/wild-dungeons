package com.danielkkrafft.wilddungeons.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WDDropdown extends AbstractWidget {
    private final List<Component> options;
    private int selectedIndex;
    private boolean isExpanded;
    private int maxVisibleOptions = 5;
    private int scrollOffset = 0;
    private Consumer<Integer> selectionChangeListener;
    protected final Minecraft minecraft;


    public WDDropdown(Minecraft minecraft,int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.minecraft = minecraft;
        this.options = new ArrayList<>();
        this.selectedIndex = -1;
        this.isExpanded = false;
    }

    public void addOption(Component option) {
        options.add(option);
        if (selectedIndex == -1 && !options.isEmpty()) {
            selectedIndex = 0;
        }
    }

    public void setOptions(List<Component> newOptions) {
        options.clear();
        options.addAll(newOptions);
        selectedIndex = options.isEmpty() ? -1 : 0;
    }

    public int calculateMaxDisplayableOptions(int parentHeight){
        return Math.min(options.size(), Math.max(1, (parentHeight - getY() - 30) / 20));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Component getSelectedOption() {
        return selectedIndex >= 0 && selectedIndex < options.size() ? options.get(selectedIndex) : Component.empty();
    }

    public void setSelectedIndex(int index) {
        if (index >= -1 && index < options.size()) {
            selectedIndex = index;
            if (selectionChangeListener != null) {
                selectionChangeListener.accept(selectedIndex);
            }
        }
    }

    public void setSelectionChangeListener(Consumer<Integer> listener) {
        this.selectionChangeListener = listener;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw main dropdown button
        int backgroundColor = isHovered ? 0xFF3C3C3C : 0xFF2C2C2C;
        int borderColor = isFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;

        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + 1, borderColor); // Top
        guiGraphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor); // Bottom
        guiGraphics.fill(getX(), getY(), getX() + 1, getY() + height, borderColor); // Left
        guiGraphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor); // Right

        // Draw selected option or placeholder
        Component displayText = selectedIndex >= 0 ? options.get(selectedIndex) : getMessage();
        int textY = getY() + (height - 8) / 2;
        guiGraphics.drawString(minecraft.font, displayText, getX() + 5, textY, 0xFFFFFFFF);

        // Draw dropdown arrow
        String arrow = isExpanded ? "▲" : "▼";
        guiGraphics.drawString(minecraft.font, arrow, getX() + width - 12, textY, 0xFFFFFFFF);

        // Draw dropdown list if expanded
        if (isExpanded && !options.isEmpty()) {
            int dropdownY = getY() + height;
            int visibleOptions = Math.min(options.size(), maxVisibleOptions);
            int dropdownHeight = visibleOptions * height;

            // Background
            guiGraphics.fill(getX(), dropdownY, getX() + width, dropdownY + dropdownHeight, 0xFF1C1C1C);
            guiGraphics.fill(getX(), dropdownY, getX() + width, dropdownY + 1, borderColor); // Top
            guiGraphics.fill(getX(), dropdownY + dropdownHeight - 1, getX() + width, dropdownY + dropdownHeight, borderColor); // Bottom
            guiGraphics.fill(getX(), dropdownY, getX() + 1, dropdownY + dropdownHeight, borderColor); // Left
            guiGraphics.fill(getX() + width - 1, dropdownY, getX() + width, dropdownY + dropdownHeight, borderColor); // Right

            // Options
            for (int i = 0; i < visibleOptions; i++) {
                int optionIndex = i + scrollOffset;
                if (optionIndex >= options.size()) break;

                int optionY = dropdownY + (i * height);
                boolean isHovered = mouseX >= getX() && mouseX <= getX() + width &&
                        mouseY >= optionY && mouseY <= optionY + height;
                boolean isSelected = optionIndex == selectedIndex;

                if (isHovered || isSelected) {
                    guiGraphics.fill(getX() + 1, optionY + 1, getX() + width - 1, optionY + height - 1,
                            isSelected ? 0xFF505050 : 0xFF404040);
                }

                guiGraphics.drawString(minecraft.font, options.get(optionIndex),
                        getX() + 5, optionY + (height - 8) / 2, 0xFFFFFFFF);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narration.dropdown", getMessage()));
        if (selectedIndex >= 0) {
            narrationElementOutput.add(NarratedElementType.HINT, Component.translatable("narration.selection", options.get(selectedIndex)));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isExpanded && options.size() > maxVisibleOptions) {
            int maxScroll = options.size() - maxVisibleOptions;
            if (scrollY < 0 && scrollOffset < maxScroll) {
                scrollOffset++;
                return true;
            } else if (scrollY > 0 && scrollOffset > 0) {
                scrollOffset--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isExpanded) {
            if (keyCode == 256) { // Escape
                isExpanded = false;
                return true;
            } else if (keyCode == 265) { // Up arrow
                if (selectedIndex > 0) {
                    setSelectedIndex(selectedIndex - 1);
                    if (selectedIndex < scrollOffset) {
                        scrollOffset = selectedIndex;
                    }
                    return true;
                }
            } else if (keyCode == 264) { // Down arrow
                if (selectedIndex < options.size() - 1) {
                    setSelectedIndex(selectedIndex + 1);
                    if (selectedIndex >= scrollOffset + maxVisibleOptions) {
                        scrollOffset = selectedIndex - maxVisibleOptions + 1;
                    }
                    return true;
                }
            } else if (keyCode == 257 || keyCode == 32) { // Enter or Space
                isExpanded = false;
                return true;
            }
        } else if (keyCode == 257 || keyCode == 32) { // Enter or Space when collapsed
            Expand();
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (isExpanded){
            int dropdownY = getY() + height;
            int visibleOptions = Math.min(options.size(), maxVisibleOptions);
            return mouseX >= getX() && mouseX <= getX() + width &&
                    mouseY >= getY() && mouseY <= dropdownY + (visibleOptions * height);
        } else {
            return super.isMouseOver(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible) {
            return false;
        }
        if (isMouseOver(mouseX, mouseY)){
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }

        if (isExpanded) {
            int dropdownY = getY() + height;
            int visibleOptions = Math.min(options.size(), maxVisibleOptions);

            // Check if click is within the dropdown list
            if (mouseX >= getX() && mouseX <= getX() + width &&
                    mouseY >= dropdownY && mouseY <= dropdownY + (visibleOptions * height)) {

                int clickedIndex = (int)((mouseY - dropdownY) / height) + scrollOffset;
                if (clickedIndex >= 0 && clickedIndex < options.size()) {
                    setSelectedIndex(clickedIndex);
                }
            }
            isExpanded = false;
            return true;
        } else if (mouseX >= getX() && mouseX <= getX() + width &&
                mouseY >= getY() && mouseY <= getY() + height) {
            // Click on closed dropdown, expand it
            Expand();
            return true;
        }

        return false;
    }

    public void Expand(){
        isExpanded = true;
        scrollOffset = Math.min(options.size() - maxVisibleOptions, selectedIndex);
    }

    public void setMaxVisibleOptions(int maxOptions) {
        this.maxVisibleOptions = Math.max(1, maxOptions);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public int getMaxVisibleOptions(){
        return maxVisibleOptions;
    }

    public List<Component> getOptions() {
        return options;
    }

    /**
     * Gets the height of the expanded dropdown area.
     * @return The height in pixels of the dropdown when expanded, or 0 if not expanded
     */
    public int getExpandedDropdownHeight() {
        if (!isExpanded) {
            return 0;
        }

        int visibleOptions = Math.min(options.size(), maxVisibleOptions);
        return visibleOptions * height;
    }

    public boolean isIsCoveredByDropdown(int x, int y, int width) {
        int dropdownY = getY() + getHeight();
        int dropdownX = getX();

        // Check if the dropdown is expanded and there's an overlap
        return isExpanded() &&
                y >= dropdownY &&
                y <= dropdownY + getExpandedDropdownHeight() &&
                x + width > dropdownX &&
                x < dropdownX + getWidth();
    }
}
