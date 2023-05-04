package me.roundaround.pickupnotifications.client.gui.config;

import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationLine;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
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

    config.GUI_OFFSET.setValue(getValue());

    notifications.clear();
    notifications.add(new PickupNotificationLine(new ItemStack(Items.DIAMOND, 64), config, true));
    notifications.add(new PickupNotificationLine(new ItemStack(Items.GOLDEN_APPLE, 16),
        config,
        true));
    notifications.add(new PickupNotificationLine(new ItemStack(Items.ELYTRA), config, true));
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);
    config.GUI_OFFSET.setValue(getValue());
  }

  @Override
  protected void renderBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(matrixStack, mouseX, mouseY, partialTicks);

    drawCenteredTextWithShadow(matrixStack,
        textRenderer,
        Text.literal(getValue().toString()).asOrderedText(),
        width / 2,
        height / 2,
        GuiUtil.LABEL_COLOR);

    int i = 0;
    for (PickupNotificationLine notification : notifications) {
      notification.render(matrixStack, i++);
    }
  }
}
