package me.roundaround.pickupnotifications.client.gui.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MockInGameHud {
  private static final int HOTBAR_WIDTH = 182;
  private static final int HOTBAR_HEIGHT = 22;
  private static final int SLOT_SIZE = 20;
  private static final int SELECTION_WIDTH = 24;
  private static final int SELECTION_HEIGHT = 23;
  private static final int MOCK_SELECTED_SLOT = 2;
  private static final Container MOCK_INVENTORY = new SimpleContainer(
      new ItemStack(Items.DIAMOND_SWORD),
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

  private static final Identifier HOTBAR_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar");
  private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar_selection");
  private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace(
      "hud/experience_bar_background");
  private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.withDefaultNamespace(
      "hud/experience_bar_progress");
  private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/armor_empty");
  private static final Identifier ARMOR_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/armor_half");
  private static final Identifier ARMOR_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/armor_full");
  private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/food_empty");
  private static final Identifier FOOD_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/food_half");
  private static final Identifier FOOD_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/food_full");
  private static final Identifier HEART_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/heart/hardcore_full");
  private static final Identifier HEART_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/heart/hardcore_half");
  private static final Identifier HEART_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/heart/container_hardcore");

  private final Font textRenderer;

  public MockInGameHud(Font textRenderer) {
    this.textRenderer = textRenderer;
  }

  public void renderMockHud(GuiGraphicsExtractor context) {
    renderHotbar(context, this.textRenderer);
    renderExperienceBar(context);
    renderExperienceLevel(context, this.textRenderer);
    renderStatusBars(context);
  }

  private static void renderHotbar(GuiGraphicsExtractor context, Font textRenderer) {
    int x = getHotbarLeft(context);
    int y = getHotbarTop(context);

    context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, x, y, HOTBAR_WIDTH, HOTBAR_HEIGHT);
    context.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        HOTBAR_SELECTION_TEXTURE,
        x - 1 + MOCK_SELECTED_SLOT * SLOT_SIZE,
        y - 1,
        SELECTION_WIDTH,
        SELECTION_HEIGHT
    );

    for (int slot = 0; slot < 9; slot++) {
      ItemStack stack = MOCK_INVENTORY.getItem(slot);
      if (!stack.isEmpty()) {
        int itemX = x + 1 + slot * SLOT_SIZE + 2;
        int itemY = context.guiHeight() - SLOT_SIZE + 1;

        context.fakeItem(stack, itemX, itemY, slot + 1);
        context.itemDecorations(textRenderer, stack, itemX, itemY);
      }
    }
  }

  private static void renderExperienceBar(GuiGraphicsExtractor context) {
    int x = getHotbarLeft(context);
    int y = getExperienceBarTop(context);
    int progress = (int) (MOCK_XP_PROGRESS * (HOTBAR_WIDTH + 1f));

    context.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        EXPERIENCE_BAR_BACKGROUND_TEXTURE,
        x,
        y,
        HOTBAR_WIDTH,
        XP_BAR_HEIGHT
    );
    context.blitSprite(
        RenderPipelines.GUI_TEXTURED,
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
  }

  private static void renderExperienceLevel(GuiGraphicsExtractor context, Font textRenderer) {
    String level = Integer.toString(MOCK_XP_LEVEL);
    int x = getCenteredLeft(context, textRenderer.width(level));
    int y = getExperienceBarTop(context) - textRenderer.lineHeight + Mth.ceil(XP_BAR_HEIGHT / 2f);
    context.text(textRenderer, level, x + 1, y, 0, false);
    context.text(textRenderer, level, x - 1, y, 0, false);
    context.text(textRenderer, level, x, y + 1, 0, false);
    context.text(textRenderer, level, x, y - 1, 0, false);
    context.text(textRenderer, level, x, y, 8453920, false);
  }

  private static void renderStatusBars(GuiGraphicsExtractor context) {
    renderStatusBar(
        context,
        getHotbarLeft(context),
        getLowerStatusRowTop(context),
        HEART_EMPTY_TEXTURE,
        HEART_HALF_TEXTURE,
        HEART_FULL_TEXTURE,
        MOCK_HEALTH,
        false
    );
    renderStatusBar(
        context,
        getHotbarRight(context),
        getLowerStatusRowTop(context),
        FOOD_EMPTY_TEXTURE,
        FOOD_HALF_TEXTURE,
        FOOD_FULL_TEXTURE,
        MOCK_FOOD,
        true
    );
    renderStatusBar(
        context,
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
      GuiGraphicsExtractor context,
      int x,
      int y,
      Identifier empty,
      Identifier half,
      Identifier full,
      int scaledValue,
      boolean inverted
  ) {
    for (int index = 0; index < 10; index++) {
      int offset = inverted ? -(index + 1) * (ICON_SIZE - 1) : index * (ICON_SIZE - 1);

      Identifier icon = empty;
      if (index * 2 + 1 < scaledValue) {
        icon = full;
      } else if (index * 2 + 1 == scaledValue) {
        icon = half;
      }

      context.blitSprite(RenderPipelines.GUI_TEXTURED, empty, x + offset, y, ICON_SIZE, ICON_SIZE);
      context.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x + offset, y, ICON_SIZE, ICON_SIZE);
    }
  }

  private static int getCenteredLeft(GuiGraphicsExtractor context, int width) {
    return (context.guiWidth() - width) / 2;
  }

  private static int getHotbarLeft(GuiGraphicsExtractor context) {
    return getCenteredLeft(context, HOTBAR_WIDTH);
  }

  private static int getHotbarRight(GuiGraphicsExtractor context) {
    return getHotbarLeft(context) + HOTBAR_WIDTH;
  }

  private static int getHotbarTop(GuiGraphicsExtractor context) {
    return context.guiHeight() - HOTBAR_HEIGHT;
  }

  private static int getExperienceBarTop(GuiGraphicsExtractor context) {
    return getHotbarTop(context) - 2 - XP_BAR_HEIGHT;
  }

  private static int getLowerStatusRowTop(GuiGraphicsExtractor context) {
    return getExperienceBarTop(context) - 1 - ICON_SIZE;
  }

  private static int getUpperStatusRowTop(GuiGraphicsExtractor context) {
    return getLowerStatusRowTop(context) - 1 - ICON_SIZE;
  }
}
