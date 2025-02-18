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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements HasServerPlayer, CanRegisterScreenCloseItems {
  @Unique
  private ServerPlayerEntity player;
  @Unique
  private boolean firstRun = true;
  @Unique
  private boolean pauseNotifications = false;
  @Unique
  private final InventorySnapshot quickCraftItems = new InventorySnapshot();
  @Unique
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
    this.returnedItemsFromScreenClose.add(stack);
  }

  @Inject(
      method = "internalOnSlotClick", at = @At(
      value = "INVOKE",
      target = "net/minecraft/screen/ScreenHandler.quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)" +
               "Lnet/minecraft/item/ItemStack;",
      ordinal = 0
  )
  )
  public void beforeFirstTransferSlot(
      int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info
  ) {
    this.pauseNotifications = true;
  }

  @Redirect(
      method = "internalOnSlotClick", at = @At(
      value = "INVOKE",
      target = "net/minecraft/screen/ScreenHandler.quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)" +
               "Lnet/minecraft/item/ItemStack;"
  )
  )
  public ItemStack wrapTransferSlot(
      ScreenHandler self, PlayerEntity player, int slotIndex
  ) {
    ItemStack result = self.quickMove(player, slotIndex);

    if (!result.isEmpty()) {
      this.quickCraftItems.add(result.copy());
    }

    return result;
  }

  @Inject(method = "internalOnSlotClick", at = @At(value = "TAIL"))
  public void internalOnSlotClick(
      int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info
  ) {
    this.pauseNotifications = false;
  }

  @Inject(method = "sendContentUpdates", at = @At(value = "HEAD"))
  public void sendContentUpdates(CallbackInfo info) {
    if (this.pauseNotifications || this.disableSync || this.player == null) {
      return;
    }

    if (this.firstRun) {
      this.firstRun = false;
      return;
    }

    // Declaring a separate snapshot so that we can collect bits from different
    // sources while keeping them separate.
    InventorySnapshot extraItemsForPrevious = new InventorySnapshot();

    if (!this.quickCraftItems.isEmpty()) {
      extraItemsForPrevious.addAll(this.quickCraftItems);
      this.quickCraftItems.clear();
    }

    if (!this.returnedItemsFromScreenClose.isEmpty()) {
      extraItemsForPrevious.addAll(this.returnedItemsFromScreenClose);
      this.returnedItemsFromScreenClose.clear();
    }

    CheckForNewItems.run(
        ((ScreenHandler) (Object) this),
        this.previousTrackedStacks,
        this.previousCursorStack,
        extraItemsForPrevious,
        this.player
    );
  }
}
