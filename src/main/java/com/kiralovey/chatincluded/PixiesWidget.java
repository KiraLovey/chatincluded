package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstance;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsItem;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsLayout;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsSection;
import co.casterlabs.rakurai.json.element.JsonElement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PixiesWidget extends Widget {

    static final List<PixiesWidget> instances = new CopyOnWriteArrayList<>();

    @Override
    public void onInit() {
        instances.add(this);
        this.setSettingsLayout(buildLayout());
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public void onNewInstance(WidgetInstance instance) {
        // Forward viewer sprite requests to all config panel instances
        instance.on("pixie_request", (JsonElement data) -> {
            for (PixiesConfigWidget cfg : PixiesConfigWidget.instances) {
                try { cfg.broadcastToAll("pixie_request", data); } catch (Exception ignored) {}
            }
        });
        // Forward active pixie state to config panel
        instance.on("pixie_state", (JsonElement data) -> {
            for (PixiesConfigWidget cfg : PixiesConfigWidget.instances) {
                try { cfg.broadcastToAll("pixie_state", data); } catch (Exception ignored) {}
            }
        });
        // Forward streamer account detection to config panel
        instance.on("streamer_detected", (JsonElement data) -> {
            for (PixiesConfigWidget cfg : PixiesConfigWidget.instances) {
                try { cfg.broadcastToAll("streamer_detected", data); } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        return "/pixies/pixie-overlay.html";
    }

    private WidgetSettingsLayout buildLayout() {
        WidgetSettingsSection appearance = new WidgetSettingsSection("appearance", "Appearance")
                .addItem(WidgetSettingsItem.asCheckbox("hueShift",
                        "Randomise pixie colour (hue-shift per viewer)", true))
                .addItem(WidgetSettingsItem.asCheckbox("showPlatformIcon",
                        "Show platform icon badge on pixie", false))
                .addItem(WidgetSettingsItem.asRange("pixieScale",
                        "Pixie scale (1 = tiny, 6 = huge)", 3, 1, 1, 6))
                .addItem(WidgetSettingsItem.asText("customPixies",
                        "Custom pixies (managed via Config panel)", "[]", "[]"))
                .addItem(WidgetSettingsItem.asText("disabledPixies",
                        "Disabled stock pixies (managed via Config panel)", "[]", "[]"));

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
