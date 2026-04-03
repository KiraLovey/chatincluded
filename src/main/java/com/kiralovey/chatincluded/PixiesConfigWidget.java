package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstance;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
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

    // Chunked export assembly state
    private volatile StringBuilder _exportBuf   = null;
    private volatile int           _exportTotal = 0;
    private final AtomicInteger    _exportRecv  = new AtomicInteger(0);

    @Override
    public void onInit() {
        instances.add(this);
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public void onNewInstance(WidgetInstance instance) {
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
        // Chunked export: JS sends 25 KB slices; Java assembles and writes to Downloads
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

        // Forward custom sprite updates to overlay so it reloads + persists
        instance.on("customPixies_update", (JsonElement data) -> {
            for (PixiesWidget w : PixiesWidget.instances) {
                try { w.broadcastToAll("customPixies_update", data); } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        return "/pixies/pixie-config.html";
    }
}
