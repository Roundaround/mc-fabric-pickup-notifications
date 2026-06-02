package me.roundaround.pickupnotifications.mixin;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.pickupnotifications.client.gui.hud.PickupNotificationsHud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnv(MixinEnv.Env.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
  @Inject(method = "extractRenderState", at = @At("TAIL"))
  private void pickupnotifications$renderNotifications(
      GuiGraphicsExtractor graphics,
      DeltaTracker deltaTracker,
      CallbackInfo ci
  ) {
    PickupNotificationsHud.renderHud(graphics, deltaTracker);
  }
}
