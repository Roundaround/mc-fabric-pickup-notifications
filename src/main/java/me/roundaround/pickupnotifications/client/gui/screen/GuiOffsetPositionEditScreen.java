package me.roundaround.pickupnotifications.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationLine;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Map;

import static me.roundaround.roundalib.config.value.GuiAlignment.AlignmentX;
import static me.roundaround.roundalib.config.value.GuiAlignment.AlignmentY;
import static me.roundaround.roundalib.config.value.Position.Direction;

public class GuiOffsetPositionEditScreen extends PositionEditScreen {
  private static final int HOTBAR_WIDTH = 182;
  private static final int HOTBAR_HEIGHT = 22;
  private static final int SLOT_SIZE = 20;
  private static final int SELECTION_WIDTH = 24;
  private static final int SELECTION_HEIGHT = 23;
  private static final int MOCK_SELECTED_SLOT = 2;
  private static final Inventory MOCK_INVENTORY = new SimpleInventory(new ItemStack(Items.DIAMOND_SWORD),
      new ItemStack(Items.BOW),
      new ItemStack(Items.NETHERITE_AXE),
      new ItemStack(Items.NETHERITE_PICKAXE),
      ItemStack.EMPTY,
      new ItemStack(Items.COOKED_PORKCHOP, 15),
      new ItemStack(Items.GOLDEN_APPLE, 3),
      ItemStack.EMPTY,
      new ItemStack(Items.TORCH, 57)
  );
  private static final int XP_BAR_HEIGHT = 5;
  private static final int MOCK_XP_LEVEL = 14;
  private static final float MOCK_XP_PROGRESS = 0.7f;
  private static final int ICON_SIZE = 9;
  private static final int MOCK_HEALTH = 17;
  private static final int MOCK_ARMOR = 7;
  private static final int MOCK_FOOD = 19;

  private static final Identifier HOTBAR_TEXTURE = new Identifier("hud/hotbar");
  private static final Identifier HOTBAR_SELECTION_TEXTURE = new Identifier("hud/hotbar_selection");
  private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = new Identifier("hud/experience_bar_background");
  private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = new Identifier("hud/experience_bar_progress");
  private static final Identifier ARMOR_EMPTY_TEXTURE = new Identifier("hud/armor_empty");
  private static final Identifier ARMOR_HALF_TEXTURE = new Identifier("hud/armor_half");
  private static final Identifier ARMOR_FULL_TEXTURE = new Identifier("hud/armor_full");
  private static final Identifier FOOD_EMPTY_TEXTURE = new Identifier("hud/food_empty");
  private static final Identifier FOOD_HALF_TEXTURE = new Identifier("hud/food_half");
  private static final Identifier FOOD_FULL_TEXTURE = new Identifier("hud/food_full");
  private static final Identifier HEART_FULL_TEXTURE = new Identifier("hud/heart/hardcore_full");
  private static final Identifier HEART_HALF_TEXTURE = new Identifier("hud/heart/hardcore_half");
  private static final Identifier HEART_EMPTY_TEXTURE = new Identifier("hud/heart/container_hardcore");

  private static final Map<Direction, Direction> OPPOSITE_DIRECTIONS = Map.of(Direction.UP,
      Direction.DOWN,
      Direction.DOWN,
      Direction.UP,
      Direction.LEFT,
      Direction.RIGHT,
      Direction.RIGHT,
      Direction.LEFT
  );

  private final ArrayList<PickupNotificationLine> notifications = new ArrayList<>();

  private GuiOffsetPositionEditScreen(ConfigScreen parent, PositionConfigOption option) {
    super(Text.translatable("pickupnotifications.guiOffset.title"), parent, option);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return GuiOffsetPositionEditScreen::new;
  }

  @Override
  protected void init() {
    super.init();

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

    context.drawCenteredTextWithShadow(this.textRenderer,
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
    this.renderMockHud(context);
  }

  private void renderMockHud(DrawContext context) {
    renderHotbar(context, this.textRenderer);
    renderExperienceBar(context);
    renderExperienceLevel(context, this.textRenderer);
    renderStatusBars(context);
  }

  private static Direction translateMove(Direction direction) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    GuiAlignment alignment = config.guiAlignment.getPendingValue();

    return switch (direction) {
      case UP, DOWN -> alignment.getAlignmentY() == AlignmentY.BOTTOM ? OPPOSITE_DIRECTIONS.get(direction) : direction;
      case LEFT, RIGHT ->
          alignment.getAlignmentX() == AlignmentX.RIGHT ? OPPOSITE_DIRECTIONS.get(direction) : direction;
    };
  }

