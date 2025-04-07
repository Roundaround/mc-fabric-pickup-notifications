package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.roundalib.client.gui.util.GuiUtil;
import me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment;
import me.roundaround.pickupnotifications.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import static me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment.AlignmentX;
import static me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment.AlignmentY;

public class PickupNotificationLine {
  public static final int SHOW_DURATION = 120;
  public static final int DURATION_INCREASE_ON_ADD = 60;
  public static final int POP_DURATION = 5;
  public static final int ANIM_DURATION = 6;
  public static final int ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;
  public static final int LEFT_PADDING = 1;

  private final ItemStack stack;
  private final boolean timeless;
  private final MinecraftClient client;
  private int originalTimeRemaining;
  private int timeRemaining;
  private int popTimeRemaining;
  private long lastTick;

  public PickupNotificationLine(ItemStack initialItems) {
    this(initialItems, false);
  }

  public PickupNotificationLine(ItemStack initialItems, boolean timeless) {
    this.stack = initialItems.copy();
    this.timeless = timeless;
    this.client = MinecraftClient.getInstance();
    this.originalTimeRemaining = SHOW_DURATION;
    this.timeRemaining = SHOW_DURATION;
  }

  public void tick() {
    this.timeRemaining--;
    this.originalTimeRemaining--;
    this.popTimeRemaining--;
    this.lastTick = Util.getMeasuringTimeMs();
  }

  public void render(DrawContext context, int idx) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    GuiAlignment alignment = config.guiAlignment.getPendingValue();
    Position offset = config.guiOffset.getPendingValue();
    float scale = config.guiScale.getPendingValue();

    float x = alignment.getPosX() + offset.x() * alignment.getOffsetMultiplierX();
    float y = alignment.getPosY() + offset.y() * alignment.getOffsetMultiplierY();

    MutableText text = this.getFormattedDisplayString(config);
    TextRenderer textRenderer = this.client.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    int textWidth = textRenderer.getWidth(text);
    int fullWidth = 3 * LEFT_PADDING + textWidth + spriteSize;

    float xAdjust = 0f;
    float yAdjust = 0f;

    if (alignment.getAlignmentX() == AlignmentX.RIGHT) {
      xAdjust = -fullWidth;
    }

    if (alignment.getAlignmentY() == AlignmentY.BOTTOM) {
      yAdjust = -textRenderer.fontHeight - 2;
    }

    xAdjust -= fullWidth * this.getXOffsetPercent() * alignment.getOffsetMultiplierX();
    yAdjust += idx * (textRenderer.fontHeight + 2) * alignment.getOffsetMultiplierY();

    x += xAdjust * scale;
    y += yAdjust * scale;

    this.renderBackgroundAndText(context, idx, x, y, fullWidth);
  }

  private void renderBackgroundAndText(DrawContext context, int idx, float x, float y, int totalWidth) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    boolean rightAligned = this.isRightAligned();
    float scale = config.guiScale.getPendingValue();

    MutableText text = this.getFormattedDisplayString(config);
    TextRenderer textRenderer = this.client.textRenderer;

    int height = textRenderer.fontHeight + 1;
    int leftPad = rightAligned ? LEFT_PADDING : 2 * LEFT_PADDING + height;

    MatrixStack matrixStack = context.getMatrices();
    matrixStack.push();
    matrixStack.translate(x, y, 800 + idx);
    matrixStack.scale(scale, scale, 1);

    if (config.renderBackground.getPendingValue()) {
      context.fill(
          -1,
          -1,
          totalWidth,
          height,
          GuiUtil.genColorInt(0, 0, 0, config.backgroundOpacity.getPendingValue())
      );
    }

    {
      matrixStack.push();
      matrixStack.translate(leftPad, 0, 0);
      context.drawText(textRenderer, text, 0, 1, GuiUtil.LABEL_COLOR, config.renderShadow.getPendingValue());
      matrixStack.pop();
    }

    this.renderItem(context, height, rightAligned, totalWidth);

    matrixStack.pop();
  }

  private boolean isRightAligned() {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    IconAlignment icon = config.iconAlignment.getPendingValue();
    if (icon == IconAlignment.RIGHT) {
      return true;
    }

    return switch (config.guiAlignment.getPendingValue().getAlignmentX()) {
      case LEFT -> icon == IconAlignment.INSIDE;
      case RIGHT -> icon == IconAlignment.OUTSIDE;
    };
  }

  private void renderItem(DrawContext context, int size, boolean rightAligned, int totalWidth) {
    MatrixStack matrixStack = context.getMatrices();

    long renderTime = Util.getMeasuringTimeMs();

    // 50ms per tick
    float partialTick = MathHelper.clamp((renderTime - this.lastTick) / 50f, 0, 1);
    float partialPopTimeRemaining = this.popTimeRemaining - partialTick;

    matrixStack.push();
    matrixStack.translate(rightAligned ? totalWidth - LEFT_PADDING - size + 0.25f : LEFT_PADDING - 0.5f, -0.5f, 0);
    matrixStack.scale(size / 16f, size / 16f, 1);

    float popAmount = MathHelper.clamp(partialPopTimeRemaining, 0, 5) / 5f;
    float popScale = 1f + popAmount;

    matrixStack.push();
    matrixStack.translate(8, 12, 0);
    matrixStack.scale(1f / popScale, (1f + popScale) / 2f, 1);
    matrixStack.translate(-8, -12, 0);

    context.drawItemWithoutEntity(this.stack, 0, 0);

    matrixStack.pop();
    matrixStack.pop();
  }

  public void pop() {
    this.popTimeRemaining = POP_DURATION;
  }

  public boolean isExpired() {
    return this.timeRemaining <= 0;
  }

  public boolean attemptAdd(ItemStack addition) {
    if (this.isExpired()) {
      return false;
    }

    if (areItemStacksMergeable(this.stack, addition)) {
      this.stack.increment(addition.getCount());
      this.timeRemaining = Math.min(this.timeRemaining + DURATION_INCREASE_ON_ADD, SHOW_DURATION);
      return true;
    }

    return false;
  }

  private MutableText getFormattedDisplayString(PickupNotificationsConfig config) {
    MutableText name = Text.empty().append(this.stack.getName());
    if (config.showUniqueInfo.getPendingValue()) {
      name.formatted(this.stack.getRarity().getFormatting());
      if (this.stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
        name.formatted(Formatting.ITALIC);
      }
    }
    return Text.literal(this.stack.getCount() + "x ").append(name);
  }

  private float getXOffsetPercent() {
    if (this.timeless) {
      return 0f;
    }

    long renderTime = Util.getMeasuringTimeMs();

    // 50ms per tick
    float partialTick = MathHelper.clamp((renderTime - this.lastTick) / 50f, 0, 1);
    float partialTimeRemaining = this.timeRemaining - partialTick;

    if (this.originalTimeRemaining > ANIM_IN_FINISH_TIME) {
      // Animating in
      float animTime = Math.max(0f, partialTimeRemaining) - ANIM_IN_FINISH_TIME;
      float basePercent = MathHelper.clamp(animTime / ANIM_DURATION, 0, 1);
      return basePercent * basePercent;
    } else if (partialTimeRemaining < ANIM_DURATION) {
      // Animating out
      float animTime = Math.max(0f, partialTimeRemaining);
      float basePercent = MathHelper.clamp(animTime / ANIM_DURATION, 0, 1);
      return 1f - (basePercent * basePercent);
    } else {
      // Fully showing
      return 0f;
    }
  }

  private static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.areItemsAndComponentsEqual(a, b);
  }
}
