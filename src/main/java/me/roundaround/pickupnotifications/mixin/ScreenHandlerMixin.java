package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.util.CheckForNewItems;
import me.roundaround.pickupnotifications.util.HasServerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements HasServerPlayer {
  private ServerPlayerEntity player;
  private boolean firstRun = true;

  @Shadow
  boolean disableSync;

  @Shadow
  DefaultedList<ItemStack> previousTrackedStacks;

  @Shadow
  ItemStack previousCursorStack;

  @Shadow
  abstract Slot getSlot(int index);

  @Shadow
  abstract ItemStack getCursorStack();

  public void setPlayer(ServerPlayerEntity player) {
    this.player = player;
  }

  @Inject(method = "sendContentUpdates", at = @At(value = "HEAD"))
  public void sendContentUpdates(CallbackInfo info) {
    if (disableSync) {
      return;
    }

    if (firstRun) {
      firstRun = false;
      return;
    }

    CheckForNewItems.run(
        ((ScreenHandler) (Object) this),
        previousTrackedStacks,
        previousCursorStack,
        player);
  }
}
