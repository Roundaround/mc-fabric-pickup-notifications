package me.roundaround.pickupnotifications.client.gui.hud;

import org.joml.Matrix3x2fStack;

import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.roundalib.client.gui.util.GuiUtil;
import me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment;
import me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment.AlignmentX;
import me.roundaround.pickupnotifications.roundalib.config.value.GuiAlignment.AlignmentY;
import me.roundaround.pickupnotifications.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public abstract class PickupNotification<T> {
  public static final int SHOW_DURATION = 120;
  public static final int DURATION_INCREASE_ON_ADD = 60;
  public static final int POP_DURATION = 5;
  public static final int ANIM_DURATION = 6;
  public static final int ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;
  public static final int LEFT_PADDING = 1;

  protected final boolean timeless;
  protected final MinecraftClient client;
  protected int originalTimeRemaining;
  protected int timeRemaining;
  protected int popTimeRemaining;
  protected long lastTick;

  protected PickupNotification(boolean timeless) {
    this.timeless = timeless;
    this.client = MinecraftClient.getInstance();
    this.originalTimeRemaining = SHOW_DURATION;
    this.timeRemaining = SHOW_DURATION;
  }

  protected abstract void renderIcon(DrawContext context, RenderTickCounter tickCounter);

  protected abstract Text getFormattedDisplayString(PickupNotificationsConfig config);

  protected abstract boolean canAdd(Object value);

  protected abstract void add(T value);

  public void render(DrawContext context, RenderTickCounter tickCounter, int idx) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    GuiAlignment alignment = config.guiAlignment.getPendingValue();
    Position offset = config.guiOffset.getPendingValue();
    float scale = config.guiScale.getPendingValue();

    float x = alignment.getPosX() + offset.x() * alignment.getOffsetMultiplierX();
    float y = alignment.getPosY() + offset.y() * alignment.getOffsetMultiplierY();

    Text text = this.getFormattedDisplayString(config);
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

    this.renderBackgroundAndText(context, tickCounter, idx, x, y, fullWidth);
  }

  @SuppressWarnings("unchecked")
  public boolean attemptAdd(Object addition) {
    if (this.isExpired()) {
      return false;
    }

    if (this.canAdd(addition)) {
      this.add((T) addition);
      return true;
    }

    return false;
  }

  public void extendTime() {
    this.timeRemaining = Math.min(this.timeRemaining + DURATION_INCREASE_ON_ADD, SHOW_DURATION);
    this.pop();
  }

  public void pop() {
    this.popTimeRemaining = POP_DURATION;
  }

  public boolean isExpired() {
    return this.timeRemaining <= 0;
  }

  public void tick() {
    this.timeRemaining--;
    this.originalTimeRemaining--;
    this.popTimeRemaining--;
    this.lastTick = Util.getMeasuringTimeMs();
  }

  protected void renderBackgroundAndText(
      DrawContext context,
      RenderTickCounter tickCounter,
      int idx,
      float x,
      float y,
      int totalWidth) {
    PickupNotificationsConfig config = PickupNotificationsConfig.getInstance();
    boolean rightAligned = this.isRightAligned();
    float scale = config.guiScale.getPendingValue();

    Text text = this.getFormattedDisplayString(config);
    TextRenderer textRenderer = this.client.textRenderer;

    int height = textRenderer.fontHeight + 1;
    int leftPad = rightAligned ? LEFT_PADDING : 2 * LEFT_PADDING + height;

    context.createNewRootLayer();

    Matrix3x2fStack matrices = context.getMatrices();
    matrices.pushMatrix();
    matrices.translate(x, y);
    matrices.scale(scale, scale);

    if (config.renderBackground.getPendingValue()) {
      context.fill(
          -1,
          -1,
          totalWidth,
          height,
          GuiUtil.genColorInt(0, 0, 0, config.backgroundOpacity.getPendingValue()));
    }

    {
      matrices.pushMatrix();
      matrices.translate(leftPad, 0);
      context.drawText(textRenderer, text, 0, 1, GuiUtil.LABEL_COLOR, config.renderShadow.getPendingValue());
      matrices.popMatrix();
    }

    {
      float partialPopTimeRemaining = this.popTimeRemaining - tickCounter.getTickProgress(false);

      matrices.pushMatrix();
      matrices.translate(rightAligned ? totalWidth - LEFT_PADDING - height + 0.25f : LEFT_PADDING - 0.5f, -0.5f);
      matrices.scale(height / 16f, height / 16f);

      float popAmount = MathHelper.clamp(partialPopTimeRemaining, 0, 5) / 5f;
      float popScale = 1f + popAmount;

      matrices.pushMatrix();
      matrices.translate(8, 12);
      matrices.scale(1f / popScale, (1f + popScale) / 2f);
      matrices.translate(-8, -12);

      this.renderIcon(context, tickCounter);

      matrices.popMatrix();
      matrices.popMatrix();
    }

    matrices.popMatrix();
  }

  protected boolean isRightAligned() {
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

  protected float getXOffsetPercent() {
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
}
