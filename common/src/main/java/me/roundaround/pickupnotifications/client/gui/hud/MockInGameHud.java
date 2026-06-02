package me.roundaround.pickupnotifications.client.gui.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class MockInGameHud {
  private static final int HOTBAR_WIDTH = 182;
  private static final int HOTBAR_HEIGHT = 22;
  private static final int SLOT_SIZE = 20;
  private static final int ITEM_SIZE = 16;
  private static final int SELECTION_WIDTH = 24;
  private static final int SELECTION_HEIGHT = 23;
  private static final int MOCK_SELECTED_SLOT = 2;
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

  // The hotbar preview is rendered straight from item textures rather than ItemStacks. ItemStacks
  // eagerly read their item's data components, which are only bound during a world load, so a real
  // stack would throw "Components not bound yet" on the title screen where the config screen is
  // reachable via Mod Menu. A null entry leaves the slot empty.
  private static final MockItem[] MOCK_HOTBAR = new MockItem[]{
      new MockItem(itemTexture("diamond_sword"), 1),
      new MockItem(itemTexture("bow"), 1),
      new MockItem(itemTexture("netherite_axe"), 1),
      new MockItem(itemTexture("netherite_pickaxe"), 1),
      null,
      new MockItem(itemTexture("cooked_porkchop"), 15),
      new MockItem(itemTexture("golden_apple"), 3),
      null,
      new MockItem(Identifier.withDefaultNamespace("textures/block/torch.png"), 57)
  };

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

  private static Identifier itemTexture(String name) {
    return Identifier.withDefaultNamespace("textures/item/" + name + ".png");
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

    for (int slot = 0; slot < MOCK_HOTBAR.length; slot++) {
      MockItem item = MOCK_HOTBAR[slot];
      if (item == null) {
        continue;
      }

      int itemX = x + 1 + slot * SLOT_SIZE + 2;
      int itemY = context.guiHeight() - SLOT_SIZE + 1;

      context.blit(RenderPipelines.GUI_TEXTURED, item.sprite(), itemX, itemY, 0f, 0f, ITEM_SIZE, ITEM_SIZE, ITEM_SIZE,
          ITEM_SIZE);
      if (item.count() > 1) {
        // Matches vanilla GuiGraphics#renderItemCount: count drawn bottom-right of the 16px icon.
        String count = Integer.toString(item.count());
        context.text(textRenderer, count, itemX + 19 - 2 - textRenderer.width(count), itemY + 6 + 3, -1, true);
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

  private record MockItem(Identifier sprite, int count) {
  }
}
