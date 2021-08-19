package me.roundaround.pickupnotifications.config;

import com.google.common.collect.Lists;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;

import java.util.Collection;

public class PickupNotificationsConfig extends ModConfig {
    public static final OptionListConfigOption GUI_ALIGNMENT = new OptionListConfigOption("guiAlignment", GuiAlignment.TOP_LEFT);
    public static final IntConfigOption GUI_OFFSET_X = new IntConfigOption("guiOffsetX", 4);
    public static final IntConfigOption GUI_OFFSET_Y = new IntConfigOption("guiOffsetY", 4);

    public PickupNotificationsConfig() {
        super(PickupNotificationsMod.MOD_INFO);
    }

    @Override
    protected Collection<ConfigOption<?>> getConfigOptions() {
        return Lists.newArrayList(GUI_ALIGNMENT, GUI_OFFSET_X, GUI_OFFSET_Y);
    }

    public void init() {
        this.loadFromFile();
        this.saveToFile();
    }
}
