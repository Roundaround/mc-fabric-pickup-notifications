package me.roundaround.pickupnotifications.client.gui.hud;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.MathHelper;

public class PickupNotificationLine extends DrawableHelper {
    public static final float SHOW_DURATION = 120f;
    public static final float DURATION_INCREASE_ON_ADD = 60f;
    public static final float POP_DURATION = 5f;
    public static final float ANIM_DURATION = 6f;
    public static final float ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;
    public static final int SPRITE_SIZE = 14;
    public static final int SPRITE_RAW_SIZE = 16;
    public static final float SPRITE_SCALE = (float) SPRITE_SIZE / SPRITE_RAW_SIZE;
    public static final int PADDING = 2;

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

    public void render(MatrixStack matrixStack, int x, int y, int idx) {
        MutableText text = getFormattedDisplayString();
        TextRenderer textRenderer = minecraft.textRenderer;

        int textWidth = textRenderer.getWidth(text);
        int fullWidth = 3 * PADDING + textWidth + SPRITE_SIZE;

        x -= fullWidth * getXOffsetPercent();
        y += idx * (textRenderer.fontHeight + 2);

        renderBackgroundAndText(matrixStack, x, y, fullWidth);
        renderItem(x, y);
    }

    private void renderBackgroundAndText(MatrixStack matrixStack, float x, float y, int width) {
        MutableText text = getFormattedDisplayString();
        TextRenderer textRenderer = minecraft.textRenderer;

        float textOpacity = (float) minecraft.options.chatOpacity * 0.9f + 0.1f;
        float backgroundOpacity = (float) minecraft.options.textBackgroundOpacity;

        matrixStack.push();
        matrixStack.scale(1, 1, 1);
        matrixStack.translate(x, y, 800 + y);

        fill(matrixStack, -1, -1, width, textRenderer.fontHeight + 1, genColorInt(0, 0, 0, backgroundOpacity));

        {
            matrixStack.push();
            matrixStack.translate(2 * PADDING + SPRITE_SIZE, 0.5f, 0);
            RenderSystem.enableBlend();
            textRenderer.draw(matrixStack, text, 0, 0, genColorInt(1, 1, 1, textOpacity));
            RenderSystem.disableBlend();
            matrixStack.pop();
        }

        matrixStack.pop();
    }

    private void renderItem(float x, float y) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        RenderSystem.enableDepthTest();

        float popScale = 1f + MathHelper.clamp(popTimeRemaining, 0, 5) / 5f;
        float scaleX = SPRITE_SCALE / popScale;
        float scaleY = SPRITE_SCALE * (1f + popScale) / 2f;

        float xPos = x + PADDING - 2;
        float yPos = y - 2 + 0.5f - (SPRITE_SIZE - minecraft.textRenderer.fontHeight) / 2f;

        matrixStack.push();
        matrixStack.translate(0, 0, 801 + y);

        matrixStack.translate(xPos + 8, yPos + 12, 0);
        matrixStack.scale(scaleX, scaleY, 1);
        matrixStack.translate(-(xPos + 8), -(yPos + 12), 0);

        RenderSystem.applyModelViewMatrix();

        minecraft.getItemRenderer().renderInGui(itemStack, Math.round(xPos), Math.round(yPos));

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
        return new LiteralText(itemStack.getCount() + "x ").append(itemStack.getItem().getName());
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
