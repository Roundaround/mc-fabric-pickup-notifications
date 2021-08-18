package me.roundaround.pickupnotifications.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.util.JsonUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.HashMap;

public class ModConfig {
    private final String modId;
    private final String version;

    private HashMap<String, ConfigOption<String>> configOptions = new HashMap<>();

    public ModConfig(String modId, String version) {
        this.modId = modId;
        this.version = version;
    }

    public void loadFromFile() {
        File file = new File(this.getConfigDirectory(), this.getConfigFileName());

        JsonElement element = JsonUtil.parseJsonFile(file);

        if (element != null && element.isJsonObject()) {
            JsonObject root = element.getAsJsonObject();
            String configVersion = JsonUtil.getStringOrDefault(root, "config_version", "0.0.1");

            // TODO: Upgrade versions as necessary.

            for (ConfigOption<String> configOption : configOptions.values()) {
                configOption.setValue(JsonUtil.getStringOrDefault(root, configOption.id, null));
            }
        } else {

            for (ConfigOption<?> configOption : configOptions.values()) {
                configOption.resetToDefault();
            }
        }
    }

    public boolean saveToFile() {
        return false;
    }

    public String getConfigVersion() {
        return this.version;
    }

    public File getConfigDirectory() {
        File dir = FabricLoader.getInstance().getConfigDir().toFile();

        if (!dir.exists() && !dir.mkdirs()) {
            PickupNotificationsMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
        }

        return dir;
    }

    public String getConfigFileName() {
        return modId + ".json";
    }
}
