package me.roundaround.pickupnotifications.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MockInGameHud {
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

  private static final Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
  private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
  private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla(
      "hud/experience_bar_background");
  private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");
  private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/armor_empty");
  private static final Identifier ARMOR_HALF_TEXTURE = Identifier.ofVanilla("hud/armor_half");
  private static final Identifier ARMOR_FULL_TEXTURE = Identifier.ofVanilla("hud/armor_full");
  private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
  private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
  private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");
  private static final Identifier HEART_FULL_TEXTURE = Identifier.ofVanilla("hud/heart/hardcore_full");
  private static final Identifier HEART_HALF_TEXTURE = Identifier.ofVanilla("hud/heart/hardcore_half");
  private static final Identifier HEART_EMPTY_TEXTURE = Identifier.ofVanilla("hud/heart/container_hardcore");

  private final TextRenderer textRenderer;

  public MockInGameHud(TextRenderer textRenderer) {
    this.textRenderer = textRenderer;
  }

  public void renderMockHud(DrawContext context) {
    renderHotbar(context, this.textRenderer);
    renderExperienceBar(context);
    renderExperienceLevel(context, this.textRenderer);
    renderStatusBars(context);
  }

  private static void renderHotbar(DrawContext context, TextRenderer textRenderer) {
    int x = getHotbarLeft(context);
    int y = getHotbarTop(context);

    RenderSystem.enableBlend();
    context.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_TEXTURE, x, y, HOTBAR_WIDTH, HOTBAR_HEIGHT);
    context.drawGuiTexture(RenderLayer::getGuiTextured,
        HOTBAR_SELECTION_TEXTURE,
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
        context.drawStackOverlay(textRenderer, stack, itemX, itemY);
      }
    }
  }

  private static void renderExperienceBar(DrawContext context) {
    int x = getHotbarLeft(context);
    int y = getExperienceBarTop(context);
    int progress = (int) (MOCK_XP_PROGRESS * (HOTBAR_WIDTH + 1f));

    RenderSystem.enableBlend();
    context.drawGuiTexture(RenderLayer::getGuiTextured,
        EXPERIENCE_BAR_BACKGROUND_TEXTURE,
        x,
        y,
        HOTBAR_WIDTH,
        XP_BAR_HEIGHT
    );
    context.drawGuiTexture(RenderLayer::getGuiTextured,
        EXPERIENCE_BAR_PROGRESS_TEXTURE,
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

      context.drawGuiTexture(RenderLayer::getGuiTextured, empty, x + offset, y, ICON_SIZE, ICON_SIZE);
      context.drawGuiTexture(RenderLayer::getGuiTextured, icon, x + offset, y, ICON_SIZE, ICON_SIZE);
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
