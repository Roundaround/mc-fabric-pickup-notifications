package me.roundaround.pickupnotifications.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.pickupnotifications.PickupNotificationsMod;
import me.roundaround.pickupnotifications.config.IconAlignment;
import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
  private final PickupNotificationsConfig config;
  private final boolean timeless;
  private final MinecraftClient minecraft;
  private int originalTimeRemaining;
  private int timeRemaining;
  private int popTimeRemaining;
  private long lastTick;

  public PickupNotificationLine(ItemStack initialItems) {
    this(initialItems, PickupNotificationsMod.CONFIG, false);
  }

  public PickupNotificationLine(ItemStack initialItems, PickupNotificationsConfig config, boolean timeless) {
    itemStack = initialItems.copy();
    this.config = config;
    this.timeless = timeless;
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
    GuiAlignment alignment = config.GUI_ALIGNMENT.getValue();
    Position offset = config.GUI_OFFSET.getValue();
    float scale = config.GUI_SCALE.getValue();

    float x = alignment.getPosX() + offset.x() * alignment.getOffsetMultiplierX();
    float y = alignment.getPosY() + offset.y() * alignment.getOffsetMultiplierY();

    MutableText text = getFormattedDisplayString();
    TextRenderer textRenderer = minecraft.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    int textWidth = textRenderer.getWidth(text);
    int fullWidth = 3 * LEFT_PADDING + textWidth + spriteSize;

    float xAdjust = 0f;
    float yAdjust = 0f;

    if (alignment.getAlignmentX() == GuiAlignment.AlignmentX.RIGHT) {
      xAdjust = -fullWidth;
    }

    if (alignment.getAlignmentY() == GuiAlignment.AlignmentY.BOTTOM) {
      yAdjust = -textRenderer.fontHeight - 2;
    }

    xAdjust -= fullWidth * getXOffsetPercent() * alignment.getOffsetMultiplierX();
    yAdjust += idx * (textRenderer.fontHeight + 2) * alignment.getOffsetMultiplierY();

    x += xAdjust * scale;
    y += yAdjust * scale;

    renderBackgroundAndText(matrixStack, idx, x, y, fullWidth);
    renderItem(matrixStack, idx, x, y, fullWidth);
  }

  private void renderBackgroundAndText(
      MatrixStack matrixStack,
      int idx,
      float x,
      float y,
      int width) {
    boolean guiRight = config.GUI_ALIGNMENT
        .getValue()
        .getAlignmentX()
        .equals(GuiAlignment.AlignmentX.RIGHT);
    IconAlignment iconAlignment = config.ICON_ALIGNMENT.getValue();
    boolean rightAligned = iconAlignment.equals(IconAlignment.RIGHT)
        || guiRight && iconAlignment.equals(IconAlignment.OUTSIDE);
    float scale = config.GUI_SCALE.getValue();

    MutableText text = getFormattedDisplayString();
    TextRenderer textRenderer = minecraft.textRenderer;

    int height = textRenderer.fontHeight + 1;
    int spriteSize = height;
    int leftPad = rightAligned ? LEFT_PADDING : 2 * LEFT_PADDING + spriteSize;

    matrixStack.push();
    matrixStack.translate(
        x,
        y,
        800 + idx);
    matrixStack.scale(
        scale,
        scale,
        1);

    if (config.RENDER_BACKGROUND.getValue()) {
      fill(matrixStack, -1, -1, width, height,
          genColorInt(0, 0, 0, config.BACKGROUND_OPACITY.getValue()));
    }

    {
      matrixStack.push();
      matrixStack.translate(leftPad, 0, 0);
      RenderSystem.enableBlend();
      if (config.RENDER_SHADOW.getValue()) {
        textRenderer.drawWithShadow(matrixStack, text, 0, 1, GuiUtil.LABEL_COLOR);
      } else {
        textRenderer.draw(matrixStack, text, 0, 1, GuiUtil.LABEL_COLOR);
      }
      RenderSystem.disableBlend();
      matrixStack.pop();
    }

    matrixStack.pop();
  }

  private void renderItem(
      MatrixStack matrixStack,
      int idx,
      float x,
      float y,
      int width) {
    float scale = config.GUI_SCALE.getValue();

    matrixStack.push();
    matrixStack.translate(
        x,
        y,
        900 + idx);
    matrixStack.scale(
        scale,
        scale,
        1);

    boolean guiRight = config.GUI_ALIGNMENT
        .getValue()
        .getAlignmentX()
        .equals(GuiAlignment.AlignmentX.RIGHT);
    IconAlignment iconAlignment = config.ICON_ALIGNMENT.getValue();
    boolean rightAligned = iconAlignment.equals(IconAlignment.RIGHT)
        || guiRight && iconAlignment.equals(IconAlignment.OUTSIDE);

    TextRenderer textRenderer = minecraft.textRenderer;

    int spriteSize = textRenderer.fontHeight + 1;
    long renderTime = Util.getMeasuringTimeMs();

    // 50ms per tick
    float partialTick = MathHelper.clamp((renderTime - lastTick) / 50f, 0, 1);
    float partialPopTimeRemaining = popTimeRemaining - partialTick;

    float xPos = rightAligned
        ? width - spriteSize / 2f - LEFT_PADDING - 0.5f
        : spriteSize / 2f + LEFT_PADDING - 0.5f;
    float yPos = spriteSize / 2f - 0.5f;

    matrixStack.push();
    matrixStack.translate(xPos, yPos, 0);
    matrixStack.scale(spriteSize, -spriteSize, 1);

    float popScale = 1f + MathHelper.clamp(partialPopTimeRemaining, 0, 5) / 5f;

    matrixStack.push();
    matrixStack.scale(1f / popScale, (1f + popScale) / 2f, 1);

    ItemRenderer itemRenderer = minecraft.getItemRenderer();
    TextureManager textureManager = minecraft.getTextureManager();

    BakedModel model = itemRenderer.getModel(itemStack, null, minecraft.player, 0);
    textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
    RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderColor(1, 1, 1, 1);
    VertexConsumerProvider.Immediate immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();

    boolean isNotLit = !model.isSideLit();
    if (isNotLit) {
      DiffuseLighting.disableGuiDepthLighting();
    }

    itemRenderer.renderItem(
        itemStack,
        ModelTransformation.Mode.GUI,
        false,
        matrixStack,
        immediate,
        LightmapTextureManager.MAX_LIGHT_COORDINATE,
        OverlayTexture.DEFAULT_UV,
        model);
    immediate.draw();

    matrixStack.pop();
    matrixStack.pop();
    matrixStack.pop();

    if (isNotLit) {
      DiffuseLighting.enableGuiDepthLighting();
    }
    RenderSystem.enableDepthTest();
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
    MutableText name = Text.empty().append(itemStack.getName());
    if (config.SHOW_UNIQUE_INFO.getValue()) {
      name.formatted(itemStack.getRarity().formatting);
      if (itemStack.hasCustomName()) {
        name.formatted(Formatting.ITALIC);
      }
    }
    return Text.literal(itemStack.getCount() + "x ").append(name);
  }

  private float getXOffsetPercent() {
    if (timeless) {
      return 0f;
    }

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
