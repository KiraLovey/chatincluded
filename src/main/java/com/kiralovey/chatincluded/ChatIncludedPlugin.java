package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.CaffeinatedPlugin;
import co.casterlabs.caffeinated.pluginsdk.CaffeinatedPluginImplementation;
import co.casterlabs.caffeinated.pluginsdk.koi.Koi;
import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetDetails;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetDetails.WidgetDetailsCategory;

@CaffeinatedPluginImplementation
public class ChatIncludedPlugin extends CaffeinatedPlugin {

    private ChatListener chatListener;

    @Override
    public void onInit() {
        getLogger().info("Initialising ChatIncluded v1.0.0...");

        this.getPlugins().registerWidget(
                this,
                new WidgetDetails()
                        .withNamespace("com.kiralovey.chatincluded.settings")
                        .withIcon("language")
                        .withCategory(WidgetDetailsCategory.OTHER)
                        .withFriendlyName("ChatIncluded Settings"),
                ChatIncludedWidget.class
        );

        Koi koi = resolveKoi();
        if (koi == null) {
            getLogger().severe("Could not resolve Koi service -- chat replies will not be sent.");
        } else {
            getLogger().info("Koi service resolved successfully.");
        }

        chatListener = new ChatListener(this, koi);
        this.addKoiListener(chatListener);

        getLogger().info("Ready. Waiting for chat events.");
    }

    private Koi resolveKoi() {
        try {
            Class<?> appClass = Class.forName("co.casterlabs.caffeinated.app.CaffeinatedApp");
            Object appInstance = appClass.getMethod("getInstance").invoke(null);
            Object globalKoi = appClass.getMethod("getKoi").invoke(appInstance);
            return (Koi) globalKoi;
        } catch (Exception e) {
            getLogger().severe("Failed to resolve Koi via reflection: " + e.getMessage());
            return null;
        }
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

    @Override
    public String getName() { return "ChatIncluded"; }

    @Override
    public String getId() { return "com.kiralovey.chatincluded"; }
}
