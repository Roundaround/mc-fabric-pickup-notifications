package me.roundaround.pickupnotifications.client.gui.screen;

import me.roundaround.pickupnotifications.client.gui.hud.ExperiencePickupNotification;
import me.roundaround.pickupnotifications.client.gui.hud.MockInGameHud;
import me.roundaround.pickupnotifications.client.gui.hud.MockItemPickupNotification;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotification;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.client.gui.screen.PositionEditScreen;
import me.roundaround.trove.client.gui.util.GuiUtil;
import me.roundaround.trove.client.gui.widget.config.SubScreenControl;
import me.roundaround.trove.config.option.PositionConfigOption;
import me.roundaround.trove.config.value.GuiAlignment;
import me.roundaround.trove.config.value.Position;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static me.roundaround.trove.config.value.Position.Direction;

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

  private final ArrayList<PickupNotification<?>> notifications = new ArrayList<>();

  private MockInGameHud inGameHud;

  private GuiOffsetPositionEditScreen(ConfigScreen parent, PositionConfigOption option) {
    super(Component.translatable("pickupnotifications.guiOffset.title"), parent, option);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return GuiOffsetPositionEditScreen::new;
  }

  @Override
  protected void init() {
    super.init();

    this.inGameHud = new MockInGameHud(this.font);

    this.notifications.clear();
    // Rendered from item textures rather than ItemStacks so the preview works on the title screen
    // (reachable via Mod Menu) where item data components aren't bound yet. See MockInGameHud.
    this.notifications.add(new MockItemPickupNotification(
        Items.DIAMOND, Identifier.withDefaultNamespace("textures/item/diamond.png"), 64));
    this.notifications.add(new MockItemPickupNotification(
        Items.GOLDEN_APPLE, Identifier.withDefaultNamespace("textures/item/golden_apple.png"), 16));
    this.notifications.add(new MockItemPickupNotification(
        Items.ELYTRA, Identifier.withDefaultNamespace("textures/item/elytra.png"), 1));
    this.notifications.add(new ExperiencePickupNotification(2500, true));
  }

  @Override
  protected void move(Direction direction) {
    super.move(translateMove(direction));
  }

  @Override
  public void tick() {
    super.tick();

    for (PickupNotification<?> notification : this.notifications) {
      notification.tick();
    }
  }

  @Override
  public void extractRenderState(@NotNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);

    context.centeredText(
        this.font,
        Component.literal(this.getValue().toString()).getVisualOrderText(),
        this.width / 2,
        this.height / 2,
        GuiUtil.LABEL_COLOR
    );

    int i = 0;
    for (PickupNotification<?> notification : this.notifications) {
      notification.render(context, this.minecraft.getDeltaTracker(), i++);
    }
  }

  @Override
  public void extractBackground(@NotNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractBackground(context, mouseX, mouseY, delta);
    this.inGameHud.renderMockHud(context);
  }

  private static Direction translateMove(Direction direction) {
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
