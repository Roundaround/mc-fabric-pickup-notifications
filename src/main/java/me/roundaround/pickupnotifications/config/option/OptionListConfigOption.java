package me.roundaround.pickupnotifications.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.roundaround.pickupnotifications.config.value.ListOptionValue;

public class OptionListConfigOption extends ConfigOption<ListOptionValue> {
    public OptionListConfigOption(String id, ListOptionValue defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public ListOptionValue deserializeFromJson(JsonElement data) {
        return this.getValue().getFromId(data.getAsString());
    }

    @Override
    public JsonElement serializeToJson() {
        return new JsonPrimitive(this.getId());
    }
}
