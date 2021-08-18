package me.roundaround.pickupnotifications.config.value;

import java.util.Arrays;

public enum GuiPosition implements ListOptionValue {
    TOP_LEFT ("top", "left"),
    TOP_CENTER ("top", "center"),
    TOP_RIGHT ("top", "right"),
    MIDDLE_LEFT ("middle", "left"),
    MIDDLE_CENTER ("middle", "center"),
    MIDDLE_RIGHT ("middle", "right"),
    BOTTOM_LEFT ("bottom", "left"),
    BOTTOM_CENTER ("bottom", "center"),
    BOTTOM_RIGHT ("bottom", "right");

    private final String alignmentX;
    private final String alignmentY;
    private final String id;

    GuiPosition(String alignmentX, String alignmentY) {
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.id = "me.roundaround.roundalib.config.gui_position." + alignmentX + "_" + alignmentY;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public GuiPosition getFromId(String id) {
        return fromId(id);
    }

    @Override
    public String getDisplayString() {
        return net.minecraft.client.resource.language.I18n.translate(this.id);
    }
    
    @Override
    public GuiPosition getNext() {
        return values()[this.ordinal() + 1 % values().length];
    }

    @Override
    public GuiPosition getPrev() {
        return values()[this.ordinal() + values().length - 1 % values().length];
    }

    public String getAlignmentX() {
        return alignmentX;
    }

    public String getAlignmentY() {
        return alignmentY;
    }

    public static GuiPosition getDefault() {
        return TOP_LEFT;
    }

    public static GuiPosition fromId(String id) {
        return Arrays.stream(GuiPosition.values())
                .filter(guiPosition -> guiPosition.id.equals(id))
                .findFirst()
                .orElse(getDefault());
    }
}
