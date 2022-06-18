package me.roundaround.pickupnotifications.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.roundalib.config.value.GuiAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class PickupNotificationLine extends DrawableHelper {
  public static final int SHOW_DURATION = 120;
  public static final int DURATION_INCREASE_ON_ADD = 60;
  public static final int POP_DURATION = 5;
  public static final int ANIM_DURATION = 6;
  public static final int ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;
  public static final int SPRITE_RAW_SIZE = 16;
  public static final int LEFT_PADDING = 1;

  private final ItemStack itemStack;
  private final MinecraftClient minecraft;
  private int originalTimeRemaining;
  private int timeRemaining;
  private int popTimeRemaining;
  private long lastTick;

  public PickupNotificationLine(ItemStack initialItems) {
    itemStack = initialItems.copy();
    minecraft = MinecraftClient.getInstance();
    originalTimeRemaining = SHOW_DURATION;
    timeRemaining = SHOW_DURATION;
  }

  public void tick() {
    timeRemaining--;
    originalTimeRemaining--;
    popTimeRemaining--;
    lastTick = Util.getMeasuringTimeMs();
  }

  public void render(MatrixStack matrixStack, int idx) {
    GuiAlignment alignment = PickupNotificationsMod.CONFIG.GUI_ALIGNMENT.getValue();
    int x = alignment.getPosX() +
        PickupNotificationsMod.CONFIG.GUI_OFFSET_X.getValue() * alignment.getOffsetMultiplierX();
    int y = alignment.getPosY() +
        PickupNotificationsMod.CONFIG.GUI_OFFSET_Y.getValue() * alignment.getOffsetMultiplierY();

    MutableText text = getFormattedDisplayString();
    TextRenderer textRenderer = minecraft.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    int textWidth = textRenderer.getWidth(text);
    int fullWidth = 3 * LEFT_PADDING + textWidth + spriteSize;

    if (alignment.getAlignmentX() == GuiAlignment.AlignmentX.RIGHT) {
      x -= fullWidth;
    }

    if (alignment.getAlignmentY() == GuiAlignment.AlignmentY.BOTTOM) {
      y -= textRenderer.fontHeight + 2;
    }

    x -= fullWidth * getXOffsetPercent() * alignment.getOffsetMultiplierX();
    y += idx * (textRenderer.fontHeight + 2) * alignment.getOffsetMultiplierY();

    renderBackgroundAndText(matrixStack, idx, x, y, fullWidth);
    renderItem(idx, x, y, fullWidth);
  }

  private void renderBackgroundAndText(MatrixStack matrixStack, int idx, float x, float y, int width) {
    boolean guiRight = PickupNotificationsMod.CONFIG.GUI_ALIGNMENT
        .getValue()
        .getAlignmentX()
        .equals(GuiAlignment.AlignmentX.RIGHT);
    IconAlignment iconAlignment = PickupNotificationsMod.CONFIG.ICON_ALIGNMENT.getValue();
    boolean rightAligned = iconAlignment.equals(IconAlignment.RIGHT)
        || guiRight && iconAlignment.equals(IconAlignment.OUTSIDE);

    MutableText text = getFormattedDisplayString();
    TextRenderer textRenderer = minecraft.textRenderer;

    float textOpacity = (float) minecraft.options.chatOpacity * 0.9f + 0.1f;
    float backgroundOpacity = (float) minecraft.options.textBackgroundOpacity;

    int height = textRenderer.fontHeight + 1;
    int spriteSize = height;
    int leftPad = rightAligned ? LEFT_PADDING : 2 * LEFT_PADDING + spriteSize;

    matrixStack.push();
    matrixStack.scale(1, 1, 1);
    matrixStack.translate(x, y, 800 + idx);

    if (PickupNotificationsMod.CONFIG.RENDER_BACKGROUND.getValue()) {
      fill(matrixStack, -1, -1, width, height, genColorInt(0, 0, 0, backgroundOpacity));
    }

    {
      matrixStack.push();
      matrixStack.translate(leftPad, 0.5f, 0);
      RenderSystem.enableBlend();
      textRenderer.draw(matrixStack, text, 0, 0, genColorInt(1, 1, 1, textOpacity));
      RenderSystem.disableBlend();
      matrixStack.pop();
    }

    matrixStack.pop();
  }

  private void renderItem(int idx, float x, float y, int width) {
    boolean guiRight = PickupNotificationsMod.CONFIG.GUI_ALIGNMENT
        .getValue()
        .getAlignmentX()
        .equals(GuiAlignment.AlignmentX.RIGHT);
    IconAlignment iconAlignment = PickupNotificationsMod.CONFIG.ICON_ALIGNMENT.getValue();
    boolean rightAligned = iconAlignment.equals(IconAlignment.RIGHT)
        || guiRight && iconAlignment.equals(IconAlignment.OUTSIDE);

    TextRenderer textRenderer = minecraft.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    float spriteScale = (float) spriteSize / SPRITE_RAW_SIZE;

    long renderTime = Util.getMeasuringTimeMs();

    // 50ms per tick
    float partialTick = MathHelper.clamp((renderTime - lastTick) / 50f, 0, 1);
    float partialPopTimeRemaining = popTimeRemaining - partialTick;

    float popScale = 1f + MathHelper.clamp(partialPopTimeRemaining, 0, 5) / 5f;
    int xPos = Math.round(rightAligned ? x + width - spriteSize - LEFT_PADDING + 1 : x + LEFT_PADDING - 1);
    int yPos = Math.round(y - 0.75f);

    RenderSystem.enableDepthTest();
    MatrixStack matrixStack = RenderSystem.getModelViewStack();
    matrixStack.push();
    matrixStack.translate(0, 0, 801 + idx);

    matrixStack.push();
    matrixStack.translate(xPos + 8 * spriteScale, yPos + 12 * spriteScale, 0);
    matrixStack.scale(spriteScale / popScale, spriteScale * (1f + popScale) / 2f, 1);
    matrixStack.translate(-(xPos + 8 * spriteScale), -(yPos + 12 * spriteScale), 0);

    RenderSystem.applyModelViewMatrix();
    minecraft.getItemRenderer().renderInGuiWithOverrides(itemStack, xPos - 4, yPos - 4);

    matrixStack.pop();
    matrixStack.pop();
    RenderSystem.applyModelViewMatrix();
  }

  public void pop() {
    popTimeRemaining = POP_DURATION;
  }

  public boolean isExpired() {
    return timeRemaining <= 0;
  }

  public boolean attemptAdd(ItemStack addition) {
    if (isExpired()) {
      return false;
    }

    if (areItemStacksMergeable(itemStack, addition)) {
      itemStack.increment(addition.getCount());
      timeRemaining = Math.min(timeRemaining + DURATION_INCREASE_ON_ADD, SHOW_DURATION);
      return true;
    }

    return false;
  }

  private MutableText getFormattedDisplayString() {
    MutableText name = new LiteralText("").append(itemStack.getName());
    if (PickupNotificationsMod.CONFIG.SHOW_UNIQUE_INFO.getValue()) {
      name.formatted(itemStack.getRarity().formatting);
      if (itemStack.hasCustomName()) {
        name.formatted(Formatting.ITALIC);
      }
    }
    return new LiteralText(itemStack.getCount() + "x ").append(name);
  }

  private float getXOffsetPercent() {
    long renderTime = Util.getMeasuringTimeMs();

    // 50ms per tick
    float partialTick = MathHelper.clamp((renderTime - lastTick) / 50f, 0, 1);
    float partialTimeRemaining = timeRemaining - partialTick;

    if (originalTimeRemaining > ANIM_IN_FINISH_TIME) {
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

  private static int genColorInt(float r, float g, float b, float a) {
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
  }

  private static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && ItemStack.canCombine(a, b);
  }
}
