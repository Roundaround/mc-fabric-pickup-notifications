package me.roundaround.pickupnotifications.client.gui.config;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationLine;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class GuiOffsetPositionEditScreen extends PositionEditScreen {
  private final ArrayList<PickupNotificationLine> notifications = new ArrayList<>();
  private final PickupNotificationsConfig config = new PickupNotificationsConfig();

  private GuiOffsetPositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.translatable("pickupnotifications.gui_offset.title"), parent, configOption);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return GuiOffsetPositionEditScreen::new;
  }

  @Override
  protected void init() {
    super.init();

    this.config.GUI_OFFSET.setValue(getValue());

    this.notifications.clear();
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.DIAMOND, 64),
        this.config,
        true));
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.GOLDEN_APPLE, 16),
        this.config,
        true));
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.ELYTRA),
        this.config,
        true));
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);
    this.config.GUI_OFFSET.setValue(this.getValue());
  }

  @Override
  protected void renderBackground(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    this.renderTextureBackground(drawContext, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(drawContext, mouseX, mouseY, partialTicks);

    drawContext.drawCenteredTextWithShadow(this.textRenderer,
        Text.literal(this.getValue().toString()).asOrderedText(),
        this.width / 2,
        this.height / 2,
        GuiUtil.LABEL_COLOR);

    int i = 0;
    for (PickupNotificationLine notification : this.notifications) {
      notification.render(drawContext, i++);
    }
  }
}
