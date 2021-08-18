package me.roundaround.pickupnotifications.util;

import com.google.gson.*;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    public static boolean writeJsonToFile(JsonElement root, File file) {
        OutputStreamWriter writer = null;
        File tempFile = new File(file.getParentFile(), UUID.randomUUID() + ".json.tmp");

        try {
            writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8);
            writer.write(GSON.toJson(root));
            writer.close();

            if (file.exists() && file.isFile() && !file.delete()) {
                PickupNotificationsMod.LOGGER.error("Failed to overwrite existing JSON file '{}'", file.getAbsolutePath());
            }

            return tempFile.renameTo(file);
        } catch (Exception e) {
            PickupNotificationsMod.LOGGER.error("Failed to save the JSON file '{}'", file.getAbsolutePath(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                PickupNotificationsMod.LOGGER.warn("Failed to close JSON file stream", e);
            }
        }

        return false;
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
