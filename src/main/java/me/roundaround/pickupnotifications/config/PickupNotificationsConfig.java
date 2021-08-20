package me.roundaround.pickupnotifications.config;

import com.google.common.collect.ImmutableList;
import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;

public class PickupNotificationsConfig extends ModConfig {
    public static final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT = new OptionListConfigOption<>("guiAlignment", "config.label.gui_alignment", GuiAlignment.TOP_LEFT);
    public static final IntConfigOption GUI_OFFSET_X = new IntConfigOption("guiOffsetX", "config.label.gui_offset_x", 4);
    public static final IntConfigOption GUI_OFFSET_Y = new IntConfigOption("guiOffsetY", "config.label.gui_offset_y", 4);

    private static final ImmutableList<ConfigOption<?>> ALL_CONFIG_OPTIONS = ImmutableList.of(GUI_ALIGNMENT, GUI_OFFSET_X, GUI_OFFSET_Y);

    public PickupNotificationsConfig() {
        super(PickupNotificationsMod.MOD_INFO);
    }

    @Override
    public ImmutableList<ConfigOption<?>> getConfigOptions() {
        return ALL_CONFIG_OPTIONS;
    }

    public void init() {
        this.loadFromFile();
        this.saveToFile();
    }
}
