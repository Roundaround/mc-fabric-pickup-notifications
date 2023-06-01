package me.roundaround.pickupnotifications.mixin;

import me.roundaround.pickupnotifications.util.CanRegisterScreenCloseItems;
import me.roundaround.pickupnotifications.util.CheckForNewItems;
import me.roundaround.pickupnotifications.util.HasServerPlayer;
import me.roundaround.pickupnotifications.util.InventorySnapshot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements HasServerPlayer, CanRegisterScreenCloseItems {
  private ServerPlayerEntity player;
  private boolean firstRun = true;
  private boolean pauseNotifications = false;
  private final InventorySnapshot quickCraftItems = new InventorySnapshot();
  private final InventorySnapshot returnedItemsFromScreenClose = new InventorySnapshot();

  @Shadow
  private boolean disableSync;

  @Final
  @Shadow
  private DefaultedList<ItemStack> previousTrackedStacks;

  @Shadow
  private ItemStack previousCursorStack;

  @Shadow
  public abstract Slot getSlot(int index);

  @Shadow
  public abstract ItemStack getCursorStack();

  @Override
  public void setPlayer(ServerPlayerEntity player) {
    this.player = player;
  }

  @Override
  public void registerScreenCloseReturns(ItemStack stack) {
    returnedItemsFromScreenClose.add(stack);
  }

  @Inject(
      method = "internalOnSlotClick", at = @At(
      value = "INVOKE",
      target = "net/minecraft/screen/ScreenHandler.quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",
      ordinal = 0
  )
  )
  public void beforeFirstTransferSlot(
      int slotIndex,
      int button,
      SlotActionType actionType,
      PlayerEntity player,
      CallbackInfo info) {
    pauseNotifications = true;
  }

  @Redirect(
      method = "internalOnSlotClick", at = @At(
      value = "INVOKE",
      target = "net/minecraft/screen/ScreenHandler.quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"
  )
  )
  public ItemStack wrapTransferSlot(
      ScreenHandler self, PlayerEntity player, int slotIndex) {
    ItemStack result = self.quickMove(player, slotIndex);

    if (!result.isEmpty()) {
      quickCraftItems.add(result.copy());
    }

    return result;
  }

  @Inject(method = "internalOnSlotClick", at = @At(value = "TAIL"))
  public void internalOnSlotClick(
      int slotIndex,
      int button,
      SlotActionType actionType,
      PlayerEntity player,
      CallbackInfo info) {
    pauseNotifications = false;
  }

  @Inject(method = "sendContentUpdates", at = @At(value = "HEAD"))
  public void sendContentUpdates(CallbackInfo info) {
    if (pauseNotifications || disableSync || player == null) {
      return;
    }

    if (firstRun) {
      firstRun = false;
      return;
    }

    // Declaring a separate snapshot so that we can collect bits from different
    // sources while keeping them separate.
    InventorySnapshot extraItemsForPrevious = new InventorySnapshot();

    if (!quickCraftItems.isEmpty()) {
      extraItemsForPrevious.addAll(quickCraftItems);
      quickCraftItems.clear();
    }

    if (!returnedItemsFromScreenClose.isEmpty()) {
      extraItemsForPrevious.addAll(returnedItemsFromScreenClose);
      returnedItemsFromScreenClose.clear();
    }

    CheckForNewItems.run(((ScreenHandler) (Object) this),
        previousTrackedStacks,
        previousCursorStack,
        extraItemsForPrevious,
        player);
  }
}
