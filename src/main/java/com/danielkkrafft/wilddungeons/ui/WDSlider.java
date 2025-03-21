package com.danielkkrafft.wilddungeons.ui;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class WDSlider extends ExtendedSlider {
    private Runnable applyCallback;

    public WDSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    public WDSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    @Override
    protected void applyValue() {
        super.applyValue();
        if (this.applyCallback != null) {
            this.applyCallback.run();
        }
    }

    public void SetApplyCallback(Runnable callback) {
        this.applyCallback = callback;
    }
}
