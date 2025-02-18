package me.roundaround.pickupnotifications.client.gui.screen;

import me.roundaround.pickupnotifications.client.gui.hud.MockInGameHud;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationLine;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Map;

import static me.roundaround.roundalib.config.value.Position.Direction;

public class GuiOffsetPositionEditScreen extends PositionEditScreen {
  private static final Map<Direction, Direction> OPPOSITE_DIRECTIONS = Map.of(
      Direction.UP,
      Direction.DOWN,
      Direction.DOWN,
      Direction.UP,
      Direction.LEFT,
      Direction.RIGHT,
      Direction.RIGHT,
      Direction.LEFT
  );

  private final ArrayList<PickupNotificationLine> notifications = new ArrayList<>();

  private MockInGameHud inGameHud;

  private GuiOffsetPositionEditScreen(ConfigScreen parent, PositionConfigOption option) {
    super(Text.translatable("pickupnotifications.guiOffset.title"), parent, option);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return GuiOffsetPositionEditScreen::new;
  }

  @Override
  protected void init() {
    super.init();

    this.inGameHud = new MockInGameHud(this.textRenderer);

    this.notifications.clear();
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.DIAMOND, 64), true));
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.GOLDEN_APPLE, 16), true));
    this.notifications.add(new PickupNotificationLine(new ItemStack(Items.ELYTRA), true));
  }

  @Override
  protected void move(Direction direction) {
    super.move(translateMove(direction));
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    context.drawCenteredTextWithShadow(
        this.textRenderer,
        Text.literal(this.getValue().toString()).asOrderedText(),
        this.width / 2,
        this.height / 2,
        GuiUtil.LABEL_COLOR
    );

    int i = 0;
    for (PickupNotificationLine notification : this.notifications) {
      notification.render(context, i++);
    }
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderBackground(context, mouseX, mouseY, delta);
    this.inGameHud.renderMockHud(context);
  }

  private static Position.Direction translateMove(Position.Direction direction) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    GuiAlignment alignment = config.guiAlignment.getPendingValue();

    return switch (direction) {
      case UP, DOWN ->
          alignment.getAlignmentY() == GuiAlignment.AlignmentY.BOTTOM ? OPPOSITE_DIRECTIONS.get(direction) : direction;
      case LEFT, RIGHT ->
          alignment.getAlignmentX() == GuiAlignment.AlignmentX.RIGHT ? OPPOSITE_DIRECTIONS.get(direction) : direction;
    };
  }
}
