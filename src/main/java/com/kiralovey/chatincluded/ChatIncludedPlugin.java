package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.Caffeinated;
import co.casterlabs.caffeinated.pluginsdk.CaffeinatedPlugin;
import co.casterlabs.caffeinated.pluginsdk.CaffeinatedPluginImplementation;
import co.casterlabs.caffeinated.pluginsdk.koi.Koi;
import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetDetails;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetDetails.WidgetDetailsCategory;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetType;
import co.casterlabs.commons.functional.tuples.Pair;
import java.io.IOException;

@CaffeinatedPluginImplementation
public class ChatIncludedPlugin extends CaffeinatedPlugin {

    private ChatListener chatListener;

    @Override
    public void onInit() {
        getLogger().info("Initialising ChatIncluded v" + VersionChecker.CURRENT_VERSION + "...");

        this.getPlugins().registerWidget(
                this,
                new WidgetDetails()
                        .withNamespace("com.kiralovey.chatincluded.settings")
                        .withIcon("language")
                        .withCategory(WidgetDetailsCategory.OTHER)
                        .withFriendlyName("ChatIncluded Settings"),
                ChatIncludedWidget.class
        );

        this.getPlugins().registerWidget(
                this,
                new WidgetDetails()
                        .withNamespace("com.kiralovey.chatincluded.pixies")
                        .withIcon("auto_fix_high")
                        .withCategory(WidgetDetailsCategory.OTHER)
                        .withFriendlyName("ChatIncluded Pixies")
                        .withType(WidgetType.WIDGET),
                PixiesWidget.class
        );

        this.getPlugins().registerWidget(
                this,
                new WidgetDetails()
                        .withNamespace("com.kiralovey.chatincluded.pixies.config")
                        .withIcon("auto_fix_high")
                        .withCategory(WidgetDetailsCategory.OTHER)
                        .withFriendlyName("ChatIncluded Pixies Config")
                        .withType(WidgetType.APPLET),
                PixiesConfigWidget.class
        );

        // Clean API call using the public Caffeinated interface from the SDK
        Koi koi = Caffeinated.getInstance().getKoi();

        if (koi == null) {
            getLogger().severe("Could not resolve Koi service -- chat replies will not be sent.");
        } else {
            getLogger().info("Koi service resolved successfully.");
        }

        chatListener = new ChatListener(this, koi);
        this.addKoiListener(chatListener);

        // Wire the UsageTracker reference into any already-registered widget
        wireTrackerToWidgets();

        getLogger().info("Ready. Waiting for chat events.");

        // Check for updates asynchronously
        VersionChecker.checkAsync(this);
    }

    @Override
    public void onClose() {
        getLogger().info("Shutting down ChatIncluded.");
        if (chatListener != null) {
            this.removeKoiListener(chatListener);
            chatListener.shutdown();
        }
    }

    public PluginSettings readSettings() {
        try {
            for (Widget w : this.getWidgets()) {
                if ("com.kiralovey.chatincluded.settings".equals(w.getNamespace())) {
                    return ((ChatIncludedWidget) w).readSettings();
                }
            }
        } catch (Exception ignored) {}
        return new PluginSettings();
    }

    /**
     * Calls refreshStats() on the settings widget so the Stats section
     * displays current tracker values.
     */
    public void refreshWidgetStats() {
        try {
            for (Widget w : this.getWidgets()) {
                if ("com.kiralovey.chatincluded.settings".equals(w.getNamespace())) {
                    ((ChatIncludedWidget) w).refreshStats();
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Injects the UsageTracker reference into all registered settings widgets.
     * Called after the chatListener is constructed so the tracker is available.
     */
    private void wireTrackerToWidgets() {
        try {
            if (chatListener == null) return;
            UsageTracker tracker = chatListener.getTracker();
            for (Widget w : this.getWidgets()) {
                if ("com.kiralovey.chatincluded.settings".equals(w.getNamespace())) {
                    ((ChatIncludedWidget) w).setTrackerRef(tracker);
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public Pair<String, String> getResource(String path) throws IOException {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return super.getResource(path);
    }

    @Override
    public String getName() { return "ChatIncluded"; }

    @Override
    public String getId() { return "com.kiralovey.chatincluded"; }
}
