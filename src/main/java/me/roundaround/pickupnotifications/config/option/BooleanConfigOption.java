package me.roundaround.pickupnotifications.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanConfigOption extends ConfigOption<Boolean> {
    public BooleanConfigOption(String id, boolean defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public Boolean deserializeFromJson(JsonElement data) {
        return data.getAsBoolean();
    }

    @Override
    public JsonElement serializeToJson() {
        return new JsonPrimitive(this.getValue());
    }
}
