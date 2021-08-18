package me.roundaround.pickupnotifications.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringConfigOption extends ConfigOption<String> {
    public StringConfigOption(String id, String defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public String deserializeFromJson(JsonElement data) {
        return data.getAsString();
    }

    @Override
    public JsonElement serializeToJson() {
        return new JsonPrimitive(this.getValue());
    }
}
