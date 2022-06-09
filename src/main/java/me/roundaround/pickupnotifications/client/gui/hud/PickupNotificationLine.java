package me.roundaround.pickupnotifications.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.roundalib.config.value.GuiAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class PickupNotificationLine extends DrawableHelper {
  public static final float SHOW_DURATION = 120f;
  public static final float DURATION_INCREASE_ON_ADD = 60f;
  public static final float POP_DURATION = 5f;
  public static final float ANIM_DURATION = 6f;
  public static final float ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;
  public static final int SPRITE_RAW_SIZE = 16;
  public static final int LEFT_PADDING = 1;

  private final ItemStack itemStack;
  private final MinecraftClient minecraft;
  private float originalTimeRemaining;
  private float timeRemaining;
  private float popTimeRemaining;

  public PickupNotificationLine(ItemStack initialItems) {
    itemStack = initialItems.copy();
    minecraft = MinecraftClient.getInstance();
    originalTimeRemaining = SHOW_DURATION;
    timeRemaining = SHOW_DURATION;
  }

  public void tick(float tickDelta) {
    timeRemaining -= tickDelta;
    originalTimeRemaining -= tickDelta;
    popTimeRemaining -= tickDelta;
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
    renderItem(idx, x, y);
  }

  private void renderBackgroundAndText(MatrixStack matrixStack, int idx, float x, float y, int width) {
    MutableText text = getFormattedDisplayString();
    TextRenderer textRenderer = minecraft.textRenderer;

    float textOpacity = minecraft.options.getChtOpacity().getValue().floatValue() * 0.9f + 0.1f;
    float backgroundOpacity = (float) minecraft.options.getTextBackgroundOpacity(0.5f);

    int height = textRenderer.fontHeight + 1;
    int spriteSize = height;

    matrixStack.push();
    matrixStack.scale(1, 1, 1);
    matrixStack.translate(x, y, 800 + idx);

    fill(matrixStack, -1, -1, width, height, genColorInt(0, 0, 0, backgroundOpacity));

    {
      matrixStack.push();
      matrixStack.translate(2 * LEFT_PADDING + spriteSize, 0.5f, 0);
      RenderSystem.enableBlend();
      textRenderer.draw(matrixStack, text, 0, 0, genColorInt(1, 1, 1, textOpacity));
      RenderSystem.disableBlend();
      matrixStack.pop();
    }

    matrixStack.pop();
  }

  private void renderItem(int idx, float x, float y) {
    TextRenderer textRenderer = minecraft.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    float spriteScale = (float) spriteSize / SPRITE_RAW_SIZE;

    float popScale = 1f + MathHelper.clamp(popTimeRemaining, 0, 5) / 5f;
    int xPos = Math.round(x + LEFT_PADDING - 1);
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
    itemStack.setDamage(0);
    minecraft.getItemRenderer().renderInGui(itemStack, xPos - 4, yPos - 4);

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
    return Text.literal(itemStack.getCount() + "x ").append(itemStack.getItem().getName());
  }

  private float getXOffsetPercent() {
    if (originalTimeRemaining > ANIM_IN_FINISH_TIME) {
      // Animating in
      float animTime = Math.max(0f, timeRemaining) - ANIM_IN_FINISH_TIME;
      float basePercent = MathHelper.clamp(animTime / ANIM_DURATION, 0, 1);
      return basePercent * basePercent;
    } else if (timeRemaining < ANIM_DURATION) {
      // Animating out
      float animTime = Math.max(0f, timeRemaining);
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
