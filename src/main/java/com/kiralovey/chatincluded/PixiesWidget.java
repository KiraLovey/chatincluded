package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstance;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsItem;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsLayout;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsSection;

public class PixiesWidget extends Widget {

    @Override
    public void onInit() {
        this.setSettingsLayout(buildLayout());
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public void onNewInstance(WidgetInstance instance) {}

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        switch (mode) {
            case DOCK:
            case APPLET:
                return "/pixies/pixie-config.html";
            default:
                return "/pixies/pixie-overlay.html";
        }
    }

    private WidgetSettingsLayout buildLayout() {
        WidgetSettingsSection appearance = new WidgetSettingsSection("appearance", "Appearance")
                .addItem(WidgetSettingsItem.asCheckbox("hueShift",
                        "Randomise pixie colour (hue-shift per viewer)", true))
                .addItem(WidgetSettingsItem.asCheckbox("showPlatformIcon",
                        "Show platform icon badge on pixie", false))
                .addItem(WidgetSettingsItem.asRange("spriteScale",
                        "Pixie scale (1 = tiny, 6 = huge)", 3, 1, 1, 6));

        WidgetSettingsSection movement = new WidgetSettingsSection("movement", "Movement")
                .addItem(WidgetSettingsItem.asNumber("walkSpeedMin",
                        "Min walk speed (fraction of screen width per second)", 0.03, 0.01, 0.01, 0.5))
                .addItem(WidgetSettingsItem.asNumber("walkSpeedMax",
                        "Max walk speed (fraction of screen width per second)", 0.07, 0.01, 0.01, 0.5))
                .addItem(WidgetSettingsItem.asNumber("maxPixies",
                        "Max pixies on screen at once", 40, 1, 5, 80));

        WidgetSettingsSection timing = new WidgetSettingsSection("timing", "Timing")
                .addItem(WidgetSettingsItem.asNumber("inactivityMs",
                        "Remove pixie after inactivity (ms)", 90000, 1000, 10000, 600000));

        return new WidgetSettingsLayout()
                .addSection(appearance)
                .addSection(movement)
                .addSection(timing);
    }
}
