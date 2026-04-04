package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstance;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsItem;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsLayout;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsSection;
import co.casterlabs.rakurai.json.element.JsonElement;
import co.casterlabs.rakurai.json.element.JsonString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PixiesConfigWidget extends Widget {

    static final List<PixiesConfigWidget> instances = new CopyOnWriteArrayList<>();

    // File-based backup persistence
    private static final Path DATA_DIR = Paths.get(System.getProperty("user.home"), ".chatincluded");

    private static String readFile(String filename) {
        try {
            Path p = DATA_DIR.resolve(filename);
            if (Files.exists(p)) return Files.readString(p);
        } catch (Exception ignored) {}
        return "[]";
    }

    private static void writeFile(String filename, String content) {
        try {
            Files.createDirectories(DATA_DIR);
            Files.writeString(DATA_DIR.resolve(filename), content);
        } catch (Exception ignored) {}
    }

    private static String toJsonStr(JsonElement data) {
        if (data == null) return "[]";
        return data.isJsonString() ? data.getAsString() : data.toString();
    }

    // Chunked export assembly state
    private volatile StringBuilder _exportBuf   = null;
    private volatile int           _exportTotal = 0;
    private final AtomicInteger    _exportRecv  = new AtomicInteger(0);

    @Override
    public void onInit() {
        instances.add(this);
        this.setSettingsLayout(new WidgetSettingsLayout()
                .addSection(new WidgetSettingsSection("appearance", "Pixie Data")
                        .addItem(WidgetSettingsItem.asText("customPixies",
                                "Custom pixies (managed via UI)", "[]", "[]"))
                        .addItem(WidgetSettingsItem.asText("disabledPixies",
                                "Disabled stock pixies (managed via UI)", "[]", "[]"))));
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public void onNewInstance(WidgetInstance instance) {
        // JS requests saved data on init — file is authoritative, settings only as first-install fallback
        instance.on("requestPixieData", (JsonElement ignored) -> {
            // File is written on every add/remove, so it always reflects the latest state.
            // Settings are only consulted if no file exists yet (first install before any save).
            String cp = readFile("custom-pixies.json");
            if (cp.equals("[]")) {
                String s = this.settings().getString("appearance.customPixies", "[]");
                if (s != null && !s.isEmpty() && !s.equals("[]")) cp = s;
            }

            String dp = readFile("disabled-pixies.json");
            if (dp.equals("[]")) {
                String s = this.settings().getString("appearance.disabledPixies", "[]");
                if (s != null && !s.isEmpty() && !s.equals("[]")) dp = s;
            }

            try { this.broadcastToAll("customPixies_fileData",  new JsonString(cp));  } catch (Exception e) {}
            try { this.broadcastToAll("disabledPixies_fileData", new JsonString(dp)); } catch (Exception e) {}
        });

        // Forward approvals and removals to all overlay instances
        instance.on("pixie_approved", (JsonElement data) -> {
            for (PixiesWidget w : PixiesWidget.instances) {
                try { w.broadcastToAll("pixie_approved", data); } catch (Exception ignored) {}
            }
        });
        instance.on("pixie_remove", (JsonElement data) -> {
            for (PixiesWidget w : PixiesWidget.instances) {
                try { w.broadcastToAll("pixie_remove", data); } catch (Exception ignored) {}
            }
        });

        // Chunked export
        instance.on("export_chunk", (JsonElement data) -> {
            try {
                var obj   = data.getAsObject();
                int idx   = obj.getNumber("i").intValue();
                int total = obj.getNumber("n").intValue();
                String chunk = obj.getString("d");

                synchronized (this) {
                    if (idx == 0) {
                        _exportBuf   = new StringBuilder();
                        _exportTotal = total;
                        _exportRecv.set(0);
                    }
                    if (_exportBuf != null) {
                        _exportBuf.append(chunk);
                        int recv = _exportRecv.incrementAndGet();
                        if (recv == _exportTotal) {
                            String dataUrl = _exportBuf.toString();
                            _exportBuf = null;
                            String base64 = dataUrl.substring(dataUrl.indexOf(',') + 1);
                            byte[] bytes  = Base64.getDecoder().decode(base64);
                            Path out = Paths.get(System.getProperty("user.home"), "Downloads", "chatincluded-pixies.png");
                            Files.write(out, bytes);
                            this.broadcastToAll("export_complete", new JsonString(out.toAbsolutePath().toString()));
                        }
                    }
                }
            } catch (Exception e) {
                try { this.broadcastToAll("export_error", new JsonString(
                    e.getMessage() != null ? e.getMessage() : "Unknown error"
                )); } catch (Exception ignored) {}
            }
        });

        // Custom pixie updates: save via Java settings + file, then forward to overlay
        instance.on("customPixies_update", (JsonElement data) -> {
            String json = toJsonStr(data);
            try { this.settings().set("appearance.customPixies", json); } catch (Exception ignored) {}
            writeFile("custom-pixies.json", json);
            for (PixiesWidget w : PixiesWidget.instances) {
                try { w.broadcastToAll("customPixies_update", data); } catch (Exception ignored) {}
            }
        });

        // Disabled pixie updates: save via Java settings + file, then forward to overlay
        instance.on("disabledPixies_update", (JsonElement data) -> {
            String json = toJsonStr(data);
            try { this.settings().set("appearance.disabledPixies", json); } catch (Exception ignored) {}
            writeFile("disabled-pixies.json", json);
            for (PixiesWidget w : PixiesWidget.instances) {
                try { w.broadcastToAll("disabledPixies_update", data); } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        return "/pixies/pixie-config.html";
    }
}
