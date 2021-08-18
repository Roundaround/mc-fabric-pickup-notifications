package me.roundaround.pickupnotifications.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.config.option.ConfigOption;
import me.roundaround.pickupnotifications.util.JsonUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModConfig {
    private final String modId;
    private final String version;
    private final HashMap<String, ConfigOption<?>> configOptions = new HashMap<>();

    public ModConfig(String modId, String version) {
        this.modId = modId;
        this.version = version;
    }

    public void registerConfigOption(ConfigOption<?> configOption) {
        this.configOptions.put(configOption.getId(), configOption);
    }

    public void registerConfigOptions(ConfigOption<?>... configOptions) {
        this.configOptions.putAll(Arrays.stream(configOptions)
                .collect(Collectors.toMap(ConfigOption::getId, Function.identity())));
    }

    public void loadFromFile() {
        JsonElement element = JsonUtil.parseJsonFile(this.getConfigFile());

        if (element != null && element.isJsonObject()) {
            JsonObject root = element.getAsJsonObject();

            String configVersion = JsonUtil.getStringOrDefault(root, "config_version", "0.0.1");
            // TODO: Upgrade versions as necessary.

            this.configOptions.values().forEach(configOption -> configOption.readFromJsonRoot(root));
        } else {
            this.configOptions.values().forEach(ConfigOption::resetToDefault);
        }
    }

    public boolean saveToFile() {
        JsonObject root = new JsonObject();
        root.add("config_version", new JsonPrimitive(this.version));

        this.configOptions.values().forEach((configOption -> configOption.writeToJsonRoot(root)));

        return JsonUtil.writeJsonToFile(root, this.getConfigFile());
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

    public File getConfigFile() {
        return new File(this.getConfigDirectory(), this.getConfigFileName());
    }
}
