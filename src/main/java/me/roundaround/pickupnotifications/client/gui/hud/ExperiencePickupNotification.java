package me.roundaround.pickupnotifications.client.gui.hud;

import me.roundaround.pickupnotifications.config.PickupNotificationsConfig;
import me.roundaround.pickupnotifications.roundalib.client.gui.util.GuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ExperiencePickupNotification extends PickupNotification<Integer> {
  private static final int ICON_SIZE = 16;
  private static final int TEXTURE_SIZE = 64;
  private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");

  private int amount;
  private int age;

  public ExperiencePickupNotification(int amount) {
    this(amount, false);
  }

  public ExperiencePickupNotification(int amount, boolean timeless) {
    super(timeless);
    this.amount = amount;
    this.age = 0;
  }

  @Override
  public void tick() {
    super.tick();
    this.age++;
  }

  @Override
  protected void renderIcon(DrawContext context, RenderTickCounter tickCounter) {
    int index = this.getOrbSize();
    int u = index % 4 * ICON_SIZE;
    int v = index / 4 * ICON_SIZE;

    float t = (this.age + tickCounter.getTickProgress(false)) / 2f;
    float r = (MathHelper.sin(t) + 1) * 0.5f;
    float b = (MathHelper.sin(t + (float) (Math.PI * 4 / 3)) + 1) * 0.1f;
    int color = GuiUtil.genColorInt(r, 1, b);

    context.drawTexture(
        RenderLayer::getGuiTextured,
        TEXTURE,
        0,
        0,
        u,
        v,
        ICON_SIZE,
        ICON_SIZE,
        ICON_SIZE,
        ICON_SIZE,
        TEXTURE_SIZE,
        TEXTURE_SIZE,
        color
    );
  }

  @Override
  protected Text getFormattedDisplayString(PickupNotificationsConfig config) {
    MutableText name = Text.translatable("subtitles.entity.experience_orb.pickup");
    return Text.literal(this.amount + "x ").append(name);
  }

  @Override
  protected boolean canAdd(Object value) {
    return value instanceof Integer;
  }

  @Override
  protected void add(Integer value) {
    this.amount += value;
  }

  private int getOrbSize() {
    // From ExperienceOrbEntity.getOrbSize();
    if (this.amount >= 2477) {
      return 10;
    } else if (this.amount >= 1237) {
      return 9;
    } else if (this.amount >= 617) {
      return 8;
    } else if (this.amount >= 307) {
      return 7;
    } else if (this.amount >= 149) {
      return 6;
    } else if (this.amount >= 73) {
      return 5;
    } else if (this.amount >= 37) {
      return 4;
    } else if (this.amount >= 17) {
      return 3;
    } else if (this.amount >= 7) {
      return 2;
    } else {
      return this.amount >= 3 ? 1 : 0;
    }
  }
}
