package me.roundaround.pickupnotifications.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.pickupnotifications.util.CanRegisterScreenCloseItems;
import me.roundaround.pickupnotifications.util.CheckForNewItems;
import me.roundaround.pickupnotifications.util.HasServerPlayer;
import me.roundaround.pickupnotifications.util.InventorySnapshot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements HasServerPlayer, CanRegisterScreenCloseItems {
  private ServerPlayerEntity player;
  private boolean firstRun = true;
  private boolean pauseNotifications = false;
  private InventorySnapshot quickCraftItems = new InventorySnapshot();
  private InventorySnapshot returnedItemsFromScreenClose = new InventorySnapshot();

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

  @Override
  public void setPlayer(ServerPlayerEntity player) {
    this.player = player;
  }

  @Override
  public void registerScreenCloseReturns(ItemStack stack) {
    returnedItemsFromScreenClose.add(stack);
  }

  @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "net/minecraft/screen/ScreenHandler.transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;", ordinal = 0))
  public void beforeFirstTransferSlot(
      int slotIndex,
      int button,
      SlotActionType actionType,
      PlayerEntity player,
      CallbackInfo info) {
    pauseNotifications = true;
  }

  @Redirect(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "net/minecraft/screen/ScreenHandler.transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"))
  public ItemStack wrapTransferSlot(
      ScreenHandler self,
      PlayerEntity player,
      int slotIndex) {
    ItemStack result = self.transferSlot(player, slotIndex);

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

  @Inject(method = "dropInventory", at = @At(value = "HEAD"))
  public void dropInventory(PlayerEntity player, Inventory inventory, CallbackInfo info) {
    if (!(player instanceof ServerPlayerEntity)) {
      return;
    }

    if (!player.isAlive() || ((ServerPlayerEntity) player).isDisconnected()) {
      return;
    }

    if (inventory instanceof PlayerInventory) {
      return;
    }

    if (!(player.playerScreenHandler instanceof CanRegisterScreenCloseItems)) {
      return;
    }

    for (int i = 0; i < inventory.size(); i++) {
      // Register under the playerScreenHandler since currentScreenHandler is
      // closing - the diff checking will happen there.
      ((CanRegisterScreenCloseItems) player.playerScreenHandler)
          .registerScreenCloseReturns(inventory.getStack(i).copy());
    }
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

    CheckForNewItems.run(
        ((ScreenHandler) (Object) this),
        previousTrackedStacks,
        previousCursorStack,
        extraItemsForPrevious,
        player);
  }
}