  private static void renderHotbar(DrawContext context, TextRenderer textRenderer) {
    int x = getHotbarLeft(context);
    int y = getHotbarTop(context);

    RenderSystem.enableBlend();
    context.drawGuiTexture(HOTBAR_TEXTURE, x, y, HOTBAR_WIDTH, HOTBAR_HEIGHT);
    context.drawGuiTexture(HOTBAR_SELECTION_TEXTURE,
        x - 1 + MOCK_SELECTED_SLOT * SLOT_SIZE,
        y - 1,
        SELECTION_WIDTH,
        SELECTION_HEIGHT
    );
    RenderSystem.disableBlend();

    for (int slot = 0; slot < 9; slot++) {
      ItemStack stack = MOCK_INVENTORY.getStack(slot);
      if (!stack.isEmpty()) {
        int itemX = x + 1 + slot * SLOT_SIZE + 2;
        int itemY = context.getScaledWindowHeight() - SLOT_SIZE + 1;

        context.drawItemWithoutEntity(stack, itemX, itemY, slot + 1);
        context.drawItemInSlot(textRenderer, stack, itemX, itemY);
      }
    }
  }

  private static void renderExperienceBar(DrawContext context) {
    int x = getHotbarLeft(context);
    int y = getExperienceBarTop(context);
    int progress = (int) (MOCK_XP_PROGRESS * (HOTBAR_WIDTH + 1f));

    RenderSystem.enableBlend();
    context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, x, y, HOTBAR_WIDTH, XP_BAR_HEIGHT);
    context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE,
        HOTBAR_WIDTH,
        XP_BAR_HEIGHT,
        0,
        0,
        x,
        y,
        progress,
        XP_BAR_HEIGHT
    );
    RenderSystem.disableBlend();
  }

  private static void renderExperienceLevel(DrawContext context, TextRenderer textRenderer) {
    String level = Integer.toString(MOCK_XP_LEVEL);
    int x = getCenteredLeft(context, textRenderer.getWidth(level));
    int y = getExperienceBarTop(context) - textRenderer.fontHeight + MathHelper.ceil(XP_BAR_HEIGHT / 2f);
    context.drawText(textRenderer, level, x + 1, y, 0, false);
    context.drawText(textRenderer, level, x - 1, y, 0, false);
    context.drawText(textRenderer, level, x, y + 1, 0, false);
    context.drawText(textRenderer, level, x, y - 1, 0, false);
    context.drawText(textRenderer, level, x, y, 8453920, false);
  }

  private static void renderStatusBars(DrawContext context) {
    renderStatusBar(context,
        getHotbarLeft(context),
        getLowerStatusRowTop(context),
        HEART_EMPTY_TEXTURE,
        HEART_HALF_TEXTURE,
        HEART_FULL_TEXTURE,
        MOCK_HEALTH,
        false
    );
    renderStatusBar(context,
        getHotbarRight(context),
        getLowerStatusRowTop(context),
        FOOD_EMPTY_TEXTURE,
        FOOD_HALF_TEXTURE,
        FOOD_FULL_TEXTURE,
        MOCK_FOOD,
        true
    );
    renderStatusBar(context,
        getHotbarLeft(context),
        getUpperStatusRowTop(context),
        ARMOR_EMPTY_TEXTURE,
        ARMOR_HALF_TEXTURE,
        ARMOR_FULL_TEXTURE,
        MOCK_ARMOR,
        false
    );
  }

  private static void renderStatusBar(
      DrawContext context,
      int x,
      int y,
      Identifier empty,
      Identifier half,
      Identifier full,
      int scaledValue,
      boolean inverted
  ) {
    RenderSystem.enableBlend();
    for (int index = 0; index < 10; index++) {
      int offset = inverted ? -(index + 1) * (ICON_SIZE - 1) : index * (ICON_SIZE - 1);

      Identifier icon = empty;
      if (index * 2 + 1 < scaledValue) {
        icon = full;
      } else if (index * 2 + 1 == scaledValue) {
        icon = half;
      }

      context.drawGuiTexture(empty, x + offset, y, ICON_SIZE, ICON_SIZE);
      context.drawGuiTexture(icon, x + offset, y, ICON_SIZE, ICON_SIZE);
    }
    RenderSystem.disableBlend();
  }

  private static int getCenteredLeft(DrawContext context, int width) {
    return (context.getScaledWindowWidth() - width) / 2;
  }

  private static int getHotbarLeft(DrawContext context) {
    return getCenteredLeft(context, HOTBAR_WIDTH);
  }

  private static int getHotbarRight(DrawContext context) {
    return getHotbarLeft(context) + HOTBAR_WIDTH;
  }

  private static int getHotbarTop(DrawContext context) {
    return context.getScaledWindowHeight() - HOTBAR_HEIGHT;
  }

  private static int getExperienceBarTop(DrawContext context) {
    return getHotbarTop(context) - 2 - XP_BAR_HEIGHT;
  }

  private static int getLowerStatusRowTop(DrawContext context) {
    return getExperienceBarTop(context) - 1 - ICON_SIZE;
  }

  private static int getUpperStatusRowTop(DrawContext context) {
    return getLowerStatusRowTop(context) - 1 - ICON_SIZE;
  }
}
