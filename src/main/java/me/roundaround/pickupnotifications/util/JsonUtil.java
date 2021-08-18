package me.roundaround.pickupnotifications.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonUtil {
    @Nullable
    public static JsonElement parseJsonFile(File file) {
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            String fileName = file.getAbsolutePath();

            try {
                JsonParser parser = new JsonParser();
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

                JsonElement element = parser.parse(reader);
                reader.close();

                return element;
            } catch (Exception e) {
                PickupNotificationsMod.LOGGER.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    @Nullable
    public static String getStringOrDefault(JsonObject obj, String name, @Nullable String defaultValue) {
        if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
            try {
                return obj.get(name).getAsString();
            } catch (Exception ignore) {
            }
        }

        return defaultValue;
    }
}
